// SVGElement scope is useful to ensure SVG children are only used within an SVG
@file:Suppress("UnusedReceiverParameter")

package com.varabyte.kobweb.compose.dom.svg

import androidx.compose.runtime.*
import com.varabyte.kobweb.browser.util.titleCamelCaseToKebabCase
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.dom.GenericTag
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.attributes.HtmlAttrMarker
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.AttrBuilderContext
import org.jetbrains.compose.web.dom.ContentBuilder
import org.jetbrains.compose.web.dom.ElementScope
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.svg.SVGCircleElement
import org.w3c.dom.svg.SVGDefsElement
import org.w3c.dom.svg.SVGElement
import org.w3c.dom.svg.SVGEllipseElement
import org.w3c.dom.svg.SVGGElement
import org.w3c.dom.svg.SVGGradientElement
import org.w3c.dom.svg.SVGImageElement
import org.w3c.dom.svg.SVGLineElement
import org.w3c.dom.svg.SVGLinearGradientElement
import org.w3c.dom.svg.SVGPathElement
import org.w3c.dom.svg.SVGPatternElement
import org.w3c.dom.svg.SVGPolygonElement
import org.w3c.dom.svg.SVGPolylineElement
import org.w3c.dom.svg.SVGRadialGradientElement
import org.w3c.dom.svg.SVGRectElement
import org.w3c.dom.svg.SVGSVGElement
import org.w3c.dom.svg.SVGStopElement
import org.w3c.dom.svg.SVGSymbolElement
import org.w3c.dom.svg.SVGTextElement
import org.w3c.dom.svg.SVGUseElement

/**
 * An ID tied to some reusable SVG element.
 *
 * Useful as a way to get a reference to IDs for gradients and patterns.
 *
 * For example:
 * ```
 * Svg(...) {
 *     val goldToOrangeGradientId = SvgId("goldToOrangeGradient")
 *     Defs {
 *         LinearGradient(goldToOrangeGradientId) {
 *             Stop(10.percent, Colors.Gold)
 *             Stop(90.percent, Colors.DarkOrange)
 *         }
 *     }
 *
 *     Circle {
 *         cx(100); cy(100); r(50)
 *         fill(goldToOrangeGradientId)
 *     }
 * }
 * ```
 */
value class SvgId(val value: String) {
    override fun toString() = value
    val urlReference get() = "url(#$value)"
    val hashReference get() = "#$value"
}

@HtmlAttrMarker
class SVGTransformScope internal constructor() {
    internal val transformCommands = mutableListOf<String>()

    fun matrix(a: Number, b: Number, c: Number, d: Number, e: Number, f: Number) {
        transformCommands.add("matrix($a $b $c $d $e $f)")
    }

    fun translate(x: Number, y: Number? = null) {
        transformCommands.add(buildString {
            append("translate($x")
            y?.let { append(" $it") }
            append(")")
        })
    }

    fun translateX(value: Number) {
        translate(value)
    }

    fun translateY(value: Number) {
        translate(0, value)
    }

    fun scale(x: Number, y: Number? = null) {
        transformCommands.add(buildString {
            append("scale($x")
            y?.let { append(" $it") }
            append(")")
        })
    }

    fun scaleX(value: Number) {
        scale(value)
    }

    fun scaleY(value: Number) {
        scale(1, value)
    }

    fun rotate(angle: Number, x: Number? = null, y: Number? = null) {
        transformCommands.add(buildString {
            append("rotate($angle")
            x?.let { append(" $it") }
            y?.let { append(" $it") }
            append(")")
        })
    }

    fun rotate(angle: CSSAngleValue, x: Number? = null, y: Number? = null) {
        rotate(angle.toDegrees(), x, y)
    }

    fun skewX(angle: Number) {
        transformCommands.add("skewX($angle)")
    }

    fun skewX(angle: CSSAngleValue) {
        skewX(angle.toDegrees())
    }

    fun skewY(angle: Number) {
        transformCommands.add("skewY($angle)")
    }

