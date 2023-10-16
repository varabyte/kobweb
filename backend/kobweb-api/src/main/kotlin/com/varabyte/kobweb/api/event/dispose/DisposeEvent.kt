package com.varabyte.kobweb.api.event.dispose

/**
 * An enum that represents the reason for the api disposal
 */
enum class DisposeReason {
    /**
     * The dispose event is triggered by the dev server being restarted due to a live reload request
     */
    DEV_API_RELOAD,
    /**
     * The dispose event is triggered by because the server is shutting down normally
     */
    SERVER_SHUTDOWN,
    /**
     * The dispose event is triggered because the JVM running the server was requested to shut down, e.g. in response to Ctrl-C
     */
    SHUTDOWN_HOOK
}

/**
 * Represents an event triggered for the disposal of server resources.
 *
 * Encapsulates the reason behind the server's resource disposal.
 *
 * @property reason Specifies why the disposal event was triggered. Represented by the [DisposeReason] enum.
 * @see DisposeReason for possible reasons for the event.
 */
data class DisposeEvent(
    val reason: DisposeReason
)
