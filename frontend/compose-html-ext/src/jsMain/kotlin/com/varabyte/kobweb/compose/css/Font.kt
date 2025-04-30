// Sealed class private constructors are useful, actually!
@file:Suppress("RedundantVisibilityModifier")

package com.varabyte.kobweb.compose.css

import com.varabyte.kobweb.browser.util.wrapQuotesIfNecessary
import org.jetbrains.compose.web.css.*

sealed interface FontOpticalSizing : StylePropertyValue {
    companion object : CssGlobalValues<FontOpticalSizing> {
        val Auto get() = "auto".unsafeCast<FontOpticalSizing>()
        val None get() = "none".unsafeCast<FontOpticalSizing>()
    }
}

fun StyleScope.fontOpticalSizing(sizing: FontOpticalSizing) {
    property("font-optical-sizing", sizing)
}

// https://developer.mozilla.org/en-US/docs/Web/CSS/font-style
sealed interface FontStyle : StylePropertyValue {
    companion object : CssGlobalValues<FontStyle> {
        // Keyword
        val Normal get() = "normal".unsafeCast<FontStyle>()
        val Italic get() = "italic".unsafeCast<FontStyle>()
        val Oblique get() = "oblique".unsafeCast<FontStyle>()

        fun Oblique(angle: CSSAngleNumericValue) = "oblique $angle".unsafeCast<FontStyle>()
    }
}

fun StyleScope.fontStyle(style: FontStyle) {
    property("font-style", style)
}

// region FontVariant

// https://developer.mozilla.org/en-US/docs/Web/CSS/font-variant-alternates
@Suppress("FunctionName") // Functional notation values intentionally named
sealed interface FontVariantAlternates : StylePropertyValue {
    sealed interface Listable : FontVariantAlternates

    companion object : CssGlobalValues<FontVariantAlternates> {
        // Keyword
        val Normal: FontVariantAlternates get() = "normal".unsafeCast<FontVariantAlternates>()
        val HistoricalForms: Listable get() = "historical-forms".unsafeCast<Listable>()

        // Functional notation values
        fun Stylistic(ident: String): Listable = "stylistic($ident)".unsafeCast<Listable>()
        fun Styleset(ident: String): Listable = "styleset($ident)".unsafeCast<Listable>()
        fun CharacterVariant(ident: String): Listable = "character-variant($ident)".unsafeCast<Listable>()
        fun Swash(ident: String): Listable = "swash($ident)".unsafeCast<Listable>()
        fun Ornaments(ident: String): Listable = "ornaments($ident)".unsafeCast<Listable>()
        fun Annotation(ident: String): Listable = "annotation($ident)".unsafeCast<Listable>()

        fun list(vararg values: Listable): FontVariantAlternates = values.toList().joinToString(" ").unsafeCast<FontVariantAlternates>()
    }
}

fun StyleScope.fontVariantAlternates(fontVariantAlternates: FontVariantAlternates) {
    property("font-variant-alternates", fontVariantAlternates)
}

// https://developer.mozilla.org/en-US/docs/Web/CSS/font-variant-caps
sealed interface FontVariantCaps : StylePropertyValue {
    companion object : CssGlobalValues<FontVariantCaps> {
        // Keyword
        val Normal get() = "normal".unsafeCast<FontVariantCaps>()
        val SmallCaps get() = "small-caps".unsafeCast<FontVariantCaps>()
        val AllSmallCaps get() = "all-small-caps".unsafeCast<FontVariantCaps>()
        val PetiteCaps get() = "petite-caps".unsafeCast<FontVariantCaps>()
        val AllPetiteCaps get() = "all-petite-caps".unsafeCast<FontVariantCaps>()
        val Unicase get() = "unicase".unsafeCast<FontVariantCaps>()
        val TitlingCaps get() = "titling-caps".unsafeCast<FontVariantCaps>()
    }
}

fun StyleScope.fontVariantCaps(caps: FontVariantCaps) {
    property("font-variant-caps", caps)
}

// https://developer.mozilla.org/en-US/docs/Web/CSS/font-variant-east-asian
sealed interface FontVariantEastAsian : StylePropertyValue {
    sealed interface Listable : FontVariantEastAsian

    companion object : CssGlobalValues<FontVariantEastAsian> {
        // Keyword
        val Normal: FontVariantEastAsian get() = "normal".unsafeCast<FontVariantEastAsian>()

        // Ruby
        val Ruby get() = "ruby".unsafeCast<Listable>()

        // East Asian variants
        val Jis78 get() = "jis78".unsafeCast<Listable>()
        val Jis83 get() = "jis83".unsafeCast<Listable>()
        val Jis90 get() = "jis90".unsafeCast<Listable>()
        val Jis04 get() = "jis04".unsafeCast<Listable>()
        val Simplified get() = "simplified".unsafeCast<Listable>()
        val Traditional get() = "traditional".unsafeCast<Listable>()

        // East Asian widths
        val FullWidth get() = "full-width".unsafeCast<Listable>()
        val ProportionalWidth get() = "proportional-width".unsafeCast<Listable>()

        fun list(vararg values: Listable): FontVariantEastAsian = values.toList().joinToString(" ").unsafeCast<FontVariantEastAsian>()
    }
}