    fun skewY(angle: CSSAngleValue) {
        skewY(angle.toDegrees())
    }
}

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
abstract class SVGElementAttrsScope<E : SVGElement> protected constructor(attrs: AttrsScope<E>) :
    AttrsScope<E> by attrs {

    fun transform(transformScope: SVGTransformScope.() -> Unit) {
        val scope = SVGTransformScope()
        scope.transformScope()
        attr("transform", scope.transformCommands.joinToString(" "))
    }
}

// Reformat to value expected by SVG tag, e.g. "CurrentColor" -> "currentColor"
// Enums have to be capitalized title case for this method to work.
internal fun <E : Enum<E>> Enum<E>.toSvgValue() = name.replaceFirstChar { it.lowercase() }

// region Common SVG enumerations

enum class SVGCrossOrigin {
    Anonymous,
    UseCredentials;

    override fun toString() = this.name.titleCamelCaseToKebabCase()
}

enum class SVGFillType {
    None,
    CurrentColor;

    override fun toString() = this.toSvgValue()
}

enum class SVGStrokeType {
    None,
    CurrentColor;

    override fun toString() = this.toSvgValue()
}

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

enum class SVGAspectRatioAlignment {
    None,
    XMinYMin,
    XMidYMin,
    XMaxYMin,
    XMinYMid,
    XMidYMid,
    XMaxYMid,
    XMinYMax,
    XMidYMax,
    XMaxYMax;

    override fun toString() = this.toSvgValue()
}

enum class SVGAspectRatioScale {
    Meet,
    Slice;

    override fun toString() = this.toSvgValue()
}


// endregion

// region Shared SVG traits

interface SvgCenterCoordinateAttrs<T : SVGElement> : AttrsScope<T> {
    fun cx(value: Number) {
        attr("cx", value.toString())
    }

    fun cx(value: CSSLengthOrPercentageValue) {
        attr("cx", value.toString())
    }

    fun cy(value: Number) {
        attr("cy", value.toString())
    }

    fun cy(value: CSSLengthOrPercentageValue) {
        attr("cy", value.toString())
    }
}

interface SvgCoordinateAttrs<T : SVGElement> : AttrsScope<T> {
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
}

interface SvgCrossOriginAttrs<T : SVGElement> : AttrsScope<T> {
    fun crossOrigin(value: SVGCrossOrigin) {
        attr("crossOrigin", value.toString())
    }
}

interface SvgLengthAttrs<T : SVGElement> : AttrsScope<T> {
    fun height(value: Number) {
        attr("height", value.toString())
    }

    fun height(value: CSSLengthOrPercentageValue) {
        attr("height", value.toString())
    }

    fun width(value: Number) {
        attr("width", value.toString())
    }

    fun width(value: CSSLengthOrPercentageValue) {
        attr("width", value.toString())
    }
}

interface SvgPointsAttrs<T : SVGElement> : AttrsScope<T> {
    fun points(vararg pairs: Pair<Number, Number>) {
        val pointString = pairs.joinToString(" ") { "${it.first},${it.second}" }
        attr("points", pointString)
    }
}

interface SvgPresentationAttrs<T : SVGElement> : AttrsScope<T> {
    fun stroke(value: CSSColorValue) = this.attr("stroke", value.toString())
    fun stroke(value: SVGStrokeType) = this.attr("stroke", value.toString())
    fun stroke(id: SvgId) = this.attr("stroke", id.urlReference)

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
    fun fill(value: SVGFillType) = this.attr("fill", value.toString())
    fun fill(id: SvgId) = this.attr("fill", id.urlReference)

    fun fillRule(value: SVGFillRule) = this.attr("fill-rule", value.toString())

    fun fillOpacity(value: Number) = this.attr("fill-opacity", value.toString())

    fun filter(id: SvgId) = this.attr("filter", id.urlReference)

    fun floodColor(color: CSSColorValue) = attr("flood-color", color.toString())
    fun floodOpacity(value: Number) = attr("flood-opacity", value.toString())

    fun lightingColor(color: CSSColorValue) = attr("lighting-color", color.toString())
}

