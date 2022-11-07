package com.varabyte.kobweb.compose.ui.modifiers

import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.styleModifier
import org.jetbrains.compose.web.css.AlignContent as JbAlignContent
import org.jetbrains.compose.web.css.alignContent as jbAlignContent
import org.jetbrains.compose.web.css.AlignItems as JbAlignItems
import org.jetbrains.compose.web.css.alignItems as jbAlignItems
import org.jetbrains.compose.web.css.AlignSelf as JbAlignSelf
import org.jetbrains.compose.web.css.alignSelf as jbAlignSelf
import org.jetbrains.compose.web.css.JustifyContent as JbJustifyContent
import org.jetbrains.compose.web.css.justifyContent as jbJustifyContent

// region Legacy JB modifiers

fun Modifier.alignContent(alignContent: JbAlignContent) = styleModifier {
    jbAlignContent(alignContent)
}

fun Modifier.alignItems(alignItems: JbAlignItems) = styleModifier {
    jbAlignItems(alignItems)
}

fun Modifier.alignSelf(alignSelf: JbAlignSelf) = styleModifier {
    jbAlignSelf(alignSelf)
}

fun Modifier.justifyContent(justifyContent: JbJustifyContent) = styleModifier {
    jbJustifyContent(justifyContent)
}

// endregion

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

fun Modifier.justifyItems(justifyItems: JustifyItems) = styleModifier {
    justifyItems(justifyItems)
}

fun Modifier.justifySelf(justifySelf: JustifySelf) = styleModifier {
    justifySelf(justifySelf)
}

fun Modifier.placeContent(alignContent: AlignContent, justifyContent: JustifyContent) = styleModifier {
    placeContent(alignContent, justifyContent)
}

fun Modifier.placeItems(alignItems: AlignItems, justifyItems: JustifyItems) = styleModifier {
    placeItems(alignItems, justifyItems)
}

fun Modifier.placeSelf(alignSelf: AlignSelf, justifySelf: JustifySelf) = styleModifier {
    placeSelf(alignSelf, justifySelf)
}
