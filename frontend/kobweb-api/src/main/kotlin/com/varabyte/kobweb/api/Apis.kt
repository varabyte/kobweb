package com.varabyte.kobweb.api

import java.nio.file.Path

/**
 * The class which manages all API paths and handlers within a Kobweb project.
 */
@Suppress("unused") // Called by generated code
class Apis(val dataRoot: Path) {
    private val handlers = mutableMapOf<String, suspend (ApiContext) -> Unit>()

    fun register(path: String, handler: suspend (ApiContext) -> Unit) {
        handlers[path] = handler
    }

    suspend fun handle(path: String, request: Request): Response? {
        return handlers[path]?.let { handler ->
            val ctx = ApiContext(dataRoot, request)
            handler.invoke(ctx)
            ctx.res
        }
    }
}