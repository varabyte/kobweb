package com.varabyte.kobweb.compose.foundation.layout

import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.style.KOBWEB_ARRANGE_BOTTOM
import com.varabyte.kobweb.compose.style.KOBWEB_ARRANGE_CENTER
import com.varabyte.kobweb.compose.style.KOBWEB_ARRANGE_END
import com.varabyte.kobweb.compose.style.KOBWEB_ARRANGE_FROM_STYLE
import com.varabyte.kobweb.compose.style.KOBWEB_ARRANGE_SPACED_BY
import com.varabyte.kobweb.compose.style.KOBWEB_ARRANGE_START
import com.varabyte.kobweb.compose.style.KOBWEB_ARRANGE_TOP
import com.varabyte.kobweb.compose.ui.Alignment
import org.jetbrains.compose.web.css.*

/**
 * Used to specify the arrangement of the layout's children in layouts like
 * [Row] or [Column] in their main axis direction (horizontal and vertical,
 * respectively).
 */
object Arrangement {
    /**
     * Specifies the horizontal arrangement of the children. Mostly used on [Row].
     */
    sealed interface Horizontal {
        /**
         * Spacing added between any two adjacent children.
         * defaults to `0.px`
         */
        val spacing: CSSLengthNumericValue get() = 0.px
    }

    /**
     * Specifies the vertical arrangement of the children. Mostly used on [Column].
     */
    sealed interface Vertical {
        /**
         * Spacing added between any two adjacent children.
         * defaults to `0.px`
         */
        val spacing: CSSLengthNumericValue get() = 0.px
    }

    /**
     * Specifies either horizontal or vertical arrangement, depending on the layout
     * main axis of the container layout.
     *
     * E.g.: [Row] will take a horizontal arrangement and [Column] takes vertical
     * arrangement.
     */
    sealed interface HorizontalOrVertical : Horizontal, Vertical {
        /**
         * Spacing added between any two adjacent children.
         * defaults to `0.px`
         */
        override val spacing: CSSLengthNumericValue get() = 0.px
    }

    data object End : Horizontal

    data object Start : Horizontal

    data object Top : Vertical

    data object Bottom : Vertical

    data object Center : HorizontalOrVertical

    data object SpaceEvenly : HorizontalOrVertical

    data object SpaceBetween : HorizontalOrVertical

    data object SpaceAround : HorizontalOrVertical