fun StyleScope.fontVariantEastAsian(eastAsian: FontVariantEastAsian) {
    property("font-variant-east-asian", eastAsian)
}

// https://developer.mozilla.org/en-US/docs/Web/CSS/font-variant-emoji
sealed interface FontVariantEmoji : StylePropertyValue {
    companion object : CssGlobalValues<FontVariantEmoji> {
        // Keyword
        val Normal get() = "normal".unsafeCast<FontVariantEmoji>()
        val Text get() = "text".unsafeCast<FontVariantEmoji>()
        val Emoji get() = "emoji".unsafeCast<FontVariantEmoji>()
        val Unicode get() = "unicode".unsafeCast<FontVariantEmoji>()
    }
}

fun StyleScope.fontVariantEmoji(emoji: FontVariantEmoji) {
    property("font-variant-emoji", emoji)
}

// https://developer.mozilla.org/en-US/docs/Web/CSS/font-variant-ligatures
sealed interface FontVariantLigatures : StylePropertyValue {
    sealed interface Listable : FontVariantLigatures

    companion object : CssGlobalValues<FontVariantLigatures> {
        // Keyword
        val Normal get() = "normal".unsafeCast<FontVariantLigatures>()
        val None get() = "none".unsafeCast<FontVariantLigatures>()

        // Common ligature values
        val CommonLigatures get() = "common-ligatures".unsafeCast<Listable>()
        val NoCommonLigatures get() = "no-common-ligatures".unsafeCast<Listable>()

        // Discretionary ligature values
        val DiscretionaryLigatures get() = "discretionary-ligatures".unsafeCast<Listable>()
        val NoDiscretionaryLigatures get() = "no-discretionary-ligatures".unsafeCast<Listable>()

        // Historical ligature values
        val HistoricalLigatures get() = "historical-ligatures".unsafeCast<Listable>()
        val NoHistoricalLigatures get() = "no-historical-ligatures".unsafeCast<Listable>()

        // Contextual ligature values
        val Contextual get() = "contextual".unsafeCast<Listable>()
        val NoContextual get() = "no-contextual".unsafeCast<Listable>()

        fun list(vararg values: Listable) = values.toList().joinToString(" ").unsafeCast<FontVariantLigatures>()

        fun of(
            common: Boolean? = null,
            discretionary: Boolean? = null,
            historical: Boolean? = null,
            contextual: Boolean? = null
        ) = list(
            *buildList {
                if (common != null) {
                    add(if (common) CommonLigatures else NoCommonLigatures)
                }
                if (discretionary != null) {
                    add(if (discretionary) DiscretionaryLigatures else NoDiscretionaryLigatures)
                }
                if (historical != null) {
                    add(if (historical) HistoricalLigatures else NoHistoricalLigatures)
                }
                if (contextual != null) {
                    add(if (contextual) Contextual else NoContextual)
                }
            }.toTypedArray()
        )
    }
}

fun StyleScope.fontVariantLigatures(ligatures: FontVariantLigatures) {
    property("font-variant-ligatures", ligatures)
}

// https://developer.mozilla.org/en-US/docs/Web/CSS/font-variant-numeric
sealed interface FontVariantNumeric : StylePropertyValue {
    sealed interface Listable : FontVariantNumeric

    companion object : CssGlobalValues<FontVariantNumeric> {
        // Keyword
        val Normal: FontVariantNumeric get() = "normal".unsafeCast<FontVariantNumeric>()
        val Ordinal get() = "ordinal".unsafeCast<Listable>()
        val SlashedZero get() = "slashed-zero".unsafeCast<Listable>()

        // Numeric figure
        val LiningNums get() = "lining-nums".unsafeCast<Listable>()
        val OldstyleNums get() = "oldstyle-nums".unsafeCast<Listable>()

        // Numeric spacing
        val ProportionalNums get() = "proportional-nums".unsafeCast<Listable>()
        val TabularNums get() = "tabular-nums".unsafeCast<Listable>()

        // Numeric fractions
        val DiagonalFractions get() = "diagonal-fractions".unsafeCast<Listable>()
        val StackedFractions get() = "stacked-fractions".unsafeCast<Listable>()

        fun list(vararg keywords: Listable): FontVariantNumeric = keywords.toList().joinToString(" ").unsafeCast<FontVariantNumeric>()
    }
}

fun StyleScope.fontVariantNumeric(numeric: FontVariantNumeric) {
    property("font-variant-numeric", numeric)
}

