package com.varabyte.kobweb.silk.theme.colors

import androidx.compose.runtime.*
import com.varabyte.kobweb.browser.storage.createStorageKey
import com.varabyte.kobweb.browser.storage.getItem
import com.varabyte.kobweb.browser.storage.setItem
import com.varabyte.kobweb.compose.ui.graphics.Color
import com.varabyte.kobweb.compose.ui.graphics.lightened
import com.varabyte.kobweb.silk.init.SilkConfig
import kotlinx.browser.window
import kotlin.math.absoluteValue

private val rootColorModeState by lazy { mutableStateOf(SilkConfig.Instance.initialColorMode) }

// Set by [ColorMode#provide]
private val LocalColorMode = compositionLocalOf { rootColorModeState }

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
         * within a `Surface` that sets the `colorModeOverride` parameter, it will fetch the local color mode instead.
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

    /**
     * Provide this color, useful within a [CompositionLocalProvider] call.
     *
     * For example:
     *
     * ```
     * CompositionLocalProvider(colorMode.provide()) {
     *   // ColorMode.current will return "colorMode" within this block
     * }
     * ```
     */
    fun provide() = LocalColorMode provides mutableStateOf(this)
}

/**
 * Returns the system color preference (which represents the user's device color preference).
 *
 * It can be useful to set this as your site's initial color mode:
 *
 * ```
 * @InitSilk
 * fun updateTheme(ctx: InitSilkContext) = ctx.config.apply {
 *     initialColorMode = ColorMode.systemPreference
 * }
 * ```
 *
 * as otherwise, the initial color mode will simply default to [ColorMode.LIGHT].
 *
 * NOTE: Following the guidelines set out
 * by the [CSS Working Group](https://drafts.csswg.org/mediaqueries-5/#prefers-color-scheme), this value will assume
 * light mode as a default fallback. (Originally, browsers supported a "no-preference" option but that has since been
 * removed from the spec.)
 */
val ColorMode.Companion.systemPreference: ColorMode get() {
    return when {
        window.matchMedia("(prefers-color-scheme: dark)").matches -> ColorMode.DARK
        else -> ColorMode.LIGHT
    }
}

private const val DEFAULT_COLOR_MODE_STORAGE_KEY_NAME = "silk-color-mode"

private fun createColorModeStorageKey(key: String) = ColorMode.entries.createStorageKey(key)

fun ColorMode.Companion.loadFromLocalStorage(key: String = DEFAULT_COLOR_MODE_STORAGE_KEY_NAME): ColorMode? {
    val colorModeKey = createColorModeStorageKey(key)
    return window.localStorage.getItem(colorModeKey)
}

fun ColorMode.saveToLocalStorage(key: String = DEFAULT_COLOR_MODE_STORAGE_KEY_NAME) {
    val colorModeKey = createColorModeStorageKey(key)
    window.localStorage.setItem(colorModeKey, this)
}


// Note: We use an underscore here as a separator instead of a hyphen, since we otherwise use hyphens when generating
// names, so this makes the separator stand out as something more orthogonal to the base name.
//
// It also avoids ambiguity if you call some style's variant "dark", as in `ComponentStyle.addVariant("dark")`, since
// that would generate a full style name of "style-dark".
//
// By using underscores instead, if we have dark and light mode variants of the parent style and its "dark" variant, we
// would have "style_dark", "style_light", "style-dark_dark", and "style-dark_light"
//
// Finally, note that this method can be used to REPLACE the suffix of a colored style, e.g. `style_light` when suffixed
// with `ColorMode.Dark` will be converted to `style_dark`.
fun String.suffixedWith(colorMode: ColorMode) = "${this.substringBeforeLast('_')}_${colorMode.name.lowercase()}"

fun String.withColorModeSuffixRemoved() = ColorMode.entries.fold(this) { str, colorMode ->
    str.removeSuffix("_${colorMode.name.lowercase()}")
}

fun String.isSuffixedWith(colorMode: ColorMode) = this.endsWith("_${colorMode.name.lowercase()}")

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
