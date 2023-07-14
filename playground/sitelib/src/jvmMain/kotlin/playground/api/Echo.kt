package playground.api

import com.varabyte.kobweb.api.stream.ApiStream

val echo = ApiStream { evt ->
    evt.stream.send(evt.text)
}

// Or, if you need access to connect / disconnect messages
// val echo = object : ApiStream() {
//    override suspend fun onUserConnected(ctx: UserConnectedContext) { ... }
//    override suspend fun onMessageReceived(ctx: MessageReceivedContext) { ... }
//    override suspend fun onUserDisconnected(ctx: UserDisconnectedContext) { ... }
//}
