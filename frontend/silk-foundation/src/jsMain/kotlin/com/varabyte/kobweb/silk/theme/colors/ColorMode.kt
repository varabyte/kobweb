package com.varabyte.kobweb.silk.theme.colors

import androidx.compose.runtime.*
import com.varabyte.kobweb.browser.dom.css.CssIdent
import com.varabyte.kobweb.browser.storage.createStorageKey
import com.varabyte.kobweb.browser.storage.getItem
import com.varabyte.kobweb.browser.storage.setItem
import com.varabyte.kobweb.compose.ui.graphics.Color
import com.varabyte.kobweb.compose.ui.graphics.lightened
import com.varabyte.kobweb.silk.init.SilkConfig
import com.varabyte.kobweb.silk.style.ColorModeStrategy
import com.varabyte.kobweb.silk.theme.colors.ColorMode.Companion.current
import com.varabyte.kobweb.silk.theme.colors.ColorMode.Companion.currentState
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

/**
 * A CSS class representing the given color mode.
 *
 * When using [ColorModeStrategy.SCOPE] mode (the default), this class must be set on an element so that it and its
 * descendants use styles for the current color mode.
 *
 * Setting this class on a child element will override the color mode for styles on that element and its descendants.
 */
val ColorMode.cssClass
    get() = when (this) {
        ColorMode.LIGHT -> "silk-light"
        ColorMode.DARK -> "silk-dark"
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

private fun ColorMode.toSuffix() = "_${name.lowercase()}"

/**
 * Assuming this string represents a CSS class name, return the color mode suffix (if any) associated with it.
 *
 * For example, `"my-style_dark"` will return `ColorMode.DARK`, while `"my-style"` will return `null`.
 */
private val CssIdent.colorModeSuffix: ColorMode?
    get() {
        val self = this
        return ColorMode.entries.firstOrNull { colorMode -> self.isSuffixedWith(colorMode) }
    }

/**
 * For a String that represents a CSS class name, append the appropriate color mode suffix to it.
 *
 * For example, `"my-class".suffixedWith(ColorMode.DARK)` will return `"my-class_dark"`.
 *
 * Note: We use an underscore here as a separator instead of a hyphen, as Silk otherwise uses hyphens when generating
 * class names, so this makes the separator stand out as something more orthogonal to the base name.
 *
 * This also avoids ambiguity if you create a variant called "dark", as in `MenuStyle.addVariant("dark")`, since that
 * would generate a full style name of "menu-dark". In this case, when applying color mode suffixes to this, we will
 * end up with "menu-dark_dark" and "menu-dark_light".
 */
fun CssIdent.suffixedWith(colorMode: ColorMode) = this.withColorModeSuffixRemoved().renamed { "${this}${colorMode.toSuffix()}" }

/**
 * Assuming this string represents a CSS class name, test whether it has the specified color mode suffix.
 */
fun CssIdent.isSuffixedWith(colorMode: ColorMode) = this.endsWith(colorMode.toSuffix())

/**
 * Assuming this string represents a CSS class name, remove its color mode suffix if it has one.
 *
 * This will return the style base without the color suffix if it has one, or it will return the original string
 * otherwise.
 */
fun CssIdent.withColorModeSuffixRemoved() =
    this
        .colorModeSuffix
        ?.let { colorMode -> this.renamed { removeSuffix(colorMode.toSuffix()) } }
        ?: this

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
