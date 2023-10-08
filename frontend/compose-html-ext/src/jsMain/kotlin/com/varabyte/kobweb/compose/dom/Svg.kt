// SVGElement scope is useful to ensure SVG children are only used within an SVG
@file:Suppress("UnusedReceiverParameter")

package com.varabyte.kobweb.compose.dom

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.*
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.AttrBuilderContext
import org.jetbrains.compose.web.dom.ContentBuilder
import org.jetbrains.compose.web.dom.ElementScope
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.svg.SVGCircleElement
import org.w3c.dom.svg.SVGElement
import org.w3c.dom.svg.SVGEllipseElement
import org.w3c.dom.svg.SVGGElement
import org.w3c.dom.svg.SVGLineElement
import org.w3c.dom.svg.SVGPathElement
import org.w3c.dom.svg.SVGPolygonElement
import org.w3c.dom.svg.SVGPolylineElement
import org.w3c.dom.svg.SVGRectElement
import org.w3c.dom.svg.SVGTextElement

/**
 * Our own SVG-specific extensions on top of `AttrsScope<SVGElement>`.
 *
 * At the time of writing this, the SVG APIs for Compose HTML are still underbaked. They are missing a lot of type-safe
 * APIs for various SVG attributes.
 *
 * We originally thought we would fix this by a liberal use of extension methods
 * (e.g. `fun AttrsScope<SVGCircleElement>.cx(value: Number)`), but this approach was fairly inconvenient as doing even
 * the most basic things with SVG elements required a bunch of imports and code completions were slow.
 *
 * As a compromise, we create our own subclasses of `AttrsScope<SVGElement>` and layer our own methods on top of them
 * directly. This approach is basically invisible to users of our APIs while providing a much better developer
 * experience. For example, this approaches uses our Circle extensions seamlessly:
 *
 * ```
 * Svg {
 *   Circle {
 *     cx(25)
 *     cy(25)
 *   }
 * }
 * ```
 */
abstract class SVGElementAttrsScope<E: SVGElement>(attrs: AttrsScope<E>) : AttrsScope<E> by attrs

// Reformat to value expected by SVG tag, e.g. "CurrentColor" -> "currentColor"
// Enums have to be capitalized title case for this method to work.
private fun <E: Enum<E>> Enum<E>.toSvgValue() = name.replaceFirstChar { it.lowercase() }

// region SVG paint attributes (https://www.w3.org/TR/SVG11/painting.html#SpecifyingPaint)

enum class SVGPaintType {
    None,
    CurrentColor;

    override fun toString() = this.toSvgValue()
}

typealias SVGFillType = SVGPaintType
typealias SVGStrokeType = SVGPaintType

enum class SVGStrokeLineCap {
    Butt,
    Round,
    Square;

    override fun toString() = this.toSvgValue()
}

enum class SVGStrokeLineJoin {
    Miter,
    Round,
    Bevel;

    override fun toString() = this.toSvgValue()
}

enum class SVGFillRule {
    NonZero,
    EvenOdd;

    override fun toString() = this.toSvgValue()
}

// endregion

abstract class SVGShapeElementAttrsScope<E: SVGElement>(attrs: AttrsScope<E>) : SVGElementAttrsScope<E>(attrs) {
    fun stroke(value: CSSColorValue) = this.attr("stroke", value.toString())
    fun stroke(value: SVGPaintType) = this.attr("stroke", value.toString())

    fun strokeDashArray(vararg values: Number) {
        this.attr("stroke-dasharray", values.joinToString(",") { it.toString() })
    }

    fun strokeDashArray(vararg values: CSSLengthOrPercentageValue) {
        this.attr("stroke-dasharray", values.joinToString(",") { it.toString() })
    }

    fun strokeDashOffset(value: Number) = this.attr("stroke-dashoffset", value.toString())
    fun strokeDashOffset(value: CSSLengthOrPercentageValue) = this.attr("stroke-dashoffset", value.toString())

    fun strokeLineCap(value: SVGStrokeLineCap) = this.attr("stroke-linecap", value.toString())

    fun strokeLineJoin(value: SVGStrokeLineJoin) = this.attr("stroke-linejoin", value.toString())

    fun strokeMiterLimit(value: Number) = this.attr("stroke-miterlimit", value.toString())

    fun strokeOpacity(value: Number) = this.attr("stroke-opacity", value.toString())

    fun strokeWidth(value: Number) = this.attr("stroke-width", value.toString())
    fun strokeWidth(value: CSSLengthOrPercentageValue) = this.attr("stroke-width", value.toString())

