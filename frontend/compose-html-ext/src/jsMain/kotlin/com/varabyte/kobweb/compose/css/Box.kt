package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/box-decoration-break
sealed interface BoxDecorationBreak : StylePropertyValue {
    companion object : CssGlobalValues<BoxDecorationBreak> {
        // Keyword
        val Slice get() = "slice".unsafeCast<BoxDecorationBreak>()
        val Clone get() = "clone".unsafeCast<BoxDecorationBreak>()
    }
}

fun StyleScope.boxDecorationBreak(boxDecorationBreak: BoxDecorationBreak) {
    property("box-decoration-break", boxDecorationBreak)
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/box-sizing
sealed interface BoxSizing : StylePropertyValue {
    companion object : CssGlobalValues<BoxSizing> {
        // Keyword
        val BorderBox get() = "border-box".unsafeCast<BoxSizing>()
        val ContentBox get() = "content-box".unsafeCast<BoxSizing>()
    }
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/box-sizing
fun StyleScope.boxSizing(boxSizing: BoxSizing) {
    boxSizing(boxSizing.toString())
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/box-shadow
fun StyleScope.boxShadow(value: String) {
    property("box-shadow", value)
}

fun StyleScope.boxShadow(
    offsetX: CSSLengthNumericValue = 0.px,
    offsetY: CSSLengthNumericValue = 0.px,
    blurRadius: CSSLengthNumericValue? = null,
    spreadRadius: CSSLengthNumericValue? = null,
    color: CSSColorValue? = null,
    inset: Boolean = false,
) {
    boxShadow(
        BoxShadow.of(
            offsetX = offsetX,
            offsetY = offsetY,
            blurRadius = blurRadius,
            spreadRadius = spreadRadius,
            color = color,
            inset = inset,
        ),
    )
}

/**
 * Creates a box-shadow property with a single shadow.
 * The property accepts either the [BoxShadow.None] value, the
 * default global keywords, which indicates no shadows, or a
 * list of shadows, created by using [BoxShadow.of], ordered
 * front to back.
 *
 * Usages:
 * ```kotlin
 * style {
 *     boxShadow(BoxShadow.None)
 * }
 * ```
 * Will generate:
 * ```css
 * box-shadow: none
 * ```
 *
 * ```kotlin
 * style {
 *     boxShadow(BoxShadow.Unset)
 * }
 * ```
 * Will generate:
 * ```css
 * box-shadow: unset
 * ```
 * ```kotlin
 * style {
 *     boxShadow(
 *         BoxShadow.of(
 *             offsetX = 0.px,
 *             offsetY = 1.px,
 *             blurRadius = 3.px,
 *             spreadRadius = 1.px,
 *             color = Colors.Black.copyf(alpha = 0.15f),
 *         ),
 *     )
 * }
 * ```
 * Will generate:
 * ```css
 * box-shadow: rgba(0, 0, 0, 0.15) 0px 1px 3px 1px;
 * ```
 *
 * @see [BoxShadow.Inherit]
 * @see [BoxShadow.Initial]
 * @see [BoxShadow.None]
 * @see [BoxShadow.Unset]
 * @see [BoxShadow.of]
 */
fun StyleScope.boxShadow(boxShadow: BoxShadow) {
    boxShadow(boxShadow.toString())
}

// Needed temporarily until we can remove the deprecated `vararg` version
fun StyleScope.boxShadow(boxShadow: BoxShadow.Listable) {
    boxShadow(boxShadow.unsafeCast<BoxShadow>())
}
@Deprecated("Use `boxShadow(BoxShadow.list(*boxShadows))` instead", ReplaceWith("boxShadow(BoxShadow.list(*boxShadows))"))
fun StyleScope.boxShadow(vararg boxShadows: BoxShadow.Listable) {
    boxShadow(BoxShadow.list(*boxShadows))
}

/**
 * The Kotlin representation of the CSS box-shadow.
 */
sealed interface BoxShadow : StylePropertyValue {
    sealed interface Listable : BoxShadow

    companion object : CssGlobalValues<BoxShadow> {
        // Keyword
        val None: BoxShadow get() = "none".unsafeCast<BoxShadow>()

        // Custom
        /**
         * Creates a shadow for the box-shadow property
         *
         * @property offsetX Specifies the **horizontal offset** of the shadow.
         * A positive value draws a shadow that is offset to the right
         * of the box, a negative length to the left.
         * @property offsetY Specifies the **vertical offset** of the shadow.
         * A positive value offsets the shadow down, a negative one up.
         * @property blurRadius Specifies the **blur radius**. Negative values are
         * invalid. If the blur value is zero, the shadow’s edge is sharp.
         * Otherwise, the larger the value, the more the shadow’s edge is blurred.
         * See [Shadow Blurring](https://www.w3.org/TR/css-backgrounds-3/#shadow-blur).
         * @property spreadRadius Specifies the **spread distance**. Positive values
         * cause the shadow to expand in all directions by the specified radius.
         * Negative values cause the shadow to contract.
         * See [Shadow Shape](https://www.w3.org/TR/css-backgrounds-3/#shadow-shape).
         * @property color Specifies the color of the shadow. If the color is absent,
         * it defaults to `currentColor` on CSS.
         * @property inset If `true`, the `inset` keyword is inserted at the end and
         * changes the drop shadow from an outer box-shadow (one that shadows the box
         * onto the canvas, as if it were lifted above the canvas) to an inner
         * box-shadow (one that shadows the canvas onto the box, as if the box were
         * cut out of the canvas and shifted behind it).
         */
        fun of(
            offsetX: CSSLengthNumericValue = 0.px,
            offsetY: CSSLengthNumericValue = 0.px,
            blurRadius: CSSLengthNumericValue? = null,
            spreadRadius: CSSLengthNumericValue? = null,
            color: CSSColorValue? = null,
            inset: Boolean = false,
        ) = buildString {
            if (inset) {
                append("inset")
                append(' ')
            }
            append(offsetX)
            append(' ')
            append(offsetY)

            if (blurRadius != null) {
                append(' ')
                append(blurRadius)
            }

            if (spreadRadius != null) {
                if (blurRadius == null) {
                    append(' ')
                    append('0')
                }
                append(' ')
                append(spreadRadius)
            }

            if (color != null) {
                append(' ')
                append(color)
            }
        }.unsafeCast<Listable>()


        /**
         * Creates a box-shadow property with multiple shadow.
         * The property accepts a list of shadows, created by
         * using [BoxShadow.of], ordered from front to back.
         *
         * **The style is not created if no shadows are specified.**
         *
         * Usage:
         * ```kotlin
         * style {
         *    boxShadow(
         *       BoxShadow.list(
         *          BoxShadow.of(
         *             offsetX = 0.px,
         *             offsetY = 1.px,
         *             blurRadius = 3.px,
         *             spreadRadius = 1.px,
         *             color = Colors.Black.copyf(alpha = 0.15f),
         *          ),
         *          BoxShadow.of(
         *             offsetX = 5.px,
         *             offsetY = 8.px,
         *             blurRadius = 10.px,
         *             spreadRadius = (-1).px,
         *             color = Colors.Black.copyf(alpha = 0.32f),
         *          ),
         *       )
         *    )
         * }
         * ```
         * Will generate:
         * ```css
         * box-shadow: rgba(0, 0, 0, 0.15) 0px 1px 3px 1px,
         *             rgba(0, 0, 0, 0.318) 5px 8px 10px -1px;
         * ```
         */
        fun list(vararg shadows: Listable): BoxShadow = shadows.joinToString().unsafeCast<BoxShadow>()
    }
}
