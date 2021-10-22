package com.varabyte.kobweb.server.api

import com.varabyte.kobweb.common.error.KobwebException

private val PROPERTY_SERVER_ENVIRONMENT = "kobweb.server.environment"

enum class ServerEnvironment {
    DEV,
    PROD;

    companion object {
        fun get(): ServerEnvironment {
            val envValue: String = System.getProperty(PROPERTY_SERVER_ENVIRONMENT) ?: PROD.name
            return values().firstOrNull { env -> env.name == envValue } ?:
                throw KobwebException("Invalid server property: $envValue, expected one of [${values().joinToString()}]")
        }
    }

    fun toSystemPropertyParam(): String = "-D${PROPERTY_SERVER_ENVIRONMENT}=${this.name}"
}