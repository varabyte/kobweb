package kobweb.silk.theme.colors

import androidx.compose.runtime.Composable
import kobweb.compose.ui.graphics.Color

data class Palette(
    val fg: Color,
    val bg: Color,
    val link: Color,
)

data class Colors(
    val light: Palette,
    val dark: Palette,
) {
    fun getPalette(colorMode: ColorMode): Palette {
        return when (colorMode) {
            ColorMode.LIGHT -> light
            ColorMode.DARK -> dark
        }
    }

    @Composable
    fun getActivePalette(): Palette = getPalette(getColorMode())
}

/**
 * A veritable rainbow of colors for themes to choose from.
 *
 * Special thanks to Chakra UI here:
 * https://github.com/chakra-ui/chakra-ui/blob/main/packages/theme/src/foundations/colors.ts
 */
object SilkColors {
    val White50 = Color(255, 255, 255, 0.04f)
    val White100 = Color(255, 255, 255, 0.06f)
    val White200 = Color(255, 255, 255, 0.08f)
    val White300 = Color(255, 255, 255, 0.16f)
    val White400 = Color(255, 255, 255, 0.24f)
    val White500 = Color(255, 255, 255, 0.36f)
    val White600 = Color(255, 255, 255, 0.48f)
    val White700 = Color(255, 255, 255, 0.64f)
    val White800 = Color(255, 255, 255, 0.80f)
    val White900 = Color(255, 255, 255, 0.92f)

    val Black50 = Color(0, 0, 0, 0.04f)
    val Black100 = Color(0, 0, 0, 0.06f)
    val Black200 = Color(0, 0, 0, 0.08f)
    val Black300 = Color(0, 0, 0, 0.16f)
    val Black400 = Color(0, 0, 0, 0.24f)
    val Black500 = Color(0, 0, 0, 0.36f)
    val Black600 = Color(0, 0, 0, 0.48f)
    val Black700 = Color(0, 0, 0, 0.64f)
    val Black800 = Color(0, 0, 0, 0.80f)
    val Black900 = Color(0, 0, 0, 0.92f)

    val Gray50 = Color(0xF7FAFC)
    val Gray100 = Color(0xEDF2F7)
    val Gray200 = Color(0xE2E8F0)
    val Gray300 = Color(0xCBD5E0)
    val Gray400 = Color(0xA0AEC0)
    val Gray500 = Color(0x718096)
    val Gray600 = Color(0x4A5568)
    val Gray700 = Color(0x2D3748)
    val Gray800 = Color(0x1A202C)
    val Gray900 = Color(0x171923)

    val Red50 = Color(0xFFF5F5)
    val Red100 = Color(0xFED7D7)
    val Red200 = Color(0xFEB2B2)
    val Red300 = Color(0xFC8181)
    val Red400 = Color(0xF56565)
    val Red500 = Color(0xE53E3E)
    val Red600 = Color(0xC53030)
    val Red700 = Color(0x9B2C2C)
    val Red800 = Color(0x822727)
    val Red900 = Color(0x63171B)

    val Orange50 = Color(0xFFFAF0)
    val Orange100 = Color(0xFEEBC8)
    val Orange200 = Color(0xFBD38D)
    val Orange300 = Color(0xF6AD55)
    val Orange400 = Color(0xED8936)
    val Orange500 = Color(0xDD6B20)
    val Orange600 = Color(0xC05621)
    val Orange700 = Color(0x9C4221)
    val Orange800 = Color(0x7B341E)
    val Orange900 = Color(0x652B19)

    val Yellow50 = Color(0xFFFFF0)
    val Yellow100 = Color(0xFEFCBF)
    val Yellow200 = Color(0xFAF089)
    val Yellow300 = Color(0xF6E05E)
    val Yellow400 = Color(0xECC94B)
    val Yellow500 = Color(0xD69E2E)
    val Yellow600 = Color(0xB7791F)
    val Yellow700 = Color(0x975A16)
    val Yellow800 = Color(0x744210)
    val Yellow900 = Color(0x5F370E)

    val Green50 = Color(0xF0FFF4)
    val Green100 = Color(0xC6F6D5)
    val Green200 = Color(0x9AE6B4)
    val Green300 = Color(0x68D391)
    val Green400 = Color(0x48BB78)
    val Green500 = Color(0x38A169)
    val Green600 = Color(0x2F855A)
    val Green700 = Color(0x276749)
    val Green800 = Color(0x22543D)
    val Green900 = Color(0x1C4532)

    val Teal50 = Color(0xE6FFFA)
    val Teal100 = Color(0xB2F5EA)
    val Teal200 = Color(0x81E6D9)
    val Teal300 = Color(0x4FD1C5)
    val Teal400 = Color(0x38B2AC)
    val Teal500 = Color(0x319795)
    val Teal600 = Color(0x2C7A7B)
    val Teal700 = Color(0x285E61)
    val Teal800 = Color(0x234E52)
    val Teal900 = Color(0x1D4044)

    val Blue50 = Color(0xEBF8FF)
    val Blue100 = Color(0xBEE3F8)
    val Blue200 = Color(0x90CDF4)
    val Blue300 = Color(0x63B3ED)
    val Blue400 = Color(0x4299E1)
    val Blue500 = Color(0x3182CE)
    val Blue600 = Color(0x2B6CB0)
    val Blue700 = Color(0x2C5282)
    val Blue800 = Color(0x2A4365)
    val Blue900 = Color(0x1A365D)

    val Cyan50 = Color(0xEDFDFD)
    val Cyan100 = Color(0xC4F1F9)
    val Cyan200 = Color(0x9DECF9)
    val Cyan300 = Color(0x76E4F7)
    val Cyan400 = Color(0x0BC5EA)
    val Cyan500 = Color(0x00B5D8)
    val Cyan600 = Color(0x00A3C4)
    val Cyan700 = Color(0x0987A0)
    val Cyan800 = Color(0x086F83)
    val Cyan900 = Color(0x065666)

    val Purple50 = Color(0xFAF5FF)
    val Purple100 = Color(0xE9D8FD)
    val Purple200 = Color(0xD6BCFA)
    val Purple300 = Color(0xB794F4)
    val Purple400 = Color(0x9F7AEA)
    val Purple500 = Color(0x805AD5)
    val Purple600 = Color(0x6B46C1)
    val Purple700 = Color(0x553C9A)
    val Purple800 = Color(0x44337A)
    val Purple900 = Color(0x322659)

    val Pink50 = Color(0xFFF5F7)
    val Pink100 = Color(0xFED7E2)
    val Pink200 = Color(0xFBB6CE)
    val Pink300 = Color(0xF687B3)
    val Pink400 = Color(0xED64A6)
    val Pink500 = Color(0xD53F8C)
    val Pink600 = Color(0xB83280)
    val Pink700 = Color(0x97266D)
    val Pink800 = Color(0x702459)
    val Pink900 = Color(0x521B41)
}
