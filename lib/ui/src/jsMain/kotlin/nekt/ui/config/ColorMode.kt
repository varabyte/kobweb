package nekt.ui.config

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf

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

private val colorModeState by lazy { mutableStateOf(Theme.config.initialColorMode) }

@Composable
fun getColorMode(): ColorMode = colorModeState.value

@Composable
fun setColorMode(value: ColorMode) { colorModeState.value = value }

fun toggleColorMode() {
    colorModeState.value = colorModeState.value.opposite()
}