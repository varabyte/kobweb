package nekt.compose.css

import org.jetbrains.compose.web.css.StyleBuilder

enum class TextAlign(val value: String) {
    LEFT("left"),
    RIGHT("right"),
    CENTER("center"),
    JUSTIFY("justify"),
    JUSTIFY_ALL("justify-all"),
    START("start"),
    END("end"),
    MATCH_PARENT("match-parent"),
}

fun StyleBuilder.textAlign(textAlign: TextAlign) {
    property("text-align", textAlign.value)
}
