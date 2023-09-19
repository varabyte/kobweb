package com.varabyte.kobweb.silk.theme.colors.palette

import com.varabyte.kobweb.compose.ui.graphics.Color

object SilkWidgetKobwebColorGroups {
    interface Link {
        /** Color used for links that the user has never clicked on before. */
        val default: Color

        /** Color used for links that have been visited before. */
        val visited: Color
    }

    class MutableLink(palette: MutablePalette) : MutablePalette.ColorGroup(palette, "link"), Link {
        override var default by paletteEntry()
        override var visited by paletteEntry()

        fun set(
            default: Color,
            visited: Color,
        ) {
            this.default = default
            this.visited = visited
        }
    }
}

val Palette.link: SilkWidgetKobwebColorGroups.Link get() = (this as MutablePalette).link
val MutablePalette.link: SilkWidgetKobwebColorGroups.MutableLink
    get() = SilkWidgetKobwebColorGroups.MutableLink(this)
