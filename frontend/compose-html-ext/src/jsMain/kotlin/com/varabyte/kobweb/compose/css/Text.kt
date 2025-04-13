package com.varabyte.kobweb.compose.css

import com.varabyte.kobweb.compose.css.global.CssGlobalValues
import org.jetbrains.compose.web.css.*

class TextAlign private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object: CssGlobalValues<TextAlign> {
        val Left get() = TextAlign("left")
        val Right get() = TextAlign("right")
        val Center get() = TextAlign("center")
        val Justify get() = TextAlign("justify")
        val JustifyAll get() = TextAlign("justify-all")
        val Start get() = TextAlign("start")
        val End get() = TextAlign("end")
        val MatchParent get() = TextAlign("match-parent")
    }
}

fun StyleScope.textAlign(textAlign: TextAlign) {
    property("text-align", textAlign)
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/text-decoration-line
class TextDecorationLine private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object: CssGlobalValues<TextDecorationLine> {
        val Underline get() = TextDecorationLine("underline")
        val Overline get() = TextDecorationLine("overline")
        val LineThrough get() = TextDecorationLine("line-through")
        val None get() = TextDecorationLine("none")
    }
}

fun StyleScope.textDecorationLine(vararg textDecorationLines: TextDecorationLine) {
    property("text-decoration-line", textDecorationLines.joinToString(" "))
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/text-overflow
class TextOverflow private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object: CssGlobalValues<TextOverflow> {
        // Keywords
        val Clip get() = TextOverflow("clip")
        val Ellipsis get() = TextOverflow("ellipsis")
    }
}

fun StyleScope.textOverflow(textOverflow: TextOverflow) {
    property("text-overflow", textOverflow)
}


// See: https://developer.mozilla.org/en-US/docs/Web/CSS/text-shadow
class TextShadow private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object: CssGlobalValues<TextShadow>
}

class CSSTextShadow(
    val offsetX: CSSLengthNumericValue,
    val offsetY: CSSLengthNumericValue,
    val blurRadius: CSSLengthNumericValue? = null,
    val color: CSSColorValue? = null
) : StylePropertyValue {
    override fun toString() = buildString {
        append(offsetX)
        append(" ")
        append(offsetY)
        if (blurRadius != null) {
            append(" ")
            append(blurRadius)
        }
        if (color != null) {
            append(" ")
            append(color)
        }
    }
}

fun StyleScope.textShadow(
    offsetX: CSSLengthNumericValue,
    offsetY: CSSLengthNumericValue,
    blurRadius: CSSLengthNumericValue? = null,
    color: CSSColorValue? = null
) {
    textShadow(CSSTextShadow(offsetX, offsetY, blurRadius, color))
}

fun StyleScope.textShadow(vararg textShadows: CSSTextShadow) {
    property("text-shadow", textShadows.joinToString())
}

fun StyleScope.textShadow(textShadow: TextShadow) {
    property("text-shadow", textShadow)
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/text-transform
class TextTransform private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object: CssGlobalValues<TextTransform> {
        // Keywords
        val None get() = TextTransform("none")
        val Capitalize get() = TextTransform("capitalize")
        val Uppercase get() = TextTransform("uppercase")
        val Lowercase get() = TextTransform("lowercase")
    }
}

fun StyleScope.textTransform(textTransform: TextTransform) {
    property("text-transform", textTransform)
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/user-select
class UserSelect private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object: CssGlobalValues<UserSelect> {
        // Keyword
        val None get() = UserSelect("none")
        val Auto get() = UserSelect("auto")
        val Text get() = UserSelect("text")
        val Contain get() = UserSelect("contain")
        val All get() = UserSelect("all")
    }
}

fun StyleScope.userSelect(userSelect: UserSelect) {
    property("user-select", userSelect)
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/white-space
class WhiteSpace private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object: CssGlobalValues<WhiteSpace> {
        val Normal get() = WhiteSpace("normal");
        val NoWrap get() = WhiteSpace("nowrap");
        val Pre get() = WhiteSpace("pre");
        val PreWrap get() = WhiteSpace("pre-wrap");
        val PreLine get() = WhiteSpace("pre-line");
        val BreakSpaces get() = WhiteSpace("break-spaces")
    }
}

fun StyleScope.whiteSpace(whiteSpace: WhiteSpace) {
    property("white-space", whiteSpace)
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/word-break
class WordBreak private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object: CssGlobalValues<WordBreak> {
        val Normal get() = WordBreak("normal");
        val BreakAll get() = WordBreak("break-all");
        val KeepAll get() = WordBreak("keep-all");
        // BreakWord is intentionally not supported as it has been deprecated:
        // https://developer.mozilla.org/en-US/docs/Web/CSS/word-break#values
        // Instead, use `overflow-wrap: break-word` or `overflow-wrap: break-anywhere`,
        // or possibly `word-break: break-all`
        // val BreakWord get() = WordBreak("break-word");
    }
}

fun StyleScope.wordBreak(wordBreak: WordBreak) {
    property("word-break", wordBreak)
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/writing-mode
class WritingMode private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object: CssGlobalValues<WritingMode> {
        // Keyword
        val HorizontalTb get() = WritingMode("horizontal-tb")
        val VerticalRl get() = WritingMode("vertical-rl")
        val VerticalLr get() = WritingMode("vertical-lr")
    }
}

fun StyleScope.writingMode(writingMode: WritingMode) {
    property("writing-mode", writingMode)
}
