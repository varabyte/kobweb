package com.varabyte.kobweb.compose.css.global

@Suppress("PropertyName")
interface GlobalStylePropertyValues<T> {
    val Inherit: T
    val Initial: T
    val Revert: T
    val Unset: T
}

// A factory function to generate global values for any wrapper class
fun <T> globalStyleValues(factory: (String) -> T): GlobalStylePropertyValues<T> =
    object : GlobalStylePropertyValues<T> {
        override val Inherit = factory("inherit")
        override val Initial = factory("initial")
        override val Revert = factory("revert")
        override val Unset = factory("unset")
    }