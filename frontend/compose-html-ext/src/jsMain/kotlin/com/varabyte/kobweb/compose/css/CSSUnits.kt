package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*
import kotlin.math.PI

typealias CSSTimeValue = CSSSizeValue<out CSSUnitTime>

fun CSSAngleValue.toDegrees() = when (this.unit.toString()) {
    "deg" -> value
    "grad" -> (value * 0.9f)
    "rad" -> (value * 180f / PI.toFloat())
    "turn" -> (value * 360f)
    else -> error("Unexpected unit type ${this.unit}")
} % 360f
