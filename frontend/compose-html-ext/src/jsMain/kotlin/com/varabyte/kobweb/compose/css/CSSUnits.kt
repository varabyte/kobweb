package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*
import kotlin.math.PI

// The type aliases below are a copy of Compose HTML's type aliases org.jetbrains.compose.web.css.CSSUnits.kt
// with the key difference that they are based on `CSSNumericValue` instead of `CSSSizeValue`.
// This means that in addition to including raw values, they also cover the result of calculations, css functions like
// `min()`, and StyleVariables. These are intended to almost entirely replace the Compose HTML versions, as in general,
// when a something like a "length" value is needed in CSS, it does not necessarily need to be a raw value.
// Eventually, we would like the values of Compose HTML's type aliases to be based on `CSSNumericValue` as well, at which
// time these would no longer be necessary.

/**
 * A CSS numeric value representing an angle.
 *
 * This should be preferred over [CSSAngleValue] when used as a value passed to a CSS property,
 * as it covers a broader range of valid values.
 */
typealias CSSAngleNumericValue = CSSNumericValue<out CSSUnitAngle>

/**
 * A CSS numeric value representing a length or percentage.
 *
 * This should be preferred over [CSSLengthOrPercentageValue] when used as a value passed to a CSS property,
 * as it covers a broader range of valid values.
 */
typealias CSSLengthOrPercentageNumericValue = CSSNumericValue<out CSSUnitLengthOrPercentage>

/**
 * A CSS numeric value representing a length.
 *
 * This should be preferred over [CSSLengthValue] when used as a value passed to a CSS property,
 * as it covers a broader range of valid values.
 */
typealias CSSLengthNumericValue = CSSNumericValue<out CSSUnitLength>

/**
 * A CSS numeric value representing a percentage.
 *
 * This should be preferred over [CSSPercentageValue] when used as a value passed to a CSS property,
 * as it covers a broader range of valid values.
 */
typealias CSSPercentageNumericValue = CSSNumericValue<out CSSUnitPercentage>

// these do not have corresponding non-numeric type aliases defined in Compose HTML, but are named for consistency

/** A CSS numeric value representing a flex value. */
typealias CSSFlexNumericValue = CSSNumericValue<out CSSUnitFlex>

/** A CSS numeric value representing a time value. */
typealias CSSTimeNumericValue = CSSNumericValue<out CSSUnitTime>

@Deprecated("Use `CSSTimeNumericValue` instead", ReplaceWith("CSSTimeNumericValue"))
typealias CSSTimeValue = CSSTimeNumericValue

fun CSSAngleValue.toDegrees() = when (this.unit.toString()) {
    "deg" -> value
    "grad" -> (value * 0.9f)
    "rad" -> (value * 180f / PI.toFloat())
    "turn" -> (value * 360f)
    else -> error("Unexpected unit type ${this.unit}")
} % 360f
