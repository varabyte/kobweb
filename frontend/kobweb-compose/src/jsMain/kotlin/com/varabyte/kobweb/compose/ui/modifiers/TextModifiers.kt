package com.varabyte.kobweb.compose.ui.modifiers

import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.attrsModifier
import com.varabyte.kobweb.compose.ui.styleModifier
import org.jetbrains.compose.web.attributes.DirType
import org.jetbrains.compose.web.css.*

fun Modifier.dir(dirType: DirType) = attrsModifier {
    dir(dirType)
}

fun Modifier.lang(value: String) = attrsModifier {
    lang(value)
}

fun Modifier.letterSpacing(value: CSSLengthNumericValue) = styleModifier {
    letterSpacing(value)
}

fun Modifier.spellCheck(enabled: Boolean) = attrsModifier {
    spellCheck(enabled)
}

fun Modifier.textAlign(textAlign: TextAlign): Modifier = styleModifier {
    textAlign(textAlign)
}

fun Modifier.textDecorationLine(textDecorationLine: TextDecorationLine): Modifier = styleModifier {
    textDecorationLine(textDecorationLine)
}

fun Modifier.textOverflow(textOverflow: TextOverflow): Modifier = styleModifier {
    textOverflow(textOverflow)
}

fun Modifier.textShadow(
    offsetX: CSSLengthNumericValue,
    offsetY: CSSLengthNumericValue,
    blurRadius: CSSLengthNumericValue? = null,
    color: CSSColorValue? = null
) = styleModifier {
    textShadow(TextShadow.of(offsetX, offsetY, blurRadius, color))
}

@Suppress("DEPRECATION")
fun Modifier.textShadow(vararg shadows: CSSTextShadow) = styleModifier {
    textShadow(*shadows.map {
        TextShadow.of(it.offsetX, it.offsetY, it.blurRadius, it.color)
    }.toTypedArray())
}

fun Modifier.textShadow(vararg shadows: TextShadow.Repeatable) = styleModifier {
    textShadow(*shadows)
}

fun Modifier.textShadow(shadows: List<TextShadow.Repeatable>) = textShadow(*shadows.toTypedArray())

fun Modifier.textShadow(textShadow: TextShadow): Modifier = styleModifier {
    textShadow(textShadow)
}

fun Modifier.textTransform(textTransform: TextTransform): Modifier = styleModifier {
    textTransform(textTransform)
}

fun Modifier.whiteSpace(whiteSpace: WhiteSpace): Modifier = styleModifier {
    whiteSpace(whiteSpace)
}

fun Modifier.wordBreak(wordBreak: WordBreak): Modifier = styleModifier {
    wordBreak(wordBreak)
}

fun Modifier.writingMode(writingMode: WritingMode) = styleModifier {
    writingMode(writingMode)
}
