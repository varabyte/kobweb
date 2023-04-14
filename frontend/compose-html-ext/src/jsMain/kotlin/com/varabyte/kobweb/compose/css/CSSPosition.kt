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
        val CenterX get() = CenterX()
        val CenterY get() = CenterY()
    }
}
sealed class EdgeXOrCenter(value: String) : Edge(value)
class EdgeX internal constructor(value: String) : EdgeXOrCenter(value) {
    operator fun invoke(offset: CSSLengthOrPercentageValue) = EdgeXOffset(this, offset)
}
class CenterX internal constructor() : EdgeXOrCenter("center")
class EdgeXOffset(val edgeX: EdgeX, val offset: CSSLengthOrPercentageValue) {
    override fun toString() = "$edgeX $offset"
}
sealed class EdgeYOrCenter(value: String) : Edge(value)
class EdgeY internal constructor(value: String) : EdgeYOrCenter(value) {
    operator fun invoke(offset: CSSLengthOrPercentageValue) = EdgeYOffset(this, offset)
}
class CenterY internal constructor() : EdgeYOrCenter("center")
class EdgeYOffset(val edgeY: EdgeY, val offset: CSSLengthOrPercentageValue) {
    override fun toString() = "$edgeY $offset"
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

    constructor(x: CSSLengthOrPercentageValue = 50.percent, y: CSSLengthOrPercentageValue = 50.percent) : this("$x $y")

    constructor(xAnchor: EdgeXOrCenter) : this("$xAnchor")
    constructor(yAnchor: EdgeYOrCenter) : this("center $yAnchor")
    constructor(xAnchor: EdgeXOrCenter, yAnchor: EdgeYOrCenter) : this("$xAnchor $yAnchor")

    constructor(xAnchor: EdgeX, xOffset: CSSLengthOrPercentageValue) : this(EdgeXOffset(xAnchor, xOffset).toString())
    constructor(xOffset: EdgeXOffset) : this(xOffset.toString())
    constructor(yAnchor: EdgeY, yOffset: CSSLengthOrPercentageValue) : this(Edge.CenterX, yAnchor, yOffset)
    constructor(yOffset: EdgeYOffset) : this(Edge.CenterX, yOffset)

    constructor(xCenter: CenterX, y: CSSLengthOrPercentageValue) : this("$xCenter $y")
    constructor(x: CSSLengthOrPercentageValue, yCenter: CenterY) : this("$x $yCenter")

    constructor(xAnchor: EdgeX, xOffset: CSSLengthOrPercentageValue, yAnchor: EdgeYOrCenter) : this(EdgeXOffset(xAnchor, xOffset), yAnchor)
    constructor(xOffset: EdgeXOffset, yAnchor: EdgeYOrCenter) : this("$xOffset $yAnchor")

    constructor(xAnchor: EdgeXOrCenter, yAnchor: EdgeY, yOffset: CSSLengthOrPercentageValue) : this(xAnchor, EdgeYOffset(yAnchor, yOffset))
    constructor(xAnchor: EdgeXOrCenter, yOffset: EdgeYOffset) : this("$xAnchor $yOffset")

    constructor(xAnchor: EdgeX, xOffset: CSSLengthOrPercentageValue, yAnchor: EdgeY, yOffset: CSSLengthOrPercentageValue) :
        this(EdgeXOffset(xAnchor, xOffset), EdgeYOffset(yAnchor, yOffset))
    constructor(xAnchor: EdgeXOffset, yAnchor: EdgeYOffset) : this("$xAnchor $yAnchor")

    companion object {
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
