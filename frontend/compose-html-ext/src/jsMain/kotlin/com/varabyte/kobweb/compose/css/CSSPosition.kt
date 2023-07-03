package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*

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
class EdgeXOffset internal constructor(val edgeX: EdgeX, val offset: CSSLengthOrPercentageValue) {
    override fun toString() = "$edgeX $offset"
}

sealed class EdgeYOrCenter(value: String) : Edge(value)
class EdgeY internal constructor(value: String) : EdgeYOrCenter(value) {
    operator fun invoke(offset: CSSLengthOrPercentageValue) = EdgeYOffset(this, offset)
}

class CenterY internal constructor() : EdgeYOrCenter("center")
class EdgeYOffset internal constructor(val edgeY: EdgeY, val offset: CSSLengthOrPercentageValue) {
    override fun toString() = "$edgeY $offset"
}

// region CSS position 2-arg hack

// CSS positions support the "center" keyword; however, position combinations sometimes result in a 3-arg position
// output, e.g. "left 10% center", which works fine for "background position" but not for other locations that take a
// CSS position, like gradients. These other places only support 1, 2, and 4 arg versions.
//
// Therefore, we provide support for forcing a 1-arg edge parameter into a 2-arg position offset with the same meaning.
// This allows us to convert some 3-arg outputs into 4-arg outputs, enabling CSSPosition to be used universally without
// the user worrying about it.

private fun EdgeXOrCenter.toOffset(): EdgeXOffset {
    return when (this) {
        is EdgeX -> this(0.percent)
        is CenterX -> Edge.Left(50.percent)
    }
}

private fun EdgeYOrCenter.toOffset(): EdgeYOffset {
    return when (this) {
        is EdgeY -> this(0.percent)
        is CenterY -> Edge.Top(50.percent)
    }
}

// endregion

/**
 * Support for declaring a 2D coordinate relative to some element rectangle.
 *
 * We also introduce the [Edge] class to support a strongly-typed API for the
 * position concept. For example, `CSSPosition(Edge.Left, Edge.Top)`
 *
 * You can specify an offset from an edge by "invoking" it: `CSSPosition(Edge.Left(10.percent), Edge.Top(20.percent))`
 *
 * For more information about position values and what they mean, see: https://developer.mozilla.org/en-US/docs/Web/CSS/position_value
 */
class CSSPosition private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    constructor(x: CSSLengthOrPercentageValue = 50.percent, y: CSSLengthOrPercentageValue = 50.percent) : this("$x $y")

    constructor(xAnchor: EdgeXOrCenter) : this("$xAnchor")
    constructor(yAnchor: EdgeYOrCenter) : this("${Edge.CenterX.toOffset()} ${yAnchor.toOffset()}")
    constructor(xAnchor: EdgeXOrCenter, yAnchor: EdgeYOrCenter) : this(xAnchor.toOffset(), yAnchor.toOffset())

    @Deprecated(
        "Create an offset directly instead (by invoking `Edge(offset)`).",
        replaceWith = ReplaceWith("CSSPosition(xAnchor(xOffset))")
    )
    constructor(xAnchor: EdgeX, xOffset: CSSLengthOrPercentageValue) : this(xAnchor(xOffset))
    constructor(xOffset: EdgeXOffset) : this(xOffset, Edge.CenterY.toOffset())

    @Deprecated(
        "Create an offset directly instead (by invoking `Edge(offset)`).",
        replaceWith = ReplaceWith("CSSPosition(yAnchor(yOffset))")
    )
    constructor(yAnchor: EdgeY, yOffset: CSSLengthOrPercentageValue) : this(yAnchor(yOffset))
    constructor(yOffset: EdgeYOffset) : this(Edge.CenterX.toOffset(), yOffset)

    constructor(xCenter: CenterX, y: CSSLengthOrPercentageValue) : this("$xCenter $y")
    constructor(x: CSSLengthOrPercentageValue, yCenter: CenterY) : this("$x $yCenter")

    @Deprecated(
        "Create an offset directly instead (by invoking `Edge(offset)`).",
        replaceWith = ReplaceWith("CSSPosition(xAnchor(xOffset), yAnchor)")
    )
    constructor(xAnchor: EdgeX, xOffset: CSSLengthOrPercentageValue, yAnchor: EdgeYOrCenter) : this(
        xAnchor(xOffset),
        yAnchor
    )

    constructor(xOffset: EdgeXOffset, yAnchor: EdgeYOrCenter) : this(xOffset, yAnchor.toOffset())

    @Deprecated(
        "Create an offset directly instead (by invoking `Edge(offset)`).",
        replaceWith = ReplaceWith("CSSPosition(xAnchor, yAnchor(yOffset))")
    )
    constructor(xAnchor: EdgeXOrCenter, yAnchor: EdgeY, yOffset: CSSLengthOrPercentageValue) : this(
        xAnchor,
        yAnchor(yOffset)
    )

    constructor(xAnchor: EdgeXOrCenter, yOffset: EdgeYOffset) : this(xAnchor.toOffset(), yOffset)

    @Deprecated(
        "Create an offset directly instead (by invoking `Edge(offset)`).",
        replaceWith = ReplaceWith("CSSPosition(xAnchor(xOffset), yAnchor(yOffset))")
    )
    constructor(
        xAnchor: EdgeX,
        xOffset: CSSLengthOrPercentageValue,
        yAnchor: EdgeY,
        yOffset: CSSLengthOrPercentageValue
    ) :
        this(xAnchor(xOffset), yAnchor(yOffset))

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
