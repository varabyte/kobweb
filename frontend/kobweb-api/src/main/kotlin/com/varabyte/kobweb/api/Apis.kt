package com.varabyte.kobweb.api

/**
 * The class which manages all API paths and handlers within a Kobweb project.
 */
@Suppress("unused") // Called by generated code
class Apis {
    private val handlers = mutableMapOf<String, suspend (ApiContext) -> Unit>()

    fun register(path: String, handler: suspend (ApiContext) -> Unit) {
        handlers[path] = handler
    }

    suspend fun handle(path: String): Response? {
        return handlers[path]?.let { handler ->
            val ctx = ApiContext()
            handler.invoke(ctx)
            ctx.res
        }
    }
}