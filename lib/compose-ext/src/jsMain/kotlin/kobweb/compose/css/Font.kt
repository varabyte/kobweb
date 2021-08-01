package kobweb.compose.css

import org.jetbrains.compose.web.css.StyleBuilder

fun StyleBuilder.fontFamily(value: String) {
    property("font-family", value)
}

enum class FontStyle(val value: String) {
    NORMAL("normal"),
    ITALIC("italic"),
}

fun StyleBuilder.fontStyle(style: FontStyle) {
    property("font-style", style.value)
}

enum class FontWeight(val value: String) {
    NORMAL("normal"),
    BOLD("bold"),
    LIGHTER("lighter"),
    BOLDER("bolder"),
}

fun StyleBuilder.fontWeight(weight: FontWeight) {
    property("font-weight", weight.value)
}

fun StyleBuilder.fontWeight(value: Int) {
    require(value in 1..1000) { "Font weight must be between 1 and 1000. Got: $value" }
    property("font-weight", value)
}
