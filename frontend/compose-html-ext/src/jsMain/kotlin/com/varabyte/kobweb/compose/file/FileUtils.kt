@file:Suppress("DEPRECATION")

package com.varabyte.kobweb.compose.file

import com.varabyte.kobweb.browser.file.loadDataUrlFromDisk
import com.varabyte.kobweb.browser.file.loadFromDisk
import com.varabyte.kobweb.browser.file.loadMultipleDataUrlFromDisk
import com.varabyte.kobweb.browser.file.loadMultipleFromDisk
import com.varabyte.kobweb.browser.file.loadMultipleTextFromDisk
import com.varabyte.kobweb.browser.file.loadTextFromDisk
import com.varabyte.kobweb.browser.file.readBytes
import com.varabyte.kobweb.browser.file.saveTextToDisk
import com.varabyte.kobweb.browser.file.saveToDisk
import org.w3c.dom.Document
import org.w3c.files.File
import org.w3c.files.FileReader

@Deprecated("We are migrating non-Compose utilities to a new artifact. Please change your imports to use `com.varabyte.kobweb.browser.file.FileException` instead (that is, `compose` → `browser`).")
typealias FileException = com.varabyte.kobweb.browser.file.FileException
@Deprecated("We are migrating non-Compose utilities to a new artifact. Please change your imports to use `com.varabyte.kobweb.browser.file.FileErrorException` instead (that is, `compose` → `browser`).")
typealias FileErrorException = com.varabyte.kobweb.browser.file.FileErrorException

/**
 * Read the contents of a file as a ByteArray, suspending until the read is complete.
 *
 * @throws FileErrorException if the file could not be read.
 */
@Suppress("DeprecatedCallableAddReplaceWith") // Migrating deprecated extension methods is not a good experience
@Deprecated("We are migrating non-Compose utilities to a new artifact. Please change your imports to use `com.varabyte.kobweb.browser.file.readBytes` instead (that is, `compose` → `browser`).")
suspend fun File.readBytes() = readBytes()

/**
 * Read the contents of a file as a ByteArray, asynchronously.
 */
@Suppress("DeprecatedCallableAddReplaceWith") // Migrating deprecated extension methods is not a good experience
@Deprecated("We are migrating non-Compose utilities to a new artifact. Please change your imports to use `com.varabyte.kobweb.browser.file.readBytes` instead (that is, `compose` → `browser`).")
fun File.readBytes(onError: () -> Unit = {}, onLoad: (ByteArray) -> Unit) = readBytes(onError, onLoad)

/**
 * Save some content to disk, presenting the user with a dialog to choose the file location.
 *
 * This method extends the global `document` variable, so you can use it like this:
 *
 * ```
 * document.saveToDisk("picture.png", bytes, "image/png")
 * ```
 *
 * @param filename The suggested name of the file to save (users will be given a chance to override it).
 * @param content The content to save.
 * @param mimeType Optional mime type information you can save about the file. For example, if you're saving a PNG image, you
 *   could pass in "image/png" here. This information may not be necessary if you're just saving / loading binary or
 *   text contents, but it can be useful if you expect something else might consume this file. It will be made available
 *   in [LoadContext] when the file is loaded and will be embedded into the URL returned by [loadDataUrlFromDisk].
 */
@Suppress("DeprecatedCallableAddReplaceWith") // Migrating deprecated extension methods is not a good experience
@Deprecated("We are migrating non-Compose utilities to a new artifact. Please change your imports to use `com.varabyte.kobweb.browser.file.saveToDisk` instead (that is, `compose` → `browser`).")
fun Document.saveToDisk(
    filename: String,
    content: ByteArray,
    mimeType: String? = null,
) = saveToDisk(filename, content, mimeType)

/**
 * A convenience method to call [saveToDisk] with a String instead of a ByteArray.
 *
 * Note that this always encodes text in UTF-8 format.
 */
@Suppress("DeprecatedCallableAddReplaceWith") // Migrating deprecated extension methods is not a good experience
@Deprecated("We are migrating non-Compose utilities to a new artifact. Please change your imports to use `com.varabyte.kobweb.browser.file.saveTextToDisk` instead (that is, `compose` → `browser`).")
fun Document.saveTextToDisk(
    filename: String,
    content: String,
    mimeType: String? = null,
) = saveTextToDisk(filename, content, mimeType)

@Deprecated("We are migrating non-Compose utilities to a new artifact. Please change your imports to use `com.varabyte.kobweb.browser.file.LoadContext` instead (that is, `compose` → `browser`).")
typealias LoadContext = com.varabyte.kobweb.browser.file.LoadContext

