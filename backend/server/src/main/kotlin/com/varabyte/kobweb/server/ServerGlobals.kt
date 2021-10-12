package com.varabyte.kobweb.server

import com.varabyte.kobweb.server.api.ServerRequest

/**
 * Some global server values which can be affected via [ServerRequest]s.
 */
class ServerGlobals {
    var version: Int = 0
    var status: String? = null
    var isStatusError: Boolean = false
    var timeout = Long.MAX_VALUE
}