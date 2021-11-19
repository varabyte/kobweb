package com.varabyte.kobweb.api.log

interface Logger {
    fun trace(message: String)
    fun debug(message: String)
    fun info(message: String)
    fun warn(message: String)
    fun error(message: String)
}