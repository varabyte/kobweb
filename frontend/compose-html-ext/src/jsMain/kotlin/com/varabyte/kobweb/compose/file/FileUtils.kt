package com.varabyte.kobweb.compose.file

import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Int8Array
import org.khronos.webgl.get
import org.w3c.dom.Document
import org.w3c.dom.HTMLAnchorElement
import org.w3c.dom.HTMLInputElement
import org.w3c.files.Blob
import org.w3c.files.BlobPropertyBag
import org.w3c.files.File
import org.w3c.files.FileReader
import org.w3c.dom.url.URL as DomURL // to avoid ambiguity with Document.URL

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
)

// I = input type (from the disk)
// O = output type (produced for users)
private fun <I, O> Document.loadFromDisk(
    accept: String = "",
    triggerLoad: FileReader.(Blob) -> Unit,
    deserialize: (I) -> O,
    onLoading: LoadContext.(O) -> Unit,
) {
    val tempInput = (createElement("input") as HTMLInputElement).apply {
        type = "file"
        style.display = "none"
        this.accept = accept
        multiple = false
    }

    tempInput.onchange = { changeEvt ->
        val file = changeEvt.target.asDynamic().files[0] as File

        val reader = FileReader()
        reader.onload = { loadEvt ->
            val result = loadEvt.target.asDynamic().result as I
            onLoading(LoadContext(file.name, file.type.takeIf { it.isNotBlank() }), deserialize(result))
        }
        reader.triggerLoad(file)
    }

    body!!.append(tempInput)
    tempInput.click()
    tempInput.remove()
}

/**
 * Load some binary content from disk, presenting the user with a dialog to choose the file to load.
 *
 * This method extends the global `document` variable, so you can use it like this:
 *
 * ```
 * document.loadFromDisk("*.png") { bytes -> /* ... */ }
 * ```
 *
 * @param accept A comma-separated list of extensions to filter by (e.g. ".txt,*.sav")
 * @param onLoaded A callback which will contain the contents of your file, if successfully loaded. The callback is
 *   scoped by a [LoadContext] which contains additional information about the file, such as its name and mime type.
 *
 * @see [FileReader.readAsArrayBuffer]
 */
fun Document.loadFromDisk(
    accept: String = "",
    onLoaded: LoadContext.(ByteArray) -> Unit,
) {
    loadFromDisk<ArrayBuffer, ByteArray>(
        accept,
        FileReader::readAsArrayBuffer,
        { result ->
            val intArray = Int8Array(result)
            ByteArray(intArray.byteLength) { i -> intArray[i] }
        },
        onLoaded
    )
}

/**
 * Like [loadFromDisk] but specifically loads some content from disk as a URL with base64-encoded data.
 *
 * This is useful (necessary?) for loading images in a format that image elements can consume.
 *
 * See [loadFromDisk] for details about the parameters.
 *
 * @see [FileReader.readAsDataURL]
 */
fun Document.loadDataUrlFromDisk(
    accept: String = "",
    onLoaded: LoadContext.(String) -> Unit,
) {
    loadFromDisk<String, String>(
        accept,
        FileReader::readAsDataURL,
        { result -> result },
        onLoaded
    )
}

/**
 * Like [loadFromDisk] but convenient for dealing with text files.
 *
 * See [loadFromDisk] for details about the parameters.
 *
 * @see [FileReader.readAsText]
 *
 * @see [saveTextToDisk]
 */
fun Document.loadTextFromDisk(
    accept: String = "",
    encoding: String = "UTF-8",
    onLoaded: LoadContext.(String) -> Unit,
) {
    loadFromDisk<String, String>(
        accept,
        { file -> this.readAsText(file, encoding) },
        { result -> result },
        onLoaded
    )
}
