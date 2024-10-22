package com.varabyte.kobweb.gradle.application.buildservices

import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters

abstract class KobwebServerService : BuildService<BuildServiceParameters.None>, AutoCloseable {
    init {
        println("KobwebServerService created")
    }

    fun start() {
        println("kobweb server started")
    }

    fun stop() {
        println("kobweb server stopped")
    }

    override fun close() {
        println("KobwebServerService close called....")
        stop()
    }
}
