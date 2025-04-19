package com.varabyte.kobweb.compose.css.global

import org.jetbrains.compose.web.css.*

@Suppress("PropertyName")
interface CssGlobalValues<T : StylePropertyValue> {
    val Inherit: T get() = "inherit".unsafeCast<T>()
    val Initial: T get() = "initial".unsafeCast<T>()
    val Revert: T get() = "revert".unsafeCast<T>()
    val Unset: T get() = "unset".unsafeCast<T>()
}