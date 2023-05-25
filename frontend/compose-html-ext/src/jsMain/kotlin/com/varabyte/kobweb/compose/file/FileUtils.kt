package com.varabyte.kobweb.compose.file

import kotlinx.browser.document
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
    content: String,
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

/**
 * Load some content from disk, presenting the user with a dialog to choose the file to load.
 *
 * @param accept A comma-separated list of extensions to filter by (e.g. ".txt,*.sav")
 * @param onLoaded A callback which will contain the contents of your file, if successfully loaded.
 */
fun Document.loadFromDisk(
    accept: String = "",
    onLoaded: (String) -> Unit,
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
            val content = loadEvt.target.asDynamic().result as String
            onLoaded(content)
        }
        reader.readAsText(file, "UTF-8")
    }

    body!!.append(tempInput)
    tempInput.click()
    tempInput.remove()
}