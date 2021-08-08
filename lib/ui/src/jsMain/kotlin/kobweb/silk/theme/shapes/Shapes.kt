package kobweb.silk.theme.shapes

import org.jetbrains.compose.common.internal.castOrCreate
import org.jetbrains.compose.common.ui.Modifier
import org.jetbrains.compose.common.ui.unit.Dp
import org.jetbrains.compose.common.ui.unit.dp
import org.jetbrains.compose.web.css.px

data class Shapes(
    /** Size useful for small widgets, e.g. buttons */
    val small: Shape,
    /** Size useful for medium areas, like cards or text blocks */
    val medium: Shape,
    /** Size useful for fullscreen areas, like backgrounds */
    val large: Shape,
)

fun Modifier.clip(shape: Shape): Modifier = shape.path?.let { path ->
    castOrCreate().apply {
        add {
            property("clip-path", path.toPathStr())
        }
    }
} ?: this

sealed class Path {
    abstract fun toPathStr(): String
    protected fun Any.toPercentStr() = "${this}%"
    protected fun Pair<Any, Any>.toPercentStr() = "${first}% ${second}%"
}

class CirclePath(private val radiusPercent: Float = 50f, private val centerPercent: Pair<Float, Float> = 50f to 50f) : Path() {
    override fun toPathStr() = "circle(${radiusPercent.toPercentStr()} at ${centerPercent.toPercentStr()})"
}

class PolygonPath(private vararg val pointPercents: Pair<Float, Float>) : Path() {
    override fun toPathStr() = "polygon(${pointPercents.joinToString(", ") { it.toPercentStr() }})"
}

// Right and bottom insets are actually calculated from the right and bottom of the parent. So 90% from the top left
// of the page would be 10% from the bottom right.
private fun Pair<Float, Float>.from100() = (100f - first) to (100f - second)
private fun Pair<Int, Int>.toFloatPair() = first.toFloat() to second.toFloat()
class InsetPath(
    private val topLeft: Pair<Float, Float>,
    botRight: Pair<Float, Float>,
    private val roundness: Dp,
    ) : Path() {
    private val botRight = botRight.from100()

    override fun toPathStr(): String {
        val roundnessPart = roundness.value.takeIf { it > 0 }?.let { roundness -> "round ${roundness.px}" } ?: ""

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
class Rect(
    val topLeftPercent: Pair<Float, Float>,
    val botRightPercent: Pair<Float, Float>,
    val cornerRadius: Dp = 0.dp,
) : Shape {
    constructor() : this(0.dp)
    constructor(cornerRadius: Dp) : this(0f to 0f, 100f to 100f, cornerRadius)

    constructor(
        topBottomPercent: Float,
        leftRightPercent: Float,
        cornerRadius: Dp = 0.dp
    ) : this(leftRightPercent to topBottomPercent, (leftRightPercent to topBottomPercent).from100(), cornerRadius)

    constructor(sidePercent: Float, cornerRadius: Dp = 0.dp) : this(
        sidePercent to sidePercent,
        (sidePercent to sidePercent).from100(),
        cornerRadius
    )

    constructor(topLeftPercent: Pair<Int, Int>, botRightPercent: Pair<Int, Int>, cornerRadius: Dp = 0.dp):
            this(topLeftPercent.toFloatPair(), botRightPercent.toFloatPair(), cornerRadius)

    constructor(
        topBottomPercent: Int,
        leftRightPercent: Int,
        cornerRadius: Dp = 0.dp
    ) : this(
        leftRightPercent.toFloat() to topBottomPercent.toFloat(),
        (leftRightPercent.toFloat() to topBottomPercent.toFloat()).from100(),
        cornerRadius
    )

    constructor(sidePercent: Int, cornerRadius: Dp = 0.dp) : this(
        sidePercent.toFloat() to sidePercent.toFloat(),
        (sidePercent.toFloat() to sidePercent.toFloat()).from100(),
        cornerRadius
    )

    override val path: Path?
        get() = if (topLeftPercent.first != 0f || topLeftPercent.second != 0f
            || botRightPercent.first != 100f || botRightPercent.second != 100f
            || cornerRadius.value != 0f
        ) {
            InsetPath(topLeftPercent, botRightPercent, cornerRadius)
        } else {
            null
        }
}

class Circle(val radiusPercent: Float = 50f) : Shape {
    constructor(radiusPercent: Int): this(radiusPercent.toFloat())

    override val path: Path
        get() = CirclePath(radiusPercent)
}

class Polygon(vararg val pointPercents: Pair<Float, Float>) : Shape {
    constructor(vararg pointPercents: Pair<Int, Int>): this(*pointPercents.map { it.toFloatPair() }.toTypedArray())

    override val path: Path?
        get() = PolygonPath(*pointPercents)
}
