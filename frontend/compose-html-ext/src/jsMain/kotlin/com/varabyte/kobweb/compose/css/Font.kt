package com.varabyte.kobweb.compose.css

import com.varabyte.kobweb.compose.util.wrapQuotesIfNecessary
import org.jetbrains.compose.web.css.*

fun StyleScope.fontFamily(value: String) {
    property("font-family", value)
}

// https://developer.mozilla.org/en-US/docs/Web/CSS/font-style
class FontStyle private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object {
        // Keyword
        val Normal get() = FontStyle("normal")
        val Italic get() = FontStyle("italic")
        val Oblique get() = FontStyle("oblique")

        fun Oblique(angle: CSSAngleNumericValue) = FontStyle("oblique $angle")

        // Global
        val Inherit get() = FontStyle("inherit")
        val Initial get() = FontStyle("initial")
        val Revert get() = FontStyle("revert")
        val Unset get() = FontStyle("unset")
    }
}

fun StyleScope.fontStyle(style: FontStyle) {
    property("font-style", style)
}

// region FontVariant

// https://developer.mozilla.org/en-US/docs/Web/CSS/font-variant-alternates
sealed class FontVariantAlternates private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    private class SingleKeyword(value: String) : FontVariantAlternates(value)
    sealed class ListableValue(value: String) : FontVariantAlternates(value)
    private class ListableKeyword(value: String) : ListableValue(value)
    private class FunctionalNotation(name: String, ident: String) : ListableValue("$name($ident)")
    class ValueList(vararg values: ListableValue) : FontVariantAlternates(values.joinToString(" "))

    companion object {
        // Keyword
        val Normal: FontVariantAlternates get() = SingleKeyword("normal")
        val HistoricalForms: ListableValue get() = ListableKeyword("historical-forms")

        // Functional notation values
        fun Stylistic(ident: String): ListableValue = FunctionalNotation("stylistic", ident)
        fun Styleset(ident: String): ListableValue = FunctionalNotation("styleset", ident)
        fun CharacterVariant(ident: String): ListableValue = FunctionalNotation("character-variant", ident)
        fun Swash(ident: String): ListableValue = FunctionalNotation("swash", ident)
        fun Ornament(ident: String): ListableValue = FunctionalNotation("ornament", ident)
        fun Annotation(ident: String): ListableValue = FunctionalNotation("annotation", ident)

        // Global
        val Inherit: FontVariantAlternates get() = SingleKeyword("inherit")
        val Initial: FontVariantAlternates get() = SingleKeyword("initial")
        val Revert: FontVariantAlternates get() = SingleKeyword("revert")
        val Unset: FontVariantAlternates get() = SingleKeyword("unset")
    }
}

fun StyleScope.fontVariantAlternates(fontVariantAlternates: FontVariantAlternates) {
    property("font-variant-alternates", fontVariantAlternates)
}

fun StyleScope.fontVariantAlternates(vararg fontVariantAlternates: FontVariantAlternates.ListableValue) {
    fontVariantAlternates(FontVariantAlternates.ValueList(*fontVariantAlternates))
}

// https://developer.mozilla.org/en-US/docs/Web/CSS/font-variant-caps
class FontVariantCaps private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object {
        // Keyword
        val Normal get() = FontVariantCaps("normal")
        val SmallCaps get() = FontVariantCaps("small-caps")
        val AllSmallCaps get() = FontVariantCaps("all-small-caps")
        val PetiteCaps get() = FontVariantCaps("petite-caps")
        val AllPetiteCaps get() = FontVariantCaps("all-petite-caps")
        val Unicase get() = FontVariantCaps("unicase")
        val TitlingCaps get() = FontVariantCaps("titling-caps")

        // Global
        val Inherit get() = FontVariantCaps("inherit")
        val Initial get() = FontVariantCaps("initial")
        val Revert get() = FontVariantCaps("revert")
        val Unset get() = FontVariantCaps("unset")
    }
}

fun StyleScope.fontVariantCaps(caps: FontVariantCaps) {
    property("font-variant-caps", caps)
}

