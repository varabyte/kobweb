package com.varabyte.kobweb.compose.ui.modifiers

import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.styleModifier
import org.jetbrains.compose.web.css.*

class FontScope internal constructor(private val styleScope: StyleScope) {
    fun family(vararg values: String) = styleScope.fontFamily(*values)
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
    fontFamily(*values)
}

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
    fun alternates(vararg alternates: FontVariantAlternates.ListableValue) =
        styleScope.fontVariantAlternates(FontVariantAlternates.of(*alternates))

    fun caps(caps: FontVariantCaps) = styleScope.fontVariantCaps(caps)

    fun eastAsian(eastAsian: FontVariantEastAsian) = styleScope.fontVariantEastAsian(eastAsian)
    fun eastAsian(vararg eastAsians: FontVariantEastAsian.ListableKeyword) =
        styleScope.fontVariantEastAsian(FontVariantEastAsian.of(*eastAsians))

    fun emoji(emoji: FontVariantEmoji) = styleScope.fontVariantEmoji(emoji)

    fun ligatures(ligatures: FontVariantLigatures) = styleScope.fontVariantLigatures(ligatures)
    fun ligatures(vararg ligatures: FontVariantLigatures.ListableKeyword) =
        styleScope.fontVariantLigatures(FontVariantLigatures.of(*ligatures))

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
    fun numeric(vararg numerics: FontVariantNumeric.ListableKeyword) =
        styleScope.fontVariantNumeric(FontVariantNumeric.of(*numerics))

    fun settings(settings: FontVariationSettings) = styleScope.fontVariationSettings(settings)
    fun settings(vararg axes: FontVariationSettings.Axis) =
        styleScope.fontVariationSettings(FontVariationSettings.Axes(*axes))
}

fun Modifier.fontVariant(scope: FontVariantScope.() -> Unit) = styleModifier {
    FontVariantScope(this).scope()
}

fun Modifier.fontVariant(
    alternates: FontVariantAlternates? = null,
    caps: FontVariantCaps? = null,
    eastAsian: FontVariantEastAsian? = null,
    emoji: FontVariantEmoji? = null,
    ligatures: FontVariantLigatures? = null,
    numeric: FontVariantNumeric? = null,
    position: FontVariantPosition? = null,
) = styleModifier {
    fontVariant(alternates, caps, eastAsian, emoji, ligatures, numeric, position)
}

fun Modifier.fontWeight(value: FontWeight): Modifier = styleModifier {
    fontWeight(value)
}

fun Modifier.fontWeight(value: Int): Modifier = styleModifier {
    fontWeight(value)
}
