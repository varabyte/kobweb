package com.varabyte.kobweb.navigation

import androidx.compose.runtime.*
import com.varabyte.kobweb.core.PageContext

typealias LayoutMethod = @Composable (PageContext, PageMethod) -> Unit
typealias PageMethod = @Composable (PageContext) -> Unit

/**
 * Typealias for a composable method which takes an error code as its first and only argument (e.g. 404).
 *
 * Use [Router.setErrorHandler] to override with your own custom handler.
 */
typealias ErrorPageMethod = @Composable (Int) -> Unit

internal class PageData(
    val pageMethod: PageMethod,
    val routeInfo: PageContext.RouteInfo,
)
