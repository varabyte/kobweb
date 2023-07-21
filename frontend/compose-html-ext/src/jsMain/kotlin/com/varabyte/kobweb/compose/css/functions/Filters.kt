package com.varabyte.kobweb.compose.css.functions

import org.jetbrains.compose.web.css.*

class CSSFilter internal constructor(private val value: String) : CSSStyleValue {
    override fun toString() = value
}

// https://developer.mozilla.org/en-US/docs/Web/CSS/filter-function/blur
fun blur(radius: CSSLengthValue) = CSSFilter("blur($radius)")

// https://developer.mozilla.org/en-US/docs/Web/CSS/filter-function/brightness
fun brightness(amount: Number) = CSSFilter("brightness($amount)")
fun brightness(amount: CSSPercentageValue) = CSSFilter("brightness($amount)")

// https://developer.mozilla.org/en-US/docs/Web/CSS/filter-function/contrast
fun contrast(amount: Number) = CSSFilter("contrast($amount)")
fun contrast(amount: CSSPercentageValue) = CSSFilter("contrast($amount)")

// https://developer.mozilla.org/en-US/docs/Web/CSS/filter-function/drop-shadow
fun dropShadow(
    offsetX: CSSLengthValue,
    offsetY: CSSLengthValue,
    blurRadius: CSSLengthValue? = null,
    color: CSSColorValue? = null
): CSSFilter {
    val args = buildString {
        append(offsetX)
        append(' '); append(offsetY)
        if (blurRadius != null) {
            append(' '); append(blurRadius)
        }
        if (color != null) {
            append(' '); append(color)
        }
    }
    return CSSFilter("drop-shadow($args)")
}

// https://developer.mozilla.org/en-US/docs/Web/CSS/filter-function/grayscale
fun grayscale(amount: Number = 1) = CSSFilter("grayscale($amount)")
fun grayscale(amount: CSSPercentageValue) = CSSFilter("grayscale($amount)")

// https://developer.mozilla.org/en-US/docs/Web/CSS/filter-function/hue-rotate
fun hueRotate(angle: CSSAngleValue) = CSSFilter("hue-rotate($angle)")

// https://developer.mozilla.org/en-US/docs/Web/CSS/filter-function/invert
fun invert(amount: Number = 1) = CSSFilter("invert($amount)")
fun invert(amount: CSSPercentageValue) = CSSFilter("invert($amount)")

// https://developer.mozilla.org/en-US/docs/Web/CSS/filter-function/opacity
fun opacity(amount: Number = 1) = CSSFilter("opacity($amount)")
fun opacity(amount: CSSPercentageValue) = CSSFilter("opacity($amount)")

// https://developer.mozilla.org/en-US/docs/Web/CSS/filter-function/saturate
fun saturate(amount: Number = 1) = CSSFilter("saturate($amount)")
fun saturate(amount: CSSPercentageValue) = CSSFilter("saturate($amount)")

// https://developer.mozilla.org/en-US/docs/Web/CSS/filter-function/sepia
fun sepia(amount: Number = 1) = CSSFilter("sepia($amount)")
fun sepia(amount: CSSPercentageValue) = CSSFilter("sepia($amount)")