    fun fill(value: CSSColorValue) = this.attr("fill", value.toString())
    fun fill(value: SVGPaintType) = this.attr("fill", value.toString())

    // TODO: Support Gradients. Unfortunately, com.varabyte.kobweb.compose.css.functions.Gradient doesn't work here as
    //  SVG gradients are different from CSS gradients.
//    fun fill(value: Gradient) = this.attr("fill", value.toString())

    fun fillRule(value: SVGFillRule) = this.attr("fill-rule", value.toString())

    fun fillOpacity(value: Number) = this.attr("fill-opacity", value.toString())
}

/**
 * A composable for creating an SVG element.
 *
 * Within an SVG scope, various children types are available. For example, you can use a [Path] to create an SVG icon:
 *
 * ```
 * Svg(attrs = Modifier.width(24.px).height(24.px)
 *     .toAttrs {
 *         attr("viewBox", "0 0 16 16")
 *         attr("role", "img")
 *         attr("aria-label", "Warning:")
 *     }
 * ) {
 *     Path(attrs = {
 *         attr(
 *             "d",
 *             "M8.982 1.566a1.13 1.13 0 0 0-1.96 0L.165 13.233c-.457.778.091 1.767.98 1.767h13.713c.889 0 1.438-.99.98-1.767L8.982 1.566zM8 5c.535 0 .954.462.9.995l-.35 3.507a.552.552 0 0 1-1.1 0L7.1 5.995A.905.905 0 0 1 8 5zm.002 6a1 1 0 1 1 0 2 1 1 0 0 1 0-2z"
 *         )
 *     })
 * }
 * ```
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/SVG
 */
@Composable
fun Svg(
    attrs: AttrBuilderContext<SVGElement>? = null,
    content: ContentBuilder<SVGElement>
) {
    GenericTag("svg", "http://www.w3.org/2000/svg", attrs, content)
}

// region SVG children

class SVGCircleAttrsScope internal constructor(attrs: AttrsScope<SVGCircleElement>) :
    SVGShapeElementAttrsScope<SVGCircleElement>(attrs) {
    fun cx(value: Number) {
        attr("cx", value.toString())
    }

    fun cy(value: Number) {
        attr("cy", value.toString())
    }

    fun r(value: Number) {
        attr("r", value.toString())
    }

    fun cx(value: CSSLengthOrPercentageValue) {
        attr("cx", value.toString())
    }

    fun cy(value: CSSLengthOrPercentageValue) {
        attr("cy", value.toString())
    }

    fun r(value: CSSLengthOrPercentageValue) {
        attr("r", value.toString())
    }
}

/**
 * Type-safe API for creating an [SVGCircleElement].
 *
 * For example, to create a circle with a radius of 8 centered at 12x12:
 *
 * ```
 * Svg {
 *  Circle {
 *      cx(12)
 *      cy(12)
 *      r(8)
 *  }
 * }
 * ```
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/SVG/Element/circle">SVG Element Circle (Mozilla Docs)</a>
 */

@Composable
fun ElementScope<SVGElement>.Circle(attrs: SVGCircleAttrsScope.() -> Unit) {
    GenericTag("circle", "http://www.w3.org/2000/svg", attrs = {
        SVGCircleAttrsScope(this).attrs()
    })
}


class SVGCEllipseAttrsScope internal constructor(attrs: AttrsScope<SVGEllipseElement>) :
    SVGShapeElementAttrsScope<SVGEllipseElement>(attrs) {
    fun cx(value: Number) {
        attr("cx", value.toString())
    }

    fun cy(value: Number) {
        attr("cy", value.toString())
    }

    fun rx(value: Number) {
        attr("rx", value.toString())
    }

    fun ry(value: Number) {
        attr("ry", value.toString())
    }

    fun cx(value: CSSLengthOrPercentageValue) {
        attr("cx", value.toString())
    }

    fun cy(value: CSSLengthOrPercentageValue) {
        attr("cy", value.toString())
    }

    fun rx(value: CSSLengthOrPercentageValue) {
        attr("rx", value.toString())
    }

    fun ry(value: CSSLengthOrPercentageValue) {
        attr("ry", value.toString())
    }
}


/**
 * Type-safe API for creating an [SVGEllipseElement].
 *
 * For example, to create an Ellipse with a radius x-axis of 100 and y-axis of 50 centered at 12x12:
 *
 * ```
 * Svg {
 *  Ellipse {
 *      cx(100)
 *      cy(50)
 *      rx(100)
 *      ry(50)
 *  }
 * }
 * ```
 *
 * @see <a href=https://developer.mozilla.org/en-US/docs/Web/SVG/Element/ellipse">SVG Ellipse Line (Mozilla Docs)</a>
 */
