package com.varabyte.kobweb.api

import com.varabyte.kobweb.api.data.Data
import com.varabyte.kobweb.api.env.Environment
import com.varabyte.kobweb.api.http.Request
import com.varabyte.kobweb.api.http.Response
import com.varabyte.kobweb.api.log.Logger
import com.varabyte.kobweb.api.stream.ApiStream
import com.varabyte.kobweb.api.stream.StreamEvent
import com.varabyte.kobweb.navigation.RouteTree
import com.varabyte.kobweb.navigation.captureDynamicValues

typealias ApiHandler = suspend (ApiContext) -> Unit

/**
 * The class which manages all API paths and handlers within a Kobweb project.
 */
@Suppress("unused") // Called by generated code
class Apis(private val env: Environment, private val data: Data, private val logger: Logger) {
    private val apiHandlers = RouteTree<ApiHandler>()
    private val apiStreamHandlers = mutableMapOf<String, ApiStream>()

    val numApiStreams get() = apiStreamHandlers.size

    fun register(path: String, handler: ApiHandler) {
        apiHandlers.register(path, handler)
    }

    fun registerStream(path: String, streamHandler: ApiStream) {
        apiStreamHandlers[path.removePrefix("/")] = streamHandler
    }

    suspend fun handle(path: String, request: Request): Response? {
        return apiHandlers.resolve(path, allowRedirects = false)?.let { entries ->
            val captured = entries.captureDynamicValues()
            // Captured params, if any, should take precedence over query parameters
            @Suppress("NAME_SHADOWING") val request = if (captured.isEmpty()) request else
                Request(
                    request.connection,
                    request.method,
                    request.queryParams + captured,
                    request.queryParams,
                    request.headers,
                    request.cookies,
                    request.body,
                    request.contentType
                )

            val apiCtx = ApiContext(env, request, data, logger)
            entries.last().node.data!!.invoke(apiCtx)
            apiCtx.res
        }
    }

    suspend fun handle(path: String, event: StreamEvent) {
        apiStreamHandlers[path.removePrefix("/")]?.let { streamHandler ->
            when (event) {
                is StreamEvent.ClientConnected -> {
                    streamHandler.onClientConnected(
                        ApiStream.ClientConnectedContext(
                            event.stream,
                            event.clientId,
                            env,
                            data,
                            logger
                        )
                    )
                }

                is StreamEvent.Text -> {
                    streamHandler.onTextReceived(
                        ApiStream.TextReceivedContext(
                            event.stream,
                            event.clientId,
                            event.text,
                            env,
                            data,
                            logger
                        )
                    )
                }

                is StreamEvent.ClientDisconnected -> {
                    streamHandler.onClientDisconnected(
                        ApiStream.ClientDisconnectedContext(
                            event.stream,
                            event.clientId,
                            env,
                            data,
                            logger
                        )
                    )
                }
            }
        }
    }
}
