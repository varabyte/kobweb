// SVGElement scope is useful to ensure SVG children are only used within an SVG
@file:Suppress("UnusedReceiverParameter")

package com.varabyte.kobweb.compose.dom

import androidx.compose.runtime.*
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

abstract class SVGElementScope(private val attrs: AttrsScope<SVGElement>) {
    fun attr(name: String, value: String) {
        attrs.attr(name, value)
    }
}

abstract class SVGShapeElementScope(attrs: AttrsScope<SVGElement>) : SVGElementScope(attrs) {
    fun stroke(value: CSSColorValue) { this.attr("stroke", value.toString()) }
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
    content: ContentBuilder<SVGElement>? = null
) {
    GenericTag("svg", "http://www.w3.org/2000/svg", attrs, content)
}

// region SVG children

class SVGCircleScope internal constructor(private val attrs: AttrsScope<SVGCircleElement>) : SVGShapeElementScope(attrs) {
    fun cx(value: Number) { attrs.attr("cx", value.toString()) }

    fun cy(value: Number) { attrs.attr("cy", value.toString()) }

    fun r(value: Number) { attrs.attr("r", value.toString()) }

    fun cx(value: CSSLengthOrPercentageValue) { attrs.attr("cx", value.value.toString()) }

    fun cy(value: CSSLengthOrPercentageValue) { attrs.attr("cy", value.value.toString()) }

    fun r(value: CSSLengthOrPercentageValue) { attrs.attr("r", value.value.toString()) }
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
fun ElementScope<SVGElement>.Circle(scope: SVGCircleScope.() -> Unit) {
    GenericTag("circle", "http://www.w3.org/2000/svg", attrs = {
        SVGCircleScope(this).scope()
    })
}


class SVGCEllipseScope internal constructor(private val attrs: AttrsScope<SVGEllipseElement>) : SVGShapeElementScope(attrs) {
    fun cx(value: Number) { attrs.attr("cx", value.toString()) }

    fun cy(value: Number) { attrs.attr("cy", value.toString()) }

    fun rx(value: Number) { attrs.attr("rx", value.toString()) }

    fun ry(value: Number) { attrs.attr("ry", value.toString()) }

    fun cx(value: CSSLengthOrPercentageValue) { attrs.attr("cx", value.value.toString()) }

    fun cy(value: CSSLengthOrPercentageValue) { attrs.attr("cy", value.value.toString()) }

    fun rx(value: CSSLengthOrPercentageValue) { attrs.attr("rx", value.value.toString()) }

    fun ry(value: CSSLengthOrPercentageValue) { attrs.attr("ry", value.value.toString()) }
}


/**
 * Type-safe API for creating an [SVGEllipseElement].
 *
 * For example, to create a circle with a radius of 8 centered at 12x12:
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
fun ElementScope<SVGElement>.Ellipse(scope: SVGCEllipseScope.() -> Unit) {
    GenericTag("ellipse", "http://www.w3.org/2000/svg", attrs = {
        SVGCEllipseScope(this).scope()
    })
}


// https://developer.mozilla.org/en-US/docs/Web/SVG/Element/g
@Composable
fun ElementScope<SVGElement>.Group(
    attrs: AttrBuilderContext<SVGGElement>? = null,
    content: ContentBuilder<SVGGElement>
) {
    GenericTag("group", "http://www.w3.org/2000/svg", attrs, content)
}


class SVGLineScope internal constructor(private val attrs: AttrsScope<SVGLineElement>) : SVGShapeElementScope(attrs) {
    fun x1(value: Number) { attrs.attr("x1", value.toString()) }

    fun x2(value: Number) { attrs.attr("x2", value.toString()) }

    fun y1(value: Number) { attrs.attr("y1", value.toString()) }

    fun y2(value: Number) { attrs.attr("y2", value.toString()) }

    fun x1(value: CSSLengthOrPercentageValue) { attrs.attr("x1", value.value.toString()) }

    fun x2(value: CSSLengthOrPercentageValue) { attrs.attr("x2", value.value.toString()) }

    fun y1(value: CSSLengthOrPercentageValue) { attrs.attr("y1", value.value.toString()) }

    fun y2(value: CSSLengthOrPercentageValue) { attrs.attr("y2", value.value.toString()) }
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
fun ElementScope<SVGElement>.Line(scope: SVGLineScope.() -> Unit) {
    GenericTag("line", "http://www.w3.org/2000/svg", attrs = {
        SVGLineScope(this).scope()
    })
}


// https://developer.mozilla.org/en-US/docs/Web/SVG/Element/path
@Composable
fun ElementScope<SVGElement>.Path(attrs: AttrBuilderContext<SVGPathElement>) {
    GenericTag("path", "http://www.w3.org/2000/svg", attrs)
}


class SVGPolygonScope internal constructor(private val attrs: AttrsScope<SVGPolygonElement>) : SVGShapeElementScope(attrs) {
    fun points(vararg pairs: Pair<Number, Number>) {
        val pointString = pairs.joinToString(" ") { "${it.first},${it.second}" }
        attrs.attr("points", pointString)
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
fun ElementScope<SVGElement>.Polygon(scope: SVGPolygonScope.() -> Unit) {
    GenericTag("polygon", "http://www.w3.org/2000/svg", attrs = {
        SVGPolygonScope(this).scope()
    })
}


class SVGPolylineScope internal constructor(private val attrs: AttrsScope<SVGPolylineElement>) : SVGShapeElementScope(attrs) {
    fun points(vararg pairs: Pair<Number, Number>) {
        val pointString = pairs.joinToString(" ") { "${it.first},${it.second}" }
        attrs.attr("points", pointString)
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
fun ElementScope<SVGElement>.Polyline(scope: SVGPolylineScope.() -> Unit) {
    GenericTag("polyline", "http://www.w3.org/2000/svg", attrs = {
        SVGPolylineScope(this).scope()
    })
}


class SVGCRectScope internal constructor(private val attrs: AttrsScope<SVGRectElement>) : SVGShapeElementScope(attrs) {
    fun x(value: Number) { attrs.attr("x", value.toString()) }

    fun y(value: Number) { attrs.attr("y", value.toString()) }

    fun width(value: Number) { attrs.attr("width", value.toString()) }

    fun height(value: Number) { attrs.attr("height", value.toString()) }

    fun rx(value: Number) { attrs.attr("rx", value.toString()) }

    fun ry(value: Number) { attrs.attr("ry", value.toString()) }

    fun x(value: CSSLengthOrPercentageValue) { attrs.attr("x", value.value.toString()) }

    fun y(value: CSSLengthOrPercentageValue) { attrs.attr("y", value.value.toString()) }

    fun width(value: CSSLengthOrPercentageValue) { attrs.attr("width", value.value.toString()) }

    fun height(value: CSSLengthOrPercentageValue) { attrs.attr("height", value.value.toString()) }

    fun rx(value: CSSLengthOrPercentageValue) { attrs.attr("rx", value.value.toString()) }

    fun ry(value: CSSLengthOrPercentageValue) { attrs.attr("ry", value.value.toString()) }
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
fun ElementScope<SVGElement>.Rect(scope: SVGCRectScope.() -> Unit) {
    GenericTag("rect", "http://www.w3.org/2000/svg", attrs = {
        SVGCRectScope(this).scope()
    })
}

// https://developer.mozilla.org/en-US/docs/Web/SVG/Element/text
@Composable
fun ElementScope<SVGElement>.Text(text: String, attrs: AttrBuilderContext<SVGTextElement>) {
    GenericTag("text", "http://www.w3.org/2000/svg", attrs) {
        Text(text)
    }
}

// endregion