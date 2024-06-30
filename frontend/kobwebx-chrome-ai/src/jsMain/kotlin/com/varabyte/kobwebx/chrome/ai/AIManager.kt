@file:Suppress("unused") // All functions will be used by [Window.ai]

package com.varabyte.kobwebx.chrome.ai

import js.promise.Promise

/**
 * The manager that could create a new session for the model.
 */
external interface AIManager {
    /**
     * Returns if it is possible to create a text session.
     * For example, when the service in charge of model loading and session creation is not available,
     * this should return [AIModelAvailability.NO]
     *
     * @return [Promise] of [AIModelAvailability]
     */
    fun canCreateGenericSession(): Promise<AIModelAvailability>

    /**
     * Returns if it is possible to create a text session.
     * For example, when the service in charge of model loading and session creation is not available,
     * this should return [AIModelAvailability.NO]
     *
     * @return [Promise] of [AIModelAvailability]
     */
    fun canCreateTextSession(): Promise<AIModelAvailability>

    /**
     * Returns the default generic options for a generic text session.
     *
     * @return [Promise] of [SamplingParams]
     */
    fun defaultGenericSessionOptions(): Promise<SamplingParams>

    /**
     * Returns the default options for a text session.
     *
     * @return [Promise] of [SamplingParams]
     */
    fun defaultTextSessionOptions(): Promise<SamplingParams>

    /**
     * Creates a new generic session for the model.
     *
     * @param samplingParams The sampling parameters for the session.
     * @return [Promise] of [TextSession]
     */
    fun createGenericSession(samplingParams: SamplingParams? = definedExternally): Promise<TextSession>

    /**
     * Creates a new session for the model.
     *
     * @param samplingParams The sampling parameters for the session.
     * @return [Promise] of [TextSession]
     */
    fun createTextSession(samplingParams: SamplingParams? = definedExternally): Promise<TextSession>
}