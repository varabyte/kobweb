package com.varabyte.kobweb.server.api

import com.charleskorn.kaml.Yaml
import com.varabyte.kobweb.common.KobwebFolder
import com.varabyte.kobweb.common.io.KobwebWritableFile
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/*
@Serializable
sealed class Instruction(
    val condition: String? = null,
) {
    /**
     * Inform the user about something.
     *
     * @param message The message to show to the user. This value will be processed by freemarker and can be dynamic!
     */
    @Serializable
    @SerialName("Inform")
    class Inform(
        val message: String,
    ) : Instruction()

 */

@Serializable
sealed class ServerRequest {
    @Serializable
    @SerialName("Stop")
    class Stop : ServerRequest()
}

@Serializable
class ServerRequests(
    val requests: List<ServerRequest>
)

class ServerRequestsFile(kobwebFolder: KobwebFolder) : KobwebWritableFile<ServerRequests>(
    kobwebFolder,
    "server/requests.yaml",
    serialize = { requests -> Yaml.default.encodeToString(ServerRequests.serializer(), requests) },
    deserialize = { text -> Yaml.default.decodeFromString(ServerRequests.serializer(), text) }
) {
    fun enqueueRequest(request: ServerRequest) {
        val currRequests = content
        content = ServerRequests((currRequests?.requests ?: emptyList()) + request)
    }

    fun dequeueRequest(): ServerRequest? {
        val currRequests = content
        val nextRequest = currRequests?.requests?.firstOrNull()
        if (nextRequest != null) {
            content = ServerRequests(currRequests.requests.drop(1))
        }

        return nextRequest
    }
}