// SVGElement scope is useful to ensure SVG children are only used within an SVG
@file:Suppress("UnusedReceiverParameter")

package com.varabyte.kobweb.compose.dom

import androidx.compose.runtime.*
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

// https://developer.mozilla.org/en-US/docs/Web/SVG/Element/circle
@Composable
fun ElementScope<SVGElement>.Circle(attrs: AttrBuilderContext<SVGCircleElement>) {
    GenericTag("circle", "http://www.w3.org/2000/svg", attrs)
}

// https://developer.mozilla.org/en-US/docs/Web/SVG/Element/ellipse
@Composable
fun ElementScope<SVGElement>.Ellipse(attrs: AttrBuilderContext<SVGEllipseElement>) {
    GenericTag("ellipse", "http://www.w3.org/2000/svg", attrs)
}

// https://developer.mozilla.org/en-US/docs/Web/SVG/Element/g
@Composable
fun ElementScope<SVGElement>.Group(
    attrs: AttrBuilderContext<SVGGElement>? = null,
    content: ContentBuilder<SVGGElement>
) {
    GenericTag("group", "http://www.w3.org/2000/svg", attrs, content)
}

// https://developer.mozilla.org/en-US/docs/Web/SVG/Element/line
@Composable
fun ElementScope<SVGElement>.Line(attrs: AttrBuilderContext<SVGLineElement>) {
    GenericTag("line", "http://www.w3.org/2000/svg", attrs)
}

// https://developer.mozilla.org/en-US/docs/Web/SVG/Element/path
@Composable
fun ElementScope<SVGElement>.Path(attrs: AttrBuilderContext<SVGPathElement>) {
    GenericTag("path", "http://www.w3.org/2000/svg", attrs)
}

// https://developer.mozilla.org/en-US/docs/Web/SVG/Element/polygon
@Composable
fun ElementScope<SVGElement>.Polygon(attrs: AttrBuilderContext<SVGPolygonElement>) {
    GenericTag("polygon", "http://www.w3.org/2000/svg", attrs)
}

// https://developer.mozilla.org/en-US/docs/Web/SVG/Element/polyline
@Composable
fun ElementScope<SVGElement>.Polyline(attrs: AttrBuilderContext<SVGPolylineElement>) {
    GenericTag("polyline", "http://www.w3.org/2000/svg", attrs)
}

// https://developer.mozilla.org/en-US/docs/Web/SVG/Element/rect
@Composable
fun ElementScope<SVGElement>.Rect(attrs: AttrBuilderContext<SVGRectElement>) {
    GenericTag("rect", "http://www.w3.org/2000/svg", attrs)
}

// https://developer.mozilla.org/en-US/docs/Web/SVG/Element/text
@Composable
fun ElementScope<SVGElement>.Text(text: String, attrs: AttrBuilderContext<SVGTextElement>) {
    GenericTag("text", "http://www.w3.org/2000/svg", attrs) {
        Text(text)
    }
}

// endregion
