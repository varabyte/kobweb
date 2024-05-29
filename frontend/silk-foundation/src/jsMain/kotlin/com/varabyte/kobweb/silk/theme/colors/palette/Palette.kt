package com.varabyte.kobweb.silk.theme.colors.palette

import com.varabyte.kobweb.compose.ui.graphics.Color
import com.varabyte.kobweb.silk.theme.SilkTheme
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import kotlin.reflect.KProperty

/**
 * An extensible collection of colors, keyed by name.
 *
 * Any library building on top of Silk can add its own colors to this collection, and they are then encouraged to define
 * extension properties to make it easy for users to access them.
 */
interface Palette {
    operator fun get(key: String): Color?
    fun getValue(key: String) = get(key)!!
}

class MutablePalette : Palette {
    abstract class ColorGroup(private val palette: MutablePalette, private val groupName: String) {
        /**
         * A property delegate class which helps reduce boilerplate when defining palette color groups.
         *
         * @see paletteEntry
         */
        class EntryDelegate(private val palette: MutablePalette, private val prefix: String? = null) {
            operator fun getValue(thisRef: Any?, property: KProperty<*>): Color {
                return palette.colors.getValue(prefix.orEmpty() + property.name)
            }

            operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Color) {
                palette.colors[prefix.orEmpty() + property.name] = value
            }
        }

        /**
         * A property delegate which helps reduce boilerplate when defining palette color groups.
         *
         * For example, to create a color group for a `Button` widget:
         * ```
         * class MutableButton(palette: MutablePalette) : MutablePalette.ColorGroup(palette, "button"), Button {
         *    override var default by paletteEntry()
         *    override var hover by paletteEntry()
         *    override var focus by paletteEntry()
         *    override var pressed by paletteEntry()
         * }
         * ```
         * The above will create color entries for `button.default`, `button.hover`, `button.focus`, and `button.pressed` in the
         * underlying palette automatically.
         *
         * @see paletteEntry
         */
        fun paletteEntry() = EntryDelegate(palette, "$groupName.")
    }

    private val colors = mutableMapOf<String, Color>()
    override fun get(key: String) = colors[key]
    operator fun set(key: String, value: Color) { colors[key] = value }
}

interface Palettes {
    val light: Palette
    val dark: Palette

    operator fun get(colorMode: ColorMode) = when (colorMode) {
        ColorMode.LIGHT -> light
        ColorMode.DARK -> dark
    }
}

class MutablePalettes : Palettes {
    override val light: MutablePalette = MutablePalette()
    override val dark: MutablePalette = MutablePalette()
}

/**
 * Convenience method for fetching the silk palette associated with the target color mode, useful for when you aren't
 * in a `@Composable` scope (which is common when defining CssStyles).
 */
fun ColorMode.toPalette() = SilkTheme.palettes[this]

/**
 * Convenience property for discovering the color mode that this palette is associated with.
 */
val Palette.colorMode: ColorMode get() = when (this) {
    SilkTheme.palettes.light -> ColorMode.LIGHT
    SilkTheme.palettes.dark -> ColorMode.DARK
    else -> error("Got unexpected palette that's not part of `SilkTheme.palettes`")
}
