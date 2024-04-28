@file:Suppress("DeprecatedCallableAddReplaceWith")

package com.varabyte.kobweb.silk.components.style.breakpoint

import com.varabyte.kobweb.silk.style.breakpoint.BreakpointUnitValue
import org.jetbrains.compose.web.css.*

@Deprecated("Please change your import to `com.varabyte.kobweb.silk.style.breakpoint.BreakpointUnitValue`")
typealias BreakpointUnitValue<T> = BreakpointUnitValue<T>

@Deprecated("Please change your import to `com.varabyte.kobweb.silk.style.breakpoint.BreakpointValues`")
typealias BreakpointValues<T> = com.varabyte.kobweb.silk.style.breakpoint.BreakpointValues<T>

@Deprecated("Please change your import to `com.varabyte.kobweb.silk.style.breakpoint.BreakpointSizes`")
fun BreakpointSizes(
    sm: CSSpxValue,
    md: CSSpxValue,
    lg: CSSpxValue,
    xl: CSSpxValue,
) = com.varabyte.kobweb.silk.style.breakpoint.BreakpointSizes(sm, md, lg, xl)

@Deprecated("Please change your import to `com.varabyte.kobweb.silk.style.breakpoint.BreakpointSizes`")
fun BreakpointSizes(
    sm: CSSSizeValue<CSSUnit.em>,
    md: CSSSizeValue<CSSUnit.em>,
    lg: CSSSizeValue<CSSUnit.em>,
    xl: CSSSizeValue<CSSUnit.em>,
) = com.varabyte.kobweb.silk.style.breakpoint.BreakpointSizes(sm, md, lg, xl)

@Deprecated("Please change your import to `com.varabyte.kobweb.silk.style.breakpoint.BreakpointSizes`")
fun BreakpointSizes(
    sm: CSSSizeValue<CSSUnit.rem>,
    md: CSSSizeValue<CSSUnit.rem>,
    lg: CSSSizeValue<CSSUnit.rem>,
    xl: CSSSizeValue<CSSUnit.rem>,
) = com.varabyte.kobweb.silk.style.breakpoint.BreakpointSizes(sm, md, lg, xl)
