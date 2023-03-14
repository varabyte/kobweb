package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.CSSLengthOrPercentageValue
import org.jetbrains.compose.web.css.StylePropertyValue
import org.jetbrains.compose.web.css.percent

/**
 * Support for declaring a 2D coordinate relative to some element rectangle.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/position_value
 *
 * Note that CSS positions support the concept of a general "center" position,
 * which uses heuristics to figure out if you mean horizontal centering or vertical
 * centering. However, this API is strongly typed and provides explicit [CenterX]
 * and [CenterY] versions.
 */
class CSSPosition internal constructor(private val value: String): StylePropertyValue {
    override fun toString() = value

    sealed class Edge(private val value: String) {
        override fun toString() = value
    }
    open class EdgeX internal constructor(value: String) : Edge(value)
    class CenterX internal constructor() : EdgeX("center")
    open class EdgeY internal constructor(value: String) : Edge(value)
    class CenterY internal constructor() : EdgeY("center")
    private class EdgeOffset(val edge: Edge? = null, val offset: CSSLengthOrPercentageValue) {
        init {
            require(edge !is CenterX && edge !is CenterY) {
                "Specifying an offset from a center position is not supported"
            }
        }
        override fun toString() = buildString {
            if (edge != null) {
                append(edge.toString())
                append(' ')
            }
            append(offset)
        }
    }

    companion object {
        operator fun invoke() = this(0.percent, 0.percent)
        operator fun invoke(xAnchor: EdgeX) = CSSPosition("$xAnchor")
        operator fun invoke(yAnchor: EdgeY) = CSSPosition("center $yAnchor")
        operator fun invoke(xAnchor: EdgeX, yAnchor: EdgeY) = CSSPosition("$xAnchor $yAnchor")
        operator fun invoke(xAnchor: EdgeX, x: CSSLengthOrPercentageValue, yAnchor: EdgeY) = CSSPosition("${EdgeOffset(xAnchor, x)} $yAnchor")
        operator fun invoke(xAnchor: EdgeX, yAnchor: EdgeY, y: CSSLengthOrPercentageValue) = CSSPosition("$xAnchor ${EdgeOffset(yAnchor, y)}")
        operator fun invoke(xAnchor: EdgeX, x: CSSLengthOrPercentageValue) = CSSPosition(EdgeOffset(xAnchor, x).toString())
        operator fun invoke(yAnchor: EdgeY, y: CSSLengthOrPercentageValue) = this(Left, 50.percent, yAnchor, y)
        operator fun invoke(xAnchor: EdgeX, x: CSSLengthOrPercentageValue, yAnchor: EdgeY, y: CSSLengthOrPercentageValue) =
            CSSPosition("${EdgeOffset(xAnchor, x)} ${EdgeOffset(yAnchor, y)}")

        operator fun invoke(x: CSSLengthOrPercentageValue, y: CSSLengthOrPercentageValue) = CSSPosition("$x $y")

        fun x(offset: CSSLengthOrPercentageValue) = CSSPosition("$offset")
        fun y(offset: CSSLengthOrPercentageValue) = CSSPosition("center $offset")

        // Edges
        val Top get() = EdgeY("top")
        val Bottom get() = EdgeY("bottom")
        val Left get() = EdgeX("left")
        val Right get() = EdgeX("right")
        val CenterX get() = EdgeX("center")
        val CenterY get() = EdgeY("center")
    }
}
