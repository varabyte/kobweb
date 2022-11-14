package com.varabyte.kobweb.compose.foundation.layout

object Arrangement {
    sealed interface Horizontal
    sealed interface Vertical
    sealed interface HorizontalOrVertical : Horizontal, Vertical

    object End : Horizontal
    object Start : Horizontal
    object Top : Vertical
    object Bottom : Vertical
    object Center : HorizontalOrVertical
    object SpaceEvenly : HorizontalOrVertical
    object SpaceBetween : HorizontalOrVertical
    object SpaceAround : HorizontalOrVertical

    /**
     * A special value indicating that this element's arrangement will be controlled manually using CSS styles.
     *
     * For example:
     *
     * ```
     * // We want to use componentstyle + breakpoints to control the layout of our row
     * val ResponsiveStyle = ComponentStyle("responsive") {
     *   base { Modifier.justifyContent(Start) }
     *   Breakpoints.MD { Modifier.justifyContent(SpaceEvenly) }
     * }
     *
     * /* ... later ... */
     * // Row arrangement is controlled by the responsive style!
     * Row(ResponsiveStyle.toModifier(), horizontalArrangement = Arrangement.FromStyle)
     * ```
     *
     * Using this means you know what you are doing! And that you understand that the underlying element is using
     * flexbox display, and that you understand what that even means! It will be up to you to use the right
     * `justify` / `align` modifier methods to get the behavior you want.
     */
    object FromStyle : HorizontalOrVertical
}
