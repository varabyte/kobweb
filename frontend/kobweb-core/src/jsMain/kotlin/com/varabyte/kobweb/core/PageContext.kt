package com.varabyte.kobweb.core

import androidx.compose.runtime.*
import com.varabyte.kobweb.navigation.Router

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
class PageContext(val router: Router) {
    companion object {
        internal val active by lazy { mutableStateOf<PageContext?>(null) }
    }
    internal val mutableParams = mutableMapOf<String, String>()
    val params: Map<String, String> = mutableParams
}

@Composable
fun rememberPageContext(): PageContext = remember { PageContext.active.value!! }