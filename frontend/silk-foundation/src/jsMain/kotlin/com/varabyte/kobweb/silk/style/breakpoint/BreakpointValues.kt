package com.varabyte.kobweb.silk.style.breakpoint

import com.varabyte.kobweb.compose.css.*
import kotlinx.browser.window
import org.jetbrains.compose.web.css.*
import org.w3c.dom.Window

private val Window.bodyFontSize: Number
    get() {
        val bodySize = document.body?.let { body ->
            getComputedStyle(body).getPropertyValue("font-size").removeSuffix("px").toIntOrNull()
        }
        return bodySize ?: 16
    }


sealed class BreakpointUnitValue<out T : CSSLengthNumericValue>(val width: T) {
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
data class BreakpointValues<out T : CSSLengthNumericValue>(
    val sm: BreakpointUnitValue<T>,
    val md: BreakpointUnitValue<T>,
    val lg: BreakpointUnitValue<T>,
    val xl: BreakpointUnitValue<T>,
    val xxl: BreakpointUnitValue<T>,
)

/**
 * A convenience class for constructing an association of breakpoints to CSS pixel sizes.
 */
fun BreakpointSizes(
    sm: CSSpxValue,
    md: CSSpxValue,
    lg: CSSpxValue,
    xl: CSSpxValue,
    xxl: CSSpxValue = xl, // Default fallback provided for backwards compat
) = BreakpointValues(
    BreakpointUnitValue.Px(sm),
    BreakpointUnitValue.Px(md),
    BreakpointUnitValue.Px(lg),
    BreakpointUnitValue.Px(xl),
    BreakpointUnitValue.Px(xxl),
)

/**
 * A convenience class for constructing an association of breakpoints to CSS em sizes.
 */
fun BreakpointSizes(
    sm: CSSSizeValue<CSSUnit.em>,
    md: CSSSizeValue<CSSUnit.em>,
    lg: CSSSizeValue<CSSUnit.em>,
    xl: CSSSizeValue<CSSUnit.em>,
    xxl: CSSSizeValue<CSSUnit.em> = xl, // Default fallback provided for backwards compat
) = BreakpointValues(
    BreakpointUnitValue.Em(sm),
    BreakpointUnitValue.Em(md),
    BreakpointUnitValue.Em(lg),
    BreakpointUnitValue.Em(xl),
    BreakpointUnitValue.Em(xxl),
)

/**
 * A convenience class for constructing an association of breakpoints to CSS rem sizes.
 */
fun BreakpointSizes(
    sm: CSSSizeValue<CSSUnit.rem>,
    md: CSSSizeValue<CSSUnit.rem>,
    lg: CSSSizeValue<CSSUnit.rem>,
    xl: CSSSizeValue<CSSUnit.rem>,
    xxl: CSSSizeValue<CSSUnit.rem> = xl, // Default fallback provided for backwards compat
) = BreakpointValues(
    BreakpointUnitValue.Rem(sm),
    BreakpointUnitValue.Rem(md),
    BreakpointUnitValue.Rem(lg),
    BreakpointUnitValue.Rem(xl),
    BreakpointUnitValue.Rem(xxl),
)
