package com.varabyte.kobweb.cli.common

import com.varabyte.kobweb.common.io.consumeAsync

fun Runtime.git(vararg args: String): Process {
    val finalArgs = mutableListOf("git")
    finalArgs.addAll(args)
    return exec(finalArgs.toTypedArray())
}

private fun defaultOutputHandler(line: String, isError: Boolean) {
    if (isError) {
        System.err.println(line)
    }
    else {
        println(line)
    }
}

fun Process.consumeProcessOutput(onLineRead: (line: String, isError: Boolean) -> Unit = ::defaultOutputHandler) {
    inputStream.consumeAsync { line -> onLineRead(line, false) }
    errorStream.consumeAsync { line -> onLineRead(line, true) }
}
