package com.varabyte.kobweb.silk.theme.animation

import com.varabyte.kobweb.compose.css.*
import org.jetbrains.compose.web.css.*

val TransitionDurationUltraFastVar by StyleVariable<CSSTimeValue>(prefix = "silk", defaultFallback = 50.ms)
val TransitionDurationVeryFastVar by StyleVariable<CSSTimeValue>(prefix = "silk", defaultFallback = 100.ms)
val TransitionDurationFastVar by StyleVariable<CSSTimeValue>(prefix = "silk", defaultFallback = 150.ms)
val TransitionDurationNormalVar by StyleVariable<CSSTimeValue>(prefix = "silk", defaultFallback = 200.ms)
val TransitionDurationSlowVar by StyleVariable<CSSTimeValue>(prefix = "silk", defaultFallback = 300.ms)
val TransitionDurationVerySlowVar by StyleVariable<CSSTimeValue>(prefix = "silk", defaultFallback = 400.ms)
val TransitionDurationUltraSlowVar by StyleVariable<CSSTimeValue>(prefix = "silk", defaultFallback = 500.ms)