@Composable
fun ElementScope<SVGElement>.Ellipse(attrs: SVGCEllipseAttrsScope.() -> Unit) {
    GenericTag("ellipse", "http://www.w3.org/2000/svg", attrs = {
        SVGCEllipseAttrsScope(this).attrs()
    })
}


// https://developer.mozilla.org/en-US/docs/Web/SVG/Element/g
@Composable
fun ElementScope<SVGElement>.Group(
    attrs: AttrBuilderContext<SVGGElement>? = null,
    content: ContentBuilder<SVGGElement>
) {
    GenericTag("g", "http://www.w3.org/2000/svg", attrs, content)
}


class SVGLineAttrsScope internal constructor(attrs: AttrsScope<SVGLineElement>) :
    SVGShapeElementAttrsScope<SVGLineElement>(attrs) {
    fun x1(value: Number) {
        attr("x1", value.toString())
    }

    fun x2(value: Number) {
        attr("x2", value.toString())
    }

    fun y1(value: Number) {
        attr("y1", value.toString())
    }

    fun y2(value: Number) {
        attr("y2", value.toString())
    }

    fun x1(value: CSSLengthOrPercentageValue) {
        attr("x1", value.toString())
    }

    fun x2(value: CSSLengthOrPercentageValue) {
        attr("x2", value.toString())
    }

    fun y1(value: CSSLengthOrPercentageValue) {
        attr("y1", value.toString())
    }

    fun y2(value: CSSLengthOrPercentageValue) {
        attr("y2", value.toString())
    }
}

/**
 * Type-safe API for creating an [SVGLineElement].
 *
 * For example, to create a Line from (3, 12) to (21, 12):
 *
 * ```
 * Svg {
 *  Line {
 *      x1(3)
 *      x2(21)
 *      y1(12)
 *      y2(12)
 *  }
 * }
 * ```
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/SVG/Element/line">SVG Element Line (Mozilla Docs)</a>
 */

@Composable
fun ElementScope<SVGElement>.Line(attrs: SVGLineAttrsScope.() -> Unit) {
    GenericTag("line", "http://www.w3.org/2000/svg", attrs = {
        SVGLineAttrsScope(this).attrs()
    })
}

@DslMarker
annotation class PathDataScopeMarker

@PathDataScopeMarker
class PathDataScope internal constructor() {
    internal val pathCommands = mutableListOf<String>()

    fun moveTo(x: Number, y: Number) {
        pathCommands.add("M $x $y")
    }

    fun lineTo(x: Number, y: Number, isRelative: Boolean = false) {
        val command = if (isRelative) "l" else "L"
        pathCommands.add("$command $x $y")
    }

    fun verticalLineTo(x: Number, isRelative: Boolean = false) {
        val command = if (isRelative) "v" else "V"
        pathCommands.add("$command $x")
    }

    fun horizontalLineTo(x: Number, isRelative: Boolean = false) {
        val command = if (isRelative) "h" else "H"
        pathCommands.add("$command $x")
    }

    fun curveTo(x1: Number, y1: Number, x2: Number, y2: Number, x: Number, y: Number, isRelative: Boolean = false) {
        val command = if (isRelative) "c" else "C"
        pathCommands.add("$command $x1 $y1 $x2 $y2 $x $y")
    }

    fun smoothCurveTo(x2: Number, y2: Number, x: Number, y: Number, isRelative: Boolean = false) {
        val command = if (isRelative) "s" else "S"
        pathCommands.add("$command $x2 $y2 $x $y")
    }

    fun quadraticBezierCurve(x1: Number, y1: Number, x: Number, y: Number, isRelative: Boolean) {
        val command = if (isRelative) "q" else "Q"
        pathCommands.add("$command $x1 $y1 $x $y")
    }

    fun smoothQuadraticBezierCurve(x: Number, y: Number, isRelative: Boolean) {
        val command = if (isRelative) "t" else "T"
        pathCommands.add("$command $x $y")
    }

    fun ellipticalArc(
        rx: Number,
        ry: Number,
        rotate: Number,
        largeArcFlag: Number,
        sweepFlag: Number,
        x: Number,
        y: Number,
        isRelative: Boolean = false
    ) {
        val command = if (isRelative) "a" else "A"
        pathCommands.add("$command $rx $ry $rotate $largeArcFlag $sweepFlag $x $y")
    }

    fun closePath() {
        pathCommands.add("Z")
    }
}


