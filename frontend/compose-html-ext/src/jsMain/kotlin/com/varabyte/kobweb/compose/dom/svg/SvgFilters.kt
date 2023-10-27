// SVGFilterElement scope is useful to ensure filters are only used under filter elements
@file:Suppress("UnusedReceiverParameter")

package com.varabyte.kobweb.compose.dom.svg

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.dom.GenericTag
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.AttrBuilderContext
import org.jetbrains.compose.web.dom.ContentBuilder
import org.jetbrains.compose.web.dom.ElementScope
import org.w3c.dom.svg.SVGAnimatedEnumeration
import org.w3c.dom.svg.SVGAnimatedLength
import org.w3c.dom.svg.SVGAnimatedNumber
import org.w3c.dom.svg.SVGAnimatedNumberList
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

// https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute#filter_primitive_attributes
private interface FilterPrimitiveAttrs<T : SVGElement> : AttrsScope<T>, CoordinateAttrs<T>, LengthAttrs<T> {
    fun result(name: String) {
        attr("result", name)
    }
}

private interface CommonSvgAttrs<T : SVGElement> : PresentationAttrs<T>, FilterPrimitiveAttrs<T>

// Interface for filters that only take a single input
private interface FilterInput1Attrs<T : SVGElement> : AttrsScope<T> {
    /**
     * A convenience method which maps to `in` and has a naming convention consistent with `in2`.
     *
     * While `in1` is not an official attribute, it provides a way to avoid using `in` which is awkward to use in
     * Kotlin since `in` is an official keyword.
     */
    fun in1(input: SVGFEInput) {
        `in`(input)
    }

    fun `in`(input: SVGFEInput) {
        `in`(input.toString())
    }

    /**
     * A convenience method which maps to `in` and has a naming convention consistent with `in2`.
     *
     * While `in1` is not an official attribute, it provides a way to avoid using `in` which is awkward to use in
     * Kotlin since `in` is an official keyword.
     *
     * @param resultName The name of the result of a previous filter.
     * @see [FilterPrimitiveAttrs.result]
     */
    fun in1(resultName: String) {
        `in`(resultName)
    }

    /**
     * @param resultName The name of the result of a previous filter.
     * @see [FilterPrimitiveAttrs.result]
     */
    fun `in`(resultName: String) {
        attr("in", resultName)
    }
}

// Interface for filters that take two inputs
private interface FilterInput2Attrs<T : SVGElement> : FilterInput1Attrs<T> {
    fun in2(input: SVGFEInput) {
        in2(input.toString())
    }

    /**
     * Input is passed the result of a previous filter.
     *
     * @see [FilterPrimitiveAttrs.result]
     */
    fun in2(resultName: String) {
        attr("in2", resultName)
    }
}

private interface OffsetAttrs<T : SVGElement> : AttrsScope<T> {
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
}

private interface StdDeviationAttrs<T : SVGElement> : AttrsScope<T> {
    fun stdDeviation(value: Number) {
        attr("stdDeviation", value.toString())
    }

