package com.varabyte.kobweb.server.io

import com.varabyte.kobweb.api.Apis
import com.varabyte.kobweb.api.ApisFactory
import com.varabyte.kobweb.api.log.Logger
import com.varabyte.kobweb.project.io.LiveFile
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import kotlin.io.path.writeBytes

/**
 * Wrapper around a Kobweb API jar, which is expected (in dev mode at least) to occasionally be reloaded on the fly.
 *
 * @param path The path to the api.jar itself
 */
class ApiJarFile(path: Path, private val logger: Logger) {
    private class DynamicClassLoader(private val content: ByteArray) : ClassLoader(ApiJarFile::class.java.classLoader) {
        private val zipFile: ZipFile = run {
            val path = Files.createTempFile("KobwebApiJar", ".jar").also { it.toFile().deleteOnExit() }
            ByteArrayInputStream(content).use { stream -> path.writeBytes(stream.readBytes()) }
            ZipFile(path.toFile())
        }

        private val classCache = mutableMapOf<String, Class<*>?>()

        override fun findClass(name: String): Class<*> {
            classCache.computeIfAbsent(name) {
                findClassInZip(name)
                    ?.use { stream -> stream.readBytes() }
                    ?.let { bytes -> defineClass(name, bytes, 0, bytes.size) }
            }

            val classValue = classCache[name]
            return classValue ?: super.findClass(name)
        }

        private fun findClassInZip(name: String): InputStream? {
            // Convert a class name (e.g. "com.example.Demo") to its path form ("com/example/Demo.class")
            val entry: ZipEntry? = zipFile.getEntry("${name.replace('.', '/')}.class")
            return entry
                ?.let {
                    try {
                        zipFile.getInputStream(entry)
                    } catch (e: IOException) {
                        null
                    }
                }
        }
    }

    private class Cache(val content: ByteArray, logger: Logger) {
        val apis: Apis = run {
            val classLoader = DynamicClassLoader(content)
            val factory = classLoader.loadClass("ApisFactoryImpl").getDeclaredConstructor().newInstance() as ApisFactory
            factory.create(logger)
        }
    }

    private val delegateFile = LiveFile(path)
    private var cache: Cache? = null

    val apis: Apis
        get() {
            val currContent = delegateFile.content ?: error { "No API jar found at: ${delegateFile.path}" }

            var cache = cache // Reassign temporarily so Kotlin knows it won't change underneath us
            if (cache == null || cache.content !== delegateFile.content) {
                cache = Cache(currContent, logger)
                this.cache = cache
            }

            return cache.apis
        }
}