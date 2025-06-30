package com.varabyte.kobweb.server.api

import com.charleskorn.kaml.Yaml
import com.varabyte.kobweb.project.KobwebFolder
import com.varabyte.kobweb.project.io.KobwebWritableTextFile
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.annotations.ApiStatus

// Keep all children classes even if they can be objects; we may update them later
@Suppress("CanSealedSubClassBeObject")
@Serializable
sealed class ServerRequest {
    /**
     * Ask the server to stop running.
     *
     * This should ask the server nicely, and it will probably take 5+ seconds for the server to actually stop running
     * after this request is issued.
     *
     * You can check the process PID (which you can read out from the [ServerState] object) to see if the server is
     * still running.
     */
    @Serializable
    @SerialName("Stop")
    class Stop : ServerRequest()

    /**
     * Send a request to the server asking it to stop sending out events to its clients.
     *
     * This essentially provides the ability to broadly disable live reloading behavior for the target server.
     *
     * Sending this event more than once is a no-op. You can send
     * a [resume version event][ResumeClientEvents] to discontinue the pause.
     */
    @Serializable
    @SerialName("PauseClientEvents")
    @ApiStatus.AvailableSince("0.23.0")
    class PauseClientEvents : ServerRequest()

    /**
     * Send a request to the server asking it to resume sending out events to its clients.
     *
     * This essentially provides the ability to broadly enable live reloading behavior for the target server.
     *
     * Sending this event more than once is a no-op. You are expected to call this only after
     * previously calling a [pause version event][PauseClientEvents].
     */
    @Serializable
    @SerialName("ResumeClientEvents")
    @ApiStatus.AvailableSince("0.23.0")
    class ResumeClientEvents : ServerRequest()

    /**
     * Send a request to the server asking it to increment a monotonically increasing version count.
     *
     * The version value itself doesn't matter -- it's just a simple numeric where if you find it
     * differs from your own local copy, it means you are out of date.
     */
    @Serializable
    @SerialName("IncrementVersion")
    class IncrementVersion : ServerRequest()

    /**
     * Send a request to the server with a status message that it should broadcast to its clients.
     *
     * @property timeoutMs If set, how long before the status message on the client should be hidden.
     */
    @Serializable
    @SerialName("SetStatus")
    class SetStatus(val message: String, val isError: Boolean = false, var timeoutMs: Long? = null) : ServerRequest()

    /**
     * Send a request to the server to inform all of its clients to hide any status message that may be showing.
     *
     * This is a no-op if no status is currently being shown.
     */
    @Serializable
    @SerialName("ClearStatus")
    class ClearStatus : ServerRequest()
}

@Serializable
class ServerRequests(
    val requests: List<ServerRequest>
)

class ServerRequestsFile(kobwebFolder: KobwebFolder) : KobwebWritableTextFile<ServerRequests>(
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