interface SvgPreserveAspectRatioAttrs<T : SVGElement> : AttrsScope<T> {
    fun preserveAspectRatio(alignment: SVGAspectRatioAlignment, scale: SVGAspectRatioScale? = null) {
        attr("preserveAspectRatio", buildString {
            append(alignment)
            scale?.let { append(' '); append(it) }
        })
    }
}

/**
 * Parameters that can be used to set the viewBox attribute of an SVG.
 */
class ViewBox(val x: Int, val y: Int, val width: Int, val height: Int) {
    companion object {
        fun sized(width: Int, height: Int = width) = ViewBox(0, 0, width, height)
    }
}


interface SvgViewBoxAttrs<T : SVGElement> : AttrsScope<T> {
    fun viewBox(x: Number, y: Number, width: Number, height: Number) {
        attr("viewBox", "$x $y $width $height")
    }

    fun viewBox(viewBox: ViewBox) {
        viewBox(viewBox.x, viewBox.y, viewBox.width, viewBox.height)
    }
}

// endregion

abstract class SVGGraphicalElementAttrsScope<E : SVGElement>(attrs: AttrsScope<E>) : SVGElementAttrsScope<E>(attrs),
    SvgPresentationAttrs<E>

// Useful for attributes shared between top-level svg elements and group elements
abstract class SVGContainerElementAttrsScope<E : SVGElement>(attrs: AttrsScope<E>) :
    SVGGraphicalElementAttrsScope<E>(attrs) {
}