// https://developer.mozilla.org/en-US/docs/Web/CSS/font-variant-east-asian
class FontVariantEastAsian private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object {
        // Keyword
        val Normal get() = FontVariantEastAsian("normal")

        // East Asian variants
        val Jis78 get() = FontVariantEastAsian("jis78")
        val Jis83 get() = FontVariantEastAsian("jis83")
        val Jis90 get() = FontVariantEastAsian("jis90")
        val Jis04 get() = FontVariantEastAsian("jis04")
        val Simplified get() = FontVariantEastAsian("simplified")
        val Traditional get() = FontVariantEastAsian("traditional")

        // East Asian widths
        val FullWidth get() = FontVariantEastAsian("full-width")
        val ProportionalWidth get() = FontVariantEastAsian("proportional-width")

        // Global
        val Inherit get() = FontVariantEastAsian("inherit")
        val Initial get() = FontVariantEastAsian("initial")
        val Revert get() = FontVariantEastAsian("revert")
        val Unset get() = FontVariantEastAsian("unset")
    }
}

fun StyleScope.fontVariantEastAsian(eastAsian: FontVariantEastAsian) {
    property("font-variant-east-asian", eastAsian)
}

// https://developer.mozilla.org/en-US/docs/Web/CSS/font-variant-emoji
class FontVariantEmoji private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object {
        // Keyword
        val Normal get() = FontVariantEmoji("normal")
        val Text get() = FontVariantEmoji("text")
        val Emoji get() = FontVariantEmoji("emoji")
        val Unicode get() = FontVariantEmoji("unicode")

        // Global
        val Inherit get() = FontVariantEmoji("inherit")
        val Initial get() = FontVariantEmoji("initial")
        val Revert get() = FontVariantEmoji("revert")
        val Unset get() = FontVariantEmoji("unset")
    }
}

fun StyleScope.fontVariantEmoji(emoji: FontVariantEmoji) {
    property("font-variant-emoji", emoji)
}

// https://developer.mozilla.org/en-US/docs/Web/CSS/font-variant-ligatures
class FontVariantLigatures private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object {
        // Keyword
        val Normal get() = FontVariantLigatures("normal")
        val None get() = FontVariantLigatures("none")

        fun of(
            common: Boolean? = null,
            discretionary: Boolean? = null,
            historical: Boolean? = null,
            contextual: Boolean? = null
        ): FontVariantLigatures {
            return FontVariantLigatures(buildString {
                if (common != null) {
                    append("${if (common) "" else "no-"}common-ligatures ")
                }
                if (discretionary != null) {
                    append("${if (discretionary) "" else "no-"}discretionary-ligatures ")
                }
                if (historical != null) {
                    append("${if (historical) "" else "no-"}historical-ligatures ")
                }
                if (contextual != null) {
                    append("${if (contextual) "" else "no-"}contextual ")
                }
            })
        }

        // Global
        val Inherit get() = FontVariantLigatures("inherit")
        val Initial get() = FontVariantLigatures("initial")
        val Revert get() = FontVariantLigatures("revert")
        val Unset get() = FontVariantLigatures("unset")
    }
}

fun StyleScope.fontVariantLigatures(ligatures: FontVariantLigatures) {
    property("font-variant-ligatures", ligatures)
}

// https://developer.mozilla.org/en-US/docs/Web/CSS/font-variant-alternates
sealed class FontVariantNumeric private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    private class SingleKeyword(value: String) : FontVariantNumeric(value)
    class ListableKeyword(value: String) : FontVariantNumeric(value)
    class Keywords(vararg keywords: ListableKeyword) : FontVariantNumeric(keywords.joinToString(" "))

    companion object {
        // Keyword
        val Normal: FontVariantNumeric get() = SingleKeyword("normal")
        val Ordinal: ListableKeyword get() = ListableKeyword("ordinal")
        val SlashedZero: ListableKeyword get() = ListableKeyword("slashed-zero")

        // Numeric figure styles
        val LiningNums: ListableKeyword get() = ListableKeyword("lining-nums")
        val OldstyleNums: ListableKeyword get() = ListableKeyword("oldstyle-nums")

        // Numeric spacing
        val ProportionalNums: ListableKeyword get() = ListableKeyword("proportional-nums")
        val TabularNums: ListableKeyword get() = ListableKeyword("tabular-nums")

        // Numeric fractions
        val DiagonalFractions: ListableKeyword get() = ListableKeyword("diagonal-fractions")
        val StackedFractions: ListableKeyword get() = ListableKeyword("stacked-fractions")

        // Global
        val Inherit: FontVariantNumeric get() = SingleKeyword("inherit")
        val Initial: FontVariantNumeric get() = SingleKeyword("initial")
        val Revert: FontVariantNumeric get() = SingleKeyword("revert")
        val Unset: FontVariantNumeric get() = SingleKeyword("unset")
    }
}

