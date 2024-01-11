package com.varabyte.kobweb.worker

import androidx.compose.runtime.*

/**
 * Create and remember a [Worker] implementation.
 *
 * Users should call this instead of `remember` directly, as otherwise they may leave a worker running even if they
 * navigate to a different part of their site.
 */
@Composable
fun <W: Worker<*, *>> rememberWorker(createWorker: () -> W): W {
    val worker = remember { createWorker() }
    DisposableEffect(worker) {
        onDispose {
            worker.terminate()
        }
    }
    return worker
}
