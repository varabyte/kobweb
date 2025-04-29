// Sealed class private constructors are useful, actually!
@file:Suppress("RedundantVisibilityModifier")

package com.varabyte.kobweb.compose.css

import com.varabyte.kobweb.browser.util.wrapQuotesIfNecessary
import org.jetbrains.compose.web.css.*

class FontOpticalSizing private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object : CssGlobalValues<FontOpticalSizing> {
        val Auto get() = FontOpticalSizing("auto")
        val None get() = FontOpticalSizing("none")
    }
}

fun StyleScope.fontOpticalSizing(sizing: FontOpticalSizing) {
    property("font-optical-sizing", sizing)
}

// https://developer.mozilla.org/en-US/docs/Web/CSS/font-style
class FontStyle private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object : CssGlobalValues<FontStyle> {
        // Keyword
        val Normal get() = FontStyle("normal")
        val Italic get() = FontStyle("italic")
        val Oblique get() = FontStyle("oblique")

        fun Oblique(angle: CSSAngleNumericValue) = FontStyle("oblique $angle")
    }
}

fun StyleScope.fontStyle(style: FontStyle) {
    property("font-style", style)
}

// region FontVariant

// https://developer.mozilla.org/en-US/docs/Web/CSS/font-variant-alternates
sealed class FontVariantAlternates private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    private class SingleValue(value: String) : FontVariantAlternates(value)
    sealed class Listable(value: String) : FontVariantAlternates(value)
    private class ListableKeyword(value: String) : Listable(value)
    private class FunctionalNotation(name: String, ident: String) : Listable("$name($ident)")
    private class ValueList(values: List<Listable>) : FontVariantAlternates(values.joinToString(" "))

    companion object : CssGlobalValues<FontVariantAlternates> {
        // Keyword
        val Normal: FontVariantAlternates get() = SingleValue("normal")
        val HistoricalForms: Listable get() = ListableKeyword("historical-forms")

        // Functional notation values
        fun Stylistic(ident: String): Listable = FunctionalNotation("stylistic", ident)
        fun Styleset(ident: String): Listable = FunctionalNotation("styleset", ident)
        fun CharacterVariant(ident: String): Listable = FunctionalNotation("character-variant", ident)
        fun Swash(ident: String): Listable = FunctionalNotation("swash", ident)
        fun Ornaments(ident: String): Listable = FunctionalNotation("ornaments", ident)
        fun Annotation(ident: String): Listable = FunctionalNotation("annotation", ident)

        fun list(vararg values: Listable): FontVariantAlternates = ValueList(values.toList())
    }
}

fun StyleScope.fontVariantAlternates(fontVariantAlternates: FontVariantAlternates) {
    property("font-variant-alternates", fontVariantAlternates)
}

// https://developer.mozilla.org/en-US/docs/Web/CSS/font-variant-caps
class FontVariantCaps private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object : CssGlobalValues<FontVariantCaps> {
        // Keyword
        val Normal get() = FontVariantCaps("normal")
        val SmallCaps get() = FontVariantCaps("small-caps")
        val AllSmallCaps get() = FontVariantCaps("all-small-caps")
        val PetiteCaps get() = FontVariantCaps("petite-caps")
        val AllPetiteCaps get() = FontVariantCaps("all-petite-caps")
        val Unicase get() = FontVariantCaps("unicase")
        val TitlingCaps get() = FontVariantCaps("titling-caps")
    }
}

fun StyleScope.fontVariantCaps(caps: FontVariantCaps) {
    property("font-variant-caps", caps)
}

