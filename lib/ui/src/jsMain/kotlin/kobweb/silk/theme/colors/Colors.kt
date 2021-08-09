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
 *
 * and Material design:
 * https://material.io/design/color/the-color-system.html#tools-for-picking-colors
 */
@Suppress("unused")
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

    val Red50 = Color(0xFFEBEE)
    val Red100 = Color(0xFFCDD2)
    val Red200 = Color(0xEF9A9A)
    val Red300 = Color(0xE57373)
    val Red400 = Color(0xEF5350)
    val Red500 = Color(0xF44336)
    val Red600 = Color(0xE53935)
    val Red700 = Color(0xD32F2F)
    val Red800 = Color(0xC62828)
    val Red900 = Color(0xB71C1C)

    val Pink50 = Color(0xFCE4EC)
    val Pink100 = Color(0xF8BBD0)
    val Pink200 = Color(0xF48FB1)
    val Pink300 = Color(0xF06292)
    val Pink400 = Color(0xEC407A)
    val Pink500 = Color(0xE91E63)
    val Pink600 = Color(0xD81B60)
    val Pink700 = Color(0xC2185B)
    val Pink800 = Color(0xAD1457)
    val Pink900 = Color(0x880E4F)

    val Purple50 = Color(0xF3E5F5)
    val Purple100 = Color(0xE1BEE7)
    val Purple200 = Color(0xCE93D8)
    val Purple300 = Color(0xBA68C8)
    val Purple400 = Color(0xAB47BC)
    val Purple500 = Color(0x9C27B0)
    val Purple600 = Color(0x8E24AA)
    val Purple700 = Color(0x7B1FA2)
    val Purple800 = Color(0x6A1B9A)
    val Purple900 = Color(0x4A148C)

    val DeepPurple50 = Color(0xEDE7F6)
    val DeepPurple100 = Color(0xD1C4E9)
    val DeepPurple200 = Color(0xB39DDB)
    val DeepPurple300 = Color(0x9575CD)
    val DeepPurple400 = Color(0x7E57C2)
    val DeepPurple500 = Color(0x673AB7)
    val DeepPurple600 = Color(0x5E35B1)
    val DeepPurple700 = Color(0x512DA8)
    val DeepPurple800 = Color(0x4527A0)
    val DeepPurple900 = Color(0x311B92)

    val Indigo50 = Color(0xE8EAF6)
    val Indigo100 = Color(0xC5CAE9)
    val Indigo200 = Color(0x9FA8DA)
    val Indigo300 = Color(0x7986CB)
    val Indigo400 = Color(0x5C6BC0)
    val Indigo500 = Color(0x3F51B5)
    val Indigo600 = Color(0x3949AB)
    val Indigo700 = Color(0x303F9F)
    val Indigo800 = Color(0x283593)
    val Indigo900 = Color(0x1A237E)

    val Blue50 = Color(0xE3F2FD)
    val Blue100 = Color(0xBBDEFB)
    val Blue200 = Color(0x90CAF9)
    val Blue300 = Color(0x64B5F6)
    val Blue400 = Color(0x42A5F5)
    val Blue500 = Color(0x2196F3)
    val Blue600 = Color(0x1E88E5)
    val Blue700 = Color(0x1976D2)
    val Blue800 = Color(0x1565C0)
    val Blue900 = Color(0x0D47A1)

    val LightBlue50 = Color(0xE1F5FE)
    val LightBlue100 = Color(0xB3E5FC)
    val LightBlue200 = Color(0x81D4FA)
    val LightBlue300 = Color(0x4FC3F7)
    val LightBlue400 = Color(0x29B6F6)
    val LightBlue500 = Color(0x03A9F4)
    val LightBlue600 = Color(0x039BE5)
    val LightBlue700 = Color(0x0288D1)
    val LightBlue800 = Color(0x0277BD)
    val LightBlue900 = Color(0x01579B)

    val Cyan50 = Color(0xE0F7FA)
    val Cyan100 = Color(0xB2EBF2)
    val Cyan200 = Color(0x80DEEA)
    val Cyan300 = Color(0x4DD0E1)
    val Cyan400 = Color(0x26C6DA)
    val Cyan500 = Color(0x00BCD4)
    val Cyan600 = Color(0x00ACC1)
    val Cyan700 = Color(0x0097A7)
    val Cyan800 = Color(0x00838F)
    val Cyan900 = Color(0x006064)

    val Teal50 = Color(0xE0F2F1)
    val Teal100 = Color(0xB2DFDB)
    val Teal200 = Color(0x80CBC4)
    val Teal300 = Color(0x4DB6AC)
    val Teal400 = Color(0x26A69A)
    val Teal500 = Color(0x009688)
    val Teal600 = Color(0x00897B)
    val Teal700 = Color(0x00796B)
    val Teal800 = Color(0x00695C)
    val Teal900 = Color(0x004D40)

    val Green50 = Color(0xE8F5E9)
    val Green100 = Color(0xC8E6C9)
    val Green200 = Color(0xA5D6A7)
    val Green300 = Color(0x81C784)
    val Green400 = Color(0x66BB6A)
    val Green500 = Color(0x4CAF50)
    val Green600 = Color(0x43A047)
    val Green700 = Color(0x388E3C)
    val Green800 = Color(0x2E7D32)
    val Green900 = Color(0x1B5E20)

    val LightGreen50 = Color(0xF1F8E9)
    val LightGreen100 = Color(0xDCEDC8)
    val LightGreen200 = Color(0xC5E1A5)
    val LightGreen300 = Color(0xAED581)
    val LightGreen400 = Color(0x9CCC65)
    val LightGreen500 = Color(0x8BC34A)
    val LightGreen600 = Color(0x7CB342)
    val LightGreen700 = Color(0x689F38)
    val LightGreen800 = Color(0x558B2F)
    val LightGreen900 = Color(0x33691E)

    val Lime50 = Color(0xF9FBE7)
    val Lime100 = Color(0xF0F4C3)
    val Lime200 = Color(0xE6EE9C)
    val Lime300 = Color(0xDCE775)
    val Lime400 = Color(0xD4E157)
    val Lime500 = Color(0xCDDC39)
    val Lime600 = Color(0xC0CA33)
    val Lime700 = Color(0xAFB42B)
    val Lime800 = Color(0x9E9D24)
    val Lime900 = Color(0x827717)

    val Yellow50 = Color(0xFFFDE7)
    val Yellow100 = Color(0xFFF9C4)
    val Yellow200 = Color(0xFFF59D)
    val Yellow300 = Color(0xFFF176)
    val Yellow400 = Color(0xFFEE58)
    val Yellow500 = Color(0xFFEB3B)
    val Yellow600 = Color(0xFDD835)
    val Yellow700 = Color(0xFBC02D)
    val Yellow800 = Color(0xF9A825)
    val Yellow900 = Color(0xF57F17)

    val Amber50 = Color(0xFFF8E1)
    val Amber100 = Color(0xFFECB3)
    val Amber200 = Color(0xFFE082)
    val Amber300 = Color(0xFFD54F)
    val Amber400 = Color(0xFFCA28)
    val Amber500 = Color(0xFFC107)
    val Amber600 = Color(0xFFB300)
    val Amber700 = Color(0xFFA000)
    val Amber800 = Color(0xFF8F00)
    val Amber900 = Color(0xFF6F00)

    val Orange50 = Color(0xFFF3E0)
    val Orange100 = Color(0xFFE0B2)
    val Orange200 = Color(0xFFCC80)
    val Orange300 = Color(0xFFB74D)
    val Orange400 = Color(0xFFA726)
    val Orange500 = Color(0xFF9800)
    val Orange600 = Color(0xFB8C00)
    val Orange700 = Color(0xF57C00)
    val Orange800 = Color(0xEF6C00)
    val Orange900 = Color(0xE65100)

    val DeepOrange50 = Color(0xFBE9E7)
    val DeepOrange100 = Color(0xFFCCBC)
    val DeepOrange200 = Color(0xFFAB91)
    val DeepOrange300 = Color(0xFF8A65)
    val DeepOrange400 = Color(0xFF7043)
    val DeepOrange500 = Color(0xFF5722)
    val DeepOrange600 = Color(0xF4511E)
    val DeepOrange700 = Color(0xE64A19)
    val DeepOrange800 = Color(0xD84315)
    val DeepOrange900 = Color(0xBF360C)

    val Brown50 = Color(0xEFEBE9)
    val Brown100 = Color(0xD7CCC8)
    val Brown200 = Color(0xBCAAA4)
    val Brown300 = Color(0xA1887F)
    val Brown400 = Color(0x8D6E63)
    val Brown500 = Color(0x795548)
    val Brown600 = Color(0x6D4C41)
    val Brown700 = Color(0x5D4037)
    val Brown800 = Color(0x4E342E)
    val Brown900 = Color(0x3E2723)

    val Gray50 = Color(0xFAFAFA)
    val Gray100 = Color(0xF5F5F5)
    val Gray200 = Color(0xEEEEEE)
    val Gray300 = Color(0xE0E0E0)
    val Gray400 = Color(0xBDBDBD)
    val Gray500 = Color(0x9E9E9E)
    val Gray600 = Color(0x757575)
    val Gray700 = Color(0x616161)
    val Gray800 = Color(0x424242)
    val Gray900 = Color(0x212121)

    val BlueGrey50 = Color(0xECEFF1)
    val BlueGrey100 = Color(0xCFD8DC)
    val BlueGrey200 = Color(0xB0BEC5)
    val BlueGrey300 = Color(0x90A4AE)
    val BlueGrey400 = Color(0x78909C)
    val BlueGrey500 = Color(0x607D8B)
    val BlueGrey600 = Color(0x546E7A)
    val BlueGrey700 = Color(0x455A64)
    val BlueGrey800 = Color(0x37474F)
    val BlueGrey900 = Color(0x263238)
}
