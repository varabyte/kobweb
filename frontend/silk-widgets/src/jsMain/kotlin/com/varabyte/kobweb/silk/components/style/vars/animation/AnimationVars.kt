package com.varabyte.kobweb.silk.components.style.vars.animation

import com.varabyte.kobweb.compose.css.*
import org.jetbrains.compose.web.css.*

object TransitionDurationVars {
    val Instant by StyleVariable<CSSTimeNumericValue>(prefix = "silk", defaultFallback = 0.ms)
    val UltraFast by StyleVariable<CSSTimeNumericValue>(prefix = "silk", defaultFallback = 50.ms)
    val VeryFast by StyleVariable<CSSTimeNumericValue>(prefix = "silk", defaultFallback = 100.ms)
    val Fast by StyleVariable<CSSTimeNumericValue>(prefix = "silk", defaultFallback = 150.ms)
    val Normal by StyleVariable<CSSTimeNumericValue>(prefix = "silk", defaultFallback = 200.ms)
    val Slow by StyleVariable<CSSTimeNumericValue>(prefix = "silk", defaultFallback = 300.ms)
    val VerySlow by StyleVariable<CSSTimeNumericValue>(prefix = "silk", defaultFallback = 400.ms)
    val UltraSlow by StyleVariable<CSSTimeNumericValue>(prefix = "silk", defaultFallback = 500.ms)
}
