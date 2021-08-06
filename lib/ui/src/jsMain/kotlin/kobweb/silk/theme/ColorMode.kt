package kobweb.silk.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

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

private val colorModeState by lazy { mutableStateOf(SilkConfig.initialColorMode) }

@Composable
fun rememberColorMode() = remember { colorModeState }

@Composable
fun getColorMode(): ColorMode = colorModeState.value
