package com.varabyte.kobweb.api.http

/**
 * Represents all relevant information associated with a [multipart request](https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Methods/POST#multipart_form_submission).
 *
 * The general shape of processing a multipart request in your API method is like so:
 * ```
 * @Api
 * suspend fun multipart(ctx: ApiContext) {
 *     if (ctx.req.method != HttpMethod.POST) return
 *     val mp = ctx.req.body?.multipart() ?: return
 *
 *     mp.forEachPart { part ->
 *         // Here, part.consumeContent() gives you a ByteSource you can use to stream the content information.
 *         // If you are sure that the content is fairly limited in size, you can use `part.bytes()` or `part.text()`
 *         // instead to read everything directly.
 *
 *         // Also, if you sent file data, you can use
 *         // (part.extras as? Multipart.Extras.File)?.originalFileName
 *         // to get the original file name uploaded by the user.
 *     }
 * }
 * ```
 */
interface Multipart {
    companion object {
        fun isMultipartContentType(contentType: String) = contentType.startsWith("multipart/", ignoreCase = true)
    }

    /**
     * One section inside the parent [Multipart] request body.
     *
     * Be sure to [release] it when finished looking at it.
     */
    interface Part : ContentSource {
        override val contentLength: Long? get() = null

        val headers: Map<String, List<String>>
        val contentDisposition: ContentDisposition?
        val name: String?
        val extras: Extras?

        suspend fun release()
    }

    /**
     * Extra values beyond the common set, provided specifically based the type of part that we are dealing with.
     */
    interface Extras {
        class File(val originalFileName: String?) : Extras
    }

    /**
     * Read out the next part of this multipart request, or return null if no more parts are available.
     *
     * You can either call this directly or use [forEachPart] which is provided as a convenience method.
     *
     * If you call this yourself, be sure to [close][Part.release] each part when you're done with it.
     */
    suspend fun readNextPart(): Part?
}

/**
 * @param autoRelease Whether to automatically call [Part.close][Multipart.Part.release] after the callback this scope
 * is associated with is finished.
 */
class MultipartScope internal constructor(var autoRelease: Boolean)

/**
 * A convenience method that wraps [Multipart.readNextPart] so you don't have to collect it yourself.
 *
 * @param autoRelease If true, automatically call [Part.release][Multipart.Part.release] after each part is handled. You
 *   can set [MultipartScope.autoRelease] as well per part inside [block], if you need to override this value on a
 *   case-by-case basis. (Changing the value for one part will not affect subsequent parts.)
 */
suspend fun Multipart.forEachPart(autoRelease: Boolean = true, block: suspend MultipartScope.(Multipart.Part) -> Unit) {
    while (true) {
        val part = readNextPart() ?: break
        val scope = MultipartScope(autoRelease)
        try {
            scope.block(part)
        } finally {
            if (scope.autoRelease) {
                part.release()
            }
        }
    }
}
