package com.varabyte.kobweb.navigation

import androidx.compose.runtime.*
import com.varabyte.kobweb.core.PageContext
import com.varabyte.kobweb.core.RouteInfo
import com.varabyte.kobweb.core.init.InitRouteContext

typealias LayoutMethod = @Composable (PageContext, PageMethod) -> Unit
typealias PageMethod = @Composable (PageContext) -> Unit
typealias InitRouteMethod = (InitRouteContext) -> Unit

@Deprecated("This type is obsolete and is slated for removal after we remove `Router.setErrorHandler`")
typealias ErrorPageMethod = @Composable (Int) -> Unit

internal class PageData(
    val pageMethod: PageMethod,
    val routeInfo: RouteInfo,
)