class SVGPathAttrsScope internal constructor(attrs: AttrsScope<SVGPathElement>) :
    SVGShapeElementAttrsScope<SVGPathElement>(attrs) {
    fun d(pathDataScope: PathDataScope.() -> Unit) {
        attr("d", PathDataScope().apply(pathDataScope).pathCommands.joinToString(" "))
    }
}

/**
 * Type-safe API for creating an [SVGPathElement].
 *
 * For example, to create a ChevronDownIcon using Path
 *
 * ```
 * Svg {
 *  Path {
 *     d {
 *         moveTo(16.59, 8.59)
 *         lineTo(12, 13.17)
 *         lineTo(7.41, 8.59)
 *         lineTo(6, 10)
 *         lineTo(6, 6, isRelative = true)
 *         lineTo(6, -6, isRelative = true)
 *         closePath()
 *     }
 *  }
 * }
 * ```
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/SVG/Element/path">SVG Element Path (Mozilla Docs)</a>
 */
@Composable
fun ElementScope<SVGElement>.Path(attrs: SVGPathAttrsScope.() -> Unit) {
    GenericTag("path", "http://www.w3.org/2000/svg", attrs = {
        SVGPathAttrsScope(this).attrs()
    })
}


class SVGPolygonAttrsScope internal constructor(attrs: AttrsScope<SVGPolygonElement>) :
    SVGShapeElementAttrsScope<SVGPolygonElement>(attrs) {
    fun points(vararg pairs: Pair<Number, Number>) {
        val pointString = pairs.joinToString(" ") { "${it.first},${it.second}" }
        attr("points", pointString)
    }
}

/**
 * Type-safe API for creating an [SVGPolygonElement].
 *
 * For example, to create a Polygon from points (3, 12), (9,19), (21, 2):
 *
 * ```
 * Svg {
 *  Polygon {
 *      points(3 to 12, 9 to 19, 21 to 2)
 *  }
 * }
 * ```
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/SVG/Element/polygon">SVG Element Polygon (Mozilla Docs)</a>
 */
@Composable
fun ElementScope<SVGElement>.Polygon(attrs: SVGPolygonAttrsScope.() -> Unit) {
    GenericTag("polygon", "http://www.w3.org/2000/svg", attrs = {
        SVGPolygonAttrsScope(this).attrs()
    })
}


class SVGPolylineAttrsScope internal constructor(attrs: AttrsScope<SVGPolylineElement>) :
    SVGShapeElementAttrsScope<SVGPolylineElement>(attrs) {
    fun points(vararg pairs: Pair<Number, Number>) {
        val pointString = pairs.joinToString(" ") { "${it.first},${it.second}" }
        attr("points", pointString)
    }
}

/**
 * Type-safe API for creating an [SVGPolylineElement].
 *
 * For example, to create a Polyline from points (3, 12), (9,19), (21, 2):
 *
 * ```
 * Svg {
 *  Polyline {
 *      points(3 to 12, 9 to 19, 21 to 2)
 *  }
 * }
 * ```
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/SVG/Element/polyline">SVG Element Polyline (Mozilla Docs)</a>
 */

@Composable
fun ElementScope<SVGElement>.Polyline(attrs: SVGPolylineAttrsScope.() -> Unit) {
    GenericTag("polyline", "http://www.w3.org/2000/svg", attrs = {
        SVGPolylineAttrsScope(this).attrs()
    })
}


class SVGCRectAttrsScope internal constructor(attrs: AttrsScope<SVGRectElement>) :
    SVGShapeElementAttrsScope<SVGRectElement>(attrs) {
    fun x(value: Number) {
        attr("x", value.toString())
    }

    fun y(value: Number) {
        attr("y", value.toString())
    }

    fun width(value: Number) {
        attr("width", value.toString())
    }

    fun height(value: Number) {
        attr("height", value.toString())
    }

    fun rx(value: Number) {
        attr("rx", value.toString())
    }

    fun ry(value: Number) {
        attr("ry", value.toString())
    }

    fun x(value: CSSLengthOrPercentageValue) {
        attr("x", value.toString())
    }

    fun y(value: CSSLengthOrPercentageValue) {
        attr("y", value.toString())
    }

    fun width(value: CSSLengthOrPercentageValue) {
        attr("width", value.toString())
    }

    fun height(value: CSSLengthOrPercentageValue) {
        attr("height", value.toString())
    }

    fun rx(value: CSSLengthOrPercentageValue) {
        attr("rx", value.toString())
    }

    fun ry(value: CSSLengthOrPercentageValue) {
        attr("ry", value.toString())
    }
}

