package com.varabyte.kobweb.silk.theme.colors

import com.varabyte.kobweb.compose.ui.graphics.Color

/**
 * A range of colors related by some unifying overall concept.
 */
@Suppress("PropertyName") // Can't start with a number
interface ColorScheme {
    val _50: Color
    val _100: Color
    val _200: Color
    val _300: Color
    val _400: Color
    val _500: Color
    val _600: Color
    val _700: Color
    val _800: Color
    val _900: Color
}

/**
 * A veritable rainbow of color schemes to choose from.
 *
 * Special thanks to Chakra UI [here](https://github.com/chakra-ui/chakra-ui/blob/3c946c4b47f36b09d219555ba185e58a62bd2378/packages/components/theme/src/foundations/colors.ts)
 * and Material design [here](https://material.io/design/color/the-color-system.html#tools-for-picking-colors).
 */
@Suppress("unused")
object ColorSchemes {
    val Red = object : ColorScheme {
        override val _50 = Color.rgb(0xFFEBEE)
        override val _100 = Color.rgb(0xFFCDD2)
        override val _200 = Color.rgb(0xEF9A9A)
        override val _300 = Color.rgb(0xE57373)
        override val _400 = Color.rgb(0xEF5350)
        override val _500 = Color.rgb(0xF44336)
        override val _600 = Color.rgb(0xE53935)
        override val _700 = Color.rgb(0xD32F2F)
        override val _800 = Color.rgb(0xC62828)
        override val _900 = Color.rgb(0xB71C1C)
    }

    val Pink = object : ColorScheme {
        override val _50 = Color.rgb(0xFCE4EC)
        override val _100 = Color.rgb(0xF8BBD0)
        override val _200 = Color.rgb(0xF48FB1)
        override val _300 = Color.rgb(0xF06292)
        override val _400 = Color.rgb(0xEC407A)
        override val _500 = Color.rgb(0xE91E63)
        override val _600 = Color.rgb(0xD81B60)
        override val _700 = Color.rgb(0xC2185B)
        override val _800 = Color.rgb(0xAD1457)
        override val _900 = Color.rgb(0x880E4F)
    }

    val Purple = object : ColorScheme {
        override val _50 = Color.rgb(0xF3E5F5)
        override val _100 = Color.rgb(0xE1BEE7)
        override val _200 = Color.rgb(0xCE93D8)
        override val _300 = Color.rgb(0xBA68C8)
        override val _400 = Color.rgb(0xAB47BC)
        override val _500 = Color.rgb(0x9C27B0)
        override val _600 = Color.rgb(0x8E24AA)
        override val _700 = Color.rgb(0x7B1FA2)
        override val _800 = Color.rgb(0x6A1B9A)
        override val _900 = Color.rgb(0x4A148C)
    }

    val DeepPurple = object : ColorScheme {
        override val _50 = Color.rgb(0xEDE7F6)
        override val _100 = Color.rgb(0xD1C4E9)
        override val _200 = Color.rgb(0xB39DDB)
        override val _300 = Color.rgb(0x9575CD)
        override val _400 = Color.rgb(0x7E57C2)
        override val _500 = Color.rgb(0x673AB7)
        override val _600 = Color.rgb(0x5E35B1)
        override val _700 = Color.rgb(0x512DA8)
        override val _800 = Color.rgb(0x4527A0)
        override val _900 = Color.rgb(0x311B92)
    }

    val Indigo = object : ColorScheme {
        override val _50 = Color.rgb(0xE8EAF6)
        override val _100 = Color.rgb(0xC5CAE9)
        override val _200 = Color.rgb(0x9FA8DA)
        override val _300 = Color.rgb(0x7986CB)
        override val _400 = Color.rgb(0x5C6BC0)
        override val _500 = Color.rgb(0x3F51B5)
        override val _600 = Color.rgb(0x3949AB)
        override val _700 = Color.rgb(0x303F9F)
        override val _800 = Color.rgb(0x283593)
        override val _900 = Color.rgb(0x1A237E)
    }

