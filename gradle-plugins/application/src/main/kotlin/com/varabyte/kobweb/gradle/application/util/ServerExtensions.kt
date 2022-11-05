package com.varabyte.kobweb.gradle.application.util

import com.varabyte.kobweb.server.api.ServerState

fun ServerState.toDisplayText(): String {
    return "http://localhost:$port (PID = $pid)"
}
