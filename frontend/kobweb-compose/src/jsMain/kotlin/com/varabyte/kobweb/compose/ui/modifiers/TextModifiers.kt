package com.varabyte.kobweb.compose.ui.modifiers

import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.attrModifier
import com.varabyte.kobweb.compose.ui.styleModifier
import org.jetbrains.compose.web.attributes.DirType

fun Modifier.textAlign(textAlign: TextAlign): Modifier = styleModifier {
    textAlign(textAlign)
}

fun Modifier.textDecorationLine(textDecorationLine: TextDecorationLine): Modifier = styleModifier {
    textDecorationLine(textDecorationLine)
}

fun Modifier.whiteSpace(whiteSpace: WhiteSpace): Modifier = styleModifier {
    whiteSpace(whiteSpace)
}

fun Modifier.dir(dirType: DirType) = attrModifier {
    dir(dirType)
}

fun Modifier.spellCheck(enabled: Boolean) = attrModifier {
    spellCheck(enabled)
}

fun Modifier.lang(value: String) = attrModifier {
    lang(value)
}