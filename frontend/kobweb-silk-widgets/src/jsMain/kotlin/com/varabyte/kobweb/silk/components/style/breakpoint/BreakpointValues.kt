package com.varabyte.kobweb.silk.components.style.breakpoint

import org.jetbrains.compose.web.css.CSSUnitValue
import org.jetbrains.compose.web.css.cssRem

/**
 * A class used for storing generic values associated with breakpoints.
 */
class BreakpointValues<T>(
    val sm: T,
    val md: T,
    val lg: T,
    val xl: T,
)

/**
 * A convenience class for constructing an association of breakpoints to CSS sizes.
 */
fun BreakpointSizes(
  sm: CSSUnitValue = 0.cssRem,
  md: CSSUnitValue = sm,
  lg: CSSUnitValue = md,
  xl: CSSUnitValue = lg,
) = BreakpointValues(sm, md, lg, xl)