/**
 * Type-safe API for creating an [SVGRectElement].
 *
 * For example, to create a Rect and Rounded Corner Rect of size 100x100 :
 *
 * ```
 * // Simple Rectangle
 * Svg {
 *  Rect {
 *      width(100)
 *      height(100)
 *  }
 * }
 *
 * // Rounded corner rectangle
 * Svg {
 *  Rect {
 *      x(4)
 *      y(4)
 *      width(100)
 *      height(100)
 *      rx(15)
 *      ry(15)
 *  }
 * }
 * ```
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/SVG/Element/rect">SVG Element Rect (Mozilla Docs)</a>
 */

@Composable
fun ElementScope<SVGElement>.Rect(attrs: SVGCRectAttrsScope.() -> Unit) {
    GenericTag("rect", "http://www.w3.org/2000/svg", attrs = {
        SVGCRectAttrsScope(this).attrs()
    })
}


enum class SvgTextLengthAdjust {
    Spacing,
    SpacingAndGlyphs;

    override fun toString() = this.toSvgValue()
}

class SVGTextAttrsScope internal constructor(attrs: AttrsScope<SVGTextElement>) :
    SVGShapeElementAttrsScope<SVGTextElement>(attrs) {

    fun x(value: Number) {
        attr("x", value.toString())
    }

    fun x(value: CSSLengthOrPercentageValue) {
        attr("x", value.toString())
    }

    fun y(value: Number) {
        attr("y", value.toString())
    }

    fun y(value: CSSLengthOrPercentageValue) {
        attr("y", value.toString())
    }

    fun dx(value: Number) {
        attr("dx", value.toString())
    }

    fun dx(value: CSSLengthOrPercentageValue) {
        attr("dx", value.toString())
    }

    fun dy(value: Number) {
        attr("dy", value.toString())
    }

    fun dy(value: CSSLengthOrPercentageValue) {
        attr("dy", value.toString())
    }

    fun rotate(vararg angles: Number) {
        val value = angles.joinToString(",") { it.toString() }
        attr("rotate", value)
    }

    fun lengthAdjust(lengthAdjust: SvgTextLengthAdjust) {
        attr("lengthAdjust", lengthAdjust.toString())
    }

    fun letterSpacing(value: Number) {
        attr("letter-spacing", value.toString())
    }

    fun letterSpacing(value: CSSLengthOrPercentageValue) {
        attr("letter-spacing", value.toString())
    }

    fun wordSpacing(value: Number) {
        attr("word-spacing", value.toString())
    }

    fun wordSpacing(value: CSSLengthOrPercentageValue) {
        attr("word-spacing", value.toString())
    }

    fun textDecoration(value: TextDecorationLine) {
        attr("text-decoration", value.toString())
    }

    fun textLength(value: Number) {
        attr("textLength", value.toString())
    }

    fun textLength(value: CSSLengthOrPercentageValue) {
        attr("textLength", value.toString())
    }

    fun fontStyle(value: FontStyle) {
        attr("font-style", value.toString())
    }

    fun fontSize(value: Number) {
        attr("font-size", value.toString())
    }

    fun fontSize(value: FontSize) {
        attr("font-size", value.toString())
    }

    fun fontSize(value: CSSLengthOrPercentageValue) {
        attr("font-size", value.toString())
    }

    fun fontWeight(value: Number) {
        attr("font-weight", value.toString())
    }

    fun fontWeight(value: FontWeight) {
        attr("font-weight", value.toString())
    }
}

/**
 * Type-safe API for creating an [SVGTextElement].
 *
 * For example, to create a Text of value "Hello World":
 *
 * ```
 * Svg {
 *  Text("Hello World") {
 *      x(20)
 *      y(40)
 *      fill(Color.blue)
 *      rotate(0, 30)
 *      fontSize(15)
 *      fontWeight(FontWeight.SemiBold)
 *      textDecoration(TextDecorationLine.Underline)
 *  }
 * }
 * ```
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/SVG/Element/text">SVG Element Text (Mozilla Docs)</a>
 */
@Composable
fun ElementScope<SVGElement>.Text(text: String, attrs: SVGTextAttrsScope.() -> Unit) {
    @Suppress("RemoveExplicitTypeArguments") // IDE wants to remove generic type but that causes a compile error
    GenericTag<SVGTextElement>("text", "http://www.w3.org/2000/svg", attrs = {
        SVGTextAttrsScope(this).attrs()
    }) {
        Text(text)
    }
}

// endregion
