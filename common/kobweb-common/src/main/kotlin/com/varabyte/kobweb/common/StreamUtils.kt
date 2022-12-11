package com.varabyte.kobweb.common

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

/**
 * Consume a target input stream in a non-blocking manner, triggering lines read from it into a callback.
 */
fun InputStream.consumeAsync(onLineRead: (String) -> Unit) {
    val stream = this
    CoroutineScope(Dispatchers.IO).launch {
        val isr = InputStreamReader(stream)
        val br = BufferedReader(isr)
        try {
            while (true) {
                // No need to warn, we're in an IO block
                @Suppress("BlockingMethodInNonBlockingContext")
                val line = br.readLine()
                onLineRead(line)
            }
        } catch (ignored: IOException) { }
    }
}
