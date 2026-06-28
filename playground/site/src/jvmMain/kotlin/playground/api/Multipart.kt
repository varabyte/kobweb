package playground.api

import com.varabyte.kobweb.api.Api
import com.varabyte.kobweb.api.ApiContext
import com.varabyte.kobweb.api.http.HttpMethod
import com.varabyte.kobweb.api.http.Multipart
import com.varabyte.kobweb.api.http.bodyOf
import com.varabyte.kobweb.api.http.bytes
import com.varabyte.kobweb.api.http.forEachPart

@Api
suspend fun multipart(ctx: ApiContext) {
    if (ctx.req.method != HttpMethod.POST) return
    val mp = ctx.req.body?.multipart() ?: return

    ctx.res.body = bodyOf(buildString {
        appendLine("Received multipart request")
        var i = 0
        mp.forEachPart { part ->
            appendLine("\nPart #${++i}")
            appendLine("- Disposition: ${part.contentDisposition!!.disposition}")
            appendLine("- Name: ${part.contentDisposition!!.name}")
            (part.extras as? Multipart.Extras.File)?.let { fileExtras ->
                appendLine("- Original file name: ${fileExtras.originalFileName}")
            }
            val bytes = part.bytes()
            appendLine("- Size: ${bytes.size} bytes")
            val maxSize = 100
            if (bytes.size > maxSize) {
                appendLine("- Content (first $maxSize bytes): ${bytes.take(maxSize).toByteArray().decodeToString().replace("\n", "\\n")}...")
            } else {
                appendLine("- Content: ${bytes.decodeToString().replace("\n", "\\n")}")
            }
        }
    })
}