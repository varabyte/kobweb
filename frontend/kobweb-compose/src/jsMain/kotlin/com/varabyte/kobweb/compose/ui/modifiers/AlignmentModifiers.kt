package com.varabyte.kobweb.compose.ui.modifiers

import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.styleModifier

fun Modifier.alignContent(alignContent: AlignContent) = styleModifier {
    alignContent(alignContent)
}

fun Modifier.alignItems(alignItems: AlignItems) = styleModifier {
    alignItems(alignItems)
}

fun Modifier.alignSelf(alignSelf: AlignSelf) = styleModifier {
    alignSelf(alignSelf)
}

fun Modifier.justifyContent(justifyContent: JustifyContent) = styleModifier {
    justifyContent(justifyContent)
}

fun Modifier.justifySelf(justifySelf: JustifySelf) = styleModifier {
    justifySelf(justifySelf)
}

fun Modifier.justifyItems(justifyItems: JustifyItems) = styleModifier {
    justifyItems(justifyItems)
}

fun Modifier.placeSelf(alignSelf: AlignSelf, justifySelf: JustifySelf) = styleModifier {
    placeSelf(alignSelf, justifySelf)
}

fun Modifier.placeItems(alignItems: AlignItems, justifyItems: JustifyItems) = styleModifier {
    placeItems(alignItems, justifyItems)
}

fun Modifier.placeContent(alignContent: AlignContent, justifyContent: JustifyContent) = styleModifier {
    placeContent(alignContent, justifyContent)
}
