package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.CSSLengthOrPercentageValue
import org.jetbrains.compose.web.css.StylePropertyValue
import org.jetbrains.compose.web.css.percent

sealed class CSSPosition private constructor(private val value: String): StylePropertyValue {
    override fun toString() = value

    private class Raw(value: String) : CSSPosition(value)
    class Keyword internal constructor(value: String) : CSSPosition(value)
    sealed class Edge(value: String) : CSSPosition(value)
    class EdgeX internal constructor(value: String) : Edge(value)
    class EdgeY internal constructor(value: String) : Edge(value)
    private class EdgeOffset(edge: Edge? = null, offset: CSSLengthOrPercentageValue) : CSSPosition(
        buildString {
            if (edge != null) {
                append(edge.toString())
                append(' ')
            }
            append(offset)
        })

    companion object {
        operator fun invoke(xAnchor: EdgeX, yAnchor: EdgeY): CSSPosition = Raw("$xAnchor $yAnchor")
        operator fun invoke(xAnchor: EdgeX, x: CSSLengthOrPercentageValue, yAnchor: EdgeY): CSSPosition = Raw("${EdgeOffset(xAnchor, x)} $yAnchor")
        operator fun invoke(xAnchor: EdgeX, yAnchor: EdgeY, y: CSSLengthOrPercentageValue): CSSPosition = Raw("$xAnchor ${EdgeOffset(yAnchor, y)}")
        operator fun invoke(xAnchor: EdgeX, x: CSSLengthOrPercentageValue): CSSPosition = EdgeOffset(xAnchor, x)
        operator fun invoke(yAnchor: EdgeY, y: CSSLengthOrPercentageValue): CSSPosition = this(Left, 50.percent, yAnchor, y)
        operator fun invoke(xAnchor: EdgeX, x: CSSLengthOrPercentageValue, yAnchor: EdgeY, y: CSSLengthOrPercentageValue): CSSPosition =
            Raw("${EdgeOffset(xAnchor, x)} ${EdgeOffset(yAnchor, y)}")

        operator fun invoke(x: CSSLengthOrPercentageValue, y: CSSLengthOrPercentageValue): CSSPosition =
            Raw("$x $y")

        fun x(offset: CSSLengthOrPercentageValue): CSSPosition = Raw("$offset")
        fun y(offset: CSSLengthOrPercentageValue): CSSPosition = Raw("center $offset")

        // Edges
        val Top get() = EdgeY("top")
        val Bottom get() = EdgeY("bottom")
        val Left get() = EdgeX("left")
        val Right get() = EdgeX("right")

        // Keyword
        val Center get() = Keyword("center")
    }
}
