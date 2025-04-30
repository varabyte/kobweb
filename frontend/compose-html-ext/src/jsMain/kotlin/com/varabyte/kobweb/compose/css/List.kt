package com.varabyte.kobweb.compose.css

import com.varabyte.kobweb.browser.util.wrapQuotesIfNecessary
import com.varabyte.kobweb.compose.css.functions.CSSImage
import org.jetbrains.compose.web.css.*

// https://developer.mozilla.org/en-US/docs/Web/CSS/list-style-image
typealias ListStyleImage = CSSImage

// https://developer.mozilla.org/en-US/docs/Web/CSS/list-style-type
class ListStyleType private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object : CssGlobalValues<ListStyleType> {
        fun of(text: String) = ListStyleType(text.wrapQuotesIfNecessary())

        // Pre-defined types supported across all browsers (https://developer.mozilla.org/en-US/docs/Web/CSS/list-style-type#browser_compatibility)
        val ArabicIndic get() = ListStyleType("arabic-indic")
        val Armenian get() = ListStyleType("armenian")
        val Bengali get() = ListStyleType("bengali")
        val Cambodian get() = ListStyleType("cambodian")
        val Circle get() = ListStyleType("circle")
        val CjkDecimal get() = ListStyleType("cjk-decimal")
        val CjkEarthlyBranch get() = ListStyleType("cjk-earthly-branch")
        val CjkHeavenlyStem get() = ListStyleType("cjk-heavenly-stem")
        val CjkIdeographic get() = ListStyleType("cjk-ideographic")
        val Decimal get() = ListStyleType("decimal")
        val DecimalLeadingZero get() = ListStyleType("decimal-leading-zero")
        val Devanagari get() = ListStyleType("devanagari")
        val Disc get() = ListStyleType("disc")
        val DisclosureClosed get() = ListStyleType("disclosure-closed")
        val DisclosureOpen get() = ListStyleType("disclosure-open")
        val EthiopicNumeric get() = ListStyleType("ethiopic-numeric")
        val Georgian get() = ListStyleType("georgian")
        val Gujarati get() = ListStyleType("gujarati")
        val Gurmukhi get() = ListStyleType("gurmukhi")
        val Hebrew get() = ListStyleType("hebrew")
        val Hiragana get() = ListStyleType("hiragana")
        val HiraganaIroha get() = ListStyleType("hiragana-iroha")
        val JapaneseFormal get() = ListStyleType("japanese-formal")
        val JapaneseInformal get() = ListStyleType("japanese-informal")
        val Kannada get() = ListStyleType("kannada")
        val Katakana get() = ListStyleType("katakana")
        val KatakanaIroha get() = ListStyleType("katakana-iroha")
        val Khmer get() = ListStyleType("khmer")
        val KoreanHangulFormal get() = ListStyleType("korean-hangul-formal")
        val KoreanHanjaFormal get() = ListStyleType("korean-hanja-formal")
        val KoreanHanjaInformal get() = ListStyleType("korean-hanja-informal")
        val Lao get() = ListStyleType("lao")
        val LowerAlpha get() = ListStyleType("lower-alpha")
        val LowerArmenian get() = ListStyleType("lower-armenian")
        val LowerGreek get() = ListStyleType("lower-greek")
        val LowerLatin get() = ListStyleType("lower-latin")
        val LowerRoman get() = ListStyleType("lower-roman")
        val Malayalam get() = ListStyleType("malayalam")
        val Mongolian get() = ListStyleType("mongolian")
        val Myanmar get() = ListStyleType("myanmar")
        val Oriya get() = ListStyleType("oriya")
        val Persian get() = ListStyleType("persian")
        val SimpChineseFormal get() = ListStyleType("simp-chinese-formal")
        val SimpChineseInformal get() = ListStyleType("simp-chinese-informal")
        val Square get() = ListStyleType("square")
        val Tamil get() = ListStyleType("tamil")
        val Telugu get() = ListStyleType("telugu")
        val Thai get() = ListStyleType("thai")
        val Tibetan get() = ListStyleType("tibetan")
        val TradChineseFormal get() = ListStyleType("trad-chinese-formal")
        val TradChineseInformal get() = ListStyleType("trad-chinese-informal")
        val UpperAlpha get() = ListStyleType("upper-alpha")
        val UpperArmenian get() = ListStyleType("upper-armenian")
        val UpperLatin get() = ListStyleType("upper-latin")
        val UpperRoman get() = ListStyleType("upper-roman")

        // Keyword
        val None get() = ListStyleType("none")
    }
}

// https://developer.mozilla.org/en-US/docs/Web/CSS/list-style-position
class ListStylePosition private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object : CssGlobalValues<ListStylePosition> {
        // Keywords
        val Inside get() = ListStylePosition("inside")
        val Outside get() = ListStylePosition("outside")
    }
}

// https://developer.mozilla.org/en-US/docs/Web/CSS/list-style
class ListStyle private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object : CssGlobalValues<ListStyle> {
        fun of(
            type: ListStyleType? = null,
            position: ListStylePosition? = null,
            image: ListStyleImage? = null,
        ): ListStyle {
            return ListStyle(
                listOfNotNull(
                    type,
                    position,
                    image,
                ).joinToString(" ") { it.toString() }
            )
        }

        // Keyword
        val None get() = ListStyle("none")
    }
}

fun StyleScope.listStyle(listStyle: ListStyle) {
    property("list-style", listStyle)
}