    fun stdDeviation(x: Number, y: Number) {
        attr("stdDeviation", "$x $y")
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

    // SVGUnitTypes.SVG_UNIT_TYPE_...
    open val filterUnits: SVGAnimatedEnumeration

    // SVGUnitTypes.SVG_UNIT_TYPE_...
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

// region filter elements

/**
 * Exposes the JavaScript [SVGFEBlendElement](https://developer.mozilla.org/en/docs/Web/API/SVGFEBlendElement) to Kotlin
 */
abstract external class SVGFEBlendElement : SVGElement {
    companion object {
        val SVG_FEBLEND_MODE_NORMAL: Short
        val SVG_FEBLEND_MODE_MULTIPLY: Short
        val SVG_FEBLEND_MODE_SCREEN: Short
        val SVG_FEBLEND_MODE_DARKEN: Short
        val SVG_FEBLEND_MODE_LIGHTEN: Short
    }

    // SVGFEBlendElement.SVG_FEBLEND_MODE_...
    open val mode: SVGAnimatedEnumeration

    open val x: SVGAnimatedLength
    open val y: SVGAnimatedLength
    open val width: SVGAnimatedLength
    open val height: SVGAnimatedLength

    open val in1: SVGAnimatedString
    open val in2: SVGAnimatedString
    open val result: SVGAnimatedString
}

enum class SVGFEBlendMode {
    Normal,
    Multiply,
    Screen,
    Darken,
    Lighten;

    override fun toString() = this.toSvgValue()
}

class SVGFEBlendAttrsScope private constructor(attrs: AttrsScope<SVGFEBlendElement>) :
    SVGElementAttrsScope<SVGFEBlendElement>(attrs), CommonSvgAttrs<SVGFEBlendElement>,
    FilterInput2Attrs<SVGFEBlendElement> {

    fun mode(mode: SVGFEBlendMode) {
        attr("mode", mode.toString())
    }

    companion object {
        operator fun invoke(attrs: SVGFEBlendAttrsScope.() -> Unit): AttrBuilderContext<SVGFEBlendElement> {
            return { SVGFEBlendAttrsScope(this).attrs() }
        }
    }
}

@Composable
fun ElementScope<SVGFilterElement>.Blend(
    attrs: SVGFEBlendAttrsScope.() -> Unit,
) {
    GenericTag(
        "feBlend",
        "http://www.w3.org/2000/svg", SVGFEBlendAttrsScope(attrs)
    )
}

/**
 * Exposes the JavaScript [SVGFEColorMatrixElement](https://developer.mozilla.org/en/docs/Web/API/SVGFEColorMatrixElement) to Kotlin
 */
abstract external class SVGFEColorMatrixElement : SVGElement {
    companion object {
        val SVG_FECOLORMATRIX_TYPE_UNKNOWN: Short
        val SVG_FECOLORMATRIX_TYPE_MATRIX: Short
        val SVG_FECOLORMATRIX_TYPE_SATURATE: Short
        val SVG_FECOLORMATRIX_TYPE_HUEROTATE: Short
        val SVG_FECOLORMATRIX_TYPE_LUMINANCETOALPHA: Short
    }

    // SVGFEColorMatrixElement.SVG_FECOLORMATRIX_TYPE_...
    open val type: SVGAnimatedEnumeration

    open val x: SVGAnimatedLength
    open val y: SVGAnimatedLength
    open val width: SVGAnimatedLength
    open val height: SVGAnimatedLength

    open val values: SVGAnimatedNumberList

    open val in1: SVGAnimatedString
    open val result: SVGAnimatedString
}

enum class SVGFEColorMatrixType {
    Matrix,
    Saturate,
    HueRotate,
    LuminanceToAlpha;

    override fun toString() = this.toSvgValue()
}

class SVGFEColorMatrixAttrsScope private constructor(attrs: AttrsScope<SVGFEColorMatrixElement>) :
    SVGElementAttrsScope<SVGFEColorMatrixElement>(attrs), CommonSvgAttrs<SVGFEColorMatrixElement>,
    FilterInput1Attrs<SVGFEColorMatrixElement> {

    fun type(type: SVGFEColorMatrixType) {
        attr("type", type.toString())
    }

    /** Values to set when type is Matrix */
    fun values(
        r1: Number, r2: Number, r3: Number, r4: Number, r5: Number,
        g1: Number, g2: Number, g3: Number, g4: Number, g5: Number,
        b1: Number, b2: Number, b3: Number, b4: Number, b5: Number,
        a1: Number, a2: Number, a3: Number, a4: Number, a5: Number
    ) {
        attr("values", "$r1 $r2 $r3 $r4 $r5 $g1 $g2 $g3 $g4 $g5 $b1 $b2 $b3 $b4 $b5 $a1 $a2 $a3 $a4 $a5")
    }

    /** Value to set when type is Saturate */
    fun values(value: Number) {
        attr("values", value.toString())
    }

    /** Value to set when type is HueRotate */
    fun values(value: CSSAngleValue) {
        attr("values", value.toString())
    }

    companion object {
        operator fun invoke(attrs: SVGFEColorMatrixAttrsScope.() -> Unit): AttrBuilderContext<SVGFEColorMatrixElement> {
            return { SVGFEColorMatrixAttrsScope(this).attrs() }
        }
    }
}

@Composable
fun ElementScope<SVGFilterElement>.ColorMatrix(
    attrs: SVGFEColorMatrixAttrsScope.() -> Unit,
) {
    GenericTag(
        "feColorMatrix",
        "http://www.w3.org/2000/svg", SVGFEColorMatrixAttrsScope(attrs)
    )
}

/**
 * Exposes the JavaScript [SVGFECompositeElement](https://developer.mozilla.org/en/docs/Web/API/SVGFECompositeElement) to Kotlin
 */
abstract external class SVGFECompositeElement : SVGElement {
    companion object {
        val SVG_FECOMPOSITE_OPERATOR_UNKNOWN: Short
        val SVG_FECOMPOSITE_OPERATOR_OVER: Short
        val SVG_FECOMPOSITE_OPERATOR_IN: Short
        val SVG_FECOMPOSITE_OPERATOR_OUT: Short
        val SVG_FECOMPOSITE_OPERATOR_ATOP: Short
        val SVG_FECOMPOSITE_OPERATOR_XOR: Short
        val SVG_FECOMPOSITE_OPERATOR_ARITHMETIC: Short
    }

    // SVGFECompositeElement.SVG_FECOMPOSITE_OPERATOR_...
    open val type: SVGAnimatedEnumeration

    open val x: SVGAnimatedLength
    open val y: SVGAnimatedLength
    open val width: SVGAnimatedLength
    open val height: SVGAnimatedLength

    open val values: SVGAnimatedNumberList

    open val in1: SVGAnimatedString
    open val in2: SVGAnimatedString
    open val result: SVGAnimatedString
}

enum class SVGFECompositeOperator {
    Over,
    In,
    Out,
    Atop,
    Xor,
    Arithmetic;

    override fun toString() = this.toSvgValue()
}

class SVGFECompositeAttrsScope private constructor(attrs: AttrsScope<SVGFECompositeElement>) :
    SVGElementAttrsScope<SVGFECompositeElement>(attrs), CommonSvgAttrs<SVGFECompositeElement>,
    FilterInput2Attrs<SVGFECompositeElement> {

    fun operator(value: SVGFECompositeOperator) {
        attr("operator", value.toString())
    }

    /** k1 value, useful for arithmetic operator */
    fun k1(value: Number) {
        attr("k1", value.toString())
    }

    /** k2 value, useful for arithmetic operator */
    fun k2(value: Number) {
        attr("k2", value.toString())
    }

    /** k3 value, useful for arithmetic operator */
    fun k3(value: Number) {
        attr("k3", value.toString())
    }

    /** k4 value, useful for arithmetic operator */
    fun k4(value: Number) {
        attr("k4", value.toString())
    }

    companion object {
        operator fun invoke(attrs: SVGFECompositeAttrsScope.() -> Unit): AttrBuilderContext<SVGFECompositeElement> {
            return { SVGFECompositeAttrsScope(this).attrs() }
        }
    }
}

/** A convenience function to set all k values at once */
fun SVGFECompositeAttrsScope.values(k1: Number, k2: Number, k3: Number, k4: Number) {
    attr("k1", k1.toString())
    attr("k2", k2.toString())
    attr("k3", k3.toString())
    attr("k4", k4.toString())
}

@Composable
fun ElementScope<SVGFilterElement>.Composite(
    attrs: SVGFECompositeAttrsScope.() -> Unit,
) {
    GenericTag(
        "feComposite",
        "http://www.w3.org/2000/svg", SVGFECompositeAttrsScope(attrs)
    )
}

/**
 * Exposes the JavaScript [SVGFEConvolveMatrixElement](https://developer.mozilla.org/en/docs/Web/API/SVGFEConvolveMatrixElement) to Kotlin
 */
abstract external class SVGFEConvolveMatrixElement : SVGElement {
    companion object {
        val SVG_EDGEMODE_UNKNOWN: Short
        val SVG_EDGEMODE_DUPLICATE: Short
        val SVG_EDGEMODE_WRAP: Short
        val SVG_EDGEMODE_NONE: Short
    }
}

/**
 * Exposes the JavaScript [SVGFEDropShadowElement](https://developer.mozilla.org/en/docs/Web/API/SVGFEDropShadowElement) to Kotlin
 */
abstract external class SVGFEDropShadowElement : SVGElement {
    open val x: SVGAnimatedLength
    open val y: SVGAnimatedLength
    open val dx: SVGAnimatedLength
    open val dy: SVGAnimatedLength
    open val width: SVGAnimatedLength
    open val height: SVGAnimatedLength

    open val stdDeviationX: SVGAnimatedNumber
    open val stdDeviationY: SVGAnimatedNumber

    open val in1: SVGAnimatedString
    open val result: SVGAnimatedString

    fun setStdDeviation(stdDeviationX: Float, stdDeviationY: Float)
}

class SVGFEDropShadowAttrsScope private constructor(attrs: AttrsScope<SVGFEDropShadowElement>) :
    SVGElementAttrsScope<SVGFEDropShadowElement>(attrs), CommonSvgAttrs<SVGFEDropShadowElement>,
    OffsetAttrs<SVGFEDropShadowElement>, StdDeviationAttrs<SVGFEDropShadowElement> {

    companion object {
        operator fun invoke(attrs: SVGFEDropShadowAttrsScope.() -> Unit): AttrBuilderContext<SVGFEDropShadowElement> {
            return { SVGFEDropShadowAttrsScope(this).attrs() }
        }
    }
}

@Composable
fun ElementScope<SVGFilterElement>.DropShadow(
    attrs: SVGFEDropShadowAttrsScope.() -> Unit,
) {
    GenericTag(
        "feDropShadow",
        "http://www.w3.org/2000/svg", SVGFEDropShadowAttrsScope(attrs)
    )
}

/**
 * Exposes the JavaScript [SVGFEFloodElement](https://developer.mozilla.org/en/docs/Web/API/SVGFEFloodElement) to Kotlin
 */
abstract external class SVGFEFloodElement : SVGElement {
    open val x: SVGAnimatedLength
    open val y: SVGAnimatedLength
    open val width: SVGAnimatedLength
    open val height: SVGAnimatedLength

    open val result: SVGAnimatedString
}

class SVGFEFloodAttrsScope private constructor(attrs: AttrsScope<SVGFEFloodElement>) :
    SVGElementAttrsScope<SVGFEFloodElement>(attrs), CommonSvgAttrs<SVGFEFloodElement> {

    companion object {
        operator fun invoke(attrs: SVGFEFloodAttrsScope.() -> Unit): AttrBuilderContext<SVGFEFloodElement> {
            return { SVGFEFloodAttrsScope(this).attrs() }
        }
    }
}

@Composable
fun ElementScope<SVGFilterElement>.Flood(
    attrs: SVGFEFloodAttrsScope.() -> Unit,
) {
    GenericTag(
        "feFlood",
        "http://www.w3.org/2000/svg", SVGFEFloodAttrsScope(attrs)
    )
}

/**
 * Exposes the JavaScript [SVGFEGaussianBlurElement](https://developer.mozilla.org/en/docs/Web/API/SVGFEGaussianBlurElement) to Kotlin
 */
abstract external class SVGFEGaussianBlurElement : SVGElement {
    companion object {} // Empty companion object declaration necessary so we can extend it

    open val x: SVGAnimatedLength
    open val y: SVGAnimatedLength
    open val width: SVGAnimatedLength
    open val height: SVGAnimatedLength

    open val stdDeviationX: SVGAnimatedNumber
    open val stdDeviationY: SVGAnimatedNumber

    // SVGFEGaussianBlurElement.SVG_EDGEMODE_...
    open val edgeMode: SVGAnimatedEnumeration

    open val in1: SVGAnimatedString
    open val result: SVGAnimatedString

    fun setStdDeviation(stdDeviationX: Float, stdDeviationY: Float)
}

// EDGEMODE constants are declared on SVGFEConvolveMatrixElement but also relevant to SVGFEGaussianBlurElement
val SVGFEGaussianBlurElement.Companion.SVG_EDGEMODE_UNKNOWN get() = SVGFEConvolveMatrixElement.SVG_EDGEMODE_UNKNOWN
val SVGFEGaussianBlurElement.Companion.SVG_EDGEMODE_DUPLICATE get() = SVGFEConvolveMatrixElement.SVG_EDGEMODE_DUPLICATE
val SVGFEGaussianBlurElement.Companion.SVG_EDGEMODE_WRAP get() = SVGFEConvolveMatrixElement.SVG_EDGEMODE_WRAP
val SVGFEGaussianBlurElement.Companion.SVG_EDGEMODE_NONE get() = SVGFEConvolveMatrixElement.SVG_EDGEMODE_NONE

class SVGFEGaussianBlurAttrsScope private constructor(attrs: AttrsScope<SVGFEGaussianBlurElement>) :
    SVGElementAttrsScope<SVGFEGaussianBlurElement>(attrs), CommonSvgAttrs<SVGFEGaussianBlurElement>,
    FilterInput1Attrs<SVGFEGaussianBlurElement>, StdDeviationAttrs<SVGFEGaussianBlurElement> {

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
    attrs: SVGFEGaussianBlurAttrsScope.() -> Unit
) {
    GenericTag(
        "feGaussianBlur",
        "http://www.w3.org/2000/svg", SVGFEGaussianBlurAttrsScope(attrs)
    )
}

/**
 * Exposes the JavaScript [SVGFEMergeElement](https://developer.mozilla.org/en/docs/Web/API/SVGFEMergeElement) to Kotlin
 */
abstract external class SVGFEMergeElement : SVGElement {
    open val x: SVGAnimatedLength
    open val y: SVGAnimatedLength
    open val width: SVGAnimatedLength
    open val height: SVGAnimatedLength

    open val result: SVGAnimatedString
}

class SVGFEMergeAttrsScope private constructor(attrs: AttrsScope<SVGFEMergeElement>) :
    SVGElementAttrsScope<SVGFEMergeElement>(attrs), CommonSvgAttrs<SVGFEMergeElement> {

    companion object {
        operator fun invoke(attrs: SVGFEMergeAttrsScope.() -> Unit): AttrBuilderContext<SVGFEMergeElement> {
            return { SVGFEMergeAttrsScope(this).attrs() }
        }
    }
}

@Composable
fun ElementScope<SVGFilterElement>.Merge(
    attrs: (SVGFEMergeAttrsScope.() -> Unit)? = null,
    content: ContentBuilder<SVGFEMergeElement>
) {
    GenericTag(
        "feMerge",
        "http://www.w3.org/2000/svg", attrs?.let { SVGFEMergeAttrsScope(it) }, content
    )
}

/**
 * Exposes the JavaScript [SVGFEMergeNodeElement](https://developer.mozilla.org/en/docs/Web/API/SVGFEMergeNodeElement) to Kotlin
 */
abstract external class SVGFEMergeNodeElement : SVGElement {
    open val in1: SVGAnimatedString
}

class SVGFEMergeNodeAttrsScope private constructor(attrs: AttrsScope<SVGFEMergeNodeElement>) :
    SVGElementAttrsScope<SVGFEMergeNodeElement>(attrs), FilterInput1Attrs<SVGFEMergeNodeElement> {

    companion object {
        operator fun invoke(attrs: SVGFEMergeNodeAttrsScope.() -> Unit): AttrBuilderContext<SVGFEMergeNodeElement> {
            return { SVGFEMergeNodeAttrsScope(this).attrs() }
        }
    }
}

@Composable
fun ElementScope<SVGFEMergeElement>.MergeNode(attrs: SVGFEMergeNodeAttrsScope.() -> Unit) {
    GenericTag(
        "feMergeNode",
        "http://www.w3.org/2000/svg", SVGFEMergeNodeAttrsScope(attrs)
    )
}

/**
 * Exposes the JavaScript [SVGFEMorphologyElement](https://developer.mozilla.org/en/docs/Web/API/SVGFEMorphologyElement) to Kotlin
 */
abstract external class SVGFEMorphologyElement : SVGElement {
    companion object {
        val SVG_MORPHOLOGY_OPERATOR_UNKNOWN: Short
        val SVG_MORPHOLOGY_OPERATOR_ERODE: Short
        val SVG_MORPHOLOGY_OPERATOR_DILATE: Short
    }

    // SVGFEMorphologyElement.SVG_MORPHOLOGY_OPERATOR_...
    open val operator: SVGAnimatedEnumeration

    open val x: SVGAnimatedLength
    open val y: SVGAnimatedLength
    open val radiusX: SVGAnimatedNumber
    open val radiusY: SVGAnimatedNumber
    open val width: SVGAnimatedLength
    open val height: SVGAnimatedLength

    open val in1: SVGAnimatedString
    open val in2: SVGAnimatedString
    open val result: SVGAnimatedString
}

enum class SVGFEMorphologyOperator {
    Erode,
    Dilate;

    override fun toString() = this.toSvgValue()
}

class SVGFEMorphologyAttrsScope private constructor(attrs: AttrsScope<SVGFEMorphologyElement>) :
    SVGElementAttrsScope<SVGFEMorphologyElement>(attrs), CommonSvgAttrs<SVGFEMorphologyElement>,
    FilterInput1Attrs<SVGFEMorphologyElement> {

    fun operator(value: SVGFEMorphologyOperator) {
        attr("operator", value.toString())
    }

    fun radius(value: Number) {
        attr("radius", value.toString())
    }

    fun radius(x: Number, y: Number) {
        attr("radius", "$x $y")
    }

    companion object {
        operator fun invoke(attrs: SVGFEMorphologyAttrsScope.() -> Unit): AttrBuilderContext<SVGFEMorphologyElement> {
            return { SVGFEMorphologyAttrsScope(this).attrs() }
        }
    }
}

@Composable
fun ElementScope<SVGFilterElement>.Morphology(
    attrs: SVGFEMorphologyAttrsScope.() -> Unit,
) {
    GenericTag(
        "feMorphology",
        "http://www.w3.org/2000/svg", SVGFEMorphologyAttrsScope(attrs)
    )
}

/**
 * Exposes the JavaScript [SVGFEOffsetElement](https://developer.mozilla.org/en/docs/Web/API/SVGFEOffsetElement) to Kotlin
 */
abstract external class SVGFEOffsetElement : SVGElement {
    open val dx: SVGAnimatedLength
    open val dy: SVGAnimatedLength

    open val x: SVGAnimatedLength
    open val y: SVGAnimatedLength
    open val width: SVGAnimatedLength
    open val height: SVGAnimatedLength

    open val in1: SVGAnimatedString
    open val result: SVGAnimatedString
}

class SVGFEOffsetAttrsScope private constructor(attrs: AttrsScope<SVGFEOffsetElement>) :
    SVGElementAttrsScope<SVGFEOffsetElement>(attrs), CommonSvgAttrs<SVGFEOffsetElement>,
    FilterInput1Attrs<SVGFEOffsetElement>, OffsetAttrs<SVGFEOffsetElement> {

    companion object {
        operator fun invoke(attrs: SVGFEOffsetAttrsScope.() -> Unit): AttrBuilderContext<SVGFEOffsetElement> {
            return { SVGFEOffsetAttrsScope(this).attrs() }
        }
    }
}

@Composable
fun ElementScope<SVGFilterElement>.Offset(
    attrs: SVGFEOffsetAttrsScope.() -> Unit
) {
    GenericTag(
        "feOffset",
        "http://www.w3.org/2000/svg", SVGFEOffsetAttrsScope(attrs)
    )
}

// end region
