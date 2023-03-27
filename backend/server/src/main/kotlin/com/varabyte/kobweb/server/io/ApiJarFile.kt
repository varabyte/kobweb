package com.varabyte.kobweb.server.io

import com.varabyte.kobweb.api.Apis
import com.varabyte.kobweb.api.ApisFactory
import com.varabyte.kobweb.api.log.Logger
import com.varabyte.kobweb.project.io.LiveFile
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
class ApiJarFile(path: Path, private val logger: Logger) {
    private class DynamicClassLoader(private val content: ByteArray) : ClassLoader(ApiJarFile::class.java.classLoader) {
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
            return findFileInZip(name)
                ?.toInMemoryUrl()
                ?: super.findResource(name)
        }

        override fun findResources(name: String): Enumeration<URL> {
            val ourResource = findFileInZip(name)
                ?.toInMemoryUrl()
                ?.let { listOf(it) }
                ?: emptyList()

            return Collections.enumeration(ourResource + super.findResources(name).toList())
        }

        // Convert a class name (e.g. "com.example.Demo") to its path form ("com/example/Demo.class")
        private fun findClassInZip(name: String) = findFileInZip("${name.replace('.', '/')}.class")

        private fun findFileInZip(name: String): InputStream? {
            // Convert a class name (e.g. "com.example.Demo") to its path form ("com/example/Demo.class")
            val entry: ZipEntry? = zipFile.getEntry(name)
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
            val currContent = delegateFile.content ?: error("No API jar found at: ${delegateFile.path}")

            var cache = cache // Reassign temporarily so Kotlin knows it won't change underneath us
            if (cache == null || cache.content !== delegateFile.content) {
                cache = Cache(currContent, logger)
                this.cache = cache
            }

            return cache.apis
        }
}