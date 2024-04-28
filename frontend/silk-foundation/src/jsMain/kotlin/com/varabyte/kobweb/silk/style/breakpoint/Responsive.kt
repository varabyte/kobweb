package com.varabyte.kobweb.silk.style.breakpoint

/**
 * A class used for storing generic values associated with breakpoints plus a base style.
 */
class ResponsiveValues<T>(
    val base: T,
    val sm: T,
    val md: T,
    val lg: T,
    val xl: T,
)
