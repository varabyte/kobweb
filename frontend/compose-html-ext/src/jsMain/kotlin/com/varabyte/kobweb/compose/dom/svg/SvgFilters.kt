// SVGFilterElement scope is useful to ensure filters are only used under filter elements
@file:Suppress("UnusedReceiverParameter")

package com.varabyte.kobweb.compose.dom.svg

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.dom.GenericTag
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.dom.AttrBuilderContext
import org.jetbrains.compose.web.dom.ContentBuilder
import org.jetbrains.compose.web.dom.ElementScope
import org.w3c.dom.svg.SVGAnimatedEnumeration
import org.w3c.dom.svg.SVGAnimatedLength
import org.w3c.dom.svg.SVGAnimatedNumber
import org.w3c.dom.svg.SVGAnimatedString
import org.w3c.dom.svg.SVGDefsElement
import org.w3c.dom.svg.SVGElement

enum class SVGFilterUnits {
    UserSpaceOnUse,
    ObjectBoundingBox;

    override fun toString() = this.toSvgValue()
}

enum class SVGPrimitiveUnits {
    UserSpaceOnUse,
    ObjectBoundingBox;

    override fun toString() = this.toSvgValue()
}

enum class SVGFEEdgeMode {
    Duplicate,
    Wrap,
    None;

    override fun toString() = this.toSvgValue()
}

enum class SVGFEInput {
    SourceGraphic,
    SourceAlpha,
    BackgroundImage,
    BackgroundAlpha,
    FillPaint,
    StrokePaint;

    override fun toString() = this.name
}

// Interface for filters that only take a single input
private interface FilterInput1Attrs<T : SVGElement> : AttrsScope<T> {
    fun in1(input: SVGFEInput) {
        `in`(input)
    }

    fun `in`(input: SVGFEInput) {
        `in`(input.toString())
    }

    fun in1(filterReference: String) {
        `in`(filterReference)
    }

    fun `in`(filterReference: String) {
        attr("in", filterReference)
    }
}

// Interface for filters that take two inputs
private interface FilterInput2Attrs<T : SVGElement> : FilterInput1Attrs<T> {
    fun in2(input: SVGFEInput) {
        in2(input.toString())
    }

    fun in2(filterReference: String) {
        attr("in2", filterReference)
    }
}

/**
 * Exposes the JavaScript [SVGFilterElement](https://developer.mozilla.org/en/docs/Web/API/SVGFilterElement) to Kotlin
 */
abstract external class SVGFilterElement : SVGElement {
    open val x: SVGAnimatedLength
    open val y: SVGAnimatedLength
    open val width: SVGAnimatedLength
    open val height: SVGAnimatedLength
    open val filterUnits: SVGAnimatedEnumeration
    open val primitiveUnits: SVGAnimatedEnumeration
}

class SVGFilterAttrsScope private constructor(id: SvgId, attrs: AttrsScope<SVGFilterElement>) :
    SVGElementAttrsScope<SVGFilterElement>(attrs.id(id.toString())), CoordinateAttrs<SVGFilterElement>,
    LengthAttrs<SVGFilterElement> {

    fun filterUnits(value: SVGFilterUnits) {
        attr("filterUnits", value.toString())
    }

    fun primitiveUnits(value: SVGPrimitiveUnits) {
        attr("primitiveUnits", value.toString())
    }

    companion object {
        operator fun invoke(id: SvgId, attrs: (SVGFilterAttrsScope.() -> Unit)?): AttrBuilderContext<SVGFilterElement> {
            return {
                if (attrs != null) {
                    SVGFilterAttrsScope(id, this).attrs()
                } else {
                    id(id.toString())
                }
            }
        }
    }
}

@Composable
fun ElementScope<SVGDefsElement>.Filter(
    id: SvgId,
    attrs: (SVGFilterAttrsScope.() -> Unit)? = null,
    content: ContentBuilder<SVGFilterElement>
) {
    GenericTag(
        "filter",
        "http://www.w3.org/2000/svg", SVGFilterAttrsScope(id, attrs), content
    )
}

/**
 * Exposes the JavaScript [SVGFEGaussianBlurElement](https://developer.mozilla.org/en/docs/Web/API/SVGFEGaussianBlurElement) to Kotlin
 */
abstract external class SVGFEGaussianBlurElement : SVGElement {
    open val x: SVGAnimatedLength
    open val y: SVGAnimatedLength
    open val width: SVGAnimatedLength
    open val height: SVGAnimatedLength

    open val stdDeviationX: SVGAnimatedNumber
    open val stdDeviationY: SVGAnimatedNumber

    open val edgeMode: SVGAnimatedEnumeration

    open val in1: SVGAnimatedString
    open val result: SVGAnimatedString

    fun setStdDeviation(stdDeviationX: Float, stdDeviationY: Float)
}

class SVGFEGaussianBlurAttrsScope private constructor(attrs: AttrsScope<SVGFEGaussianBlurElement>) :
    SVGElementAttrsScope<SVGFEGaussianBlurElement>(attrs), CoordinateAttrs<SVGFEGaussianBlurElement>,
    LengthAttrs<SVGFEGaussianBlurElement>, FilterInput1Attrs<SVGFEGaussianBlurElement> {

    fun stdDeviation(value: Number) {
        attr("stdDeviation", value.toString())
    }

    fun stdDeviation(x: Number, y: Number) {
        attr("stdDeviation", "$x $y")
    }

    fun edgeMode(edgeMode: SVGFEEdgeMode) {
        attr("edgeMode", edgeMode.toString())
    }

    companion object {
        operator fun invoke(attrs: SVGFEGaussianBlurAttrsScope.() -> Unit): AttrBuilderContext<SVGFEGaussianBlurElement> {
            return { SVGFEGaussianBlurAttrsScope(this).attrs() }
        }
    }
}

@Composable
fun ElementScope<SVGFilterElement>.GaussianBlur(
    attrs: (SVGFEGaussianBlurAttrsScope.() -> Unit)? = null,
) {
    GenericTag(
        "feGaussianBlur",
        "http://www.w3.org/2000/svg", attrs?.let { SVGFEGaussianBlurAttrsScope(it) }
    )
}
