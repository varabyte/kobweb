package com.varabyte.kobweb.compose.file

import kotlinx.browser.document
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
 * @param filename The suggested name of the file to save (users will be given a chance to override it).
 * @param content The content to save
 */
fun Document.saveToDisk(
    filename: String,
    content: ByteArray,
) {
    val snapshotBlob = Blob(arrayOf(content), BlobPropertyBag())
    val url = DomURL.createObjectURL(snapshotBlob)
    val tempAnchor = (createElement("a") as HTMLAnchorElement).apply {
        style.display = "none"
        href = url
        download = filename
    }
    document.body!!.append(tempAnchor)
    tempAnchor.click()
    DomURL.revokeObjectURL(url)
    tempAnchor.remove()
}

/** A convenience method to call [saveToDisk] with a String instead of a ByteArray. */
fun Document.saveTextTDisk(
    filename: String,
    content: String,
) {
    saveToDisk(filename, content.encodeToByteArray())
}

class LoadContext(
    val filename: String,
)

/**
 * Load some content from disk, presenting the user with a dialog to choose the file to load.
 *
 * @param accept A comma-separated list of extensions to filter by (e.g. ".txt,*.sav")
 * @param onLoaded A callback which will contain the contents of your file, if successfully loaded.
 */
fun Document.loadFromDisk(
    accept: String = "",
    onLoaded: LoadContext.(ByteArray) -> Unit,
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
            val buffer = loadEvt.target.asDynamic().result as ArrayBuffer
            val intArray = Int8Array(buffer)

            onLoaded(LoadContext(file.name), ByteArray(intArray.length) { i -> intArray[i] })
        }
        reader.readAsArrayBuffer(file)
    }

    body!!.append(tempInput)
    tempInput.click()
    tempInput.remove()
}

/** A convenience method to call [loadFromDisk] with a String instead of a ByteArray. */
fun Document.loadTextFromDisk(
    accept: String = "",
    onLoaded: LoadContext.(String) -> Unit,
) {
    loadFromDisk(accept) { bytes -> onLoaded(bytes.decodeToString()) }
}
