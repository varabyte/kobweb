package nekt.ui

import nekt.compose.css.TransitionProperty
import nekt.compose.css.transitionDuration
import nekt.compose.css.transitionProperty
import org.jetbrains.compose.web.css.StyleBuilder
import org.jetbrains.compose.web.css.ms

fun StyleBuilder.withTransitionDefaults(vararg properties: String) {
    if (properties.isEmpty()) {
        transitionProperty(TransitionProperty.ALL)
    }
    else {
        transitionProperty(*properties)
    }
    transitionDuration(200.ms)
}