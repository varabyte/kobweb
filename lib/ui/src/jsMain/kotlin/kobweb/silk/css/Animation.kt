package kobweb.silk.css

import kobweb.compose.css.TransitionProperty
import kobweb.compose.css.transitionDuration
import kobweb.compose.css.transitionProperty
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