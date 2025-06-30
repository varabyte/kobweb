package com.varabyte.kobweb.worker

interface OutputDispatcher<O> {
    operator fun invoke(output: O, attachments: Attachments = Attachments.Empty)
}
