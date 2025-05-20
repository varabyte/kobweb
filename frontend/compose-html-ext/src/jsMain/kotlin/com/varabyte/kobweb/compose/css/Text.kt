package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/hyphenate-character
sealed interface HyphenateCharacter: StylePropertyValue {
    companion object {
        val Auto get() = "auto".unsafeCast<HyphenateCharacter>()

        fun of(value:String) = buildString {
            append("\"")
            append(value)
            append("\"")
        }.unsafeCast<HyphenateCharacter>()
    }
}

fun StyleScope.hyphenateCharacter(hyphenateCharacter: HyphenateCharacter) {
    property("hyphenate-character", hyphenateCharacter)
}
// https://developer.mozilla.org/en-US/docs/Web/CSS/ruby-position
sealed interface RubyPosition : StylePropertyValue {
    companion object : CssGlobalValues<RubyPosition> {
        // Keyword values
        val Over get() = "over".unsafeCast<RubyPosition>()
        val Under get() = "under".unsafeCast<RubyPosition>()
    }
}

fun StyleScope.rubyPosition(rubyPosition: RubyPosition) {
    property("ruby-position", rubyPosition)
}

// https://developer.mozilla.org/en-US/docs/Web/CSS/text-align
sealed interface TextAlign : StylePropertyValue {
    companion object : CssGlobalValues<TextAlign> {
        val Left get() = "left".unsafeCast<TextAlign>()
        val Right get() = "right".unsafeCast<TextAlign>()
        val Center get() = "center".unsafeCast<TextAlign>()
        val Justify get() = "justify".unsafeCast<TextAlign>()
        val JustifyAll get() = "justify-all".unsafeCast<TextAlign>()
        val Start get() = "start".unsafeCast<TextAlign>()
        val End get() = "end".unsafeCast<TextAlign>()
        val MatchParent get() = "match-parent".unsafeCast<TextAlign>()
    }
}

