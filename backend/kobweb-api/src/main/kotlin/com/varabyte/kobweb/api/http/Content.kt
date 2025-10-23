package com.varabyte.kobweb.api.http

import com.varabyte.kobweb.api.http.Body.Companion.multipart
import com.varabyte.kobweb.api.http.io.parseCharsetFromContentType
import com.varabyte.kobweb.framework.annotations.DelicateApi
import com.varabyte.kobweb.io.ByteSource
import com.varabyte.kobweb.io.readRemaining
import com.varabyte.kobweb.io.toInputStream
import java.io.Closeable
import java.io.InputStream
import java.nio.charset.Charset
import kotlin.io.use
import kotlin.use

/**
 * Interface for a body class that allows users to consume its body content.
 */
interface ContentSource {
    /**
     * The [content type](https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Type) that describes the bytes owned by this body.
     */
    val contentType: String?
    /**
     * The size, in bytes, of the content, if known / provided ahead of time (or null otherwise).
     */
    val contentLength: Long?

    /**
     * Open an async stream up that consumes the content of this body.
     *
     * IMPORTANT: You can only call this once! Attempting to call this a second time will throw.
     *
     * The returned [ByteSource] is [Closeable], so either call [use] on it, e.g. `body?.consumeContent()?.use { ... }`,
     * or otherwise close it when you're done with it, which will release any resources associated with file or network
     * streams.
     *
     * Note that [multipart bodies](https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Methods/POST#multipart_form_submission) should not be consumed directly; instead, call [multipart].
     */
    @DelicateApi("It is fine to call this method, but note that Kobweb had to create a custom I/O class that we are exposing here (ByteSource) because kotlinx-io doesn't have an async byte stream concept. If this ever changes in the future, we may decide migrating to it. If possible, consider using higher level helper methods instead, like `bytes()`, `text()`, or `stream()`, or file an issue with the team asking them to provide a more relevant adapter.")
    suspend fun consumeContent(): ByteSource
}

/**
 * Extra information about how content should be treated / displayed.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Disposition">Content-Disposition</a>
 */
class ContentDisposition(val disposition: String, val parameters: Map<String, String> = mapOf()) {
    /**
     * Convenience property for the name parameter, which is commonly set and can be used to identify a specific part.
     */
    val name: String? get() = parameters[Parameters.Name]

    /**
     * Frequently used content disposition parameter names
     */
    // See also: https://github.com/ktorio/ktor/blob/af24a5a1a663d6c3c4fe36360a565fe17461b2d5/ktor-http/common/src/io/ktor/http/ContentDisposition.kt#L105
    @Suppress("KDocMissingDocumentation", "unused", "PublicApiImplicitType", "ConstPropertyName")
    object Parameters {
        const val FileName: String = "filename"
        const val FileNameAsterisk: String = "filename*"
        const val Name: String = "name"
        const val CreationDate: String = "creation-date"
        const val ModificationDate: String = "modification-date"
        const val ReadDate: String = "read-date"
        const val Size: String = "size"
        const val Handling: String = "handling"
    }
}

// region ContentSource content query helper methods

// If you add a new method here, create an associated method on Body.Companion

/**
 * Convert this byte source into an [InputStream].
 */
suspend fun ContentSource.stream(): InputStream {
    @OptIn(DelicateApi::class)
    return consumeContent().toInputStream()
}

/**
 * Convenience method to convert a body's content into a raw byte array.
 *
 * @param limit If set and the size of the body is larger than it, throw an exception.
 */
suspend fun ContentSource.bytes(limit: Int? = null): ByteArray {
    @OptIn(DelicateApi::class)
    return consumeContent().use { it.readRemaining(limit) }
}

/**
 * Convenience method to convert a body's content into a UTF-8 string.
 *
 * @param limit If set and the size of the body is larger than it, throw an exception.
 */
suspend fun ContentSource.text(charset: Charset = contentType.parseCharsetFromContentType(), limit: Int? = null): String {
    return bytes(limit).toString(charset)
}
