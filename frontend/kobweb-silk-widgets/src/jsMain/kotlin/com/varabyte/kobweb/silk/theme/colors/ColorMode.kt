package com.varabyte.kobweb.silk.theme.colors

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.ui.graphics.Color
import com.varabyte.kobweb.compose.ui.graphics.lightened
import com.varabyte.kobweb.silk.init.SilkConfig
import kotlin.math.absoluteValue

private val rootColorModeState by lazy { mutableStateOf(SilkConfig.Instance.initialColorMode) }
// Used by `Surface`
internal val LocalColorMode = compositionLocalOf { rootColorModeState }

enum class ColorMode {
    LIGHT,
    DARK;

    companion object {
        /**
         * The current color mode, exposed as a [MutableState] so that you can change it and have the UI update.
         *
         * See also [current] if you only need read-only access to the current color mode.
         */
        val currentState: MutableState<ColorMode> @Composable get() = LocalColorMode.current

        /**
         * The current color mode.
         *
         * By default, this will be a global color mode that affects the whole site. However, if you check this value
         * within a `ColorMode(colorModeOverride) { ... }` block, it will fetch the local color mode instead.
         *
         * See also [currentState] if you need to modify the current color.
         */
        val current: ColorMode @Composable @ReadOnlyComposable get() = LocalColorMode.current.value
    }

    val isLight get() = (this == LIGHT)
    val isDark get() = (this == DARK)
    val opposite
        get() = when (this) {
            LIGHT -> DARK
            DARK -> LIGHT
        }

    @Deprecated("Use `isLight` property instead.", ReplaceWith("isLight"))
    @JsName("isLight_Deprecated")
    fun isLight() = (this == LIGHT)

    @Deprecated("Use `isDark` property instead.", ReplaceWith("isDark"))
    @JsName("isDark_Deprecated")
    fun isDark() = (this == DARK)

    @Deprecated("Use `opposite` property instead.", ReplaceWith("opposite"))
    @JsName("opposite_Deprecated")
    fun opposite() = when (this) {
        LIGHT -> DARK
        DARK -> LIGHT
    }
}

// Note: We use an underscore here as a separator instead of a hyphen, since we otherwise use hyphens when generating
// names, so this makes the separator stand out as something more orthogonal to the base name.
//
// It also avoids ambiguity if you call some style's variant "dark", as in `ComponentStyle.addVariant("dark")`, since
// that would generate a full style name of "style-dark".
//
// By using underscores instead, if we have dark and light mode variants of the parent style and its "dark" variant, we
// would have "style_dark", "style_light", "style-dark_dark", and "style-dark_light"
internal fun String.suffixedWith(colorMode: ColorMode) = "${this}_${colorMode.name.lowercase()}"

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
 * Convenience function for when you're inside a `Composable` context, within which you can grab the current color mode.
 */
@Composable
@ReadOnlyComposable
fun Color.shifted(byPercent: Float = Color.DEFAULT_SHIFTING_PERCENT) = shifted(ColorMode.current, byPercent)

@Deprecated("Use `ColorMode.currentState` instead.", ReplaceWith("ColorMode.currentState"))
@Composable
fun rememberColorMode() = ColorMode.currentState

@Deprecated("Use `ColorMode.current` instead.", ReplaceWith("ColorMode.current"))
@Composable
@ReadOnlyComposable
fun getColorMode(): ColorMode = ColorMode.current
