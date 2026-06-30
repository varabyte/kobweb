package com.varabyte.kobweb.browser.http

import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Int8Array
import org.w3c.dom.url.URLSearchParams
import org.w3c.files.Blob
import org.w3c.xhr.FormData
import kotlin.js.Json

/**
 * Body contents that can be set when calling [fetch].
 *
 * Instead of allocating directly, use the helper [bodyOf] methods.
 */
sealed class RequestBody {
    class OfBlob internal constructor(val blob: Blob) : RequestBody()
    class OfArrayBuffer internal constructor(val buffer: ArrayBuffer, val contentType: String) : RequestBody()
    class OfFormData internal constructor(val formData: FormData) : RequestBody()
    class OfText internal constructor(val text: String, contentType: String) : RequestBody() {
        private fun String.addCharsetIfMissing(): String {
            return if (this.split(";").map { it.trimStart() }.none { it.startsWith("charset=") }) {
                "$this; charset=UTF-8"
            } else this
        }

        val contentType = contentType.addCharsetIfMissing()
    }
    class OfUrlEncoded internal constructor(val params: URLSearchParams) : RequestBody()
}

fun bodyOf(blob: Blob): RequestBody = RequestBody.OfBlob(blob)

fun bodyOf(buffer: ArrayBuffer, contentType: String = "application/octet-stream"): RequestBody =
    RequestBody.OfArrayBuffer(buffer, contentType)
fun bodyOf(bytes: ByteArray, contentType: String = "application/octet-stream"): RequestBody =
    bodyOf(Int8Array(bytes.toTypedArray()).buffer, contentType)

fun bodyOf(formData: FormData): RequestBody = RequestBody.OfFormData(formData)

/**
 * Create a text body.
 *
 * If you want to send json, consider using [bodyOf] that takes a [Json] parameter instead.
 *
 * @param contentType The specific content type of the text, which defaults to "text/plain" if not specified. You do NOT
 *   have to add a charset to your type if it is just default UTF-8. In that case, it will be automatically appended for
 *   you.
 */
fun bodyOf(text: String, contentType: String = "text/plain"): RequestBody = RequestBody.OfText(text, contentType)
fun bodyOf(json: Json): RequestBody = bodyOf(JSON.stringify(json), contentType = "application/json")

fun bodyOf(params: URLSearchParams): RequestBody = RequestBody.OfUrlEncoded(params)
