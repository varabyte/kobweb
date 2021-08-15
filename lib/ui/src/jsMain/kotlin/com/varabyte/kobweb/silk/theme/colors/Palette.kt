package com.varabyte.kobweb.silk.theme.colors

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.ui.graphics.Color

data class Palette(
    /** The general background that appears behind the entire app */
    val background: Color,
    /** The color used for medium to large areas (e.g. cards, menus) that appear in your app*/
    val surface: Color,
    /** A color used for a majority of components in the app. */
    val primary: Color,
    /** A color used for occasional accent, such as links and badges. */
    val secondary: Color,
    /** A color used for a warning component */
    val warning: Color,
    /** A color used for an error component */
    val error: Color,
    /** A color used for text / iconography that appears on top of the background color. */
    val onBackground: Color,
    /** A color used for text / iconography that appears on top of the surface color. */
    val onSurface: Color,
    /** A color used for text / iconography that appears on top of the primary color. */
    val onPrimary: Color,
    /** A color used for text / iconography that appears on top of the secondary color. */
    val onSecondary: Color,
    /** A color used for text / iconography that appears on top of the warning color. */
    val onWarning: Color,
    /** A color used for text / iconography that appears on top of the error color. */
    val onError: Color,
)

data class Palettes(
    val light: Palette,
    val dark: Palette,
) {
    operator fun get(colorMode: ColorMode): Palette {
        return when (colorMode) {
            ColorMode.LIGHT -> light
            ColorMode.DARK -> dark
        }
    }

    @Composable
    fun getActivePalette(): Palette = this[getColorMode()]
}
