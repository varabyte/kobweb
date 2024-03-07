package com.varabyte.kobweb.worker

interface OutputDispatcher<O> {
    operator fun invoke(output: O, transferables: Transferables = Transferables.Empty)
}
