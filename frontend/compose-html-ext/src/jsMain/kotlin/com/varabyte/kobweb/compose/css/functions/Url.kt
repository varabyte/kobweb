package com.varabyte.kobweb.compose.css.functions

import org.jetbrains.compose.web.css.StylePropertyValue

class CSSUrl internal constructor(private val url: String): StylePropertyValue {
    override fun toString() = "url(\"$url\")"
}

fun url(value: String) = CSSUrl(value)
