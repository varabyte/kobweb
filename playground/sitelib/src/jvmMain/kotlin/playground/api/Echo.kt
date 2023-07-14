package playground.api

import com.varabyte.kobweb.api.stream.ApiStream

val echo = ApiStream { ctx ->
    ctx.stream.send(ctx.text)
}

// Or, if you need access to connect / disconnect messages:
// val echo = object : ApiStream() {
//    override suspend fun onClientConnected(ctx: ClientConnectedContext) {}
//    override suspend fun onTextReceived(ctx: TextReceivedContext) {}
//    override suspend fun onClientDisconnected(ctx: ClientDisconnectedContext) {}
//}
