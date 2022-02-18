package com.varabyte.kobweb.compose.ui.modifiers

import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.styleModifier
import org.jetbrains.compose.web.css.*

fun Modifier.justifyContent(justifyContent: JustifyContent): Modifier = styleModifier {
    justifyContent(justifyContent)
}

fun Modifier.justifySelf(justifySelf: JustifySelf) = styleModifier {
    justifySelf(justifySelf)
}

fun Modifier.justifyItems(justifyItems: JustifyItems) = styleModifier {
    justifyItems(justifyItems)
}

fun Modifier.alignSelf(alignSelf: AlignSelf) = styleModifier {
    alignSelf(alignSelf)
}

fun Modifier.placeSelf(alignSelf: AlignSelf, justifySelf: JustifySelf) = styleModifier {
    placeSelf(alignSelf, justifySelf)
}

fun Modifier.alignItems(alignItems: AlignItems): Modifier = styleModifier {
    alignItems(alignItems)
}

fun Modifier.alignContent(alignContent: AlignContent): Modifier = styleModifier {
    alignContent(alignContent)
}
