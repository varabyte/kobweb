package com.varabyte.kobweb.compose.file

import kotlinx.browser.window
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Int8Array
import org.khronos.webgl.get
import org.w3c.dom.Document
import org.w3c.dom.HTMLAnchorElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.asList
import org.w3c.dom.events.Event
import org.w3c.files.Blob
import org.w3c.files.BlobPropertyBag
import org.w3c.files.File
import org.w3c.files.FileReader
import org.w3c.files.get
import org.w3c.xhr.ProgressEvent
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import org.w3c.dom.url.URL as DomURL

abstract class FileException(val file: File, message: String) : Throwable("File (${file.name}): $message")
class FileErrorException(file: File) : FileException(file, "read failed")

private fun handleUnexpectedOnAbort(context: String) {
    error("Unexpected onabort occurred in $context; aborting file loads should not currently be possible. Please report this issue at https://github.com/varabyte/kobweb/issues")
}

/**
 * Read the contents of a file as a ByteArray, suspending until the read is complete.
 *
 * @throws FileErrorException if the file could not be read.
 */
suspend fun File.readBytes(): ByteArray {
    return suspendCoroutine { cont ->
        val reader = FileReader()
        reader.onload = { loadEvt ->
            val result = loadEvt.target.unsafeCast<FileReader>().result as ArrayBuffer
            val intArray = Int8Array(result)
            cont.resume(ByteArray(intArray.byteLength) { i -> intArray[i] })
        }
        reader.onabort = { handleUnexpectedOnAbort("readBytes") }
        reader.onerror = { cont.resumeWithException(FileErrorException(this)) }

        reader.readAsArrayBuffer(this)
    }
}

/**
 * Read the contents of a file as a ByteArray, asynchronously.
 */
fun File.readBytes(onError: () -> Unit = {}, onLoad: (ByteArray) -> Unit) {
    CoroutineScope(window.asCoroutineDispatcher()).launch {
        try {
            onLoad(readBytes())
        } catch (e: FileErrorException) {
            onError()
        }
    }
}

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
fun Document.saveToDisk(
    filename: String,
    content: ByteArray,
    mimeType: String? = null,
) {
    val snapshotBlob = Blob(arrayOf(content), BlobPropertyBag(mimeType.orEmpty()))
    val url = DomURL.createObjectURL(snapshotBlob)
    val tempAnchor = (createElement("a") as HTMLAnchorElement).apply {
        style.display = "none"
        href = url
        download = filename
    }
    body!!.append(tempAnchor)
    tempAnchor.click()
    DomURL.revokeObjectURL(url)
    tempAnchor.remove()
}

/**
 * A convenience method to call [saveToDisk] with a String instead of a ByteArray.
 *
 * Note that this always encodes text in UTF-8 format.
 */
fun Document.saveTextToDisk(
    filename: String,
    content: String,
    mimeType: String? = null,
) {
    saveToDisk(filename, content.encodeToByteArray(), mimeType)
}

class LoadContext(
    val filename: String,
    val mimeType: String?,
    val event: ProgressEvent,
)

private fun Document.loadFromDisk(
    accept: String,
    multiple: Boolean,
    onChange: ((Event) -> dynamic)
) {
    val tempInput = (createElement("input") as HTMLInputElement).apply {
        type = "file"
        style.display = "none"
        this.accept = accept
        this.multiple = multiple
    }

    tempInput.onchange = onChange
    body!!.append(tempInput)
    tempInput.click()
    tempInput.remove()
}

// I = input type (from the disk)
// O = output type (produced for users)
private fun <I, O> Document.loadFromDisk(
    accept: String = "",
    triggerLoad: FileReader.(Blob) -> Unit,
    deserialize: (I) -> O,
    onError: LoadContext.() -> Unit,
    onLoad: LoadContext.(O) -> Unit,
) {
    loadFromDisk(accept, multiple = false, onChange = { changeEvt ->
        val file = changeEvt.target.unsafeCast<HTMLInputElement>().files!![0]!!
        val reader = FileReader()
        reader.onabort = { handleUnexpectedOnAbort("loadFromDisk") }
        reader.onerror = { evt ->
            onError(LoadContext(file.name, file.type.takeIf { it.isNotBlank() }, evt.unsafeCast<ProgressEvent>()))
        }
        reader.onload = { loadEvt ->
            val loadContext =
                LoadContext(file.name, file.type.takeIf { it.isNotBlank() }, loadEvt.unsafeCast<ProgressEvent>())
            try {
                val result = loadEvt.target.unsafeCast<FileReader>().result as I
                onLoad(loadContext, deserialize(result))
            } catch (_: Throwable) {
                onError(loadContext)
            }
        }
        reader.triggerLoad(file)
    })
}

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
fun Document.loadFromDisk(
    accept: String = "",
    onError: LoadContext.() -> Unit = {},
    onLoad: LoadContext.(ByteArray) -> Unit,
) {
    loadFromDisk<ArrayBuffer, ByteArray>(
        accept,
        FileReader::readAsArrayBuffer,
        { result ->
            val intArray = Int8Array(result)
            ByteArray(intArray.byteLength) { i -> intArray[i] }
        },
        onError,
        onLoad
    )
}

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
fun Document.loadDataUrlFromDisk(
    accept: String = "",
    onError: LoadContext.() -> Unit = {},
    onLoad: LoadContext.(String) -> Unit,
) {
    loadFromDisk<String, String>(
        accept,
        FileReader::readAsDataURL,
        { result -> result },
        onError,
        onLoad
    )
}