fun StyleScope.textAlign(textAlign: TextAlign) {
    property("text-align", textAlign)
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/text-align-last
sealed interface TextAlignLast : StylePropertyValue {
    companion object : CssGlobalValues<TextAlignLast> {
        val Auto get() = "auto".unsafeCast<TextAlignLast>()
        val Start get() = "start".unsafeCast<TextAlignLast>()
        val End get() = "end".unsafeCast<TextAlignLast>()
        val Left get() = "left".unsafeCast<TextAlignLast>()
        val Right get() = "right".unsafeCast<TextAlignLast>()
        val Center get() = "center".unsafeCast<TextAlignLast>()
        val Justify get() = "justify".unsafeCast<TextAlignLast>()
    }
}

fun StyleScope.textAlignLast(textAlignLast: TextAlignLast) {
    property("text-align-last", textAlignLast)
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/text-combine-upright
sealed interface TextCombineUpright : StylePropertyValue {
    companion object : CssGlobalValues<TextCombineUpright> {
        val None get() = "none".unsafeCast<TextCombineUpright>()
        val All get() = "all".unsafeCast<TextCombineUpright>()
    }
}

fun StyleScope.textCombineUpright(textCombineUpright: TextCombineUpright) {
    property("text-combine-upright", textCombineUpright)
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/text-decoration-line
sealed interface TextDecorationLine : StylePropertyValue {
    companion object : CssGlobalValues<TextDecorationLine> {
        val Underline get() = "underline".unsafeCast<TextDecorationLine>()
        val Overline get() = "overline".unsafeCast<TextDecorationLine>()
        val LineThrough get() = "line-through".unsafeCast<TextDecorationLine>()
        val None get() = "none".unsafeCast<TextDecorationLine>()
    }
}

fun StyleScope.textDecorationLine(vararg textDecorationLines: TextDecorationLine) {
    if (textDecorationLines.isNotEmpty()) {
        property("text-decoration-line", textDecorationLines.joinToString(" "))
    }
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/text-decoration-skip-ink
sealed interface TextDecorationSkipInk: StylePropertyValue {

    companion object: CssGlobalValues<TextDecorationSkipInk> {

        val Auto get() = "auto".unsafeCast<TextDecorationSkipInk>()
        val None get() = "none".unsafeCast<TextDecorationSkipInk>()
    }
}

fun StyleScope.textDecorationSkipInk(textDecorationSkipInk: TextDecorationSkipInk) {
    property("text-decoration-skip-ink", textDecorationSkipInk)
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/text-emphasis-position
sealed interface TextEmphasisPosition: StylePropertyValue {

    sealed interface Listable: TextEmphasisPosition

    companion object: CssGlobalValues<TextEmphasisPosition> {

        fun of(value: String) = value.unsafeCast<Listable>()

        fun list(vararg values: Listable) = values.joinToString(" ").unsafeCast<TextEmphasisPosition>()
    }
}

fun StyleScope.textEmphasisPosition(textEmphasisPosition: TextEmphasisPosition) {
    property("text-emphasis-position", textEmphasisPosition)
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/text-orientation
sealed interface TextOrientation: StylePropertyValue {
    companion object: CssGlobalValues<TextOrientation> {
        val Mixed get() = "mixed".unsafeCast<TextOrientation>()
        val Upright get() = "upright".unsafeCast<TextOrientation>()
        val SidewaysRight get() = "sideways-right".unsafeCast<TextOrientation>()
        val Sideways get() = "sideways".unsafeCast<TextOrientation>()
        val UseGlyphOrientation get() = "use-glyph-orientation".unsafeCast<TextOrientation>()
    }
}

fun StyleScope.textOrientation(textOrientation: TextOrientation) {
    property("text-orientation", textOrientation)
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/text-overflow
sealed interface TextOverflow : StylePropertyValue {
    companion object : CssGlobalValues<TextOverflow> {
        // Keywords
        val Clip get() = "clip".unsafeCast<TextOverflow>()
        val Ellipsis get() = "ellipsis".unsafeCast<TextOverflow>()
    }
}

fun StyleScope.textOverflow(textOverflow: TextOverflow) {
    property("text-overflow", textOverflow)
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/text-rendering
sealed interface TextRendering: StylePropertyValue {

    companion object: CssGlobalValues<TextRendering> {

        val Auto get() = "auto".unsafeCast<TextRendering>()
        val OptimizeSpeed get() = "optimizeSpeed".unsafeCast<TextRendering>()
        val OptimizeLegibility get() = "optimizeLegibility".unsafeCast<TextRendering>()
        val GeometricPrecision get() = "geometricPrecision".unsafeCast<TextRendering>()
    }
}

fun StyleScope.textRendering(textRendering: TextRendering) {
    property("text-rendering", textRendering)
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/text-shadow
sealed interface TextShadow : StylePropertyValue {
    sealed interface Listable : TextShadow

    companion object : CssGlobalValues<TextShadow> {
        fun of(
            offsetX: CSSLengthNumericValue,
            offsetY: CSSLengthNumericValue,
            blurRadius: CSSLengthNumericValue? = null,
            color: CSSColorValue? = null,
        ) = buildString {
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
        }.unsafeCast<Listable>()

        fun list(vararg shadows: Listable) = shadows.joinToString().unsafeCast<TextShadow>()
    }
}

@Deprecated("Use `textShadow(TextShadow.of(...))` instead", ReplaceWith("textShadow(TextShadow.of(offsetX, offsetY, blurRadius, color))"))
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

fun StyleScope.textShadow(textShadow: TextShadow) {
    property("text-shadow", textShadow)
}

@Deprecated("Use `textShadow(TextShadow.of(...))` instead", ReplaceWith("textShadow(TextShadow.of(offsetX, offsetY, blurRadius, color))"))
fun StyleScope.textShadow(
    offsetX: CSSLengthNumericValue,
    offsetY: CSSLengthNumericValue,
    blurRadius: CSSLengthNumericValue? = null,
    color: CSSColorValue? = null
) {
    textShadow(TextShadow.of(offsetX, offsetY, blurRadius, color))
}

// Needed temporarily until we can remove the deprecated `vararg` version
fun StyleScope.textShadow(textShadow: TextShadow.Listable) {
    // Don't cast with "as", that breaks due to our internal unsafeCasting approach
    val textShadow: TextShadow = textShadow
    textShadow(textShadow)
}
// Remove the previous method too after removing this method
@Deprecated("Use `textShadow(TextShadow.list(...))` instead", ReplaceWith("textShadow(TextShadow.list(*shadows))"))
fun StyleScope.textShadow(vararg shadows: TextShadow.Listable) {
    textShadow(TextShadow.list(*shadows))
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/text-transform
sealed interface TextTransform : StylePropertyValue {
    companion object : CssGlobalValues<TextTransform> {
        // Keywords
        val None get() = "none".unsafeCast<TextTransform>()
        val Capitalize get() = "capitalize".unsafeCast<TextTransform>()
        val Uppercase get() = "uppercase".unsafeCast<TextTransform>()
        val Lowercase get() = "lowercase".unsafeCast<TextTransform>()
    }
}

fun StyleScope.textTransform(textTransform: TextTransform) {
    property("text-transform", textTransform)
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/text-underline-offset
sealed interface TextUnderlineOffset: StylePropertyValue {

    companion object: CssGlobalValues<TextUnderlineOffset> {

        val Auto get() = "auto".unsafeCast<TextUnderlineOffset>()

        fun of(value: CSSLengthOrPercentageNumericValue) = value.unsafeCast<TextUnderlineOffset>()
    }
}

fun StyleScope.textUnderlineOffset(textUnderlineOffset: TextUnderlineOffset) {
    property("text-underline-offset", textUnderlineOffset)
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/text-underline-position
sealed interface TextUnderlinePosition: StylePropertyValue {

    sealed interface Listable: TextUnderlinePosition

    companion object: CssGlobalValues<TextUnderlinePosition> {

        val Auto get() = "auto".unsafeCast<Listable>()
        val Under get() = "under".unsafeCast<Listable>()
        val Left get() = "left".unsafeCast<Listable>()
        val Right get() = "right".unsafeCast<Listable>()

        fun list(vararg values: Listable) = values.joinToString(" ").unsafeCast<TextUnderlinePosition>()
    }
}

fun StyleScope.textUnderlinePosition(textUnderlinePosition: TextUnderlinePosition) {
    property("text-underline-position", textUnderlinePosition)
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/user-select
sealed interface UserSelect : StylePropertyValue {
    companion object : CssGlobalValues<UserSelect> {
        // Keyword
        val None get() = "none".unsafeCast<UserSelect>()
        val Auto get() = "auto".unsafeCast<UserSelect>()
        val Text get() = "text".unsafeCast<UserSelect>()
        val Contain get() = "contain".unsafeCast<UserSelect>()
        val All get() = "all".unsafeCast<UserSelect>()
    }
}

fun StyleScope.userSelect(userSelect: UserSelect) {
    property("user-select", userSelect)
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/white-space
sealed interface WhiteSpace : StylePropertyValue {
    companion object : CssGlobalValues<WhiteSpace> {
        val Normal get() = "normal".unsafeCast<WhiteSpace>()
        val NoWrap get() = "nowrap".unsafeCast<WhiteSpace>()
        val Pre get() = "pre".unsafeCast<WhiteSpace>()
        val PreWrap get() = "pre-wrap".unsafeCast<WhiteSpace>()
        val PreLine get() = "pre-line".unsafeCast<WhiteSpace>()
        val BreakSpaces get() = "break-spaces".unsafeCast<WhiteSpace>()
    }
}

fun StyleScope.whiteSpace(whiteSpace: WhiteSpace) {
    property("white-space", whiteSpace)
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/word-break
sealed interface WordBreak : StylePropertyValue {
    companion object : CssGlobalValues<WordBreak> {
        val Normal get() = "normal".unsafeCast<WordBreak>()
        val BreakAll get() = "break-all".unsafeCast<WordBreak>()
        val KeepAll get() = "keep-all".unsafeCast<WordBreak>()
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

// https://developer.mozilla.org/en-US/docs/Web/CSS/word-spacing
sealed interface WordSpacing : StylePropertyValue {
    companion object : CssGlobalValues<WordSpacing> {
        // Keyword value
        val Normal get() = "normal".unsafeCast<WordSpacing>()

        // <length> values
        fun of(value: CSSLengthNumericValue) = "$value".unsafeCast<WordSpacing>()
    }
}

fun StyleScope.wordSpacing(wordSpacing: WordSpacing) {
    property("word-spacing", wordSpacing)
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/writing-mode
sealed interface WritingMode : StylePropertyValue {
    companion object : CssGlobalValues<WritingMode> {
        // Keyword
        val HorizontalTb get() = "horizontal-tb".unsafeCast<WritingMode>()
        val VerticalRl get() = "vertical-rl".unsafeCast<WritingMode>()
        val VerticalLr get() = "vertical-lr".unsafeCast<WritingMode>()
    }
}

fun StyleScope.writingMode(writingMode: WritingMode) {
    property("writing-mode", writingMode)
}
