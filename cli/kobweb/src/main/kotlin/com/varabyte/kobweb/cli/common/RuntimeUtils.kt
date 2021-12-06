package com.varabyte.kobweb.cli.common

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream
import java.io.InputStreamReader

fun Runtime.gradlew(vararg args: String): Process {
    val gradlewBinary = if (!Os.isWindows()) "gradlew" else "gradlew.bat"
    var currDir: File? = File(".").absoluteFile
    while (currDir != null && !File(currDir, gradlewBinary).exists()) {
        currDir = currDir.parentFile
    }

    if (currDir == null) {
        throw FileNotFoundException("\"$gradlewBinary\" not found in current or parent directories")
    }

    val finalArgs = mutableListOf("${currDir.absolutePath}/$gradlewBinary")
    finalArgs.addAll(args)
    return exec(finalArgs.toTypedArray())
}

private fun consumeStream(stream: InputStream, isError: Boolean, onLineRead: (String, Boolean) -> Unit) {
    val isr = InputStreamReader(stream)
    val br = BufferedReader(isr)
    while (true) {
        val line = br.readLine() ?: break
        onLineRead(line, isError)
    }
}

private fun defaultOutputHandler(line: String, isError: Boolean) {
    if (isError) {
        System.err.println(line)
    }
    else {
        println(line)
    }
}

fun Process.consumeProcessOutput(onLineRead: (String, Boolean) -> Unit = ::defaultOutputHandler) {
    CoroutineScope(Dispatchers.IO).launch { consumeStream(inputStream, isError = false, onLineRead) }
    CoroutineScope(Dispatchers.IO).launch { consumeStream(errorStream, isError = true, onLineRead) }
}