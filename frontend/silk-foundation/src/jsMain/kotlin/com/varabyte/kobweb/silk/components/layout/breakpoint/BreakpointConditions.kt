@file:Suppress("DEPRECATION", "DeprecatedCallableAddReplaceWith")

package com.varabyte.kobweb.silk.components.layout.breakpoint

import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.components.style.breakpoint.Breakpoint

@Deprecated("Update the import to use `com.varabyte.kobweb.silk.style.breakpoint.displayIfAtLeast` instead.")
fun Modifier.displayIfAtLeast(breakpoint: Breakpoint) =
    this.classNames("silk-display-if-at-least-${breakpoint.name.lowercase()}")

@Deprecated("Update the import to use `com.varabyte.kobweb.silk.style.breakpoint.displayUntil` instead.")
fun Modifier.displayUntil(breakpoint: Breakpoint) = this.classNames("silk-display-until-${breakpoint.name.lowercase()}")

@Deprecated("Update the import to use `com.varabyte.kobweb.silk.style.breakpoint.displayBetween` instead.")
fun Modifier.displayBetween(breakpointLower: Breakpoint, breakpointUpper: Breakpoint): Modifier {
    require(breakpointLower.ordinal < breakpointUpper.ordinal) { "displayBetween breakpoints passed in wrong order: $breakpointLower should be smaller than $breakpointUpper" }

    return this.classNames(
        "silk-display-if-${breakpointLower.name.lowercase()}",
        "silk-display-until-${breakpointUpper.name.lowercase()}"
    )
}
