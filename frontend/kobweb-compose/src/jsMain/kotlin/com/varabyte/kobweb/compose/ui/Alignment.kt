package com.varabyte.kobweb.compose.ui

sealed interface Alignment {
    sealed interface Vertical : Alignment
    sealed interface Horizontal : Alignment

    object TopStart : Alignment
    object TopCenter : Alignment
    object TopEnd : Alignment
    object CenterStart : Alignment
    object Center : Alignment
    object CenterEnd : Alignment
    object BottomStart : Alignment
    object BottomCenter : Alignment
    object BottomEnd : Alignment

    object Top : Vertical
    object CenterVertically : Vertical
    object Bottom : Vertical

    object Start : Horizontal
    object CenterHorizontally : Horizontal
    object End : Horizontal

    /**
     * A special value indicating that this element's alignment will be controlled manually using CSS styles.
     *
     * For example:
     *
     * ```
     * // We want to use componentstyle + breakpoints to control the layout of our row
     * val ResponsiveStyle = ComponentStyle("responsive") {
     *   base { Modifier.alignItems(Top) }
     *   Breakpoints.MD { Modifier.alignItems(Center) }
     * }
     *
     * /* ... later ... */
     * Row(ResponsiveStyle.toModifier(), verticalAlignment = Alignment.FromStyle)
     * ```
     *
     * Using this means you know what you are doing! And that you understand that the underlying element is using
     * flexbox display, and that you understand what that even means! It will be up to you to use the right
     * `justify` / `align` modifier methods to get the behavior you want.
     */
    object FromStyle : Alignment
}