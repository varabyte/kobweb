package com.varabyte.kobweb.silk.theme.colors

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.ui.graphics.Color
import com.varabyte.kobweb.compose.ui.graphics.lightened
import com.varabyte.kobweb.silk.init.SilkConfigInstance
import kotlin.math.absoluteValue

enum class ColorMode {
    LIGHT,
    DARK;

    fun isLight() = (this == LIGHT)
    fun isDark() = (this == DARK)
    fun opposite() = when (this) {
        LIGHT -> DARK
        DARK -> LIGHT
    }
}

internal fun String.suffixedWith(colorMode: ColorMode) = "$this-${colorMode.name.lowercase()}"

/**
 * Lighten or darken the color, as appropriate, based on the specified color mode.
 *
 * By default, the color will shift AWAY from the current color mode, e.g. light mode makes colors darker and vice
 * versa, although you can use a negative [byPercent] value if you need the opposite behavior.
 */
fun Color.shifted(colorMode: ColorMode, byPercent: Float = Color.DEFAULT_SHIFTING_PERCENT): Color {
    if (byPercent == 0f) return this
    val shouldLighten = when {
        colorMode == ColorMode.DARK && byPercent > 0f -> true
        colorMode == ColorMode.LIGHT && byPercent < 0f -> true
        else -> false
    }
    @Suppress("NAME_SHADOWING") val byPercent = byPercent.absoluteValue
    return if (shouldLighten) this.lightened(byPercent) else this.darkened(byPercent)
}

/**
 * Convenience function for when you're inside a `Composable` context within which you can grab the current color mode
 */
@Composable
@ReadOnlyComposable
fun Color.shifted(byPercent: Float = Color.DEFAULT_SHIFTING_PERCENT) = shifted(getColorMode(), byPercent)

private val colorModeState by lazy { mutableStateOf(SilkConfigInstance.initialColorMode) }

@Composable
fun rememberColorMode() = remember { colorModeState }

@Composable
@ReadOnlyComposable
fun getColorMode(): ColorMode = colorModeState.value