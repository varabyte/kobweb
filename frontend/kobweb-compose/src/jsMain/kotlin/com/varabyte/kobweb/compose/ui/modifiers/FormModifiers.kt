package com.varabyte.kobweb.compose.ui.modifiers

import com.varabyte.kobweb.compose.css.disabled
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.attrsModifier

fun Modifier.disabled() = attrsModifier {
    disabled()
}