    /**
     * A special value indicating that this element's arrangement will be controlled manually using CSS styles.
     *
     * For example:
     *
     * ```
     * // We want to use CssStyle + breakpoints to control the layout of our row
     * val ResponsiveStyle = CssStyle {
     *   base { Modifier.justifyContent(Start) }
     *   Breakpoint.MD { Modifier.justifyContent(SpaceEvenly) }
     * }
     *
     * /* ... later ... */
     * // Row arrangement is controlled by the responsive style!
     * Row(ResponsiveStyle.toModifier(), horizontalArrangement = Arrangement.FromStyle)
     * ```
     *
     * Using this means you know what you are doing! And that you understand which display type is powering the
     * underlying element (flexbox for rows and columns). It will be up to you to use the right `justify` / `align`
     * modifier methods to get the behavior you want.
     */
    data object FromStyle : HorizontalOrVertical

    /**
     * Arranges the children of the container with a fixed [space] for both horizontal and vertical orientations.
     * This function is marked as stable, ensuring that its result can be safely used in recompositions.
     *
     * **Important: As we use CSS gap to apply the spacing, negative spaces will be ignored.**
     *
     * **Usage**:
     * ```kotlin
     * Column(
     *     verticalArrangement = Arrangement.spacedBy(space = 10.px),
     * ) {
     *     SpanText("1")
     *     SpanText("2")
     *     SpanText("3")
     * }
     * ```
     * The above code will generate the following html:
     * ```html
     * <div class="kobweb-col kobweb-arrange-spaced-by kobweb-arrange-start kobweb-align-start" style="--kobweb-arrange-spaced-by-value: 10px;">
     *     <span class="silk-span-text">1</span>
     *     <span class="silk-span-text">2</span>
     *     <span class="silk-span-text">3</span>
     * </div>
     * ```
     *
     * or
     *
     * ```kotlin
     * Row(
     *     horizontalArrangement = Arrangement.spacedBy(space = 10.px),
     * ) {
     *     SpanText("1")
     *     SpanText("2")
     *     SpanText("3")
     * }
     * ```
     * The above code will generate the following html:
     * ```html
     * <div class="kobweb-row kobweb-arrange-spaced-by kobweb-arrange-start kobweb-align-top" style="--kobweb-arrange-spaced-by-value: 10px;">
     *     <span class="silk-span-text">1</span>
     *     <span class="silk-span-text">2</span>
     *     <span class="silk-span-text">3</span>
     * </div>
     * ```
     *
     * @param space The spacing value between elements, defined using CSS size units.
     * Expects values between [[0,∞]].
     */
    fun spacedBy(space: CSSSizeValue<out CSSUnitLength>): HorizontalOrVertical =
        SpacedAlignedHorizontalOrVertical(space)

    /**
     * Arranges the children of the container with a fixed [space] for vertical orientations.
     * An [alignment] can be specified to align the spaced children vertically inside the parent,
     * in case there is height remaining.
     *
     * This function is marked as stable, ensuring that its result can be safely used in recompositions.
     *
     * **Important: As we use CSS gap to apply the spacing, negative spaces will be ignored.**
     *
     * **Usage**:
     * ```kotlin
     * Column(
     *     verticalArrangement = Arrangement.spacedBy(space = 10.px, alignment = Alignment.CenterVertically)),
     * ) {
     *     SpanText("1")
     *     SpanText("2")
     *     SpanText("3")
     * }
     * ```
     * The above code will generate the following html:
     * ```html
     * <div class="kobweb-col kobweb-arrange-spaced-by kobweb-arrange-center kobweb-align-start" style="--kobweb-arrange-spaced-by-value: 10px;">
     *     <span class="silk-span-text">1</span>
     *     <span class="silk-span-text">2</span>
     *     <span class="silk-span-text">3</span>
     * </div>
     * ```
     *
     * **Note**:
     * When using:
     *  * [Alignment.CenterVertically], the `kobweb-arrange-center` class is placed together with
     *  `kobweb-arrange-spaced-by`
     *  * [Alignment.Top], the `kobweb-arrange-top` class is placed together with
     *  `kobweb-arrange-spaced-by`
     *  * [Alignment.Bottom], the `kobweb-arrange-bottom` class is placed together with
     *  `kobweb-arrange-spaced-by`
     *
     * @param space The spacing value between elements, defined using CSS size units.
     * Expects value between [[0,∞]].
     * @see Alignment.CenterVertically
     * @see Alignment.Top
     * @see Alignment.Bottom
     */
    fun spacedBy(space: CSSSizeValue<out CSSUnitLength>, alignment: Alignment.Vertical): Vertical =
        SpacedVerticalAligned(space, alignment)

    /**
     * Arranges the children of the container with a fixed [space] for vertical orientations.
     * An [alignment] can be specified to align the spaced children vertically inside the parent,
     * in case there is width remaining.
     *
     * This function is marked as stable, ensuring that its result can be safely used in recompositions.
     *
     * **Important: As we use CSS gap to apply the spacing, negative spaces will be ignored.**
     *
     * **Usage**:
     * ```kotlin
     * Row(
     *     horizontalArrangement = Arrangement.spacedBy(space = 10.px, alignment = Alignment.CenterHorizontally),
     * ) {
     *     SpanText("1")
     *     SpanText("2")
     *     SpanText("3")
     * }
     * ```
     * The above code will generate the following html:
     * ```html
     * <div class="kobweb-row kobweb-arrange-spaced-by kobweb-arrange-center kobweb-align-top" style="--kobweb-arrange-spaced-by-value: 10px;">
     *     <span class="silk-span-text">1</span>
     *     <span class="silk-span-text">2</span>
     *     <span class="silk-span-text">3</span>
     * </div>
     * ```
     *
     * **Note**:
     * When using:
     *  * [Alignment.CenterHorizontally], the `kobweb-arrange-center` class is placed together with
     *  `kobweb-arrange-spaced-by`
     *  * [Alignment.Start], the `kobweb-arrange-start` class is placed together with
     *  `kobweb-arrange-spaced-by`
     *  * [Alignment.End], the `kobweb-arrange-end` class is placed together with
     *  `kobweb-arrange-spaced-by`
     *
     * @param space The spacing value between elements, defined using CSS size units.
     * Expects value between [[0,∞]].
     * @see Alignment.CenterHorizontally
     * @see Alignment.Start
     * @see Alignment.End
     */
    fun spacedBy(space: CSSSizeValue<out CSSUnitLength>, alignment: Alignment.Horizontal): Horizontal =
        SpacedHorizontalAligned(space, alignment)
}

/**
 * Sealed class representing a spaced an [Arrangement] with a given spacing.
 *
 * @property spacing The spacing value used in the arrangement.
 */
internal sealed class SpacedAligned(
    override val spacing: CSSSizeValue<out CSSUnitLength>,
) : Arrangement.HorizontalOrVertical {
    /**
     * The CSS classes applied to the arrangement.
     */
    abstract val classes: Array<String>
}

/**
 * Representation either horizontal or vertical spaced and aligned arrangement.
 *
 * @property spacing The spacing value used in the arrangement.
 */
internal data class SpacedAlignedHorizontalOrVertical(
    override val spacing: CSSSizeValue<out CSSUnitLength>,
) : SpacedAligned(spacing) {
    // Custom classes for default alignment applied for spacing and alignment
    override val classes = arrayOf(KOBWEB_ARRANGE_SPACED_BY, KOBWEB_ARRANGE_START)
}

/**
 * Representation of a vertically spaced and aligned arrangement.
 *
 * @property spacing The spacing value used in the arrangement.
 * @property alignment The vertical alignment used in the arrangement.
 */
internal data class SpacedVerticalAligned(
    override val spacing: CSSSizeValue<out CSSUnitLength>,
    val alignment: Alignment.Vertical,
) : SpacedAligned(spacing), Arrangement.Vertical {
    // Custom classes for vertical alignment based on the alignment type
    override val classes: Array<String> = arrayOf(
        KOBWEB_ARRANGE_SPACED_BY,
        when (alignment) {
            Alignment.Bottom -> KOBWEB_ARRANGE_BOTTOM
            Alignment.CenterVertically -> KOBWEB_ARRANGE_CENTER
            Alignment.FromStyle -> KOBWEB_ARRANGE_FROM_STYLE
            Alignment.Top -> KOBWEB_ARRANGE_TOP
        },
    )
}

/**
 * Representation of a horizontally spaced and aligned arrangement.
 *
 * @property spacing The spacing value used in the arrangement.
 * @property alignment The horizontal alignment used in the arrangement.
 */
internal data class SpacedHorizontalAligned(
    override val spacing: CSSSizeValue<out CSSUnitLength>,
    val alignment: Alignment.Horizontal,
) : SpacedAligned(spacing), Arrangement.Horizontal {
    // Custom classes for horizontal alignment based on the alignment type
    override val classes: Array<String> = arrayOf(
        KOBWEB_ARRANGE_SPACED_BY,
        when (alignment) {
            Alignment.Start -> KOBWEB_ARRANGE_START
            Alignment.CenterHorizontally -> KOBWEB_ARRANGE_CENTER
            Alignment.FromStyle -> KOBWEB_ARRANGE_FROM_STYLE
            Alignment.End -> KOBWEB_ARRANGE_END
        },
    )
}
