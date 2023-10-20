package com.varabyte.kobweb.compose.ui.modifiers

import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.attrsModifier

fun Modifier.ariaDisabled(value: Boolean = true) = attrsModifier {
    ariaDisabled(value)
}

fun Modifier.ariaHidden(value: Boolean = true) = attrsModifier {
    ariaHidden(value)
}

fun Modifier.ariaInvalid(value: Boolean = true) = attrsModifier {
    ariaInvalid(value)
}

fun Modifier.ariaLabel(value: String) = attrsModifier {
    ariaLabel(value)
}

fun Modifier.ariaRequired(value: Boolean = true) = attrsModifier {
    ariaRequired(value)
}

fun Modifier.role(value: String) = attrsModifier {
    role(value)
}
