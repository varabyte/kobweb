@file:Suppress("ConvertObjectToDataObject") // Don't create data objects, no need to generate extra code

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

/**
 * Used to specify the arrangement of the layout's children in layouts like
 * [Row] or [Column] in their main axis direction (horizontal and vertical,
 * respectively).
 */
object Arrangement {
    /**
     * Base class for arrangement values that makes sense in a horizontal direction.
     */
    sealed interface Horizontal

    /**
     * Base class for arrangement values that makes sense in a vertical direction.
     */
    sealed interface Vertical

    /**
     * Base class for arrangement values that can be passed in as either horizontal or vertical arrangement parameters.
     */
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
    object FromStyle : HorizontalOrVertical

    /**
     * Arranges the children of the container with a fixed [space] for both horizontal and vertical orientations.
     * This function is marked as stable, ensuring that its result can be safely used in recompositions.
     *
     * **Important: As we use the CSS `gap` property to apply the spacing, negative spaces will be ignored.**
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
     * <div class="kobweb-col kobweb-arrange-spaced-by kobweb-arrange-start kobweb-align-start" style="--kobweb-arrange-spaced-by: 10px;">
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
     * <div class="kobweb-row kobweb-arrange-spaced-by kobweb-arrange-start kobweb-align-top" style="--kobweb-arrange-spaced-by: 10px;">
     *     <span class="silk-span-text">1</span>
     *     <span class="silk-span-text">2</span>
     *     <span class="silk-span-text">3</span>
     * </div>
     * ```
     *
     * @param space The space between adjacent children. If given as a percentage, the value is calculated relative to
     * the size of the container. Expects a value between [[0,∞]].
     */
    fun spacedBy(space: CSSLengthOrPercentageNumericValue): HorizontalOrVertical =
        SpacedAligned.HorizontalOrVertical(space)

    /**
     * Arranges the children of the container with a fixed [space] for vertical orientations.
     * An [alignment] can be specified to align the spaced children vertically inside the parent,
     * in case there is height remaining.
     *
     * This function is marked as stable, ensuring that its result can be safely used in recompositions.
     *
     * **Important: As we use the CSS `gap` property to apply the spacing, negative spaces will be ignored.**
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
     * <div class="kobweb-col kobweb-arrange-spaced-by kobweb-arrange-center kobweb-align-start" style="--kobweb-arrange-spaced-by: 10px;">
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
     * @param space The space between adjacent children. If given as a percentage, the value is calculated relative to
     * the size of the container. Expects a value between [[0,∞]].
     * @see Alignment.CenterVertically
     * @see Alignment.Top
     * @see Alignment.Bottom
     */
    fun spacedBy(space: CSSLengthOrPercentageNumericValue, alignment: Alignment.Vertical): Vertical =
        SpacedAligned.Vertical(space, alignment)

    /**
     * Arranges the children of the container with a fixed [space] for vertical orientations.
     * An [alignment] can be specified to align the spaced children vertically inside the parent,
     * in case there is width remaining.
     *
     * This function is marked as stable, ensuring that its result can be safely used in recompositions.
     *
     * **Important: As we use the CSS `gap` property to apply the spacing, negative spaces will be ignored.**
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
     * <div class="kobweb-row kobweb-arrange-spaced-by kobweb-arrange-center kobweb-align-top" style="--kobweb-arrange-spaced-by: 10px;">
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
     * @param space The space between adjacent children. If given as a percentage, the value is calculated relative to
     * the size of the container. Expects a value between [[0,∞]].
     * @see Alignment.CenterHorizontally
     * @see Alignment.Start
     * @see Alignment.End
     */
    fun spacedBy(space: CSSLengthOrPercentageNumericValue, alignment: Alignment.Horizontal): Horizontal =
        SpacedAligned.Horizontal(space, alignment)
}

/** Arrangement with spacing between adjacent children. */
internal sealed class SpacedAligned(
    val spacing: CSSLengthOrPercentageNumericValue,
) : Arrangement.HorizontalOrVertical {
    /**
     * The CSS classes applied to the arrangement.
     */
    abstract val classNames: Array<String>

    /** Represents a spaced (either horizontally or vertically) and aligned arrangement. */
    class HorizontalOrVertical(
        spacing: CSSLengthOrPercentageNumericValue,
    ) : SpacedAligned(spacing) {
        // Custom classes for default alignment applied for spacing and alignment
        override val classNames = arrayOf(KOBWEB_ARRANGE_SPACED_BY, KOBWEB_ARRANGE_START)
    }

    /**
     * Represents a vertically spaced and aligned arrangement.
     *
     * @param alignment The vertical alignment used in the arrangement.
     */
    class Vertical(
        spacing: CSSLengthOrPercentageNumericValue,
        alignment: Alignment.Vertical,
    ) : SpacedAligned(spacing) {
        // Custom classes for vertical alignment based on the alignment type
        override val classNames: Array<String> = arrayOf(
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
     * Represents a horizontally spaced and aligned arrangement.
     *
     * @param alignment The horizontal alignment used in the arrangement.
     */
    class Horizontal(
        spacing: CSSLengthOrPercentageNumericValue,
        alignment: Alignment.Horizontal,
    ) : SpacedAligned(spacing) {
        // Custom classes for horizontal alignment based on the alignment type
        override val classNames: Array<String> = arrayOf(
            KOBWEB_ARRANGE_SPACED_BY,
            when (alignment) {
                Alignment.Start -> KOBWEB_ARRANGE_START
                Alignment.CenterHorizontally -> KOBWEB_ARRANGE_CENTER
                Alignment.FromStyle -> KOBWEB_ARRANGE_FROM_STYLE
                Alignment.End -> KOBWEB_ARRANGE_END
            },
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        if (other::class != this::class) return false
        @Suppress("NAME_SHADOWING") val other = other as SpacedAligned

        return this.spacing == other.spacing && this.classNames.contentEquals(other.classNames)
    }

    override fun hashCode(): Int {
        var result = spacing.hashCode()
        result = 31 * result + classNames.contentHashCode()
        return result
    }
}
