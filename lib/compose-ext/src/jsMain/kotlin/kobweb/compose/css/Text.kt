package kobweb.compose.css

import org.jetbrains.compose.web.css.StyleBuilder

class TextAlign(val value: String) {
    companion object {
        val Left get() = TextAlign("left")
        val Right get() = TextAlign("right")
        val Center get() = TextAlign("center")
        val Justify get() = TextAlign("justify")
        val JustifyAll get() = TextAlign("justify-all")
        val Start get() = TextAlign("start")
        val End get() = TextAlign("end")
        val MatchParent get() = TextAlign("match-parent")
    }
}

fun StyleBuilder.textAlign(textAlign: TextAlign) {
    property("text-align", textAlign.value)
}
