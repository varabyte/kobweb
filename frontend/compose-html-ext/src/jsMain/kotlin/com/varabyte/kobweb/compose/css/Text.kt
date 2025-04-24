package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*

class TextAlign private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object {
        val Left get() = TextAlign("left")
        val Right get() = TextAlign("right")
        val Center get() = TextAlign("center")
        val Justify get() = TextAlign("justify")
        val JustifyAll get() = TextAlign("justify-all")
        val Start get() = TextAlign("start")
        val End get() = TextAlign("end")
        val MatchParent get() = TextAlign("match-parent")

        val Inherit get() = TextAlign("inherit")
        val Initial get() = TextAlign("initial")
        val Revert get() = TextAlign("revert")
        val Unset get() = TextAlign("unset")
    }
}

fun StyleScope.textAlign(textAlign: TextAlign) {
    property("text-align", textAlign)
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/text-decoration-line
class TextDecorationLine private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object {
        val Underline get() = TextDecorationLine("underline")
        val Overline get() = TextDecorationLine("overline")
        val LineThrough get() = TextDecorationLine("line-through")
        val None get() = TextDecorationLine("none")

        val Inherit get() = TextDecorationLine("inherit")
        val Initial get() = TextDecorationLine("initial")
        val Revert get() = TextDecorationLine("revert")
        val Unset get() = TextDecorationLine("unset")
    }
}

fun StyleScope.textDecorationLine(vararg textDecorationLines: TextDecorationLine) {
    property("text-decoration-line", textDecorationLines.joinToString(" "))
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/text-overflow
class TextOverflow private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object {
        // Keywords
        val Clip get() = TextOverflow("clip")
        val Ellipsis get() = TextOverflow("ellipsis")

        // Global values
        val Inherit get() = TextOverflow("inherit")
        val Initial get() = TextOverflow("initial")
        val Revert get() = TextOverflow("revert")
        val Unset get() = TextOverflow("unset")
    }
}

fun StyleScope.textOverflow(textOverflow: TextOverflow) {
    property("text-overflow", textOverflow)
}


// See: https://developer.mozilla.org/en-US/docs/Web/CSS/text-shadow
class TextShadow private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object {
        val Inherit get() = TextShadow("inherit")
        val Initial get() = TextShadow("initial")
        val Revert get() = TextShadow("revert")
        val Unset get() = TextShadow("unset")
    }
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

    companion object {
        // Keywords
        val None get() = TextTransform("none")
        val Capitalize get() = TextTransform("capitalize")
        val Uppercase get() = TextTransform("uppercase")
        val Lowercase get() = TextTransform("lowercase")

        // Globals
        val Inherit get() = TextTransform("inherit")
        val Initial get() = TextTransform("initial")
        val Revert get() = TextTransform("revert")
        val Unset get() = TextTransform("unset")
    }
}

fun StyleScope.textTransform(textTransform: TextTransform) {
    property("text-transform", textTransform)
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/user-select
class UserSelect private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object {
        // Keyword
        val None get() = UserSelect("none")
        val Auto get() = UserSelect("auto")
        val Text get() = UserSelect("text")
        val Contain get() = UserSelect("contain")
        val All get() = UserSelect("all")

        // Global
        val Inherit get() = UserSelect("inherit")
        val Initial get() = UserSelect("initial")
        val Revert get() = UserSelect("revert")
        val Unset get() = UserSelect("unset")
    }
}

fun StyleScope.userSelect(userSelect: UserSelect) {
    property("user-select", userSelect)
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/white-space
class WhiteSpace private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object {
        val Normal get() = WhiteSpace("normal");
        val NoWrap get() = WhiteSpace("nowrap");
        val Pre get() = WhiteSpace("pre");
        val PreWrap get() = WhiteSpace("pre-wrap");
        val PreLine get() = WhiteSpace("pre-line");
        val BreakSpaces get() = WhiteSpace("break-spaces");

        val Inherit get() = WhiteSpace("inherit")
        val Initial get() = WhiteSpace("initial")
        val Revert get() = WhiteSpace("revert")
        val Unset get() = WhiteSpace("unset")
    }
}

fun StyleScope.whiteSpace(whiteSpace: WhiteSpace) {
    property("white-space", whiteSpace)
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/word-break
class WordBreak private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object {
        val Normal get() = WordBreak("normal");
        val BreakAll get() = WordBreak("break-all");
        val KeepAll get() = WordBreak("keep-all");
        // BreakWord is intentionally not supported as it has been deprecated:
        // https://developer.mozilla.org/en-US/docs/Web/CSS/word-break#values
        // Instead, use `overflow-wrap: break-word` or `overflow-wrap: break-anywhere`,
        // or possibly `word-break: break-all`
        // val BreakWord get() = WordBreak("break-word");

        val Inherit get() = WordBreak("inherit")
        val Initial get() = WordBreak("initial")
        val Revert get() = WordBreak("revert")
        val Unset get() = WordBreak("unset")
    }
}

fun StyleScope.wordBreak(wordBreak: WordBreak) {
    property("word-break", wordBreak)
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/writing-mode
class WritingMode private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object {
        // Keyword
        val HorizontalTb get() = WritingMode("horizontal-tb");
        val VerticalRl get() = WritingMode("vertical-rl");
        val VerticalLr get() = WritingMode("vertical-lr");

        // Global
        val Inherit get() = WritingMode("inherit")
        val Initial get() = WritingMode("initial")
        val Revert get() = WritingMode("revert")
        val Unset get() = WritingMode("unset")
    }
}

fun StyleScope.writingMode(writingMode: WritingMode) {
    property("writing-mode", writingMode)
}

//https://developer.mozilla.org/en-US/docs/Web/CSS/ruby-position
class RubyPosition private constructor(private val value: String) : StylePropertyValue {

    override fun toString() = value

    companion object {

        /* Keyword values */
        val Over get() = RubyPosition("over")
        val Under get() = RubyPosition("under")


        /* Global values */
        val Inherit get() = RubyPosition("inherit")
        val Initial get() = RubyPosition("initial")
        val Revert get() = RubyPosition("revert")
        val RevertLayer get() = RubyPosition("revert-layer")
        val Unset get() = RubyPosition("unset")
    }
}

fun StyleScope.rubyPosition(rubyPosition: RubyPosition) {
    property("ruby-position", rubyPosition)
}

// https://developer.mozilla.org/en-US/docs/Web/CSS/word-spacing
class WordSpacing private constructor(private val value: String) : StylePropertyValue {

    companion object {

        /* Keyword value */
        val Normal get() = WordSpacing("normal")

        /* <length> values */
        fun of(value: CSSLengthNumericValue) = WordSpacing("$value")

        /* Global values */
        val Inherit get() = WordSpacing("inherit")
        val Initial get() = WordSpacing("initial")
        val Revert get() = WordSpacing("revert")
        val RevertLayer get() = WordSpacing("revert-layer")
        val Unset get() = WordSpacing("unset")
    }
}

fun StyleScope.wordSpacing(wordSpacing: WordSpacing) {
    property("word-spacing", wordSpacing)
}
