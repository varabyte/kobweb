package com.varabyte.kobweb.server.api

import com.charleskorn.kaml.Yaml
import com.varabyte.kobweb.common.KobwebFolder
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

// TODO(Bug #12): Use safer file logic here to protect against multiple writers etc.
class ServerRequestsFile(kobwebFolder: KobwebFolder) {
    private val filePath = kobwebFolder.resolve("server/requests.yaml")

    var lastModified = 0L
    lateinit var _serverRequests: ServerRequests
    val serverRequests: ServerRequests?
        get() {
            return filePath
                .takeIf { it.exists() }
                ?.let {
                    val lastModified = filePath.getLastModifiedTime()
                    if (this.lastModified != lastModified.toMillis()) {
                        this.lastModified = lastModified.toMillis()
                        _serverRequests = Yaml.default.decodeFromString(ServerRequests.serializer(), filePath.toFile().readText())
                    }

                    _serverRequests
                }
        }

    fun addRequest(request: ServerRequest) {
        if (!filePath.parent.exists()) {
            filePath.parent.createDirectories()
        }
        val currRequests = serverRequests
        val newRequests = ServerRequests((currRequests?.requests ?: emptyList()) + request)
        filePath.toFile().writeText(Yaml.default.encodeToString(ServerRequests.serializer(), newRequests))
    }
}