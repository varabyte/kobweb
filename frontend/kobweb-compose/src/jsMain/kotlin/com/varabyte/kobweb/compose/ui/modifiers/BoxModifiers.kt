package com.varabyte.kobweb.compose.ui.modifiers

import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.styleModifier
import org.jetbrains.compose.web.css.*

fun Modifier.boxDecorationBreak(boxDecorationBreak: BoxDecorationBreak) = styleModifier {
    boxDecorationBreak(boxDecorationBreak)
}

fun Modifier.boxSizing(boxSizing: BoxSizing) = styleModifier {
    boxSizing(boxSizing)
}

/**
 * Creates a single shadowed box-shadow to the desired element.
 *
 * Usage:
 * ```kotlin
 *  val ButtonBoxShadowVariant = ButtonStyle.addVariant {
 *     base {
 *          Modifier.boxShadow(
 *              offsetX = 0.px,
 *              offsetY = 1.px,
 *              blurRadius = 3.px,
 *              spreadRadius = 1.px,
 *              color = Colors.Black.copyf(alpha = 0.15f),
 *              inset = true,
 *          )
 *     }
 * }
 * ```
 * The previous example generates the following css:
 * ```css
 * @layer component-variant {
 *     .silk-button-box-shadow {
 *         box-shadow: rgba(0, 0, 0, 0.15) 0px 1px 3px 1px inset;
 *     }
 * }
 * ```
 *
 * @param offsetX Specifies the **horizontal offset** of the shadow.
 * A positive value draws a shadow that is offset to the right
 * of the box, a negative length to the left.
 * @param offsetY Specifies the **vertical offset** of the shadow.
 * A positive value offsets the shadow down, a negative one up.
 * @param blurRadius Specifies the **blur radius**. Negative values are
 * invalid. If the blur value is zero, the shadow’s edge is sharp.
 * Otherwise, the larger the value, the more the shadow’s edge is blurred.
 * See [Shadow Blurring](https://www.w3.org/TR/css-backgrounds-3/#shadow-blur).
 * @param spreadRadius Specifies the **spread distance**. Positive values
 * cause the shadow to expand in all directions by the specified radius.
 * Negative values cause the shadow to contract.
 * See [Shadow Shape](https://www.w3.org/TR/css-backgrounds-3/#shadow-shape).
 * @param color Specifies the color of the shadow. If the color is absent,
 * it defaults to `currentColor` on CSS.
 * @param inset If `true`, the `inset` keyword is inserted at the end and
 * changes the drop shadow from an outer box-shadow (one that shadows the box
 * onto the canvas, as if it were lifted above the canvas) to an inner
 * box-shadow (one that shadows the canvas onto the box, as if the box were
 * cut out of the canvas and shifted behind it).
 */
fun Modifier.boxShadow(
    offsetX: CSSLengthNumericValue = 0.px,
    offsetY: CSSLengthNumericValue = 0.px,
    blurRadius: CSSLengthNumericValue? = null,
    spreadRadius: CSSLengthNumericValue? = null,
    color: CSSColorValue? = null,
    inset: Boolean = false,
) = boxShadow(BoxShadow.of(offsetX, offsetY, blurRadius, spreadRadius, color, inset))

/**
 * The box-shadow property attaches one or more drop shadows to the box.
 * The property accepts either the [BoxShadow.None] value, which indicates no shadows,
 * or a list of shadows, created by using [BoxShadow.of], ordered front to back.
 *
 * Usage:
 * ```kotlin
 * val ButtonBoxShadowVariant = ButtonStyle.addVariant {
 *     base {
 *          Modifier.boxShadow(
 *              BoxShadow.of(
 *                  offsetX = 0.px,
 *                  offsetY = 1.px,
 *                  blurRadius = 3.px,
 *                  spreadRadius = 1.px,
 *                  color = Colors.Black.copyf(alpha = 0.15f),
 *              ),
 *              BoxShadow.of(
 *                  offsetX = 0.px,
 *                  offsetY = 1.px,
 *                  blurRadius = 2.px,
 *                  spreadRadius = 0.px,
 *                  color = Colors.Black.copyf(alpha = 0.3f),
 *              ),
 *          )
 *     }
 * }
 * ```
 * The previous example generates the following css:
 * ```css
 * @layer component-variant {
 *     .silk-button-box-shadow {
 *         box-shadow: box-shadow: rgba(0, 0, 0, 0.15) 0px 1px 3px 1px,
 *                     rgba(0, 0, 0, 0.298) 0px 1px 2px 0px;
 *     }
 * }
 * ```
 */
fun Modifier.boxShadow(vararg boxShadows: BoxShadow.Repeatable): Modifier = styleModifier {
    boxShadow(*boxShadows)
}

fun Modifier.boxShadow(boxShadows: List<BoxShadow.Repeatable>) = boxShadow(*boxShadows.toTypedArray())

/**
 * Creates a box-shadow property with a single shadow.
 * The property accepts either the [BoxShadow.None] value, the
 * default global keywords, which indicates no shadows, or a
 * list of shadows, created by using [BoxShadow.of], ordered
 * front to back.
 *
 * Usages:
 * ```kotlin
 * val ButtonBoxShadowVariant = ButtonStyle.addVariant {
 *     base {
 *         Modifier.boxShadow(BoxShadow.None)
 *     }
 * }
 * ```
 * Will generate:
 * ```css
 * @layer component-variant {
 *     .silk-button-box-shadow {
 *         box-shadow: none
 *     }
 * }
 * ```
 *
 * ```kotlin
 * val ButtonBoxShadowVariant = ButtonStyle.addVariant {
 *     base {
 *         Modifier.boxShadow(BoxShadow.Unset)
 *     }
 * }
 * ```
 * Will generate:
 * ```css
 * @layer component-variant {
 *     .silk-button-box-shadow {
 *         box-shadow: unset
 *     }
 * }
 * ```
 *
 * ```kotlin
 * val ButtonBoxShadowVariant = ButtonStyle.addVariant {
 *     base {
 *         Modifier.boxShadow(
 *             BoxShadow.of(
 *                 offsetX = 0.px,
 *                 offsetY = 1.px,
 *                 blurRadius = 3.px,
 *                 spreadRadius = 1.px,
 *                 color = Colors.Black.copyf(alpha = 0.15f),
 *             ),
 *         )
 *     }
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
fun Modifier.boxShadow(boxShadow: BoxShadow): Modifier = styleModifier {
    boxShadow(boxShadow)
}
