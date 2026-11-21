package com.varabyte.kobweb.ksp.testutil

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSNode

/**
 * A [KSPLogger] which records the messages sent to it, so tests can check what a processor reported.
 */
class RecordingLogger : KSPLogger {
    val warnings = mutableListOf<String>()
    val errors = mutableListOf<String>()

    override fun logging(message: String, symbol: KSNode?) = Unit
    override fun info(message: String, symbol: KSNode?) = Unit
    override fun warn(message: String, symbol: KSNode?) { warnings.add(message) }
    override fun error(message: String, symbol: KSNode?) { errors.add(message) }
    override fun exception(e: Throwable) = throw e
}
