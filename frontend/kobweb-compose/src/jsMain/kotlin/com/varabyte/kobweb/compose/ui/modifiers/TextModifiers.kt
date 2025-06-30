package com.varabyte.kobweb.compose.ui.modifiers

import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.css.TextShadow
import com.varabyte.kobweb.compose.css.textShadow
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.attrsModifier
import com.varabyte.kobweb.compose.ui.styleModifier
import org.jetbrains.compose.web.attributes.DirType
import org.jetbrains.compose.web.css.*

fun Modifier.dir(dirType: DirType) = attrsModifier {
    dir(dirType)
}

fun Modifier.hyphenateCharacter(hyphenateCharacter: HyphenateCharacter) = styleModifier {
    hyphenateCharacter(hyphenateCharacter)
}

fun Modifier.hyphenateCharacter(value:String) = styleModifier {
    hyphenateCharacter(HyphenateCharacter.of(value))
}

fun Modifier.lang(value: String) = attrsModifier {
    lang(value)
}

fun Modifier.letterSpacing(value: CSSLengthNumericValue) = styleModifier {
    letterSpacing(value)
}

fun Modifier.lineBreak(lineBreak: LineBreak) = styleModifier {
    lineBreak(lineBreak)
}

fun Modifier.rubyPosition(rubyPosition: RubyPosition) = styleModifier {
    rubyPosition(rubyPosition)
}

fun Modifier.spellCheck(enabled: Boolean) = attrsModifier {
    spellCheck(enabled)
}

fun Modifier.textAlign(textAlign: TextAlign): Modifier = styleModifier {
    textAlign(textAlign)
}

fun Modifier.textAlignLast(textAlignLast: TextAlignLast) = styleModifier {
    textAlignLast(textAlignLast)
}

fun Modifier.textCombineUpright(textCombineUpright: TextCombineUpright) = styleModifier {
    textCombineUpright(textCombineUpright)
}

fun Modifier.textDecorationLine(textDecorationLine: TextDecorationLine): Modifier = styleModifier {
    textDecorationLine(textDecorationLine)
}

fun Modifier.textDecorationSkipInk(textDecorationSkipInk: TextDecorationSkipInk) = styleModifier {
    textDecorationSkipInk(textDecorationSkipInk)
}

fun Modifier.textEmphasisPosition(textEmphasisPosition: TextEmphasisPosition) = styleModifier {
    textEmphasisPosition(textEmphasisPosition)
}

fun Modifier.textEmphasisPosition(firstPosition: TextEmphasisPosition.Horizontal, secondPosition: TextEmphasisPosition.Vertical) = styleModifier {
    textEmphasisPosition(TextEmphasisPosition.of(firstPosition, secondPosition))
}

fun Modifier.textEmphasisPosition(firstPosition: TextEmphasisPosition.Vertical, secondPosition: TextEmphasisPosition.Horizontal) = styleModifier {
    textEmphasisPosition(TextEmphasisPosition.of(firstPosition, secondPosition))
}

fun Modifier.textIndent(value: CSSLengthOrPercentageNumericValue) = styleModifier {
    textIndent(TextIndent.of(value))
}

fun Modifier.textOrientation(textOrientation: TextOrientation) = styleModifier {
    textOrientation(textOrientation)
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

fun Modifier.textShadow(vararg shadows: TextShadow.Listable) = styleModifier {
    textShadow(TextShadow.list(*shadows))
}

fun Modifier.textShadow(shadows: List<TextShadow.Listable>) = textShadow(*shadows.toTypedArray())

fun Modifier.textShadow(textShadow: TextShadow): Modifier = styleModifier {
    textShadow(textShadow)
}

fun Modifier.textTransform(textTransform: TextTransform): Modifier = styleModifier {
    textTransform(textTransform)
}

fun Modifier.textUnderlineOffset(value: CSSLengthOrPercentageNumericValue) = styleModifier {
    textUnderlineOffset(TextUnderlineOffset.of(value))
}

fun Modifier.textUnderlineOffset(textUnderlineOffset: TextUnderlineOffset) = styleModifier {
    textUnderlineOffset(textUnderlineOffset)
}

fun Modifier.textUnderlinePosition(textUnderlinePosition: TextUnderlinePosition) = styleModifier {
    textUnderlinePosition(textUnderlinePosition)
}

fun Modifier.textUnderlinePosition(
    firstPosition: TextUnderlinePosition.Horizontal,
    secondPosition: TextUnderlinePosition.Vertical
) = styleModifier {
    textUnderlinePosition(TextUnderlinePosition.of(firstPosition, secondPosition))
}

fun Modifier.textUnderlinePosition(
    firstPosition: TextUnderlinePosition.Vertical,
    secondPosition: TextUnderlinePosition.Horizontal
) = styleModifier {
    textUnderlinePosition(TextUnderlinePosition.of(firstPosition, secondPosition))
}

fun Modifier.whiteSpace(whiteSpace: WhiteSpace): Modifier = styleModifier {
    whiteSpace(whiteSpace)
}

fun Modifier.wordBreak(wordBreak: WordBreak): Modifier = styleModifier {
    wordBreak(wordBreak)
}

fun Modifier.wordSpacing(wordSpacing: WordSpacing) = styleModifier {
    wordSpacing(wordSpacing)
}

fun Modifier.wordSpacing(value: CSSLengthNumericValue) = styleModifier {
    wordSpacing(WordSpacing.of(value))
}
fun Modifier.writingMode(writingMode: WritingMode) = styleModifier {
    writingMode(writingMode)
}