fun StyleScope.fontVariantNumeric(numeric: FontVariantNumeric) {
    property("font-variant-numeric", numeric)
}

fun StyleScope.fontVariantNumeric(vararg numerics: FontVariantNumeric.ListableKeyword) {
    fontVariantNumeric(FontVariantNumeric.Keywords(*numerics))
}

// https://developer.mozilla.org/en-US/docs/Web/CSS/font-variant-ligatures
class FontVariantPosition private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object {
        // Keyword
        val Normal get() = FontVariantPosition("normal")
        val Sub get() = FontVariantPosition("sub")
        val Super get() = FontVariantPosition("super")

        // Global
        val Inherit get() = FontVariantPosition("inherit")
        val Initial get() = FontVariantPosition("initial")
        val Revert get() = FontVariantPosition("revert")
        val Unset get() = FontVariantPosition("unset")
    }
}

fun StyleScope.fontVariantPosition(position: FontVariantPosition) {
    property("font-variant-position", position)
}

// https://developer.mozilla.org/en-US/docs/Web/CSS/font-variant-alternates
sealed class FontVariantSettings private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    private class Keyword(value: String) : FontVariantSettings(value)
    class Axis(name: String, value: Number) : FontVariantSettings("${name.wrapQuotesIfNecessary()} $value")
    class Axes(vararg axes: Axis) : FontVariantSettings(axes.joinToString(","))

    companion object {
        // Keyword
        val Normal: FontVariantSettings get() = Keyword("normal")

        // Registered axes
        fun Wght(value: Number): Axis = Axis("wght", value)
        fun Wdth(value: Number): Axis = Axis("wdth", value)
        fun Itas(value: Number): Axis = Axis("itas", value)
        fun Slnt(value: Number): Axis = Axis("slnt", value)
        fun Opbd(value: Number): Axis = Axis("opbd", value)

        // Global
        val Inherit: FontVariantSettings get() = Keyword("inherit")
        val Initial: FontVariantSettings get() = Keyword("initial")
        val Revert: FontVariantSettings get() = Keyword("revert")
        val Unset: FontVariantSettings get() = Keyword("unset")
    }
}

fun StyleScope.fontVariantSettings(settings: FontVariantSettings) {
    property("font-variant-settings", settings)
}

fun StyleScope.fontVariantSettings(vararg axes: FontVariantSettings.Axis) {
    fontVariantSettings(FontVariantSettings.Axes(*axes))
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

// https://developer.mozilla.org/en-US/docs/Web/CSS/font-weight
class FontWeight private constructor(private val value: String) : CSSStyleValue {
    override fun toString() = value

    companion object {
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

        // Global
        val Inherit get() = FontWeight("inherit")
        val Initial get() = FontWeight("initial")
        val Revert get() = FontWeight("revert")
        val Unset get() = FontWeight("unset")
    }
}

fun StyleScope.fontWeight(weight: FontWeight) {
    property("font-weight", weight)
}

// https://developer.mozilla.org/en-US/docs/Web/CSS/font-size
class FontSize private constructor(private val value: String) : CSSStyleValue {
    override fun toString() = value

    companion object {
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

        // Global
        val Inherit get() = FontSize("inherit")
        val Initial get() = FontSize("initial")
        val Revert get() = FontSize("revert")
        val Unset get() = FontSize("unset")
    }
}

fun StyleScope.fontSize(size: FontSize) {
    property("font-size", size)
}
