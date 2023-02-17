package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.CSSStyleVariable
import org.jetbrains.compose.web.css.StylePropertyValue

/** Like [org.jetbrains.compose.web.css.variable] but you can specify the name explicitly. */
fun <T : StylePropertyValue> variable(name: String) = CSSStyleVariable<T>(name)
