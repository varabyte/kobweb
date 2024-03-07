package com.varabyte.kobweb.worker

/**
 * Message arriving from a sender.
 *
 * @param I The type of the input message.
 * @property input The raw input itself that was sent.
 * @property transferables Optional transferables that may have additionally been sent with the message.
 */
class InputMessage<I>(
    val input: I,
    val transferables: Transferables,
)