/**
 * Like [loadFromDisk] but convenient for dealing with text files.
 *
 * See `loadFromDisk` for details about the parameters.
 *
 * @see loadFromDisk
 * @see FileReader.readAsText
 * @see saveTextToDisk
 */
fun Document.loadTextFromDisk(
    accept: String = "",
    encoding: String = "UTF-8",
    onError: LoadContext.() -> Unit = {},
    onLoad: LoadContext.(String) -> Unit,
) {
    loadFromDisk<String, String>(
        accept,
        { file -> this.readAsText(file, encoding) },
        { result -> result },
        onError,
        onLoad
    )
}

class LoadedFile<O>(
    val context: LoadContext,
    val contents: O,
)

private fun <I, O> Document.loadMultipleFromDisk(
    accept: String = "",
    triggerLoad: FileReader.(Blob) -> Unit,
    deserialize: (I) -> O,
    onError: (List<LoadContext>) -> Unit = {},
    onLoad: (List<LoadedFile<O>>) -> Unit,
) {
    loadFromDisk(accept, multiple = true, onChange = { changeEvt ->
        val selectedFiles = changeEvt.target.unsafeCast<HTMLInputElement>().files!!
        val failedToLoadFiles = mutableListOf<LoadContext>()
        val loadedFiles = mutableListOf<LoadedFile<O>>()

        selectedFiles.asList().forEach { file ->
            val reader = FileReader()
            fun createLoadContext(evt: ProgressEvent) =
                LoadContext(file.name, file.type.takeIf { it.isNotBlank() }, evt)

            fun triggerCallbacksIfReady() {
                if (failedToLoadFiles.size + loadedFiles.size == selectedFiles.length) {
                    failedToLoadFiles.takeIf { it.isNotEmpty() }?.let { onError(it) }
                    loadedFiles.takeIf { it.isNotEmpty() }?.let { onLoad(it) }
                }
            }

            reader.onabort = { handleUnexpectedOnAbort("loadMultipleFromDisk") }
            reader.onerror = {
                failedToLoadFiles.add(createLoadContext(it.unsafeCast<ProgressEvent>()))
                triggerCallbacksIfReady()
            }
            reader.onload = { loadEvt ->
                val loadContext = createLoadContext(loadEvt.unsafeCast<ProgressEvent>())
                try {
                    val result = loadEvt.target.unsafeCast<FileReader>().result as I
                    loadedFiles.add(LoadedFile(loadContext, deserialize(result)))
                } catch (_: Throwable) {
                    failedToLoadFiles.add(loadContext)
                }
                triggerCallbacksIfReady()
            }
            reader.triggerLoad(file)
        }
    })
}

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
fun Document.loadMultipleFromDisk(
    accept: String = "",
    onError: (List<LoadContext>) -> Unit = {},
    onLoad: (List<LoadedFile<ByteArray>>) -> Unit,
) {
    loadMultipleFromDisk<ArrayBuffer, ByteArray>(
        accept,
        FileReader::readAsArrayBuffer,
        { result ->
            val intArray = Int8Array(result)
            ByteArray(intArray.byteLength) { i -> intArray[i] }
        },
        onError,
        onLoad
    )
}

fun Document.loadMultipleDataUrlFromDisk(
    accept: String = "",
    onError: (List<LoadContext>) -> Unit = {},
    onLoad: (List<LoadedFile<String>>) -> Unit,
) {
    loadMultipleFromDisk<String, String>(
        accept,
        FileReader::readAsDataURL,
        { result -> result },
        onError,
        onLoad
    )
}

fun Document.loadMultipleTextFromDisk(
    accept: String = "",
    encoding: String = "UTF-8",
    onError: (List<LoadContext>) -> Unit = {},
    onLoad: (List<LoadedFile<String>>) -> Unit,
) {
    loadMultipleFromDisk<String, String>(
        accept,
        { file -> this.readAsText(file, encoding) },
        { result -> result },
        onError,
        onLoad
    )
}
