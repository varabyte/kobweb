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

/**
 * A dedicated Scope for Circle
 *
 * Member methods: cx(), cy(), r()
 *
 * All of these above methods accept: Numbers, Length, Percentage values
 */

class SVGCircleScope(private val attrs: AttrsScope<SVGCircleElement>) : SVGElementScope(attrs) {
    fun cx(value: Number) { attrs.attr("cx", value.toString()) }

    fun cy(value: Number) { attrs.attr("cy", value.toString()) }

    fun r(value: Number) { attrs.attr("r", value.toString()) }

    fun cx(value: CSSLengthOrPercentageValue) { attrs.attr("cx", value.value.toString()) }

    fun cy(value: CSSLengthOrPercentageValue) { attrs.attr("cy", value.value.toString()) }

    fun r(value: CSSLengthOrPercentageValue) { attrs.attr("r", value.value.toString()) }
}

/**
 * Create a Circle, see example:
 * ```
 * Circle {
 *    cx(12) // The x-axis coordinate of the center of the circle.
 *    cy(12) // The y-axis coordinate of the center of the circle.
 *    r(8)   // The radius of the circle. A value lower or equal to zero disables rendering of the circle.
 * }
 * ```
 * See also: https://developer.mozilla.org/en-US/docs/Web/SVG/Element/circle
 * */
@Composable
fun ElementScope<SVGElement>.Circle(scope: SVGCircleScope.() -> Unit) {
    GenericTag("circle", "http://www.w3.org/2000/svg", attrs = {
        SVGCircleScope(this).scope()
    })
}


/**
 * A dedicated Scope for Line
 *
 * Member methods: x1(), x2(), y1(), y2()
 *
 * All of these above methods accept: Numbers, Length, Percentage values
 */

class SVGLineScope(private val attrs: AttrsScope<SVGLineElement>) : SVGElementScope(attrs) {
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
 * Create a Line, see example:
 * ```
 * Line {
 *     x1(3)  // The x-axis coordinate of the line starting point.
 *     x2(21) // The x-axis coordinate of the line ending point.
 *     y1(12) // The y-axis coordinate of the line starting point.
 *     y2(12) // The y-axis coordinate of the line ending point.
 * }
 * ```
 * See also: https://developer.mozilla.org/en-US/docs/Web/SVG/Element/line
 * */
@Composable
fun ElementScope<SVGElement>.Line(scope: SVGLineScope.() -> Unit) {
    GenericTag("line", "http://www.w3.org/2000/svg", attrs = {
        SVGLineScope(this).scope()
    })
}


/**
 * A dedicated Scope for Ellipse
 *
 * Member methods: cx(), cy(), rx(), ry()
 *
 * All of these above methods accept: Numbers, Length, Percentage values
 */

class SVGCEllipseScope(private val attrs: AttrsScope<SVGEllipseElement>) : SVGElementScope(attrs) {
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
 * Create an Ellipse, see example:
 * ```
 * Ellipse {
 *      cx(100) // The x position of the center of the ellipse.
 *      cy(50)  // The y position of the center of the ellipse.
 *      rx(100) // The radius of the ellipse on the x axis.
 *      ry(50)  // The radius of the ellipse on the y axis.
 * }
 * ```
 * See also: https://developer.mozilla.org/en-US/docs/Web/SVG/Element/ellipse
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


// https://developer.mozilla.org/en-US/docs/Web/SVG/Element/path
@Composable
fun ElementScope<SVGElement>.Path(attrs: AttrBuilderContext<SVGPathElement>) {
    GenericTag("path", "http://www.w3.org/2000/svg", attrs)
}



/**
 * A dedicated Scope for Polygon
 *
 * Member methods: points(), stroke()
 *
 * The above method accepts:
 *
 * 1. points() -> Pair(Number, Number).
 *
 * 2. stroke() -> CSSColorValue
 */
class SVGPolygonScope(private val attrs: AttrsScope<SVGPolygonElement>) : SVGElementScope(attrs) {
    fun points(vararg pairs: Pair<Number, Number>) {
        val pointString = pairs.joinToString(" ") { "${it.first},${it.second}" }
        attrs.attr("points", pointString)
    }

    fun stroke(value: CSSColorValue) { attrs.attr("stroke", value.toString()) }

    //TODO: fill() property
}

// https://developer.mozilla.org/en-US/docs/Web/SVG/Element/polygon
@Composable
fun ElementScope<SVGElement>.Polygon(scope: SVGPolygonScope.() -> Unit) {
    GenericTag("polygon", "http://www.w3.org/2000/svg", attrs = {
        SVGPolygonScope(this).scope()
    })
}



/**
 * A dedicated Scope for Polyline
 *
 * Member methods: points(), stroke()
 *
 * The above method accepts:
 *
 * 1. points() -> Pair(Number, Number).
 *
 * 2. stroke() -> CSSColorValue
 */
class SVGPolylineScope(private val attrs: AttrsScope<SVGPolylineElement>) : SVGElementScope(attrs) {
    fun points(vararg pairs: Pair<Number, Number>) {
        val pointString = pairs.joinToString(" ") { "${it.first},${it.second}" }
        attrs.attr("points", pointString)
    }

    fun stroke(value: CSSColorValue) { attrs.attr("stroke", value.toString()) }

    //TODO: fill() property
}

/**
 * Create a Polyline using this function, see example:
 * ```
 * Polyline {
 *    points(
 *       Pair(1.5, 6),
 *       Pair(4.5, 9),
 *       Pair(10.5, 1)
 *    )
 * }
 * ```
 * See also: https://developer.mozilla.org/en-US/docs/Web/SVG/Element/polyline
 */
@Composable
fun ElementScope<SVGElement>.Polyline(scope: SVGPolylineScope.() -> Unit) {
    GenericTag("polyline", "http://www.w3.org/2000/svg", attrs = {
        SVGPolylineScope(this).scope()
    })
}


/**
 * A dedicated Scope for Rect
 *
 * Member methods: x(), y(), width(), height(), rx(), ry()
 *
 * All of these above methods accept: Numbers, Length, Percentage values
 */

class SVGCRectScope(private val attrs: AttrsScope<SVGRectElement>) : SVGElementScope(attrs) {
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
 * Create a Rectangle using this function, see example:
 * ```
 * // Simple Rectangle
 * Rect {
 *      width(100)  // The width of the rect.
 *      height(100) // The height of the rect.
 * }
 *
 * // Rounded corner rectangle
 * Rect {
 *      x(4)        // The x coordinate of the rect.
 *      y(4)        // The y coordinate of the rect.
 *      width(100)  // The width of the rect.
 *      height(100) // The height of the rect.
 *      rx(15)      // The horizontal corner radius of the rect.
 *      ry(15)      // The vertical corner radius of the rect.
 * }
 * ```
 * See also: https://developer.mozilla.org/en-US/docs/Web/SVG/Element/rect
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