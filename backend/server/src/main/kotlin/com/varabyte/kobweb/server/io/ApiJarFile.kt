package com.varabyte.kobweb.server.io

import com.varabyte.kobweb.api.Apis
import com.varabyte.kobweb.api.ApisFactory
import com.varabyte.kobweb.api.log.Logger
import com.varabyte.kobweb.project.io.LiveFile
import io.ktor.util.date.*
import java.io.ByteArrayInputStream
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.net.URLConnection
import java.net.URLStreamHandler
import java.nio.file.Path
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

/**
 * Wrapper around a Kobweb API jar, which is expected (in dev mode at least) to occasionally be reloaded on the fly.
 *
 * @param path The path to the api.jar itself
 */
class ApiJarFile(path: Path, private val logger: Logger, private val nativeLibraryMappings: Map<String, String>) {
    private class DynamicClassLoader(
        private val content: ByteArray,
        private val logger: Logger,
        private val nativeLibraryMappings: Map<String, String>
    ) : ClassLoader(ApiJarFile::class.java.classLoader) {
        private val zipFile: ZipFile = run {
            val file = File.createTempFile("KobwebApiJar", ".jar").also { it.deleteOnExit() }
            ByteArrayInputStream(content).use { stream -> file.writeBytes(stream.readBytes()) }
            ZipFile(file)
        }

        override fun findClass(name: String): Class<*> {
            return findClassInZip(name)
                ?.use { stream -> stream.readBytes() }
                ?.let { bytes -> defineClass(name, bytes, 0, bytes.size) }
                ?: super.findClass(name)
        }

        // Code inspired by org.jetbrains.kotlin.codegen.GeneratedClassLoader and .BytesUrlUtils
        private fun InputStream.toInMemoryUrl(): URL {
            return this
                .use { readBytes() }
                .let { bytes ->
                    URL(
                        null,
                        "bytes:${Base64.getEncoder().encodeToString(bytes)}",
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
                    )
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

    private class Cache(val content: ByteArray, logger: Logger, nativeLibraryMappings: Map<String, String>) {
        val apis: Apis = run {
            val classLoader = DynamicClassLoader(content, logger, nativeLibraryMappings)
            val startMs = getTimeMillis()

            try {
                val factory =
                    classLoader.loadClass("ApisFactoryImpl").getDeclaredConstructor().newInstance() as ApisFactory
                factory.create(logger)
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
                cache?.apis?.dispose()
                cache = Cache(currContent, logger, nativeLibraryMappings)
                this.cache = cache
            }

            return cache.apis
        }
}
