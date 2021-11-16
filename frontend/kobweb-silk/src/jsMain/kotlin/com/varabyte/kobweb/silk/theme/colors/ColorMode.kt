package com.varabyte.kobweb.silk.theme.colors

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.varabyte.kobweb.compose.ui.graphics.Color
import com.varabyte.kobweb.silk.theme.SilkConfigInstance

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

/**
 * Lighten or darken the color, as appropriate, based on the specified color mode.
 *
 * This is useful to make sure that a while color (in light mode) or a black color (in dark mode) don't get stuck
 * doing nothing if you try to consistently lighten or darken ignoring the color mode.
 */
fun Color.shifted(colorMode: ColorMode) = if (colorMode == ColorMode.DARK) this.lightened() else this.darkened()

@Composable
@ReadOnlyComposable
fun Color.shifted() = shifted(getColorMode())

private val colorModeState by lazy { mutableStateOf(SilkConfigInstance.initialColorMode) }

@Composable
fun rememberColorMode() = remember { colorModeState }

@Composable
@ReadOnlyComposable
fun getColorMode(): ColorMode = colorModeState.value