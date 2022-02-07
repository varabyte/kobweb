package com.varabyte.kobweb.compose.ui.modifiers

import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.attrModifier
import com.varabyte.kobweb.compose.ui.styleModifier
import org.jetbrains.compose.web.attributes.DirType
import org.jetbrains.compose.web.css.*

fun Modifier.dir(dirType: DirType) = attrModifier {
    dir(dirType)
}

fun Modifier.fontFamily(vararg values: String): Modifier = styleModifier {
    fontFamily(*values)
}

fun Modifier.fontSize(value: CSSNumeric): Modifier = styleModifier {
    fontSize(value)
}

fun Modifier.fontStyle(value: FontStyle): Modifier = styleModifier {
    fontStyle(value)
}

fun Modifier.fontWeight(value: FontWeight): Modifier = styleModifier {
    fontWeight(value)
}

fun Modifier.lang(value: String) = attrModifier {
    lang(value)
}

fun Modifier.letterSpacing(value: CSSNumeric) = styleModifier {
    letterSpacing(value)
}

fun Modifier.spellCheck(enabled: Boolean) = attrModifier {
    spellCheck(enabled)
}

fun Modifier.textAlign(textAlign: TextAlign): Modifier = styleModifier {
    textAlign(textAlign)
}

fun Modifier.textDecorationLine(textDecorationLine: TextDecorationLine): Modifier = styleModifier {
    textDecorationLine(textDecorationLine)
}

fun Modifier.whiteSpace(whiteSpace: WhiteSpace): Modifier = styleModifier {
    whiteSpace(whiteSpace)
}
