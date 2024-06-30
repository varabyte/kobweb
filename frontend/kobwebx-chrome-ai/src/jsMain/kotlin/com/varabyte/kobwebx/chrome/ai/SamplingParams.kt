package com.varabyte.kobwebx.chrome.ai

/**
 * Params used to control sampling output tokens for the on-device model.
 *
 * **Usage**:
 * ```kotlin
 * val params = SamplingParams(
 *    temperature = 0.3f,
 *    topK = 3
 * )
 * val session = window.ai.createTextSession(params)
 * ```
 *
 * @param temperature The temperature to use for the session. This is a value between 0 and 1, where 0 is the most deterministic and 1 is the most random.
 * @param topK The maximum number of tokens to consider when sampling. Models use nucleus sampling or combined Top-k and nucleus sampling. Top-k sampling considers the set of topK most probable tokens.
 */
data class SamplingParams(
    val temperature: Float? = null,
    val topK: Int? = null
)