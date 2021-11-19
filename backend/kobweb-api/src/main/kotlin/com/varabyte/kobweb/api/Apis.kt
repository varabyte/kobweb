package com.varabyte.kobweb.api

import com.varabyte.kobweb.api.data.Data
import com.varabyte.kobweb.api.http.Request
import com.varabyte.kobweb.api.http.Response
import com.varabyte.kobweb.api.log.Logger

/**
 * The class which manages all API paths and handlers within a Kobweb project.
 */
@Suppress("unused") // Called by generated code
class Apis(private val data: Data, private val logger: Logger) {
    private val handlers = mutableMapOf<String, suspend (ApiContext) -> Unit>()

    fun register(path: String, handler: suspend (ApiContext) -> Unit) {
        handlers[path] = handler
    }

    suspend fun handle(path: String, request: Request): Response? {
        return handlers[path]?.let { handler ->
            val apiCtx = ApiContext(request, data, logger)
            handler.invoke(apiCtx)
            apiCtx.res
        }
    }
}