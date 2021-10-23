package com.varabyte.kobweb.silk.theme.shapes

import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.styleModifier
import org.jetbrains.compose.web.css.CSSNumeric
import org.jetbrains.compose.web.css.px

fun Modifier.clip(shape: Shape): Modifier = shape.path?.let { path ->
    styleModifier {
        property("clip-path", path.toPathStr())
    }
} ?: this

sealed class Path {
    abstract fun toPathStr(): String
    protected fun Any.toPercentStr() = "${this}%"
    protected fun Pair<Any, Any>.toPercentStr() = "${first}% ${second}%"
}

class CirclePath(private val radius: Float = 50f, private val center: Pair<Float, Float> = 50f to 50f) :
    Path() {
    override fun toPathStr() = "circle(${radius.toPercentStr()} at ${center.toPercentStr()})"
}

class PolygonPath(private vararg val points: Pair<Float, Float>) : Path() {
    override fun toPathStr() = "polygon(${points.joinToString(", ") { it.toPercentStr() }})"
}

// Right and bottom insets are actually calculated from the right and bottom of the parent. So 90% from the top left
// of the page would be 10% from the bottom right.
private fun Pair<Int, Int>.from100() = (100 - first) to (100 - second)
private fun Pair<Float, Float>.from100() = (100f - first) to (100f - second)
private fun Pair<Int, Int>.toFloatPair() = first.toFloat() to second.toFloat()
class InsetPath(
    private val topLeft: Pair<Float, Float>,
    botRight: Pair<Float, Float>,
    private val roundness: CSSNumeric = 0.px,
) : Path() {
    private val botRight = botRight.from100()

    override fun toPathStr(): String {
        val roundnessPart = roundness.takeIf { it != 0.px }?.let { "round $it" } ?: ""

        // Valid inset strings are: (top right bottom left), (topBottom leftRight), (all)
        // So (10% 20% 10% 20%) == (10% 20%), and (10% 10% 10% 10%) == (10%)
        val left = topLeft.first
        val top = topLeft.second
        val right = botRight.first
        val bottom = botRight.second
        val insetPart = when {
            left == top && right == bottom && left == right -> left.toPercentStr()
            left == right && top == bottom -> (top to left).toPercentStr()
            else -> "${top.toPercentStr()} ${right.toPercentStr()} ${bottom.toPercentStr()} ${left.toPercentStr()}"
        }

        return "inset($insetPart$roundnessPart)"
    }
}

interface Shape {
    val path: Path?
}

/**
 * Create a rectangle via inset values.
 *
 * That is...
 * - `Rect(10, 20.dp)` means a rectangle inset by 10% on each side, with corners that have a 20.dp radius.
 * - `Rect(20, 10)` means a rectangle with 20% cut off from top and bottom, 10% cut off from left and right
 * - `Rect(10 to 15, 20 to 25)` means a rectangle with the top left corner at (10% x 15%) and bottom right corner at (20% x 25%)
 * - `Rect(20.dp)` means a full sized rectangle with corners that have a 20.dp radius
 */
class RectF(
    val topLeft: Pair<Float, Float>,
    val botRight: Pair<Float, Float>,
    val cornerRadius: CSSNumeric = 0.px,
) : Shape {
    constructor() : this(0.px)
    constructor(cornerRadius: CSSNumeric) : this(0f to 0f, 100f to 100f, cornerRadius)

    constructor(
        topBottom: Float,
        leftRight: Float,
        cornerRadius: CSSNumeric = 0.px,
    ) : this(leftRight to topBottom, (leftRight to topBottom).from100(), cornerRadius)

    constructor(side: Float, cornerRadius: CSSNumeric = 0.px) : this(
        side to side,
        (side to side).from100(),
        cornerRadius
    )

    override val path: Path?
        get() = if (topLeft.first != 0f || topLeft.second != 0f
            || botRight.first != 100f || botRight.second != 100f
            || cornerRadius != 0.px
        ) {
            InsetPath(topLeft, botRight, cornerRadius)
        } else {
            null
        }
}

class Rect(
    val topLeft: Pair<Int, Int>,
    val botRight: Pair<Int, Int>,
    val cornerRadius: CSSNumeric = 0.px,
) : Shape by RectF(topLeft.toFloatPair(), botRight.toFloatPair(), cornerRadius) {
    constructor() : this(0.px)
    constructor(cornerRadius: CSSNumeric) : this(0 to 0, 100 to 100, cornerRadius)

    constructor(
        topBottom: Int,
        leftRight: Int,
        cornerRadius: CSSNumeric = 0.px
    ) : this(
        leftRight to topBottom,
        (leftRight to topBottom).from100(),
        cornerRadius
    )

    constructor(side: Int, cornerRadius: CSSNumeric = 0.px) : this(
        side to side,
        (side to side).from100(),
        cornerRadius
    )

}

class CircleF(val radius: Float = 50f, val center: Pair<Float, Float> = 50f to 50f) : Shape {
    override val path: Path
        get() = CirclePath(radius, center)
}

class Circle(val radius: Int = 50, val center: Pair<Int, Int> = 50 to 50) :
    Shape by CircleF(radius.toFloat(), center.toFloatPair())

class PolygonF(vararg val points: Pair<Float, Float>) : Shape {
    override val path: Path
        get() = PolygonPath(*points)
}

class Polygon(vararg val points: Pair<Int, Int>) :
    Shape by PolygonF(*points.map { it.toFloatPair() }.toTypedArray())
