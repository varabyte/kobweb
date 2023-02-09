package com.varabyte.kobweb.compose.ui.modifiers

import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.attrsModifier
import org.jetbrains.compose.web.events.SyntheticClipboardEvent

fun Modifier.onCopy(listener: (SyntheticClipboardEvent) -> Unit): Modifier = attrsModifier {
    onCopy(listener)
}

fun Modifier.onCut(listener: (SyntheticClipboardEvent) -> Unit): Modifier = attrsModifier {
    onCut(listener)
}

fun Modifier.onPaste(listener: (SyntheticClipboardEvent) -> Unit): Modifier = attrsModifier {
    onPaste(listener)
}
