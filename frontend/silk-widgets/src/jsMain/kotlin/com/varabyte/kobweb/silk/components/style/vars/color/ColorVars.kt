package com.varabyte.kobweb.silk.components.style.vars.color

import com.varabyte.kobweb.compose.css.*
import org.jetbrains.compose.web.css.*

val BackgroundColorVar by StyleVariable<CSSColorValue>(prefix = "silk")
val ColorVar by StyleVariable<CSSColorValue>(prefix = "silk")
val BorderColorVar by StyleVariable<CSSColorValue>(prefix = "silk")
val FocusOutlineColorVar by StyleVariable<CSSColorValue>(prefix = "silk")
val PlaceholderOpacityVar by StyleVariable<Number>(prefix = "silk", defaultFallback = 1.0)
val PlaceholderColorVar by StyleVariable<CSSColorValue>(prefix = "silk")