/**
 * Load some binary content from disk, presenting the user with a dialog to choose the file to load.
 *
 * This method extends the global `document` variable, so you can use it like this:
 *
 * ```
 * document.loadFromDisk(".png") { bytes -> /* ... */ }
 * ```
 *
 * @param accept A comma-separated list of extensions to filter by (e.g. ".txt,.sav").
 * @param onError A callback which will be invoked if the file could not be loaded.
 * @param onLoad A callback which will contain the contents of your file, if successfully loaded. The callback is
 *   scoped by a [LoadContext] which contains additional information about the file, such as its name and mime type.
 *
 * @see FileReader.readAsArrayBuffer
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTML/Attributes/accept">Accept values</a>
 */
@Suppress("DeprecatedCallableAddReplaceWith") // Migrating deprecated extension methods is not a good experience
@Deprecated("We are migrating non-Compose utilities to a new artifact. Please change your imports to use `com.varabyte.kobweb.browser.file.loadFromDisk` instead (that is, `compose` → `browser`).")
fun Document.loadFromDisk(
    accept: String = "",
    onError: LoadContext.() -> Unit = {},
    onLoad: LoadContext.(ByteArray) -> Unit,
) = loadFromDisk(accept, onError, onLoad)

/**
 * Like [loadFromDisk] but specifically loads some content from disk as a URL with base64-encoded data.
 *
 * This is useful (necessary?) for loading images in a format that image elements can consume.
 *
 * See `loadFromDisk` for details about the parameters.
 *
 * @see loadFromDisk
 * @see FileReader.readAsDataURL
 */
@Suppress("DeprecatedCallableAddReplaceWith") // Migrating deprecated extension methods is not a good experience
@Deprecated("We are migrating non-Compose utilities to a new artifact. Please change your imports to use `com.varabyte.kobweb.browser.file.loadDataUrlFromDisk` instead (that is, `compose` → `browser`).")
fun Document.loadDataUrlFromDisk(
    accept: String = "",
    onError: LoadContext.() -> Unit = {},
    onLoad: LoadContext.(String) -> Unit,
) = loadDataUrlFromDisk(accept, onError, onLoad)

/**
 * Like [loadFromDisk] but convenient for dealing with text files.
 *
 * See `loadFromDisk` for details about the parameters.
 *
 * @see loadFromDisk
 * @see FileReader.readAsText
 * @see saveTextToDisk
 */
@Suppress("DeprecatedCallableAddReplaceWith") // Migrating deprecated extension methods is not a good experience
@Deprecated("We are migrating non-Compose utilities to a new artifact. Please change your imports to use `com.varabyte.kobweb.browser.file.loadTextFromDisk` instead (that is, `compose` → `browser`).")
fun Document.loadTextFromDisk(
    accept: String = "",
    encoding: String = "UTF-8",
    onError: LoadContext.() -> Unit = {},
    onLoad: LoadContext.(String) -> Unit,
) = loadTextFromDisk(accept, encoding, onError, onLoad)

@Deprecated("We are migrating non-Compose utilities to a new artifact. Please change your imports to use `com.varabyte.kobweb.browser.file.LoadedFile` instead (that is, `compose` → `browser`).")
typealias LoadedFile<O> = com.varabyte.kobweb.browser.file.LoadedFile<O>

/**
 * Like [loadFromDisk] but allows loading multiple files at once.
 *
 * The `onLoad` callback will be invoked with a list of [LoadedFile] objects, each of which contains that file's
 * contents:
 *
 * ```
 * document.loadMultipleFromDisk(".png") { files ->
 *   files.forEach { file ->
 *      println("Loaded ${file.contents.size} bytes from ${file.context.filename}")
 *   }
 * }
 * ```
 */
@Suppress("DeprecatedCallableAddReplaceWith") // Migrating deprecated extension methods is not a good experience
@Deprecated("We are migrating non-Compose utilities to a new artifact. Please change your imports to use `com.varabyte.kobweb.browser.file.loadMultipleFromDisk` instead (that is, `compose` → `browser`).")
fun Document.loadMultipleFromDisk(
    accept: String = "",
    onError: (List<LoadContext>) -> Unit = {},
    onLoad: (List<LoadedFile<ByteArray>>) -> Unit,
) = loadMultipleFromDisk(accept, onError, onLoad)

@Suppress("DeprecatedCallableAddReplaceWith") // Migrating deprecated extension methods is not a good experience
@Deprecated("We are migrating non-Compose utilities to a new artifact. Please change your imports to use `com.varabyte.kobweb.browser.file.loadMultipleDataUrlFromDisk` instead (that is, `compose` → `browser`).")
fun Document.loadMultipleDataUrlFromDisk(
    accept: String = "",
    onError: (List<LoadContext>) -> Unit = {},
    onLoad: (List<LoadedFile<String>>) -> Unit,
) = loadMultipleDataUrlFromDisk(accept, onError, onLoad)

@Suppress("DeprecatedCallableAddReplaceWith") // Migrating deprecated extension methods is not a good experience
@Deprecated("We are migrating non-Compose utilities to a new artifact. Please change your imports to use `com.varabyte.kobweb.browser.file.loadMultipleTextFromDisk` instead (that is, `compose` → `browser`).")
fun Document.loadMultipleTextFromDisk(
    accept: String = "",
    encoding: String = "UTF-8",
    onError: (List<LoadContext>) -> Unit = {},
    onLoad: (List<LoadedFile<String>>) -> Unit,
) = loadMultipleTextFromDisk(accept, encoding, onError, onLoad)
