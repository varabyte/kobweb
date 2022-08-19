package com.varabyte.kobweb.compose.ui.modifiers

import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.attrsModifier

fun Modifier.role(value: String) = attrsModifier {
    attr("role", value)
}