package com.varabyte.kobweb.silk.defer

import androidx.compose.runtime.*
import kotlinx.browser.window

private class DeferredComposablesState {
    private var timeoutHandle = -1
    private val batchedCommands = mutableListOf<() -> Unit>()
    private val entries = mutableStateListOf<DeferredComposablesState.Entry>()

    // By not running some commands immediately, instead delaying and batching them together, this prevents a bunch of
    // intermediate recompositions.
    private fun invokeLater(block: () -> Unit) {
        batchedCommands.add(block)
        if (timeoutHandle == -1) {
            timeoutHandle = window.setTimeout({
                batchedCommands.forEach { it.invoke() }
                batchedCommands.clear()

                timeoutHandle = -1
            })
        }
    }

    fun append(): Entry = Entry().also {
        invokeLater {
            entries.add(it)
        }
    }

    @Composable
    fun forEach(render: @Composable (Entry) -> Unit) {
        entries.forEach { render(it) }
    }

    inner class Entry {
        var content: (@Composable () -> Unit)? = null
        fun dismiss() {
            invokeLater {
                entries.remove(this)
            }
        }
    }
}

private val LocalDeferred = staticCompositionLocalOf<DeferredComposablesState> {
    error("Attempting to defer rendering without calling `DeferringHost`, a required pre-requisite.")
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
fun Deferred(content: @Composable () -> Unit) {
    val state = LocalDeferred.current
    val deferredEntry = remember(state) { state.append() }
    deferredEntry.content = content
    DisposableEffect(deferredEntry) { onDispose { deferredEntry.dismiss() } }
}

/**
 * Wraps a scope within which users can declare [Deferred] blocks.
 *
 * Any [Deferred] blocks will be deferred until the end of the scope.
 *
 * For example:
 *
 * ```
 * DeferringHost {
 *   Box()
 *   Deferred { Row() }
 *   Column()
 * }
 * ```
 *
 * is equivalent to declaring `Box()`, `Column()`, then `Row()` in that order.
 */
@Composable
fun DeferringHost(content: @Composable () -> Unit) {
    val state = DeferredComposablesState()
    CompositionLocalProvider(LocalDeferred provides state) {
        content()
        state.forEach { entry ->
            // Deferred content itself may defer more content! Like showing a tooltip within an overlay
            // If we don't do this, we end up with the deferred list constantly getting modified and causing
            // recompositions as a result.
            entry.content?.let { DeferringHost(it) }
        }
    }
}
