package com.varabyte.kobweb.api.env

/**
 * The environment for the Kobweb API server.
 *
 * This can be used to determine if the server is running in development or production mode. For example, it can be
 * useful to stub certain methods out or use fake, in memory implementations of data storage services when running in
 * development mode.
 */
enum class Environment {
    DEV,
    PROD;
}

val Environment.isDev get() = this == Environment.DEV
val Environment.isProd get() = this == Environment.PROD