    val Blue = object : ColorScheme {
        override val _50 = Color.rgb(0xE3F2FD)
        override val _100 = Color.rgb(0xBBDEFB)
        override val _200 = Color.rgb(0x90CAF9)
        override val _300 = Color.rgb(0x64B5F6)
        override val _400 = Color.rgb(0x42A5F5)
        override val _500 = Color.rgb(0x2196F3)
        override val _600 = Color.rgb(0x1E88E5)
        override val _700 = Color.rgb(0x1976D2)
        override val _800 = Color.rgb(0x1565C0)
        override val _900 = Color.rgb(0x0D47A1)
    }

    val LightBlue = object : ColorScheme {
        override val _50 = Color.rgb(0xE1F5FE)
        override val _100 = Color.rgb(0xB3E5FC)
        override val _200 = Color.rgb(0x81D4FA)
        override val _300 = Color.rgb(0x4FC3F7)
        override val _400 = Color.rgb(0x29B6F6)
        override val _500 = Color.rgb(0x03A9F4)
        override val _600 = Color.rgb(0x039BE5)
        override val _700 = Color.rgb(0x0288D1)
        override val _800 = Color.rgb(0x0277BD)
        override val _900 = Color.rgb(0x01579B)
    }

    val Cyan = object : ColorScheme {
        override val _50 = Color.rgb(0xE0F7FA)
        override val _100 = Color.rgb(0xB2EBF2)
        override val _200 = Color.rgb(0x80DEEA)
        override val _300 = Color.rgb(0x4DD0E1)
        override val _400 = Color.rgb(0x26C6DA)
        override val _500 = Color.rgb(0x00BCD4)
        override val _600 = Color.rgb(0x00ACC1)
        override val _700 = Color.rgb(0x0097A7)
        override val _800 = Color.rgb(0x00838F)
        override val _900 = Color.rgb(0x006064)
    }

    val Teal = object : ColorScheme {
        override val _50 = Color.rgb(0xE0F2F1)
        override val _100 = Color.rgb(0xB2DFDB)
        override val _200 = Color.rgb(0x80CBC4)
        override val _300 = Color.rgb(0x4DB6AC)
        override val _400 = Color.rgb(0x26A69A)
        override val _500 = Color.rgb(0x009688)
        override val _600 = Color.rgb(0x00897B)
        override val _700 = Color.rgb(0x00796B)
        override val _800 = Color.rgb(0x00695C)
        override val _900 = Color.rgb(0x004D40)
    }

    val Green = object : ColorScheme {
        override val _50 = Color.rgb(0xE8F5E9)
        override val _100 = Color.rgb(0xC8E6C9)
        override val _200 = Color.rgb(0xA5D6A7)
        override val _300 = Color.rgb(0x81C784)
        override val _400 = Color.rgb(0x66BB6A)
        override val _500 = Color.rgb(0x4CAF50)
        override val _600 = Color.rgb(0x43A047)
        override val _700 = Color.rgb(0x388E3C)
        override val _800 = Color.rgb(0x2E7D32)
        override val _900 = Color.rgb(0x1B5E20)
    }

    val LightGreen = object : ColorScheme {
        override val _50 = Color.rgb(0xF1F8E9)
        override val _100 = Color.rgb(0xDCEDC8)
        override val _200 = Color.rgb(0xC5E1A5)
        override val _300 = Color.rgb(0xAED581)
        override val _400 = Color.rgb(0x9CCC65)
        override val _500 = Color.rgb(0x8BC34A)
        override val _600 = Color.rgb(0x7CB342)
        override val _700 = Color.rgb(0x689F38)
        override val _800 = Color.rgb(0x558B2F)
        override val _900 = Color.rgb(0x33691E)
    }

    val Lime = object : ColorScheme {
        override val _50 = Color.rgb(0xF9FBE7)
        override val _100 = Color.rgb(0xF0F4C3)
        override val _200 = Color.rgb(0xE6EE9C)
        override val _300 = Color.rgb(0xDCE775)
        override val _400 = Color.rgb(0xD4E157)
        override val _500 = Color.rgb(0xCDDC39)
        override val _600 = Color.rgb(0xC0CA33)
        override val _700 = Color.rgb(0xAFB42B)
        override val _800 = Color.rgb(0x9E9D24)
        override val _900 = Color.rgb(0x827717)
    }

