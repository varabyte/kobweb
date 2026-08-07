package com.varabyte.kobweb.server.util.log

import com.varabyte.kobweb.api.log.Logger
import org.slf4j.LoggerFactory

class KobwebLogger(type: Type) : Logger {
    enum class Type {
        System,
        User,
    }

    private val logger = LoggerFactory.getLogger("kobweb.${type.name.lowercase()}")

    override fun trace(message: String) = logger.trace(message)
    override fun debug(message: String) = logger.debug(message)
    override fun info(message: String) = logger.info(message)
    override fun warn(message: String) = logger.warn(message)
    override fun error(message: String) = logger.error(message)

}

class KobwebLoggers {
    val user = KobwebLogger(KobwebLogger.Type.User)
    val system = KobwebLogger(KobwebLogger.Type.System)
}