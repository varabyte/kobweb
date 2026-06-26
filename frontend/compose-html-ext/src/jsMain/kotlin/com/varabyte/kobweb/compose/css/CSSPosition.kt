package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*

sealed class Edge(private val value: String) : StylePropertyValue {
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

// If possible, discard 0 value lengths / percentages when indicating a default value. We might not be able to know this
private fun CSSLengthOrPercentageNumericValue.isDefinitelyZero(): Boolean {
    // If `this` is a `CSSSizeValue`, it will have a `value: Float` property'; otherwise, that will be undefined
    return this.asDynamic().value == 0f
}

sealed class EdgeXOrCenter(value: String) : Edge(value)
class EdgeX internal constructor(value: String) : EdgeXOrCenter(value) {
    operator fun invoke(offset: CSSLengthOrPercentageNumericValue) = EdgeXOffset(this, offset)
}

class CenterX internal constructor() : EdgeXOrCenter("center")
class EdgeXOffset internal constructor(val edgeX: EdgeX, val offset: CSSLengthOrPercentageNumericValue) : StylePropertyValue {
    override fun toString() = toString(dropZeroOffset = true)
    internal fun toString(dropZeroOffset: Boolean) = buildString {
        append(edgeX)
        if (!(dropZeroOffset && offset.isDefinitelyZero())) append(" $offset")
    }
}

sealed class EdgeYOrCenter(value: String) : Edge(value)
class EdgeY internal constructor(value: String) : EdgeYOrCenter(value) {
    operator fun invoke(offset: CSSLengthOrPercentageNumericValue) = EdgeYOffset(this, offset)
}

class CenterY internal constructor() : EdgeYOrCenter("center")
class EdgeYOffset internal constructor(val edgeY: EdgeY, val offset: CSSLengthOrPercentageNumericValue) : StylePropertyValue {
    override fun toString() = toString(dropZeroOffset = true)
    internal fun toString(dropZeroOffset: Boolean) = buildString {
        append(edgeY)
        if (!(dropZeroOffset && offset.isDefinitelyZero())) append(" $offset")
    }
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
// NOTE: For maximum compatibility, we only support 1-, 2-, and 4-arg output formats. For example, radial gradients do
// not support 3-arg versions.
class CSSPosition private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    constructor(
        x: CSSLengthOrPercentageNumericValue = 50.percent,
        y: CSSLengthOrPercentageNumericValue = 50.percent
    ) : this("$x $y")

    constructor(xAnchor: EdgeXOrCenter) : this("$xAnchor")
    constructor(yAnchor: EdgeYOrCenter) : this("$yAnchor")
    constructor(xAnchor: EdgeXOrCenter, yAnchor: EdgeYOrCenter) : this(if (xAnchor !is CenterX || yAnchor !is CenterY) "$xAnchor $yAnchor" else "center")

    constructor(xOffset: EdgeXOffset) : this(buildString {
        // If xOffset is just a keyword (e.g. left) no need to append center, but it is needed if an offset is specified
        append(xOffset)
        if (!xOffset.offset.isDefinitelyZero()) append(" ${CenterY().toOffset()}")
    })

    constructor(yOffset: EdgeYOffset) : this(buildString {
        // If yOffset is just a keyword (e.g. top) no need to prepend center, but it is needed if an offset is specified
        if (!yOffset.offset.isDefinitelyZero()) append("${CenterX().toOffset()} ")
        append(yOffset)
    })

    constructor(xCenter: CenterX, y: CSSLengthOrPercentageNumericValue) : this("$xCenter $y")
    constructor(x: CSSLengthOrPercentageNumericValue, yCenter: CenterY) : this("$x $yCenter")

    constructor(xOffset: EdgeXOffset, yAnchor: EdgeYOrCenter) : this(
        buildString {
            val xHasZeroOffset = xOffset.offset.isDefinitelyZero()
            append(xOffset)
            append(' ')
            append(
                if (xHasZeroOffset) {
                    val yOffset = yAnchor.toOffset()
                    val yHasZeroOffset = yOffset.offset.isDefinitelyZero()
                    if (yHasZeroOffset) yOffset else yOffset.toString(dropZeroOffset = false)
                } else yAnchor.toOffset().toString(dropZeroOffset = false)
            )
        }
    )

    constructor(xAnchor: EdgeXOrCenter, yOffset: EdgeYOffset) : this(
        buildString {
            val yHasZeroOffset = yOffset.offset.isDefinitelyZero()
            append(
                if (yHasZeroOffset) {
                    val xOffset = xAnchor.toOffset()
                    val xHasZeroOffset = xOffset.offset.isDefinitelyZero()
                    if (xHasZeroOffset) xOffset else xOffset.toString(dropZeroOffset = false)
                } else xAnchor.toOffset().toString(dropZeroOffset = false)
            )
            append(' ')
            append(yOffset)
        }
    )

    constructor(xOffset: EdgeXOffset, yOffset: EdgeYOffset) : this(
        buildString {
            val xHasZeroOffset = xOffset.offset.isDefinitelyZero()
            val yHasZeroOffset = yOffset.offset.isDefinitelyZero()
            if (!xHasZeroOffset && yHasZeroOffset) {
                append(xOffset)
                append(' ')
                append(yOffset.toString(dropZeroOffset = false))
            } else if (xHasZeroOffset && !yHasZeroOffset) {
                append(xOffset.toString(dropZeroOffset = false))
                append(' ')
                append(yOffset)
            } else {
                append(xOffset)
                append(' ')
                append(yOffset)
            }
        }
    )

    companion object {
        // Positions
        val Top get() = "top".unsafeCast<CSSPosition>()
        val TopRight get() = "right top".unsafeCast<CSSPosition>()
        val Right get() = "right".unsafeCast<CSSPosition>()
        val BottomRight get() = "right bottom".unsafeCast<CSSPosition>()
        val Bottom get() = "bottom".unsafeCast<CSSPosition>()
        val BottomLeft get() = "left bottom".unsafeCast<CSSPosition>()
        val Left get() = "left".unsafeCast<CSSPosition>()
        val TopLeft get() = "left top".unsafeCast<CSSPosition>()
        val Center get() = "center".unsafeCast<CSSPosition>()
    }
}
