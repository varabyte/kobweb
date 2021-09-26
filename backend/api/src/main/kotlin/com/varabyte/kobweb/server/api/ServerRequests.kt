package com.varabyte.kobweb.server.api

import com.charleskorn.kaml.Yaml
import com.varabyte.kobweb.common.KobwebFolder
import com.varabyte.kobweb.common.io.KobwebReadableFile
import com.varabyte.kobweb.common.io.KobwebWritableFile
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlin.io.path.createDirectories
import kotlin.io.path.exists
import kotlin.io.path.getLastModifiedTime

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
    class Stop
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
        val currRequests = wrapped
        wrapped = ServerRequests((currRequests?.requests ?: emptyList()) + request)
    }

    fun dequeueRequest(): ServerRequest? {
        val currRequests = wrapped
        val nextRequest = currRequests?.requests?.firstOrNull()
        if (nextRequest != null) {
            wrapped = ServerRequests(currRequests.requests.drop(1))
        }

        return nextRequest
    }
}