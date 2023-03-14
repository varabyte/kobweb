package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.CSSLengthOrPercentageValue
import org.jetbrains.compose.web.css.StylePropertyValue
import org.jetbrains.compose.web.css.percent

sealed class Edge(private val value: String) {
    override fun toString() = value

    companion object {
        // Edges
        val Top get() = EdgeY("top")
        val Bottom get() = EdgeY("bottom")
        val Left get() = EdgeX("left")
        val Right get() = EdgeX("right")
        val CenterX get() = EdgeX("center")
        val CenterY get() = EdgeY("center")
    }
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

/**
 * Support for declaring a 2D coordinate relative to some element rectangle.
 *
 * We also introduce the [Edge] class to support a strongly-typed API for the
 * position concept.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/position_value
 */
class CSSPosition internal constructor(private val value: String): StylePropertyValue {
    override fun toString() = value

    constructor() : this(0.percent, 0.percent)
    constructor(xAnchor: EdgeX) : this("$xAnchor")
    constructor(yAnchor: EdgeY) : this("center $yAnchor")
    constructor(xAnchor: EdgeX, yAnchor: EdgeY) : this("$xAnchor $yAnchor")
    constructor(xAnchor: EdgeX, x: CSSLengthOrPercentageValue, yAnchor: EdgeY) : this("${EdgeOffset(xAnchor, x)} $yAnchor")
    constructor(xAnchor: EdgeX, yAnchor: EdgeY, y: CSSLengthOrPercentageValue) : this("$xAnchor ${EdgeOffset(yAnchor, y)}")
    constructor(xAnchor: EdgeX, x: CSSLengthOrPercentageValue) : this(EdgeOffset(xAnchor, x).toString())
    constructor(yAnchor: EdgeY, y: CSSLengthOrPercentageValue) : this(Edge.CenterX, yAnchor, y)
    constructor(xAnchor: EdgeX, x: CSSLengthOrPercentageValue, yAnchor: EdgeY, y: CSSLengthOrPercentageValue) :
        this("${EdgeOffset(xAnchor, x)} ${EdgeOffset(yAnchor, y)}")

    constructor(x: CSSLengthOrPercentageValue, y: CSSLengthOrPercentageValue) : this("$x $y")

    companion object {
        fun x(offset: CSSLengthOrPercentageValue) = CSSPosition("$offset")
        fun y(offset: CSSLengthOrPercentageValue) = CSSPosition("center $offset")

        // Positions
        val Top get() = CSSPosition(Edge.Top)
        val TopRight get() = CSSPosition(Edge.Right, Edge.Top)
        val Right get() = CSSPosition(Edge.Right)
        val BottomRight get() = CSSPosition(Edge.Right, Edge.Bottom)
        val Bottom get() = CSSPosition(Edge.Bottom)
        val BottomLeft get() = CSSPosition(Edge.Left, Edge.Bottom)
        val Left get() = CSSPosition(Edge.Left)
        val TopLeft get() = CSSPosition(Edge.Left, Edge.Top)
        val Center get() = CSSPosition(Edge.CenterX, Edge.CenterY)
    }
}
