package com.varabyte.kobweb.server.io

import com.varabyte.kobweb.api.Apis
import com.varabyte.kobweb.api.ApisFactory
import com.varabyte.kobweb.api.env.Environment
import com.varabyte.kobweb.api.event.EventDispatcher
import com.varabyte.kobweb.api.event.dispose.DisposeEvent
import com.varabyte.kobweb.api.event.dispose.DisposeReason
import com.varabyte.kobweb.api.log.Logger
import com.varabyte.kobweb.project.io.LiveFile
import com.varabyte.kobweb.server.api.ServerEnvironment
import io.ktor.util.date.*
import java.io.ByteArrayInputStream
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.net.URI
import java.net.URL
import java.net.URLConnection
import java.net.URLStreamHandler
import java.nio.file.Path
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

// Must only ever call once or else we get a thrown Error
private object UrlStreamHandlerFactoryInitializer {
    private var initialized = false

    fun initialize() {
        if (initialized) return
        try {
            // Code inspired by org.jetbrains.kotlin.codegen.GeneratedClassLoader and .BytesUrlUtils
            // See also: toInMemoryUrl
            URL.setURLStreamHandlerFactory { protocol ->
                if (protocol == "bytes") {
                    object : URLStreamHandler() {
                        override fun openConnection(url: URL): URLConnection {
                            return object : URLConnection(url) {
                                override fun connect() {}
                                override fun getInputStream(): InputStream {
                                    return ByteArrayInputStream(Base64.getDecoder().decode(url.path))
                                }
                            }
                        }
                    }
                } else null
            }
        } finally {
            initialized = true
        }
    }
}

/**
 * Wrapper around a Kobweb API jar, which is expected (in dev mode at least) to occasionally be reloaded on the fly.
 *
 * A Kobweb API jar is a bundle of user code meant to be loaded by the server and used to handle API requests.
 *
 * @param path The path to the api.jar itself
 */
