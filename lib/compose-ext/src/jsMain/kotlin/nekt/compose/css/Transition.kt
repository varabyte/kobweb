package nekt.compose.css

import org.jetbrains.compose.web.css.AnimationTimingFunction
import org.jetbrains.compose.web.css.CSSSizeValue
import org.jetbrains.compose.web.css.CSSUnitTime
import org.jetbrains.compose.web.css.StyleBuilder

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/CSS_Transitions/Using_CSS_transitions

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/transition-property
/**
 * Special values for Transition Property. You can also pass in string values directly using
 * the relaxed version of [transitionProperty]
 */
enum class TransitionProperty(val value: String) {
    // Keywords
    NONE("none"),
    ALL("all"),

    // Global values
    INHERIT("inherit"),
    INITIAL("initial"),
    REVERT("revert"),
    UNSET("unset"),
}

fun StyleBuilder.transitionProperty(property: TransitionProperty) {
    transitionProperty(property.value)
}

fun StyleBuilder.transitionProperty(vararg properties: String) {
    property("transition-property", properties.joinToString())
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/transition-duration
fun StyleBuilder.transitionDuration(duration: CSSSizeValue<out CSSUnitTime>) {
    property("transition-duration", duration.toString())
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/transition-delay
fun StyleBuilder.transitionDelay(duration: CSSSizeValue<out CSSUnitTime>) {
    property("transition-delay", duration.toString())
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/transition-timing-function
fun StyleBuilder.transitionTimingFunction(value: AnimationTimingFunction) {
    property("transition-timing-function", value.toString())
}