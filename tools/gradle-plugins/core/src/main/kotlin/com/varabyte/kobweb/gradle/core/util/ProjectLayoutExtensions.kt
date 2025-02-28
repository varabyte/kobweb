package com.varabyte.kobweb.gradle.core.util

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.gradle.api.GradleException
import org.gradle.api.file.Directory
import org.gradle.api.file.ProjectLayout
import org.gradle.api.file.RegularFile
import org.gradle.api.logging.Logger
import org.gradle.api.provider.Provider
import java.io.File
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL


/**
 * Return a unique build cache directory.
 */
fun ProjectLayout.kobwebCacheDir(): Provider<Directory> {
    return buildDirectory.dir("kobweb/cache")
}

/**
 * Return a file under the cache directory.
 *
 * @see kobwebCacheDir
 */
fun ProjectLayout.kobwebCacheFile(path: String): Provider<RegularFile> {
    return kobwebCacheDir().map { it.file(path) }
}

/**
 * Additional information about the file we just downloaded.
 *
 * If a file is downloaded to "/path/to/cache/target.txt", then metadata about the download will be stored in
 * "/path/to/cache/target.txt.metadata.json".
 */
@Suppress("unused") // Fields can be useful for human review even if not used in code
@Serializable
class DownloadedFileMetadata(
    val fromUrl: String,
    val contentType: String?
) {
    companion object {
        const val FILE_EXT = "metadata.json"
    }
}

class DownloadResult(val file: File, val metadata: DownloadedFileMetadata)

private fun downloadFile(url: URL, targetFile: File): DownloadedFileMetadata {
    try {
        targetFile.parentFile.mkdirs()
        targetFile.createNewFile()

        with(url.openConnection() as HttpURLConnection) {
            requestMethod = "GET"
            // Act like a browser; some links return different results in that case
            // The following user-agent came from inspecting a local web session in Chrome.
            setRequestProperty(
                "User-Agent",
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/133.0.0.0 Safari/537.36"
            )

            inputStream.use { input ->
                targetFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            return DownloadedFileMetadata(url.toString(), contentType)
        }
    } catch (e: IOException) {
        println(e.toString())
        throw GradleException("e: Failed to download file from $url", e)
    }
}

fun ProjectLayout.downloadOrCached(logger: Logger, url: URL): DownloadResult {
    // Convert URL to file path
    val cachedFile = kobwebCacheFile(url.path.removePrefix("/").replace("/", File.separator)).get().asFile
    logger.info("Download requested: $url...")
    if (!cachedFile.exists()) {
        val metadata = downloadFile(url, cachedFile)
        logger.info("      Completed --> ${cachedFile.absolutePath}")
        cachedFile.parentFile.resolve("${cachedFile.name}.${DownloadedFileMetadata.FILE_EXT}").writeText(
            Json.encodeToString(metadata)
        )

        check(cachedFile.exists())

        return DownloadResult(cachedFile, metadata)
    } else {
        logger.info("      Cache hit <-- ${cachedFile.absolutePath}")

        return DownloadResult(
            cachedFile,
            cachedFile.parentFile.resolve("${cachedFile.name}.${DownloadedFileMetadata.FILE_EXT}")
                .takeIf { it.exists() }
                ?.readText()
                ?.let { text -> Json.decodeFromString<DownloadedFileMetadata?>(text) }
                ?: throw GradleException("Valid metadata file not found for \"${cachedFile.absolutePath}\". This is not expected. Please clean your build directories and try again."))
    }
}
