package com.varabyte.kobweb.server

import com.varabyte.kobweb.server.api.ServerRequest

/**
 * Some global server values which can be affected via [ServerRequest]s.
 */
class ServerGlobals() {
    class StatusMessage {
        var text: String? = null
        var isError: Boolean = false
        var timeout = Long.MAX_VALUE

        fun set(other: StatusMessage) {
            text = other.text
            isError = other.isError
            timeout = other.timeout
        }
        fun clear() { set(StatusMessage()) }
    }

    var version: Int = 0
    val status: StatusMessage = StatusMessage()

    constructor(other: ServerGlobals) : this() { set(other) }
    fun set(other: ServerGlobals) {
        version = other.version
        status.set(other.status)
    }
    fun clear() { set(ServerGlobals()) }
}