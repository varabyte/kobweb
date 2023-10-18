package com.varabyte.kobweb.compose.ui.modifiers

import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.styleModifier
import org.jetbrains.compose.web.css.*

fun Modifier.fontFamily(vararg values: String): Modifier = styleModifier {
    fontFamily(*values)
}

fun Modifier.fontSize(value: CSSNumeric): Modifier = styleModifier {
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
    fun alternates(vararg alternates: FontVariantAlternates.ListableValue) = styleScope.fontVariantAlternates(*alternates)
    fun caps(caps: FontVariantCaps) = styleScope.fontVariantCaps(caps)
    fun eastAsian(eastAsian: FontVariantEastAsian) = styleScope.fontVariantEastAsian(eastAsian)
    fun emoji(emoji: FontVariantEmoji) = styleScope.fontVariantEmoji(emoji)
    fun ligatures(ligatures: FontVariantLigatures) = styleScope.fontVariantLigatures(ligatures)
    fun numeric(numeric: FontVariantNumeric) = styleScope.fontVariantNumeric(numeric)
    fun numeric(vararg numerics: FontVariantNumeric.ListableKeyword) = styleScope.fontVariantNumeric(*numerics)
    fun settings(settings: FontVariantSettings) = styleScope.fontVariantSettings(settings)
    fun settings(vararg axes: FontVariantSettings.Axis) = styleScope.fontVariantSettings(*axes)
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