// https://developer.mozilla.org/en-US/docs/Web/CSS/font-variant-east-asian
sealed class FontVariantEastAsian private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    private class SingleValue(value: String) : FontVariantEastAsian(value)
    class Listable internal constructor(value: String) : FontVariantEastAsian(value)
    private class ValueList(vararg values: Listable) : FontVariantEastAsian(values.joinToString(" "))

    companion object : CssGlobalValues<FontVariantEastAsian> {
        // Keyword
        val Normal: FontVariantEastAsian get() = SingleValue("normal")

        // Ruby
        val Ruby get() = Listable("ruby")

        // East Asian variants
        val Jis78 get() = Listable("jis78")
        val Jis83 get() = Listable("jis83")
        val Jis90 get() = Listable("jis90")
        val Jis04 get() = Listable("jis04")
        val Simplified get() = Listable("simplified")
        val Traditional get() = Listable("traditional")

        // East Asian widths
        val FullWidth get() = Listable("full-width")
        val ProportionalWidth get() = Listable("proportional-width")

        fun list(vararg values: Listable): FontVariantEastAsian = ValueList(*values)
    }
}

fun StyleScope.fontVariantEastAsian(eastAsian: FontVariantEastAsian) {
    property("font-variant-east-asian", eastAsian)
}

// https://developer.mozilla.org/en-US/docs/Web/CSS/font-variant-emoji
class FontVariantEmoji private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object : CssGlobalValues<FontVariantEmoji> {
        // Keyword
        val Normal get() = FontVariantEmoji("normal")
        val Text get() = FontVariantEmoji("text")
        val Emoji get() = FontVariantEmoji("emoji")
        val Unicode get() = FontVariantEmoji("unicode")
    }
}

fun StyleScope.fontVariantEmoji(emoji: FontVariantEmoji) {
    property("font-variant-emoji", emoji)
}

// https://developer.mozilla.org/en-US/docs/Web/CSS/font-variant-ligatures
sealed class FontVariantLigatures private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    private class Keyword(value: String) : FontVariantLigatures(value)
    class Listable internal constructor(value: String) : FontVariantLigatures(value)
    private class ValueList(values: List<Listable>) : FontVariantLigatures(values.joinToString(" "))

    companion object : CssGlobalValues<FontVariantLigatures> {
        // Keyword
        val Normal: FontVariantLigatures get() = Keyword("normal")
        val None: FontVariantLigatures get() = Keyword("none")

        // Common ligature values
        val CommonLigatures get() = Listable("common-ligatures")
        val NoCommonLigatures get() = Listable("no-common-ligatures")

        // Discretionary ligature values
        val DiscretionaryLigatures get() = Listable("discretionary-ligatures")
        val NoDiscretionaryLigatures get() = Listable("no-discretionary-ligatures")

        // Historical ligature values
        val HistoricalLigatures get() = Listable("historical-ligatures")
        val NoHistoricalLigatures get() = Listable("no-historical-ligatures")

        // Contextual ligature values
        val Contextual get() = Listable("contextual")
        val NoContextual get() = Listable("no-contextual")

        fun list(vararg values: Listable): FontVariantLigatures = ValueList(values.toList())

        fun of(
            common: Boolean? = null,
            discretionary: Boolean? = null,
            historical: Boolean? = null,
            contextual: Boolean? = null
        ): FontVariantLigatures {
            val values = buildList {
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
            }
            return ValueList(values)
        }
    }
}

fun StyleScope.fontVariantLigatures(ligatures: FontVariantLigatures) {
    property("font-variant-ligatures", ligatures)
}

// https://developer.mozilla.org/en-US/docs/Web/CSS/font-variant-numeric
sealed class FontVariantNumeric private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    private class Keyword(value: String) : FontVariantNumeric(value)
    class Listable internal constructor(value: String) : FontVariantNumeric(value)
    private class ValueList(keywords: List<Listable>) : FontVariantNumeric(keywords.joinToString(" "))

    companion object : CssGlobalValues<FontVariantNumeric> {
        // Keyword
        val Normal: FontVariantNumeric get() = Keyword("normal")
        val Ordinal: Listable get() = Listable("ordinal")
        val SlashedZero: Listable get() = Listable("slashed-zero")

        // Numeric figure
        val LiningNums: Listable get() = Listable("lining-nums")
        val OldstyleNums: Listable get() = Listable("oldstyle-nums")

        // Numeric spacing
        val ProportionalNums: Listable get() = Listable("proportional-nums")
        val TabularNums: Listable get() = Listable("tabular-nums")

        // Numeric fractions
        val DiagonalFractions: Listable get() = Listable("diagonal-fractions")
        val StackedFractions: Listable get() = Listable("stacked-fractions")

        fun list(vararg keywords: Listable): FontVariantNumeric = ValueList(keywords.toList())
    }
}

