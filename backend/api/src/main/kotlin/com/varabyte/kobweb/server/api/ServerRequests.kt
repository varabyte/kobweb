package com.varabyte.kobweb.server.api

import com.charleskorn.kaml.Yaml
import com.varabyte.kobweb.project.KobwebFolder
import com.varabyte.kobweb.project.io.KobwebWritableFile
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Keep all children classes even if they can be objects; we may update them later
@Suppress("CanSealedSubClassBeObject")
@Serializable
sealed class ServerRequest {
    @Serializable
    @SerialName("Stop")
    class Stop : ServerRequest()

    @Serializable
    @SerialName("IncrementVersion")
    class IncrementVersion : ServerRequest()

    @Serializable
    @SerialName("SetStatus")
    class SetStatus(val message: String, val isError: Boolean = false, var timeoutMs: Long? = null) : ServerRequest()

    @Serializable
    @SerialName("ClearStatus")
    class ClearStatus : ServerRequest()
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

    fun removeRequests(): List<ServerRequest> {
        return content?.requests.also { content = null } ?: emptyList()
    }
}