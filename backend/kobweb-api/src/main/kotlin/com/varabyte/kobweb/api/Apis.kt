package com.varabyte.kobweb.api

import com.varabyte.kobweb.api.data.Data
import com.varabyte.kobweb.api.http.Request
import com.varabyte.kobweb.api.http.Response
import com.varabyte.kobweb.api.log.Logger
import com.varabyte.kobweb.api.stream.ApiStream
import com.varabyte.kobweb.api.stream.StreamEvent

/**
 * The class which manages all API paths and handlers within a Kobweb project.
 */
@Suppress("unused") // Called by generated code
class Apis(private val data: Data, private val logger: Logger) {
    private val apiHandlers = mutableMapOf<String, suspend (ApiContext) -> Unit>()
    private val apiStreamHandlers = mutableMapOf<String, ApiStream>()

    fun register(path: String, handler: suspend (ApiContext) -> Unit) {
        apiHandlers[path] = handler
    }

    fun registerStream(path: String, streamHandler: ApiStream) {
        apiStreamHandlers[path.removePrefix("/")] = streamHandler
    }

    suspend fun handle(path: String, request: Request): Response? {
        return apiHandlers[path]?.let { handler ->
            val apiCtx = ApiContext(request, data, logger)
            handler.invoke(apiCtx)
            apiCtx.res
        }
    }

    suspend fun handle(path: String, event: StreamEvent) {
        apiStreamHandlers[path.removePrefix("/")]?.let { streamHandler ->
            when (event) {
                is StreamEvent.Opened -> {
                    streamHandler.onUserConnected(
                        ApiStream.UserConnectedContext(
                            event.stream,
                            event.streamerId,
                            data,
                            logger
                        )
                    )
                }

                is StreamEvent.Text -> {
                    streamHandler.onTextReceived(
                        ApiStream.TextReceivedContext(
                            event.stream,
                            event.streamerId,
                            event.text,
                            data,
                            logger
                        )
                    )
                }

                is StreamEvent.Closed -> {
                    streamHandler.onUserDisconnected(ApiStream.UserDisconnectedContext(event.streamerId, data, logger))
                }
            }
        }
    }
}
