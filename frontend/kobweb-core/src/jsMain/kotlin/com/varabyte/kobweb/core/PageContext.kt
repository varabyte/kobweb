package com.varabyte.kobweb.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

/**
 * Various contextual information useful for a page.
 *
 * Access it using [rememberPageContext] either in the page itself or within any composable nested inside of it.
 *
 * ```
 * @Page
 * @Composable
 * fun SettingsPage() {
 *    val ctx = rememberPageContext()
 *    val userName = ctx.params["username"] ?: "Unknown user"
 *    ...
 * }
 */
class PageContext {
    companion object {
        internal var active: PageContext? = null
    }
    internal val mutableParams = mutableMapOf<String, String>()
    val params: Map<String, String> = mutableParams
}

@Composable
fun rememberPageContext(): PageContext = remember {
    return PageContext.active
        ?: error("rememberPageContext is only valid to call inside a @Page composable, as it is cleared elsewhere.")
}