fun StyleScope.fontVariantNumeric(numeric: FontVariantNumeric) {
    property("font-variant-numeric", numeric)
}

// https://developer.mozilla.org/en-US/docs/Web/CSS/font-variant-position
class FontVariantPosition private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object : CssGlobalValues<FontVariantPosition> {
        // Keyword
        val Normal get() = FontVariantPosition("normal")
        val Sub get() = FontVariantPosition("sub")
        val Super get() = FontVariantPosition("super")
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
sealed class FontVariationSettings private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    private class Keyword(value: String) : FontVariationSettings(value)
    class Axis internal constructor(name: String, value: Number) : FontVariationSettings("${name.wrapQuotesIfNecessary()} $value")
    class Axes internal constructor(vararg axes: Axis) : FontVariationSettings(axes.joinToString())

    companion object : CssGlobalValues<FontVariationSettings> {
        fun of(name: String, value: Number): FontVariationSettings = Axis(name, value)
        fun list(vararg axes: Axis): FontVariationSettings = Axes(*axes)

        // Keyword
        val Normal: FontVariationSettings get() = Keyword("normal")
        // We intentionally do not include convenience functions for registered axes, as it is preferred to use
        // the corresponding higher-level properties instead. (e.g. font-weight instead of wght)
        // From https://drafts.csswg.org/css-fonts/#font-variation-settings-def:
        // "When possible, authors should generally use the other properties related to font variations"
    }
}

fun StyleScope.fontVariationSettings(settings: FontVariationSettings) {
    property("font-variation-settings", settings)
}

// https://developer.mozilla.org/en-US/docs/Web/CSS/font-weight
class FontWeight private constructor(private val value: String) : CSSStyleValue {
    override fun toString() = value

    companion object : CssGlobalValues<FontWeight> {
        // Common value constants
        // https://developer.mozilla.org/en-US/docs/Web/CSS/font-weight#common_weight_name_mapping
        val Thin get() = FontWeight("100")
        val ExtraLight get() = FontWeight("200")
        val Light get() = FontWeight("300")

        // val Normal get() = FontWeight("400") // Same as "Normal" keyword"
        val Medium get() = FontWeight("500")
        val SemiBold get() = FontWeight("600")

        // val Bold get() = FontWeight("700") // Same as "Bold" keyword"
        val ExtraBold get() = FontWeight("800")
        val Black get() = FontWeight("900")
        val ExtraBlack get() = FontWeight("950")

        // Keyword
        val Normal get() = FontWeight("normal")
        val Bold get() = FontWeight("bold")

        // Relative
        val Lighter get() = FontWeight("lighter")
        val Bolder get() = FontWeight("bolder")
    }
}

fun StyleScope.fontWeight(weight: FontWeight) {
    property("font-weight", weight)
}

// https://developer.mozilla.org/en-US/docs/Web/CSS/font-size
class FontSize private constructor(private val value: String) : CSSStyleValue {
    override fun toString() = value

    companion object : CssGlobalValues<FontSize> {
        // Absolute keywords
        val XXSmall get() = FontSize("xx-small")
        val XSmall get() = FontSize("x-small")
        val Small get() = FontSize("small")
        val Medium get() = FontSize("medium")
        val Large get() = FontSize("large")
        val XLarge get() = FontSize("x-large")
        val XXLarge get() = FontSize("xx-large")

        // Relative keywords
        val Smaller get() = FontSize("smaller")
        val Larger get() = FontSize("larger")
    }
}

fun StyleScope.fontSize(size: FontSize) {
    property("font-size", size)
}
