package com.varabyte.kobweb.silk.defer

import androidx.compose.runtime.*
import kotlinx.browser.window

private class DeferredComposablesState {
    private var changeCount = 0
    private var timeoutHandle = -1
    private val batchedCommands = mutableListOf<() -> Unit>()
    private val entries = mutableStateListOf<DeferredComposablesState.Entry>()

    // By not running some commands immediately, instead delaying and batching them toghether, this prevents a bunch of
    // intermediate recompositions.
    private fun delayAndBatchCommand(block: () -> Unit) {
        batchedCommands.add(block)
        if (timeoutHandle == -1) {
            timeoutHandle = window.setTimeout({
                batchedCommands.forEach { it.invoke() }
                batchedCommands.clear()

                ++changeCount
                timeoutHandle = -1
            })
        }
    }

    fun append(): Entry = Entry().also {
        delayAndBatchCommand {
            entries.add(it)
        }
    }

    @Composable
    fun forEach(render: @Composable (Entry) -> Unit) {
        key(changeCount) {
            entries.forEach { render(it) }
        }
    }

    inner class Entry {
        var content: (@Composable () -> Unit)? = null
        fun dismiss() {
            delayAndBatchCommand {
                entries.remove(this)
            }
        }
    }
}

private val LocalDeferred = staticCompositionLocalOf<DeferredComposablesState> {
    error("Attempting to defer rendering without calling `renderWithDeferred`, a required pre-requisite.")
}

/**
 * Defer the target [content] from rendering until the main content is finished.
 *
 * This has the (often wanted) side effects of making sure the content always appears on top of the main content
 * (without needing to use z-index tricks) while also de-parenting the target being rendered (thereby avoiding
 * inheriting unexpected styles from element you want to appear beside, not within).
 *
 * Render deferral is particularly useful for overlays, like modals and tooltips.
 */
@Composable
fun deferRender(content: @Composable () -> Unit) {
    val state = LocalDeferred.current
    val deferredEntry = remember(state) { state.append() }
    deferredEntry.content = content
    DisposableEffect(deferredEntry) { onDispose { deferredEntry.dismiss() } }
}

/**
 * Wraps a target composable with support for allowing deferred render calls.
 *
 * With this method called, any of the children Composables in [content] can trigger [deferRender], which will append
 * a render request which only gets run *after* the main content is finished rendering.
 *
 * You should only have to call this method once. Putting it near the root of your compose hierarchy is suggested.
 */
@Composable
fun renderWithDeferred(content: @Composable () -> Unit) {
    val state = DeferredComposablesState()
    CompositionLocalProvider(LocalDeferred provides state) {
        content()
        state.forEach { entry ->
            // Deferred content itself may defer more content! Like showing a tooltip within an overlay
            // If we don't do this, we end up with the deferred list constantly getting modified and causing
            // recompositions as a result.
            entry.content?.let { renderWithDeferred(it) }
        }
    }
}
