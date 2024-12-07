package com.varabyte.kobweb.compose.css.functions

import com.varabyte.kobweb.compose.css.*
import org.jetbrains.compose.web.css.*

/** @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/color_value/color-mix">color-mix</a> */
fun colorMix(
    interpolation: ColorInterpolationMethod,
    color1: CSSColorValue,
    color2: CSSColorValue,
): CSSColorValue = Color("color-mix($interpolation, $color1, $color2)")

/** @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/color_value/color-mix">color-mix</a> */
fun colorMix(
    interpolation: ColorInterpolationMethod,
    color1: Pair<CSSColorValue, CSSPercentageNumericValue>,
    color2: CSSColorValue,
): CSSColorValue = Color("color-mix($interpolation, ${color1.toCssString()}, $color2)")

/** @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/color_value/color-mix">color-mix</a> */
fun colorMix(
    interpolation: ColorInterpolationMethod,
    color1: CSSColorValue,
    color2: Pair<CSSColorValue, CSSPercentageNumericValue>,
): CSSColorValue = Color("color-mix($interpolation, $color1, ${color2.toCssString()})")

/** @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/color_value/color-mix">color-mix</a> */
fun colorMix(
    interpolation: ColorInterpolationMethod,
    color1: Pair<CSSColorValue, CSSPercentageNumericValue>,
    color2: Pair<CSSColorValue, CSSPercentageNumericValue>,
): CSSColorValue = Color("color-mix($interpolation, ${color1.toCssString()}, ${color2.toCssString()})")

private fun Pair<CSSColorValue, CSSPercentageNumericValue>.toCssString() = "$first $second"
