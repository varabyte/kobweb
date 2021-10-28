package com.varabyte.kobweb.cli.common

object Os {
    fun isWindows() = System.getProperty("os.name").lowercase().contains("windows")
}