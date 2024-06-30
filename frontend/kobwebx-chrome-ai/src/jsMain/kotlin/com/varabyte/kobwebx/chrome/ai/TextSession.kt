@file:Suppress("unused") // Used for JS interop

package com.varabyte.kobwebx.chrome.ai

import js.promise.Promise
import web.streams.ReadableStream

/**
 * A session that can be used to interact with the AI model created by [AIManager.createTextSession] or [AIManager.canCreateGenericSession]
 */
external interface TextSession {
    /**
     * Destroys the session, freeing up resources. Once this is called, the session can no longer be used.
     */
    fun destroy(): Promise<Unit>

    /**
     * Calls the AI model with the given prompt, and returns the response.
     *
     * **Usage**:
     * ```kotlin
     *
     * suspend fun main() {
     *      val session = window.ai.createTextSession().await()
     *      val response = session.prompt("Hello, what's your name?").await()
     *      println("The AI responded: $response")
     * }
     */
    fun prompt(prompt: String): Promise<String>

    /**
     * Calls the AI model with the given prompt, and returns the response in a streaming manner.
     *
     * **Usage**:
     * ```kotlin
     * suspend fun main() {
     *      val session = window.ai.createTextSession().await()
     *      val stream = session.promptStreaming("Hello, what's your name?").getReader()
     *      while (true) {
     *          when (val result = readableStream.read().await()) {
     *              is ReadableStreamReadDoneResult -> break
     *              is ReadableStreamReadValueResult -> {
     *                     println(result.value)
     *              }
     *          }
     *      }
     */
    fun promptStreaming(prompt: String): ReadableStream<String>

    /**
     * Underlying method to execute the AI model with the given prompt.
     */
    fun execute(prompt: String): Promise<String>

    /**
     * Underlying method to execute the AI model with the given prompt in a streaming manner.
     */
    fun executeStreaming(prompt: String): ReadableStream<String>
}