    val Yellow = object : ColorScheme {
        override val _50 = Color.rgb(0xFFFDE7)
        override val _100 = Color.rgb(0xFFF9C4)
        override val _200 = Color.rgb(0xFFF59D)
        override val _300 = Color.rgb(0xFFF176)
        override val _400 = Color.rgb(0xFFEE58)
        override val _500 = Color.rgb(0xFFEB3B)
        override val _600 = Color.rgb(0xFDD835)
        override val _700 = Color.rgb(0xFBC02D)
        override val _800 = Color.rgb(0xF9A825)
        override val _900 = Color.rgb(0xF57F17)
    }

    val Amber = object : ColorScheme {
        override val _50 = Color.rgb(0xFFF8E1)
        override val _100 = Color.rgb(0xFFECB3)
        override val _200 = Color.rgb(0xFFE082)
        override val _300 = Color.rgb(0xFFD54F)
        override val _400 = Color.rgb(0xFFCA28)
        override val _500 = Color.rgb(0xFFC107)
        override val _600 = Color.rgb(0xFFB300)
        override val _700 = Color.rgb(0xFFA000)
        override val _800 = Color.rgb(0xFF8F00)
        override val _900 = Color.rgb(0xFF6F00)
    }

    val Orange = object : ColorScheme {
        override val _50 = Color.rgb(0xFFF3E0)
        override val _100 = Color.rgb(0xFFE0B2)
        override val _200 = Color.rgb(0xFFCC80)
        override val _300 = Color.rgb(0xFFB74D)
        override val _400 = Color.rgb(0xFFA726)
        override val _500 = Color.rgb(0xFF9800)
        override val _600 = Color.rgb(0xFB8C00)
        override val _700 = Color.rgb(0xF57C00)
        override val _800 = Color.rgb(0xEF6C00)
        override val _900 = Color.rgb(0xE65100)
    }

    val DeepOrange = object : ColorScheme {
        override val _50 = Color.rgb(0xFBE9E7)
        override val _100 = Color.rgb(0xFFCCBC)
        override val _200 = Color.rgb(0xFFAB91)
        override val _300 = Color.rgb(0xFF8A65)
        override val _400 = Color.rgb(0xFF7043)
        override val _500 = Color.rgb(0xFF5722)
        override val _600 = Color.rgb(0xF4511E)
        override val _700 = Color.rgb(0xE64A19)
        override val _800 = Color.rgb(0xD84315)
        override val _900 = Color.rgb(0xBF360C)
    }

    val Brown = object : ColorScheme {
        override val _50 = Color.rgb(0xEFEBE9)
        override val _100 = Color.rgb(0xD7CCC8)
        override val _200 = Color.rgb(0xBCAAA4)
        override val _300 = Color.rgb(0xA1887F)
        override val _400 = Color.rgb(0x8D6E63)
        override val _500 = Color.rgb(0x795548)
        override val _600 = Color.rgb(0x6D4C41)
        override val _700 = Color.rgb(0x5D4037)
        override val _800 = Color.rgb(0x4E342E)
        override val _900 = Color.rgb(0x3E2723)
    }

    val Gray = object : ColorScheme {
        override val _50 = Color.rgb(0xFAFAFA)
        override val _100 = Color.rgb(0xF5F5F5)
        override val _200 = Color.rgb(0xEEEEEE)
        override val _300 = Color.rgb(0xE0E0E0)
        override val _400 = Color.rgb(0xBDBDBD)
        override val _500 = Color.rgb(0x9E9E9E)
        override val _600 = Color.rgb(0x757575)
        override val _700 = Color.rgb(0x616161)
        override val _800 = Color.rgb(0x424242)
        override val _900 = Color.rgb(0x212121)
    }

    val BlueGray = object : ColorScheme {
        override val _50 = Color.rgb(0xECEFF1)
        override val _100 = Color.rgb(0xCFD8DC)
        override val _200 = Color.rgb(0xB0BEC5)
        override val _300 = Color.rgb(0x90A4AE)
        override val _400 = Color.rgb(0x78909C)
        override val _500 = Color.rgb(0x607D8B)
        override val _600 = Color.rgb(0x546E7A)
        override val _700 = Color.rgb(0x455A64)
        override val _800 = Color.rgb(0x37474F)
        override val _900 = Color.rgb(0x263238)
    }
}