class SVGSvgAttrsScope private constructor(attrs: AttrsScope<SVGSVGElement>) :
    SVGContainerElementAttrsScope<SVGSVGElement>(attrs),
    SvgCoordinateAttrs<SVGSVGElement>, SvgLengthAttrs<SVGSVGElement>, SvgViewBoxAttrs<SVGSVGElement> {
    companion object {
        operator fun invoke(attrs: (SVGSvgAttrsScope.() -> Unit)?): AttrBuilderContext<SVGSVGElement> {
            return { if (attrs != null) SVGSvgAttrsScope(this).attrs() }
        }
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
    attrs: (SVGSvgAttrsScope.() -> Unit)? = null,
    content: ContentBuilder<SVGSVGElement>
) {
    GenericTag("svg", "http://www.w3.org/2000/svg", SVGSvgAttrsScope.invoke(attrs), content)
}

// region SVG misc elements

class SVGDefsAttrsScope private constructor(attrs: AttrsScope<SVGDefsElement>) :
    SVGElementAttrsScope<SVGDefsElement>(attrs) {
    companion object {
        operator fun invoke(attrs: SVGDefsAttrsScope.() -> Unit): AttrBuilderContext<SVGDefsElement> {
            return { SVGDefsAttrsScope(this).attrs() }
        }
    }
}

@Composable
fun ElementScope<SVGElement>.Defs(
    attrs: (SVGDefsAttrsScope.() -> Unit)? = null,
    content: ContentBuilder<SVGDefsElement>
) {
    GenericTag(
        "defs",
        "http://www.w3.org/2000/svg", attrs?.let { SVGDefsAttrsScope(it) }, content
    )
}

enum class SVGGradientUnits {
    UserSpaceOnUse,
    UserSpace,
    ObjectBoundingBox;

    override fun toString() = this.toSvgValue()
}

enum class SVGGradientSpreadMethod {
    Pad,
    Reflect,
    Repeat;

    override fun toString() = this.toSvgValue()
}

abstract class SVGGradientAttrsScope<E : SVGGradientElement> protected constructor(id: SvgId, attrs: AttrsScope<E>) :
    SVGElementAttrsScope<E>(attrs.id(id.toString())) {

    fun gradientUnits(value: SVGGradientUnits) {
        attr("gradientUnits", value.toString())
    }

    fun spreadMethod(value: SVGGradientSpreadMethod) {
        attr("spreadMethod", value.toString())
    }
}

class SVGLinearGradientAttrsScope private constructor(id: SvgId, attrs: AttrsScope<SVGLinearGradientElement>) :
    SVGGradientAttrsScope<SVGLinearGradientElement>(id, attrs) {

    companion object {
        operator fun invoke(
            id: SvgId,
            attrs: (SVGLinearGradientAttrsScope.() -> Unit)?
        ): AttrBuilderContext<SVGLinearGradientElement> {
            return {
                if (attrs != null) {
                    SVGLinearGradientAttrsScope(id, this).attrs()
                } else {
                    id(id.toString())
                }
            }
        }
    }

    fun x1(value: Number) {
        attr("x1", value.toString())
    }

    fun x1(value: CSSLengthOrPercentageValue) {
        attr("x1", value.toString())
    }

    fun y1(value: Number) {
        attr("y1", value.toString())
    }

    fun y1(value: CSSLengthOrPercentageValue) {
        attr("y1", value.toString())
    }

    fun x2(value: Number) {
        attr("x2", value.toString())
    }

    fun x2(value: CSSLengthOrPercentageValue) {
        attr("x2", value.toString())
    }

    fun y2(value: Number) {
        attr("y2", value.toString())
    }

    fun y2(value: CSSLengthOrPercentageValue) {
        attr("y2", value.toString())
    }
}

@Composable
fun ElementScope<SVGDefsElement>.LinearGradient(
    id: SvgId,
    attrs: (SVGLinearGradientAttrsScope.() -> Unit)? = null,
    content: ContentBuilder<SVGLinearGradientElement>
) {
    GenericTag("linearGradient", "http://www.w3.org/2000/svg", SVGLinearGradientAttrsScope(id, attrs), content)
}

class SVGRadialGradientAttrsScope private constructor(id: SvgId, attrs: AttrsScope<SVGRadialGradientElement>) :
    SVGGradientAttrsScope<SVGRadialGradientElement>(id, attrs) {

    companion object {
        operator fun invoke(
            id: SvgId,
            attrs: (SVGRadialGradientAttrsScope.() -> Unit)?
        ): AttrBuilderContext<SVGRadialGradientElement> {
            return {
                if (attrs != null) {
                    SVGRadialGradientAttrsScope(id, this).attrs()
                } else {
                    id(id.toString())
                }
            }
        }
    }

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

@Composable
fun ElementScope<SVGDefsElement>.RadialGradient(
    id: SvgId,
    attrs: (SVGRadialGradientAttrsScope.() -> Unit)? = null,
    content: ContentBuilder<SVGRadialGradientElement>
) {
    GenericTag("radialGradient", "http://www.w3.org/2000/svg", SVGRadialGradientAttrsScope(id, attrs), content)
}

enum class SVGStopColorType {
    CurrentColor;

    override fun toString() = this.toSvgValue()
}

class SVGStopAttrsScope private constructor(attrs: AttrsScope<SVGStopElement>) :
    SVGElementAttrsScope<SVGStopElement>(attrs) {
    companion object {
        operator fun invoke(attrs: SVGStopAttrsScope.() -> Unit): AttrBuilderContext<SVGStopElement> {
            return { SVGStopAttrsScope(this).attrs() }
        }
    }

    fun offset(value: CSSPercentageValue) {
        attr("offset", value.toString())
    }

    fun stopColor(value: CSSColorValue) {
        attr("stop-color", value.toString())
    }

    fun stopColor(value: SVGStopColorType) {
        attr("stop-color", value.toString())
    }

    fun stopOpacity(value: Number) {
        attr("stop-opacity", value.toString())
    }
}

// See: https://developer.mozilla.org/en-US/docs/Web/SVG/Element/stop
@Composable
fun ElementScope<SVGGradientElement>.Stop(attrs: SVGStopAttrsScope.() -> Unit) {
    GenericTag("stop", "http://www.w3.org/2000/svg", SVGStopAttrsScope(attrs))
}

// A convenience version for one-liner stop entries
@Composable
fun ElementScope<SVGGradientElement>.Stop(
    offset: CSSPercentageValue? = null,
    stopColor: CSSColorValue? = null,
    stopOpacity: Number? = null
) {
    Stop {
        offset?.let { offset(it) }
        stopColor?.let { stopColor(it) }
        stopOpacity?.let { stopOpacity(it) }
    }
}

enum class SVGPatternUnits {
    UserSpaceOnUse,
    ObjectBoundingBox;

    override fun toString() = this.toSvgValue()
}

class SVGPatternAttrsScope private constructor(id: SvgId, attrs: AttrsScope<SVGPatternElement>) :
    SVGContainerElementAttrsScope<SVGPatternElement>(attrs.id(id.toString())),
    SvgCoordinateAttrs<SVGPatternElement>, SvgLengthAttrs<SVGPatternElement>, SvgViewBoxAttrs<SVGPatternElement> {

    fun patternContentUnits(value: SVGPatternUnits) {
        attr("patternContentUnits", value.toString())
    }

    fun patternUnits(value: SVGPatternUnits) {
        attr("patternUnits", value.toString())
    }

    fun preserveAspectRatio(value: SVGAspectRatioAlignment) {
        attr("preserveAspectRatio", value.toString())
    }

    companion object {
        operator fun invoke(
            id: SvgId,
            attrs: (SVGPatternAttrsScope.() -> Unit)?
        ): AttrBuilderContext<SVGPatternElement> {
            return {
                if (attrs != null) {
                    SVGPatternAttrsScope(id, this).attrs()
                } else {
                    id(id.toString())
                }
            }
        }
    }
}


// https://developer.mozilla.org/en-US/docs/Web/SVG/Element/pattern
@Composable
fun ElementScope<SVGDefsElement>.Pattern(
    id: SvgId,
    attrs: (SVGPatternAttrsScope.() -> Unit)? = null,
    content: ContentBuilder<SVGPatternElement>
) {
    GenericTag("pattern", "http://www.w3.org/2000/svg", SVGPatternAttrsScope(id, attrs), content)
}

class SVGSymbolAttrsScope private constructor(id: SvgId, attrs: AttrsScope<SVGSymbolElement>) :
    SVGContainerElementAttrsScope<SVGSymbolElement>(attrs.id(id.toString())),
    SvgCoordinateAttrs<SVGSymbolElement>, SvgLengthAttrs<SVGSymbolElement>, SvgViewBoxAttrs<SVGSymbolElement> {

    companion object {
        operator fun invoke(
            id: SvgId,
            attrs: (SVGSymbolAttrsScope.() -> Unit)?
        ): AttrBuilderContext<SVGSymbolElement> {
            return {
                if (attrs != null) {
                    SVGSymbolAttrsScope(id, this).attrs()
                } else {
                    id(id.toString())
                }
            }
        }
    }
}


// https://developer.mozilla.org/en-US/docs/Web/SVG/Element/pattern
@Composable
fun ElementScope<SVGElement>.Symbol(
    id: SvgId,
    attrs: (SVGSymbolAttrsScope.() -> Unit)? = null,
    content: ContentBuilder<SVGSymbolElement>
) {
    GenericTag("symbol", "http://www.w3.org/2000/svg", SVGSymbolAttrsScope(id, attrs), content)
}

class SVGUseAttrsScope private constructor(href: String?, attrs: AttrsScope<SVGUseElement>) :
    SvgCoordinateAttrs<SVGUseElement>, SvgLengthAttrs<SVGUseElement>,
    SVGGraphicalElementAttrsScope<SVGUseElement>(
        if (href != null) {
            attrs.attr("href", href.prefixWithHash())
        } else {
            attrs
        }
    ) {

    companion object {
        private fun String.prefixWithHash() = if (startsWith("#")) this else "#$this"

        operator fun invoke(
            href: String?,
            attrs: (SVGUseAttrsScope.() -> Unit)
        ): AttrBuilderContext<SVGUseElement> {
            return { SVGUseAttrsScope(href, this).attrs() }
        }
    }
}


// https://developer.mozilla.org/en-US/docs/Web/SVG/Element/pattern
@Composable
fun ElementScope<SVGElement>.Use(
    href: String? = null,
    attrs: (SVGUseAttrsScope.() -> Unit),
) {
    GenericTag("use", "http://www.w3.org/2000/svg", SVGUseAttrsScope(href, attrs))
}

// endregion

// region SVG graphical elements

class SVGCircleAttrsScope private constructor(attrs: AttrsScope<SVGCircleElement>) :
    SVGGraphicalElementAttrsScope<SVGCircleElement>(attrs), SvgCenterCoordinateAttrs<SVGCircleElement> {

    companion object {
        operator fun invoke(attrs: SVGCircleAttrsScope.() -> Unit): AttrBuilderContext<SVGCircleElement> {
            return { SVGCircleAttrsScope(this).attrs() }
        }
    }

    fun r(value: Number) {
        attr("r", value.toString())
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
    GenericTag("circle", "http://www.w3.org/2000/svg", SVGCircleAttrsScope(attrs))
}

class SVGEllipseAttrsScope private constructor(attrs: AttrsScope<SVGEllipseElement>) :
    SVGGraphicalElementAttrsScope<SVGEllipseElement>(attrs), SvgCenterCoordinateAttrs<SVGEllipseElement> {

    companion object {
        operator fun invoke(attrs: SVGEllipseAttrsScope.() -> Unit): AttrBuilderContext<SVGEllipseElement> {
            return { SVGEllipseAttrsScope(this).attrs() }
        }
    }

    fun rx(value: Number) {
        attr("rx", value.toString())
    }

    fun ry(value: Number) {
        attr("ry", value.toString())
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
fun ElementScope<SVGElement>.Ellipse(attrs: SVGEllipseAttrsScope.() -> Unit) {
    GenericTag("ellipse", "http://www.w3.org/2000/svg", SVGEllipseAttrsScope(attrs))
}

class SVGGroupAttrsScope private constructor(attrs: AttrsScope<SVGGElement>) :
    SVGContainerElementAttrsScope<SVGGElement>(attrs) {

    companion object {
        operator fun invoke(attrs: SVGGroupAttrsScope.() -> Unit): AttrBuilderContext<SVGGElement> {
            return { SVGGroupAttrsScope(this).attrs() }
        }
    }
}


// https://developer.mozilla.org/en-US/docs/Web/SVG/Element/g
@Composable
fun ElementScope<SVGElement>.Group(
    attrs: (SVGGroupAttrsScope.() -> Unit)? = null,
    content: ContentBuilder<SVGGElement>
) {
    GenericTag("g", "http://www.w3.org/2000/svg", attrs?.let { SVGGroupAttrsScope(it) }, content)
}

class SVGImageAttrsScope private constructor(attrs: AttrsScope<SVGImageElement>) :
    SVGGraphicalElementAttrsScope<SVGImageElement>(attrs), SvgCoordinateAttrs<SVGImageElement>,
    SvgLengthAttrs<SVGImageElement>, SvgPreserveAspectRatioAttrs<SVGImageElement>, SvgCrossOriginAttrs<SVGImageElement> {

    companion object {
        operator fun invoke(attrs: SVGImageAttrsScope.() -> Unit): AttrBuilderContext<SVGImageElement> {
            return { SVGImageAttrsScope(this).attrs() }
        }
    }

    fun href(value: String) {
        attr("href", value)
    }
}

/**
 * Type-safe API for creating an [SVGImageElement].
 *
 * For example, to create a Image from (3, 12) to (21, 12):
 *
 * ```
 * Svg {
 *  Image {
 *      href("image.png")
 *      x(10.percent)
 *      y(10.percent)
 *      width(80.percent)
 *      height(80.percent)
 *  }
 * }
 * ```
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/SVG/Element/image">SVG Element Image (Mozilla Docs)</a>
 */
@Composable
fun ElementScope<SVGElement>.Image(attrs: SVGImageAttrsScope.() -> Unit) {
    GenericTag("image", "http://www.w3.org/2000/svg", SVGImageAttrsScope(attrs))
}


class SVGLineAttrsScope private constructor(attrs: AttrsScope<SVGLineElement>) :
    SVGGraphicalElementAttrsScope<SVGLineElement>(attrs) {

    companion object {
        operator fun invoke(attrs: SVGLineAttrsScope.() -> Unit): AttrBuilderContext<SVGLineElement> {
            return { SVGLineAttrsScope(this).attrs() }
        }
    }

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
    GenericTag("line", "http://www.w3.org/2000/svg", SVGLineAttrsScope(attrs))
}

@HtmlAttrMarker // scope for defining SVG path "d" attribute
class SVGPathDataScope internal constructor() {
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


class SVGPathAttrsScope private constructor(attrs: AttrsScope<SVGPathElement>) :
    SVGGraphicalElementAttrsScope<SVGPathElement>(attrs) {

    companion object {
        operator fun invoke(attrs: SVGPathAttrsScope.() -> Unit): AttrBuilderContext<SVGPathElement> {
            return { SVGPathAttrsScope(this).attrs() }
        }
    }

    /**
     * Set path data using a rich, type-safe DSL.
     */
    fun d(pathDataScope: SVGPathDataScope.() -> Unit) {
        attr("d", SVGPathDataScope().apply(pathDataScope).pathCommands.joinToString(" "))
    }

    /**
     * A way to set the path data directly.
     *
     * You are highly encouraged to use the other `d { ... }` DSL builder method instead, as it is far more readable.
     * However, in practice, users might get a crazy long path string from a designer or from a tool like Figma, so we
     * begrudgingly allow setting this directly.
     */
    fun d(value: String) {
        attr("d", value)
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
    GenericTag("path", "http://www.w3.org/2000/svg", SVGPathAttrsScope(attrs))
}


class SVGPolygonAttrsScope private constructor(attrs: AttrsScope<SVGPolygonElement>) :
    SVGGraphicalElementAttrsScope<SVGPolygonElement>(attrs), SvgPointsAttrs<SVGPolygonElement> {

    companion object {
        operator fun invoke(attrs: SVGPolygonAttrsScope.() -> Unit): AttrBuilderContext<SVGPolygonElement> {
            return { SVGPolygonAttrsScope(this).attrs() }
        }
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
    GenericTag("polygon", "http://www.w3.org/2000/svg", SVGPolygonAttrsScope(attrs))
}


class SVGPolylineAttrsScope private constructor(attrs: AttrsScope<SVGPolylineElement>) :
    SVGGraphicalElementAttrsScope<SVGPolylineElement>(attrs), SvgPointsAttrs<SVGPolylineElement> {

    companion object {
        operator fun invoke(attrs: SVGPolylineAttrsScope.() -> Unit): AttrBuilderContext<SVGPolylineElement> {
            return { SVGPolylineAttrsScope(this).attrs() }
        }
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
    GenericTag("polyline", "http://www.w3.org/2000/svg", SVGPolylineAttrsScope(attrs))
}


class SVGRectAttrsScope private constructor(attrs: AttrsScope<SVGRectElement>) :
    SVGGraphicalElementAttrsScope<SVGRectElement>(attrs), SvgCoordinateAttrs<SVGRectElement>, SvgLengthAttrs<SVGRectElement> {

    companion object {
        operator fun invoke(attrs: SVGRectAttrsScope.() -> Unit): AttrBuilderContext<SVGRectElement> {
            return { SVGRectAttrsScope(this).attrs() }
        }
    }

    fun rx(value: Number) {
        attr("rx", value.toString())
    }

    fun ry(value: Number) {
        attr("ry", value.toString())
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
fun ElementScope<SVGElement>.Rect(attrs: SVGRectAttrsScope.() -> Unit) {
    GenericTag("rect", "http://www.w3.org/2000/svg", SVGRectAttrsScope(attrs))
}


enum class SVGTextLengthAdjust {
    Spacing,
    SpacingAndGlyphs;

    override fun toString() = this.toSvgValue()
}

class SVGTextAttrsScope private constructor(attrs: AttrsScope<SVGTextElement>) :
    SVGGraphicalElementAttrsScope<SVGTextElement>(attrs), SvgCoordinateAttrs<SVGTextElement> {

    companion object {
        operator fun invoke(attrs: SVGTextAttrsScope.() -> Unit): AttrBuilderContext<SVGTextElement> {
            return { SVGTextAttrsScope(this).attrs() }
        }
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

    fun lengthAdjust(lengthAdjust: SVGTextLengthAdjust) {
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
    GenericTag("text", "http://www.w3.org/2000/svg", SVGTextAttrsScope(attrs)) {
        Text(text)
    }
}

// endregion
