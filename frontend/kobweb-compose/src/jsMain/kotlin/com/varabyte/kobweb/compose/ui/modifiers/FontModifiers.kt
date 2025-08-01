package com.varabyte.kobweb.compose.ui.modifiers

import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.styleModifier
import org.jetbrains.compose.web.css.*

class FontScope internal constructor(private val styleScope: StyleScope) {
    fun family(vararg values: String) = styleScope.fontFamily(*values)
    fun family(values: List<String>) = family(*values.toTypedArray())

    fun size(value: CSSLengthOrPercentageNumericValue) = styleScope.fontSize(value)
    fun size(fontSize: FontSize) = styleScope.fontSize(fontSize)
    fun style(value: FontStyle) = styleScope.fontStyle(value)
    fun variant(scope: FontVariantScope.() -> Unit) = FontVariantScope(styleScope).scope()
    fun weight(value: FontWeight) = styleScope.fontWeight(value)
    fun weight(value: Int) = styleScope.fontWeight(value)
}

fun Modifier.font(scope: FontScope.() -> Unit) = styleModifier {
    FontScope(this).scope()
}

fun Modifier.fontFamily(vararg values: String): Modifier = styleModifier {
    values.forEach { value ->
        require(
            value.isNotEmpty() &&
                // String might be a function, like `var(--some-var-name, "Times New Roman")`
                (!value.contains(",") || value.contains(Regex("\\(.+\\)")))
        ) {
            buildString {
                append("In `Modifier.fontFamily`, a font name shouldn't contain a comma, but got \"$value\".")
                if (values.size == 1) {
                    append(
                        " Maybe you meant to call `fontFamily(${
                        value.split(",").joinToString { "\"" + it.trim() + "\"" }
                    })`?")
                }
            }
        }
    }

    fontFamily(*values)
}

fun Modifier.fontFamily(values: List<String>) = fontFamily(*values.toTypedArray())

fun Modifier.fontOpticalSizing(value: FontOpticalSizing): Modifier = styleModifier {
    fontOpticalSizing(value)
}

fun Modifier.fontSize(value: CSSLengthOrPercentageNumericValue): Modifier = styleModifier {
    fontSize(value)
}

fun Modifier.fontSize(fontSize: FontSize): Modifier = styleModifier {
    fontSize(fontSize)
}

fun Modifier.fontStyle(value: FontStyle): Modifier = styleModifier {
    fontStyle(value)
}

class FontVariantScope internal constructor(private val styleScope: StyleScope) {
    fun alternates(alternates: FontVariantAlternates) = styleScope.fontVariantAlternates(alternates)
    fun alternates(vararg alternates: FontVariantAlternates.Listable) =
        styleScope.fontVariantAlternates(FontVariantAlternates.list(*alternates))
    fun alternates(alternates: List<FontVariantAlternates.Listable>) = alternates(*alternates.toTypedArray())

    fun caps(caps: FontVariantCaps) = styleScope.fontVariantCaps(caps)

    fun eastAsian(eastAsian: FontVariantEastAsian) = styleScope.fontVariantEastAsian(eastAsian)
    fun eastAsian(vararg eastAsians: FontVariantEastAsian.Listable) =
        styleScope.fontVariantEastAsian(FontVariantEastAsian.list(*eastAsians))
    fun eastAsian(eastAsians: List<FontVariantEastAsian.Listable>) = eastAsian(*eastAsians.toTypedArray())

    fun emoji(emoji: FontVariantEmoji) = styleScope.fontVariantEmoji(emoji)

    fun ligatures(ligatures: FontVariantLigatures) = styleScope.fontVariantLigatures(ligatures)
    fun ligatures(vararg ligatures: FontVariantLigatures.Listable) =
        styleScope.fontVariantLigatures(FontVariantLigatures.list(*ligatures))
    fun ligatures(ligatures: List<FontVariantLigatures.Listable>) = ligatures(*ligatures.toTypedArray())

    fun ligatures(
        common: Boolean? = null,
        discretionary: Boolean? = null,
        historical: Boolean? = null,
        contextual: Boolean? = null
    ) = styleScope.fontVariantLigatures(
        FontVariantLigatures.of(
            common = common,
            discretionary = discretionary,
            historical = historical,
            contextual = contextual
        )
    )

    fun numeric(numeric: FontVariantNumeric) = styleScope.fontVariantNumeric(numeric)
    fun numeric(vararg numerics: FontVariantNumeric.Listable) =
        styleScope.fontVariantNumeric(FontVariantNumeric.list(*numerics))
    fun numeric(numerics: List<FontVariantNumeric.Listable>) = numeric(*numerics.toTypedArray())
}

fun Modifier.fontVariant(scope: FontVariantScope.() -> Unit) = styleModifier {
    FontVariantScope(this).scope()
}

fun Modifier.fontVariant(fontVariant: FontVariant) = styleModifier {
    fontVariant(fontVariant)
}

fun Modifier.fontVariant(
    alternates: FontVariantAlternates.Listable? = null,
    caps: FontVariantCaps.ValidInShorthand? = null,
    eastAsian: FontVariantEastAsian.Listable? = null,
    emoji: FontVariantEmoji.ValidInShorthand? = null,
    ligatures: FontVariantLigatures.Listable? = null,
    numeric: FontVariantNumeric.Listable? = null,
    position: FontVariantPosition.ValidInShorthand? = null,
) = styleModifier {
    fontVariant(FontVariant.of(alternates, caps, eastAsian, emoji, ligatures, numeric, position))
}

fun Modifier.fontVariationSettings(value: FontVariationSettings): Modifier = styleModifier {
    fontVariationSettings(value)
}

fun Modifier.fontVariationSettings(vararg axes: FontVariationSettings.Axis): Modifier = styleModifier {
    fontVariationSettings(FontVariationSettings.Axes(*axes))
}

fun Modifier.fontVariationSettings(axes: List<FontVariationSettings.Axis>): Modifier = fontVariationSettings(*axes.toTypedArray())

fun Modifier.fontWeight(value: FontWeight): Modifier = styleModifier {
    fontWeight(value)
}

fun Modifier.fontWeight(value: Int): Modifier = styleModifier {
    fontWeight(value)
}
