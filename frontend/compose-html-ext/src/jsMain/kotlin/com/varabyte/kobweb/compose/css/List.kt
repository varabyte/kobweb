package com.varabyte.kobweb.compose.css

import com.varabyte.kobweb.compose.css.functions.CSSImage
import com.varabyte.kobweb.compose.css.functions.CSSUrl
import com.varabyte.kobweb.compose.util.wrapQuotesIfNecessary
import org.jetbrains.compose.web.css.CSSColorValue
import org.jetbrains.compose.web.css.StylePropertyValue
import org.jetbrains.compose.web.css.StyleScope

typealias ListStyleImage = CSSImage

class ListStyleType private constructor(private val value: String): StylePropertyValue {
    override fun toString() = value

    companion object {
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
        val Georgian get() = ListStyleType("georgian")
        val Gujarati get() = ListStyleType("gujarati")
        val Gurumukhi get() = ListStyleType("gurumukhi")
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
        val TradChineseFormal get() = ListStyleType("trad-chinese-formal")
        val TradChineseInformal get() = ListStyleType("trad-chinese-informal")
        val UpperAlpha get() = ListStyleType("upper-alpha")
        val UpperArmenian get() = ListStyleType("upper-armenian")
        val UpperGreek get() = ListStyleType("upper-greek")
        val UpperLatin get() = ListStyleType("upper-latin")
        val UpperRoman get() = ListStyleType("upper-roman")

        // Keyword
        val None get() = ListStyleType("none")

        // Global values
        val Inherit get() = ListStyleType("inherit")
        val Initial get() = ListStyleType("initial")
        val Revert get() = ListStyleType("revert")
        val Unset get() = ListStyleType("unset")
    }
}

class ListStylePosition private constructor(private val value: String): StylePropertyValue {
    override fun toString() = value

    companion object {
        // Keywords
        val Inside get() = ListStylePosition("inside")
        val Outside get() = ListStylePosition("outside")

        // Global values
        val Inherit get() = ListStylePosition("inherit")
        val Initial get() = ListStylePosition("initial")
        val Revert get() = ListStylePosition("revert")
        val Unset get() = ListStylePosition("unset")
    }
}

fun StyleScope.listStyle(type: ListStyleType? = null, position: ListStylePosition? = null, image: ListStyleImage? = null) {
    type?.let { property("list-style-type", it) }
    position?.let { property("list-style-position", it) }
    image?.let { property("list-style-image", it) }
}
