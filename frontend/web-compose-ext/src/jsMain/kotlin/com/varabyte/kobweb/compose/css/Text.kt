package com.varabyte.kobweb.compose.css

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

class TextDecorationLine(val value: String) {
    companion object {
        val Underline get() = TextDecorationLine("underline")
        val Overline get() = TextDecorationLine("overline")
        val LineThrough get() = TextDecorationLine("line-through")
        val None get() = TextDecorationLine("none")

        val Inherit get() = TextDecorationLine("inherit")
        val Initial get() = TextDecorationLine("initial")
        val Revert get() = TextDecorationLine("revert")
        val Unset get() = TextDecorationLine("unset")
    }
}

fun StyleBuilder.textDecorationLine(vararg textDecorationLines: TextDecorationLine) {
    property("text-decoration-line", textDecorationLines.joinToString(" ") { it.value })
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/user-select
class UserSelect(val value: String) {
    companion object {
        val None get() = UserSelect("none")
        val Auto get() = UserSelect("auto")

        val Inherit get() = UserSelect("inherit")
        val Initial get() = UserSelect("initial")
        val Revert get() = UserSelect("revert")
        val Unset get() = UserSelect("unset")
    }
}

fun StyleBuilder.userSelect(userSelect: UserSelect) {
    property("user-select", userSelect.value)
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/white-space
class WhiteSpace(val value: String) {
    companion object {
        val Normal get() = WhiteSpace("normal");
        val NoWrap get() = WhiteSpace("nowrap");
        val Pre get() = WhiteSpace("pre");
        val PreWrap get() = WhiteSpace("pre-wrap");
        val PreLine get() = WhiteSpace("pre-line");
        val BreakSpaces get() = WhiteSpace("break-spaces");

        val Inherit get() = WhiteSpace("inherit")
        val Initial get() = WhiteSpace("initial")
        val Revert get() = WhiteSpace("revert")
        val Unset get() = WhiteSpace("unset")
    }
}

fun StyleBuilder.whiteSpace(whiteSpace: WhiteSpace) {
    property("white-space", whiteSpace.value)
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/writing-mode
class WritingMode(val value: String) {
    // Keyword
    val HorizontalTb get() = WritingMode("horizontal-tb");
    val VerticalRl get() = WritingMode("vertical-rl");
    val VerticalLr get() = WritingMode("vertical-lr");

    // Global
    val Inherit get() = WritingMode("inherit")
    val Initial get() = WritingMode("initial")
    val Revert get() = WritingMode("revert")
    val Unset get() = WritingMode("unset")
}

fun StyleBuilder.writingMode(writingMode: WritingMode) {
    property("writing-mode", writingMode.value)
}