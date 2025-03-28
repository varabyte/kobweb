package com.varabyte.kobweb.compose.css

abstract class CssStyleProperty<T : CssStyleProperty<T>> protected constructor(private val value: String) {
    val Inherit: T get() = "inherit".unsafeCast<T>()
    val Initial: T get() = "initial".unsafeCast<T>()
    val Revert: T get() = "revert".unsafeCast<T>()
    val Unset: T get() = "unset".unsafeCast<T>()
}