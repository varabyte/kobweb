package com.varabyte.kobweb.server.plugin

import io.ktor.server.application.*

interface KobwebServerPlugin {
    /**
     * Called when the server is starting up.
     *
     * This is a good place to register / configure Ktor plugins.
     */
    fun configure(application: Application)
}