// https://developer.mozilla.org/en-US/docs/Web/CSS/font-variant-position
sealed interface FontVariantPosition : StylePropertyValue {
    companion object : CssGlobalValues<FontVariantPosition> {
        // Keyword
        val Normal get() = "normal".unsafeCast<FontVariantPosition>()
        val Sub get() = "sub".unsafeCast<FontVariantPosition>()
        val Super get() = "super".unsafeCast<FontVariantPosition>()
    }
}

fun StyleScope.fontVariantPosition(position: FontVariantPosition) {
    property("font-variant-position", position)
}

fun StyleScope.fontVariant(
    alternates: FontVariantAlternates? = null,
    caps: FontVariantCaps? = null,
    eastAsian: FontVariantEastAsian? = null,
    emoji: FontVariantEmoji? = null,
    ligatures: FontVariantLigatures? = null,
    numeric: FontVariantNumeric? = null,
    position: FontVariantPosition? = null,
) {
    property(
        "font-variant", buildList {
            alternates?.let { add(it) }
            caps?.let { add(it) }
            eastAsian?.let { add(it) }
            emoji?.let { add(it) }
            ligatures?.let { add(it) }
            numeric?.let { add(it) }
            position?.let { add(it) }
        }.joinToString(" ")
    )
}

// endregion FontVariant

// https://developer.mozilla.org/en-US/docs/Web/CSS/font-variation-settings
sealed interface FontVariationSettings : StylePropertyValue {
    sealed interface Axis : FontVariationSettings

    companion object : CssGlobalValues<FontVariationSettings> {
        // Keyword
        val Normal: FontVariationSettings get() = "normal".unsafeCast<FontVariationSettings>()
        // We intentionally do not include convenience functions for registered axes, as it is preferred to use
        // the corresponding higher-level properties instead. (e.g. font-weight instead of wght)
        // From https://drafts.csswg.org/css-fonts/#font-variation-settings-def:
        // "When possible, authors should generally use the other properties related to font variations"
        fun Axis(name: String, value: Number) = "${name.wrapQuotesIfNecessary()} $value".unsafeCast<Axis>()
        fun Axes(vararg axes: Axis): FontVariationSettings = axes.joinToString().unsafeCast<FontVariationSettings>()
    }
}

fun StyleScope.fontVariationSettings(settings: FontVariationSettings) {
    property("font-variation-settings", settings)
}

// https://developer.mozilla.org/en-US/docs/Web/CSS/font-weight
sealed interface FontWeight : CSSStyleValue {
    companion object : CssGlobalValues<FontWeight> {
        // Common value constants
        // https://developer.mozilla.org/en-US/docs/Web/CSS/font-weight#common_weight_name_mapping
        val Thin get() = "100".unsafeCast<FontWeight>()
        val ExtraLight get() = "200".unsafeCast<FontWeight>()
        val Light get() = "300".unsafeCast<FontWeight>()

        // val Normal get() = "400") // Same as "Normal" keyword.unsafeCast<FontWeight>()
        val Medium get() = "500".unsafeCast<FontWeight>()
        val SemiBold get() = "600".unsafeCast<FontWeight>()

        // val Bold get() = "700") // Same as "Bold" keyword.unsafeCast<FontWeight>()
        val ExtraBold get() = "800".unsafeCast<FontWeight>()
        val Black get() = "900".unsafeCast<FontWeight>()
        val ExtraBlack get() = "950".unsafeCast<FontWeight>()

        // Keyword
        val Normal get() = "normal".unsafeCast<FontWeight>()
        val Bold get() = "bold".unsafeCast<FontWeight>()

        // Relative
        val Lighter get() = "lighter".unsafeCast<FontWeight>()
        val Bolder get() = "bolder".unsafeCast<FontWeight>()
    }
}

fun StyleScope.fontWeight(weight: FontWeight) {
    property("font-weight", weight)
}

// https://developer.mozilla.org/en-US/docs/Web/CSS/font-size
sealed interface FontSize : CSSStyleValue {
    companion object : CssGlobalValues<FontSize> {
        // Absolute keywords
        val XXSmall get() = "xx-small".unsafeCast<FontSize>()
        val XSmall get() = "x-small".unsafeCast<FontSize>()
        val Small get() = "small".unsafeCast<FontSize>()
        val Medium get() = "medium".unsafeCast<FontSize>()
        val Large get() = "large".unsafeCast<FontSize>()
        val XLarge get() = "x-large".unsafeCast<FontSize>()
        val XXLarge get() = "xx-large".unsafeCast<FontSize>()

        // Relative keywords
        val Smaller get() = "smaller".unsafeCast<FontSize>()
        val Larger get() = "larger".unsafeCast<FontSize>()
    }
}

fun StyleScope.fontSize(size: FontSize) {
    property("font-size", size)
}
