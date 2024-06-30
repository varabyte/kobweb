package com.varabyte.kobwebx.chrome.ai

import kotlinx.browser.window
import org.w3c.dom.Window

/**
 * Extension function which returns the AIManager instance from the window object.
 *
 * ### Usage
 *
 * #### Text Example
 * ```
 * @Composable
 * fun TextAiExample() {
 *     var session: TextSession? = null
 *     var text by remember { mutableStateOf("") }
 *     LaunchedEffect(Unit) {
 *         session = ai.createTextSession().await()
 *         text = session?.prompt("Hello, world!")?.await()
 *     }
 *     Text(text)
 * }
 * ```
 *
 * #### Streaming Example
 * ```
 * @Composable
 * fun StreamingAiExample() {
 *     var session: TextSession? = null
 *     var text by remember { mutableStateOf("") }
 *     LaunchedEffect(Unit) {
 *         session = ai.createTextSession().await()
 *         val stream = session.promptStreaming("Hello, what's your name?").getReader()
 *         while (true) {
 *             when (val result = readableStream.read().await()) {
 *                 is ReadableStreamReadDoneResult -> break
 *                 is ReadableStreamReadValueResult -> {
 *                        text = result.value
 *                 }
 *             }
 *         }
 *     }
 *     Text(text)
 * }
 */
@Suppress("unused", "UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
val Window.ai by lazy { window.asDynamic().ai as AIManager } // TODO: Use external interface once it's added in upstream
