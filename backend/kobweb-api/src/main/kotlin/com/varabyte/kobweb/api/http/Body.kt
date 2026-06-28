package com.varabyte.kobweb.api.http

import com.varabyte.kobweb.api.http.Body.Companion.invoke
import com.varabyte.kobweb.api.http.io.parseCharsetFromContentType
import com.varabyte.kobweb.framework.annotations.DelicateApi
import com.varabyte.kobweb.io.ByteSource
import com.varabyte.kobweb.io.RawByteSource
import com.varabyte.kobweb.io.toByteSource
import java.io.InputStream

/**
 * The body of a request or response.
 *
 * Note that its contents can only be consumed once, via the [ByteSource] returned by [consumeContent].
 */
class Body private constructor(
    override val contentType: String,
    private val contentProvider: ContentProvider,
    override val contentLength: Long? = null,
) : ContentSource {
    companion object  {
        fun multipart(contentType: String, contentLength: Long? = null, provideMultipart: suspend () -> Multipart) =
            Body(contentType, ContentProvider.Multi(provideMultipart), contentLength)

        operator fun invoke(contentType: String, contentLength: Long? = null, provideContent: suspend () -> ByteSource) =
            Body(contentType, ContentProvider.Single(provideContent), contentLength)
    }

    private sealed class ContentProvider {
        class Single(val provide: suspend () -> ByteSource) : ContentProvider()
        class Multi(val provide: suspend () -> Multipart) : ContentProvider()
    }

    init {
        val isMultiPartContentType = Multipart.isMultipartContentType(contentType)
        require((contentProvider is ContentProvider.Single) && !isMultiPartContentType || (contentProvider is ContentProvider.Multi) && isMultiPartContentType) {
            buildString {
                append("Registered a ")
                if (contentProvider is ContentProvider.Single) {
                    append("non-")
                }
                append("multipart request body with incompatible content type \"$contentType\".")
            }
        }
    }

    private var consumed = false

    private fun assertNotConsumed() {
        check(!consumed) { "Cannot consume body content more than once." }
        consumed = true
    }

    @DelicateApi("It is fine to call this method, but note that Kobweb had to create a custom I/O class here (ByteSource) because kotlinx-io doesn't have an async byte stream concept. If this ever changes in the future, we may decide migrating to it. If possible, consider using higher level helper methods instead, like `bytes()`, `text()`, or `stream()`, or file an issue with the team asking them to provide a more relevant adapter.")
    override suspend fun consumeContent(): ByteSource {
        assertNotConsumed()
        return (contentProvider as? ContentProvider.Single)?.provide()
            ?: error("Cannot call consumeContent() on a request with a multi-part body. Use `multipart()` instead.")
    }

    /**
     * Fetch details for this body's content IF its content is split up into [multipart sections](https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Methods/POST#multipart_form_submission)
     *
     * IMPORTANT: You can only call this once! Attempting to call this a second time will throw.
     */
    suspend fun multipart(): Multipart {
        assertNotConsumed()
        return (contentProvider as? ContentProvider.Multi)?.provide()
            ?: error("Cannot call multipart() on a request with a single-part body.")
    }
}

// region Body factory helper methods

// If you add a new method here, create an associated method on ContentSource (in Content.kt)

@Deprecated("Use `bodyOf` method instead, for consistency with frontend APIs",
    ReplaceWith("bodyOf(inputStream, contentType)")
)
fun Body.Companion.stream(inputStream: InputStream, contentType: String = "application/octet-stream") =
    bodyOf(inputStream, contentType)

@Deprecated("Use `bodyOf` method instead, for consistency with frontend APIs",
    ReplaceWith("bodyOf(bytes, contentType)")
)
fun Body.Companion.bytes(bytes: ByteArray, contentType: String = "application/octet-stream") =
    bodyOf(bytes, contentType)

@Deprecated("Use `bodyOf` method instead, for consistency with frontend APIs",
    ReplaceWith("bodyOf(text, contentType)")
)
fun Body.Companion.text(
    text: String,
    contentType: String = "text/plain"
) = bodyOf(text, contentType)

@Deprecated("Use `bodyOf` method instead, for consistency with frontend APIs",
    ReplaceWith("bodyOf(text, contentType = \"application/json\")"),
)
fun Body.Companion.json(text: String) =
    bodyOf(text, contentType = "application/json")

fun bodyOf(inputStream: InputStream, contentType: String = "application/octet-stream") =
    invoke(contentType) { inputStream.toByteSource() }
fun bodyOf(bytes: ByteArray, contentType: String = "application/octet-stream") =
    invoke(contentType) { RawByteSource(bytes) }
fun bodyOf(text: String, contentType: String = "text/plain") =
    bodyOf(text.toByteArray(contentType.parseCharsetFromContentType()), contentType)

// endregion