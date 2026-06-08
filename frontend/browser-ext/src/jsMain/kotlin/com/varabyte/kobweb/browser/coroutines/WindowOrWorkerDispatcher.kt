package com.varabyte.kobweb.browser.coroutines

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import org.w3c.dom.Window
import org.w3c.dom.WindowOrWorkerGlobalScope
import org.w3c.dom.WorkerGlobalScope

fun WindowOrWorkerGlobalScope.asCoroutineDispatcher(): CoroutineDispatcher {
    // JS call necessary because if we're in a web worker, referencing `Window` (as in `if (this is Window)` will
    // throw an exception.
    val isWindow = js("typeof window !== 'undefined'") as Boolean

    return if (isWindow) {
        (this as Window).asCoroutineDispatcher()
    } else {
        (this as WorkerGlobalScope).asCoroutineDispatcher()
    }
}
