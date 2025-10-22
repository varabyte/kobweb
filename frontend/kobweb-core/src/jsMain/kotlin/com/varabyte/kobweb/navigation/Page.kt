package com.varabyte.kobweb.navigation

import androidx.compose.runtime.*
import com.varabyte.kobweb.core.PageContext
import com.varabyte.kobweb.core.RouteInfo
import com.varabyte.kobweb.core.init.InitRouteContext

typealias LayoutMethod = @Composable (PageContext, PageMethod) -> Unit
typealias PageMethod = @Composable (PageContext) -> Unit
typealias InitRouteMethod = (InitRouteContext) -> Unit

internal class PageData(
    val pageMethod: PageMethod,
    val routeInfo: RouteInfo,
)