class ApiJarFile(
    path: Path,
    private val environment: ServerEnvironment,
    private val events: EventDispatcher,
    private val logger: Logger,
    private val nativeLibraryMappings: Map<String, String>
) {
    /**
     * A classloader provided for user code that is mostly isolated from the server classloader.
     *
     * This allows users to pull in all sorts of dependencies without worrying about conflicts with the server's
     * dependencies, i.e. both the server and user code can depend on different versions of the same library, because
     * their classloader is on a different branch than the server's classloader.
     *
     * Note that *some* of the server's dependencies are still exposed to the user code, especially the base
     * `ApisFactory` interface (and surrounding API classes). Otherwise, the JVM throws an exception when the server
     * tries to use user code, because it will see that it has two separate instances of the same class name but with
     * different byte code. Kotlin standard library methods are also provided by the server classloader. See
     * the [packagesFromServerClassLoader] variable for the full list.
     */
    private class IsolatedZipClassLoader(
        private val content: ByteArray,
        private val logger: Logger,
        private val nativeLibraryMappings: Map<String, String>,
        private val serverClassLoader: ClassLoader = ApiJarFile::class.java.classLoader,
    ) : ClassLoader(serverClassLoader.parent) {

        private val zipFile: ZipFile = run {
            val file = File.createTempFile("KobwebApiJar", ".jar").also { it.deleteOnExit() }
            ByteArrayInputStream(content).use { stream -> file.writeBytes(stream.readBytes()) }
            ZipFile(file)
        }

        // Packages that, if encountered, are provided by the server classloader instead of the user's classloader.
        // This allows the server to use user code without running into class conflicts. We want to keep this list as
        // small as possible, to avoid confusion for users. (For example, a user might try to use a newer method from a
        // more recent version of Kotlin than the one provided by the server, which would compile for them but then fail
        // at runtime; while this is done to allow server and user code to talk seamlessly, they'll just see a runtime
        // error and not be sure why it's happening.)
        private val packagesFromServerClassLoader = listOf(
            "com.varabyte.kobweb.api.",
            "kotlin.",
        )
        override fun findClass(name: String): Class<*> {
            if (packagesFromServerClassLoader.any { name.startsWith(it) }) return serverClassLoader.loadClass(name)

            return findClassInZip(name)
                ?.use { stream -> stream.readBytes() }
                ?.let { bytes -> defineClass(name, bytes, 0, bytes.size) }
                ?: super.findClass(name)
        }

        init {
            UrlStreamHandlerFactoryInitializer.initialize()
        }

        // Code inspired by org.jetbrains.kotlin.codegen.GeneratedClassLoader and .BytesUrlUtils
        private fun InputStream.toInMemoryUrl(): URL {
            return this
                .use { readBytes() }
                .let { bytes ->
                    // Note: In Java 20, URL constructors starting warning of impending deprecation. URI.toURL is now
                    // recommended.
                    URI(
                        "bytes",
                        Base64.getEncoder().encodeToString(bytes),
                        null
                    ).toURL()
                }
        }

        override fun findResource(name: String): URL {
            return findFileInZipByPath(name)
                ?.toInMemoryUrl()
                ?: super.findResource(name)
        }

        override fun findResources(name: String): Enumeration<URL> {
            val ourResource = findFileInZipByPath(name)
                ?.toInMemoryUrl()
                ?.let { listOf(it) }
                ?: emptyList()

            return Collections.enumeration(ourResource + super.findResources(name).toList())
        }

        override fun findLibrary(libname: String): String? {
            val sysLibName = System.mapLibraryName(libname)

            logger.debug("Kobweb server got a request to load native library: \"$libname\" (system mapped to \"$sysLibName\")")
            val path =
                (nativeLibraryMappings[libname]
                    ?: nativeLibraryMappings[sysLibName]
                    ?: findPathsInZipByName(sysLibName)
                        .takeIf { it.isNotEmpty() }
                        ?.let { paths ->
                            if (paths.size > 1) {
                                logger.info(
                                    "... multiple copies of $sysLibName found in the jar: [${paths.joinToString(",") { path -> "\"$path\"" }}]. Using the first match. Consider registering \"$libname\" explicitly in your conf.yaml."
                                )
                            }
                            paths.first()
                        }
                    )

            if (path != null) {
                val stream = findFileInZipByPath(path)
                if (stream != null) {
                    logger.debug("... found it in the jar at: $path")
                } else {
                    logger.debug("... could not find it in the jar at: $path")
                }

                stream?.use {
                    val bytes = stream.readBytes()
                    val ext = path.substringAfterLast('.', "")
                        .takeIf { it.isNotEmpty() }
                        ?.let { ".$it" }
                        ?: ""
                    val base = sysLibName.removeSuffix(ext)
                    val file =
                        File.createTempFile("${base}_", ext.takeIf { it.isNotEmpty() }).also { it.deleteOnExit() }
                    file.writeBytes(bytes)
                    logger.debug("... created a copy at: ${file.absolutePath}")
                    return file.absolutePath
                }
            } else {
                logger.debug("... could not find it in the jar.")
            }

            logger.debug("... falling back to system library searching logic.")
            return super.findLibrary(sysLibName)
        }

        // Convert a class name (e.g. "com.example.Demo") to its path form ("com/example/Demo.class")
        private fun findClassInZip(name: String) = findFileInZipByPath("${name.replace('.', '/')}.class")

        // Find a file in the zip by its exact path (e.g. "com/example/Demo.class")
        private fun findFileInZipByPath(path: String): InputStream? {
            // Convert a class name (e.g. "com.example.Demo") to its path form ("com/example/Demo.class")
            val entry: ZipEntry? = zipFile.getEntry(path)
            return entry
                ?.let {
                    try {
                        zipFile.getInputStream(entry)
                    } catch (e: IOException) {
                        null
                    }
                }
        }

        // Find all paths in the zip ending with the specified file name (e.g. "test.dll" -> ["lib/test.dll"]). This
        // requires running through the entire zip file, so calling findFileInZipByPath directly is preferred.
        private fun findPathsInZipByName(name: String): List<String> {
            val paths = mutableListOf<String>()
            zipFile.entries().asSequence().forEach { entry ->
                if (entry.name.substringAfterLast('/') == name) {
                    paths.add(entry.name)
                }
            }
            return paths
        }
    }

    private class Cache(
        val content: ByteArray,
        environment: ServerEnvironment,
        events: EventDispatcher,
        logger: Logger,
        nativeLibraryMappings: Map<String, String>
    ) {
        private fun ServerEnvironment.toApiEnvironment(): Environment {
            return when (this) {
                ServerEnvironment.DEV -> Environment.DEV
                ServerEnvironment.PROD -> Environment.PROD
            }
        }

        val apis: Apis = run {
            val classLoader = IsolatedZipClassLoader(content, logger, nativeLibraryMappings)
            val startMs = getTimeMillis()

            try {
                val factory =
                    classLoader.loadClass("ApisFactoryImpl").getDeclaredConstructor().newInstance() as ApisFactory
                events.reset()
                factory.create(environment.toApiEnvironment(), events, logger)
            } finally {
                val elapsedMs = getTimeMillis() - startMs
                logger.info("Loaded and initialized server API jar in ${elapsedMs}ms.")
            }
        }
    }

    private val delegateFile = LiveFile(path)
    private var cache: Cache? = null

    val apis: Apis
        get() {
            val currContent = delegateFile.content ?: error("No API jar found at: ${delegateFile.path}")

            var cache = cache // Reassign temporarily so Kotlin knows it won't change underneath us
            if (cache == null || cache.content !== delegateFile.content) {
                events.dispose(DisposeEvent(DisposeReason.DEV_API_RELOAD))
                cache = Cache(currContent, environment, events, logger, nativeLibraryMappings)
                this.cache = cache
            }

            return cache.apis
        }

    init {
        // Force initialization. This will trigger @InitApi calls when the server starts up, instead of waiting lazily
        // for the first time an API route is triggered (which could add a lot of unexpected and unwanted latency to the
        // first time a server request is made).
        apis
    }
}
