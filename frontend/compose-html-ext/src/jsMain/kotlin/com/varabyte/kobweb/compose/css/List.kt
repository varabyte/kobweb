package com.varabyte.kobweb.compose.css

import com.varabyte.kobweb.browser.util.wrapQuotesIfNecessary
import com.varabyte.kobweb.compose.css.functions.CSSImage
import org.jetbrains.compose.web.css.*

// https://developer.mozilla.org/en-US/docs/Web/CSS/list-style-image
typealias ListStyleImage = CSSImage

// https://developer.mozilla.org/en-US/docs/Web/CSS/list-style-type
sealed interface ListStyleType : StylePropertyValue {
    companion object : CssGlobalValues<ListStyleType> {
        fun of(text: String) = text.wrapQuotesIfNecessary().unsafeCast<ListStyleType>()

        // Pre-defined types supported across all browsers (https://developer.mozilla.org/en-US/docs/Web/CSS/list-style-type#browser_compatibility)
        val ArabicIndic get() = "arabic-indic".unsafeCast<ListStyleType>()
        val Armenian get() = "armenian".unsafeCast<ListStyleType>()
        val Bengali get() = "bengali".unsafeCast<ListStyleType>()
        val Cambodian get() = "cambodian".unsafeCast<ListStyleType>()
        val Circle get() = "circle".unsafeCast<ListStyleType>()
        val CjkDecimal get() = "cjk-decimal".unsafeCast<ListStyleType>()
        val CjkEarthlyBranch get() = "cjk-earthly-branch".unsafeCast<ListStyleType>()
        val CjkHeavenlyStem get() = "cjk-heavenly-stem".unsafeCast<ListStyleType>()
        val CjkIdeographic get() = "cjk-ideographic".unsafeCast<ListStyleType>()
        val Decimal get() = "decimal".unsafeCast<ListStyleType>()
        val DecimalLeadingZero get() = "decimal-leading-zero".unsafeCast<ListStyleType>()
        val Devanagari get() = "devanagari".unsafeCast<ListStyleType>()
        val Disc get() = "disc".unsafeCast<ListStyleType>()
        val DisclosureClosed get() = "disclosure-closed".unsafeCast<ListStyleType>()
        val DisclosureOpen get() = "disclosure-open".unsafeCast<ListStyleType>()
        val EthiopicNumeric get() = "ethiopic-numeric".unsafeCast<ListStyleType>()
        val Georgian get() = "georgian".unsafeCast<ListStyleType>()
        val Gujarati get() = "gujarati".unsafeCast<ListStyleType>()
        val Gurmukhi get() = "gurmukhi".unsafeCast<ListStyleType>()
        val Hebrew get() = "hebrew".unsafeCast<ListStyleType>()
        val Hiragana get() = "hiragana".unsafeCast<ListStyleType>()
        val HiraganaIroha get() = "hiragana-iroha".unsafeCast<ListStyleType>()
        val JapaneseFormal get() = "japanese-formal".unsafeCast<ListStyleType>()
        val JapaneseInformal get() = "japanese-informal".unsafeCast<ListStyleType>()
        val Kannada get() = "kannada".unsafeCast<ListStyleType>()
        val Katakana get() = "katakana".unsafeCast<ListStyleType>()
        val KatakanaIroha get() = "katakana-iroha".unsafeCast<ListStyleType>()
        val Khmer get() = "khmer".unsafeCast<ListStyleType>()
        val KoreanHangulFormal get() = "korean-hangul-formal".unsafeCast<ListStyleType>()
        val KoreanHanjaFormal get() = "korean-hanja-formal".unsafeCast<ListStyleType>()
        val KoreanHanjaInformal get() = "korean-hanja-informal".unsafeCast<ListStyleType>()
        val Lao get() = "lao".unsafeCast<ListStyleType>()
        val LowerAlpha get() = "lower-alpha".unsafeCast<ListStyleType>()
        val LowerArmenian get() = "lower-armenian".unsafeCast<ListStyleType>()
        val LowerGreek get() = "lower-greek".unsafeCast<ListStyleType>()
        val LowerLatin get() = "lower-latin".unsafeCast<ListStyleType>()
        val LowerRoman get() = "lower-roman".unsafeCast<ListStyleType>()
        val Malayalam get() = "malayalam".unsafeCast<ListStyleType>()
        val Mongolian get() = "mongolian".unsafeCast<ListStyleType>()
        val Myanmar get() = "myanmar".unsafeCast<ListStyleType>()
        val Oriya get() = "oriya".unsafeCast<ListStyleType>()
        val Persian get() = "persian".unsafeCast<ListStyleType>()
        val SimpChineseFormal get() = "simp-chinese-formal".unsafeCast<ListStyleType>()
        val SimpChineseInformal get() = "simp-chinese-informal".unsafeCast<ListStyleType>()
        val Square get() = "square".unsafeCast<ListStyleType>()
        val Tamil get() = "tamil".unsafeCast<ListStyleType>()
        val Telugu get() = "telugu".unsafeCast<ListStyleType>()
        val Thai get() = "thai".unsafeCast<ListStyleType>()
        val Tibetan get() = "tibetan".unsafeCast<ListStyleType>()
        val TradChineseFormal get() = "trad-chinese-formal".unsafeCast<ListStyleType>()
        val TradChineseInformal get() = "trad-chinese-informal".unsafeCast<ListStyleType>()
        val UpperAlpha get() = "upper-alpha".unsafeCast<ListStyleType>()
        val UpperArmenian get() = "upper-armenian".unsafeCast<ListStyleType>()
        val UpperLatin get() = "upper-latin".unsafeCast<ListStyleType>()
        val UpperRoman get() = "upper-roman".unsafeCast<ListStyleType>()

        // Keyword
        val None get() = "none".unsafeCast<ListStyleType>()
    }
}

// https://developer.mozilla.org/en-US/docs/Web/CSS/list-style-position
sealed interface ListStylePosition : StylePropertyValue {
    companion object : CssGlobalValues<ListStylePosition> {
        // Keywords
        val Inside get() = "inside".unsafeCast<ListStylePosition>()
        val Outside get() = "outside".unsafeCast<ListStylePosition>()
    }
}

// https://developer.mozilla.org/en-US/docs/Web/CSS/list-style
sealed interface ListStyle : StylePropertyValue {
    companion object : CssGlobalValues<ListStyle> {
        fun of(
            type: ListStyleType? = null,
            position: ListStylePosition? = null,
            image: ListStyleImage? = null,
        ) = listOfNotNull(
            type,
            position,
            image,
        ).joinToString(" ") { it.toString() }.unsafeCast<ListStyle>()

        // Keyword
        val None get() = "none".unsafeCast<ListStyle>()
    }
}

fun StyleScope.listStyle(listStyle: ListStyle) {
    property("list-style", listStyle)
}
