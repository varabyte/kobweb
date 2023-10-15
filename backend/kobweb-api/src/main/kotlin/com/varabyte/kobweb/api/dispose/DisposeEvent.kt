package com.varabyte.kobweb.api.dispose

/**
 * An enum that represents the reason for the api disposal
 */
enum class DisposeReason {
    DEV_API_RELOAD, SERVER_SHUTDOWN, SHUTDOWN_HOOK
}

data class DisposeEvent(
    val reason: DisposeReason
)
