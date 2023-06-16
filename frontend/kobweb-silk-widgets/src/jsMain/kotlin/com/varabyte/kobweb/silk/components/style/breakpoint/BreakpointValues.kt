package com.varabyte.kobweb.silk.components.style.breakpoint

import kotlinx.browser.window
import org.jetbrains.compose.web.css.CSSSizeValue
import org.jetbrains.compose.web.css.CSSUnit
import org.jetbrains.compose.web.css.CSSUnitValue
import org.jetbrains.compose.web.css.CSSpxValue
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.em
import org.jetbrains.compose.web.css.px
import org.w3c.dom.Window

private val Window.bodyFontSize: Number
    get() {
        val bodySize = document.body?.let { body ->
            getComputedStyle(body).getPropertyValue("font-size").removeSuffix("px").toIntOrNull()
        }
        return bodySize ?: 16
    }


sealed class BreakpointUnitValue<out T: CSSUnitValue>(val width: T) {
    abstract fun toPx(): CSSpxValue

    class Px(value: CSSpxValue) : BreakpointUnitValue<CSSpxValue>(value) {
        override fun toPx(): CSSpxValue {
            return width
        }
    }
    class Em(value: CSSSizeValue<CSSUnit.em>) : BreakpointUnitValue<CSSSizeValue<CSSUnit.em>>(value) {
        override fun toPx(): CSSpxValue {
            return (width.value.toDouble() * window.bodyFontSize.toDouble()).px
        }
    }
    class Rem(value: CSSSizeValue<CSSUnit.rem>) : BreakpointUnitValue<CSSSizeValue<CSSUnit.rem>>(value) {
        override fun toPx(): CSSpxValue {
            return (width.value.toDouble() * window.bodyFontSize.toDouble()).px
        }
    }
}

/**
 * A class used for storing generic values associated with breakpoints.
 */
data class BreakpointValues<out T: CSSUnitValue>(
    val sm: BreakpointUnitValue<T>,
    val md: BreakpointUnitValue<T>,
    val lg: BreakpointUnitValue<T>,
    val xl: BreakpointUnitValue<T>,
)

/**
 * A convenience class for constructing an association of breakpoints to CSS pixel sizes.
 */
fun BreakpointSizes(
    sm: CSSpxValue,
    md: CSSpxValue,
    lg: CSSpxValue,
    xl: CSSpxValue,
) = BreakpointValues(
    BreakpointUnitValue.Px(sm),
    BreakpointUnitValue.Px(md),
    BreakpointUnitValue.Px(lg),
    BreakpointUnitValue.Px(xl)
)

/**
 * A convenience class for constructing an association of breakpoints to CSS em sizes.
 */
fun BreakpointSizes(
    sm: CSSSizeValue<CSSUnit.em>,
    md: CSSSizeValue<CSSUnit.em>,
    lg: CSSSizeValue<CSSUnit.em>,
    xl: CSSSizeValue<CSSUnit.em>,
) = BreakpointValues(
    BreakpointUnitValue.Em(sm),
    BreakpointUnitValue.Em(md),
    BreakpointUnitValue.Em(lg),
    BreakpointUnitValue.Em(xl)
)

/**
 * A convenience class for constructing an association of breakpoints to CSS rem sizes.
 */
fun BreakpointSizes(
    sm: CSSSizeValue<CSSUnit.rem> = 0.cssRem,
    md: CSSSizeValue<CSSUnit.rem> = sm,
    lg: CSSSizeValue<CSSUnit.rem> = md,
    xl: CSSSizeValue<CSSUnit.rem> = lg,
) = BreakpointValues(
    BreakpointUnitValue.Rem(sm),
    BreakpointUnitValue.Rem(md),
    BreakpointUnitValue.Rem(lg),
    BreakpointUnitValue.Rem(xl),
)
