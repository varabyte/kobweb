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
import org.w3c.dom.svg.SVGAnimatedBoolean
import org.w3c.dom.svg.SVGAnimatedEnumeration
import org.w3c.dom.svg.SVGAnimatedInteger
import org.w3c.dom.svg.SVGAnimatedLength
import org.w3c.dom.svg.SVGAnimatedNumber
import org.w3c.dom.svg.SVGAnimatedNumberList
import org.w3c.dom.svg.SVGAnimatedPreserveAspectRatio
import org.w3c.dom.svg.SVGAnimatedString
import org.w3c.dom.svg.SVGDefsElement
import org.w3c.dom.svg.SVGElement

enum class SVGFilterInput {
    SourceGraphic,
    SourceAlpha,
    BackgroundImage,
    BackgroundAlpha,
    FillPaint,
    StrokePaint;

    override fun toString() = this.name
}

// https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute#filter_primitive_attributes
interface SvgFilterPrimitiveAttrs<T : SVGElement> : AttrsScope<T>, SvgCoordinateAttrs<T>, SvgLengthAttrs<T> {
    fun result(name: String) {
        attr("result", name)
    }
}

// Interface for filters that only take a single input
interface SvgFilterInput1Attrs<T : SVGElement> : AttrsScope<T> {
    /**
     * A convenience method which maps to `in` and has a naming convention consistent with `in2`.
     *
     * While `in1` is not an official attribute, it provides a way to avoid using `in` which is awkward to use in
     * Kotlin since `in` is an official keyword.
     */
    fun in1(input: SVGFilterInput) {
        `in`(input)
    }

    fun `in`(input: SVGFilterInput) {
        `in`(input.toString())
    }

    /**
     * A convenience method which maps to `in` and has a naming convention consistent with `in2`.
     *
     * While `in1` is not an official attribute, it provides a way to avoid using `in` which is awkward to use in
     * Kotlin since `in` is an official keyword.
     *
     * @param resultName The name of the result of a previous filter.
     * @see [SvgFilterPrimitiveAttrs.result]
     */
    fun in1(resultName: String) {
        `in`(resultName)
    }

    /**
     * @param resultName The name of the result of a previous filter.
     * @see [SvgFilterPrimitiveAttrs.result]
     */
    fun `in`(resultName: String) {
        attr("in", resultName)
    }
}

// Interface for filters that take two inputs
interface SvgFilterInput2Attrs<T : SVGElement> : SvgFilterInput1Attrs<T> {
    fun in2(input: SVGFilterInput) {
        in2(input.toString())
    }

    /**
     * Input is passed the result of a previous filter.
     *
     * @see [SvgFilterPrimitiveAttrs.result]
     */
    fun in2(resultName: String) {
        attr("in2", resultName)
    }
}

interface SvgOffsetAttrs<T : SVGElement> : AttrsScope<T> {
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

interface SvgStdDeviationAttrs<T : SVGElement> : AttrsScope<T> {
    fun stdDeviation(value: Number) {
        attr("stdDeviation", value.toString())
    }

    fun stdDeviation(x: Number, y: Number) {
        attr("stdDeviation", "$x $y")
    }
}

abstract class SVGFilterElementAttrsScope<E : SVGElement> protected constructor(attrs: AttrsScope<E>) :
    SvgPresentationAttrs<E>, SvgFilterPrimitiveAttrs<E>, SVGElementAttrsScope<E>(attrs)

/**
 * Exposes the JavaScript [SVGFilterElement](https://developer.mozilla.org/en/docs/Web/API/SVGFilterElement) to Kotlin
 */
abstract external class SVGFilterElement : SVGElement {
    val x: SVGAnimatedLength
    val y: SVGAnimatedLength
    val width: SVGAnimatedLength
    val height: SVGAnimatedLength

    // SVGUnitTypes.SVG_UNIT_TYPE_...
    val filterUnits: SVGAnimatedEnumeration

    // SVGUnitTypes.SVG_UNIT_TYPE_...
    val primitiveUnits: SVGAnimatedEnumeration
}

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

class SVGFilterAttrsScope private constructor(id: SvgId, attrs: AttrsScope<SVGFilterElement>) :
    SVGElementAttrsScope<SVGFilterElement>(attrs.id(id.toString())), SvgCoordinateAttrs<SVGFilterElement>,
    SvgLengthAttrs<SVGFilterElement> {

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

private external interface SVGFECommon {
    val x: SVGAnimatedLength
    val y: SVGAnimatedLength
    val height: SVGAnimatedLength
    val width: SVGAnimatedLength

    val result: SVGAnimatedString
}

private external interface SVGFEInput1 {
    val in1: SVGAnimatedString
}

private external interface SVGFEInput2 : SVGFEInput1 {
    val in2: SVGAnimatedString
}

private external interface SVGFEStdDeviation {
    val stdDeviationX: SVGAnimatedNumber
    val stdDeviationY: SVGAnimatedNumber

    fun setStdDeviation(stdDeviationX: Float, stdDeviationY: Float)
}


/**
 * Exposes the JavaScript [SVGFEBlendElement](https://developer.mozilla.org/en/docs/Web/API/SVGFEBlendElement) to Kotlin
 */
abstract external class SVGFEBlendElement : SVGElement, SVGFECommon, SVGFEInput2 {
    companion object {
        val SVG_FEBLEND_MODE_NORMAL: Short
        val SVG_FEBLEND_MODE_MULTIPLY: Short
        val SVG_FEBLEND_MODE_SCREEN: Short
        val SVG_FEBLEND_MODE_DARKEN: Short
        val SVG_FEBLEND_MODE_LIGHTEN: Short
    }

    // SVGFEBlendElement.SVG_FEBLEND_MODE_...
    val mode: SVGAnimatedEnumeration
}

enum class SVGBlendMode {
    Normal,
    Multiply,
    Screen,
    Darken,
    Lighten;

    override fun toString() = this.toSvgValue()
}

class SVGFEBlendAttrsScope private constructor(attrs: AttrsScope<SVGFEBlendElement>) :
    SVGFilterElementAttrsScope<SVGFEBlendElement>(attrs), SvgFilterInput2Attrs<SVGFEBlendElement> {

    fun mode(mode: SVGBlendMode) {
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
abstract external class SVGFEColorMatrixElement : SVGElement, SVGFECommon, SVGFEInput1 {
    companion object {
        val SVG_FECOLORMATRIX_TYPE_UNKNOWN: Short
        val SVG_FECOLORMATRIX_TYPE_MATRIX: Short
        val SVG_FECOLORMATRIX_TYPE_SATURATE: Short
        val SVG_FECOLORMATRIX_TYPE_HUEROTATE: Short
        val SVG_FECOLORMATRIX_TYPE_LUMINANCETOALPHA: Short
    }

    // SVGFEColorMatrixElement.SVG_FECOLORMATRIX_TYPE_...
    val type: SVGAnimatedEnumeration
    val values: SVGAnimatedNumberList
}

enum class SVGColorMatrixType {
    Matrix,
    Saturate,
    HueRotate,
    LuminanceToAlpha;

    override fun toString() = this.toSvgValue()
}

class SVGFEColorMatrixAttrsScope private constructor(attrs: AttrsScope<SVGFEColorMatrixElement>) :
    SVGFilterElementAttrsScope<SVGFEColorMatrixElement>(attrs), SvgFilterInput1Attrs<SVGFEColorMatrixElement> {

    fun type(type: SVGColorMatrixType) {
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
abstract external class SVGFECompositeElement : SVGElement, SVGFECommon, SVGFEInput2 {
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
    val type: SVGAnimatedEnumeration
    val values: SVGAnimatedNumberList
}

enum class SVGCompositeOperator {
    Over,
    In,
    Out,
    Atop,
    Xor,
    Arithmetic;

    override fun toString() = this.toSvgValue()
}

class SVGFECompositeAttrsScope private constructor(attrs: AttrsScope<SVGFECompositeElement>) :
    SVGFilterElementAttrsScope<SVGFECompositeElement>(attrs),
    SvgFilterInput2Attrs<SVGFECompositeElement> {

    fun operator(value: SVGCompositeOperator) {
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
fun SVGFECompositeAttrsScope.kValues(k1: Number, k2: Number, k3: Number, k4: Number) {
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
abstract external class SVGFEConvolveMatrixElement : SVGElement, SVGFECommon, SVGFEInput1 {
    companion object {
        val SVG_EDGEMODE_UNKNOWN: Short
        val SVG_EDGEMODE_DUPLICATE: Short
        val SVG_EDGEMODE_WRAP: Short
        val SVG_EDGEMODE_NONE: Short
    }

    val bias: SVGAnimatedNumber
    val divisor: SVGAnimatedNumber

    // SVGConvolveMatrixElement.SVG_EDGEMODE_...
    val edgeMode: SVGAnimatedEnumeration

    val kernelMatrix: SVGAnimatedNumberList
    val orderX: SVGAnimatedInteger
    val orderY: SVGAnimatedInteger
    val preserveAlpha: SVGAnimatedBoolean
    val targetX: SVGAnimatedInteger
    val targetY: SVGAnimatedInteger
}

class SVGFEConvolveMatrixAttrsScope private constructor(attrs: AttrsScope<SVGFEConvolveMatrixElement>) :
    SVGFilterElementAttrsScope<SVGFEConvolveMatrixElement>(attrs), SvgFilterInput1Attrs<SVGFEConvolveMatrixElement> {

    fun order(value: Number) {
        attr("order", value.toString())
    }

    fun order(numColumns: Number, numRows: Number) {
        attr("order", "$numColumns $numRows")
    }

    /**
     * The numbers that make up the kernel matrix.
     *
     * The number of entries in the list must match the number of columns * the number of rows, or else this filter is
     * disabled.
     */
    fun kernelMatrix(vararg values: Number) {
        attr("kernelMatrix", values.joinToString(" "))
    }

    fun divisor(value: Number) {
        attr("divisor", value.toString())
    }

    fun bias(value: Number) {
        attr("bias", value.toString())
    }

    fun targetX(value: Int) {
        attr("targetX", value.toString())
    }

    fun targetY(value: Int) {
        attr("targetY", value.toString())
    }

    fun edgeMode(value: SVGEdgeMode) {
        attr("edgeMode", value.toString())
    }

    fun preserveAlpha(value: Boolean) {
        attr("preserveAlpha", value.toString())
    }

    companion object {
        operator fun invoke(attrs: SVGFEConvolveMatrixAttrsScope.() -> Unit): AttrBuilderContext<SVGFEConvolveMatrixElement> {
            return { SVGFEConvolveMatrixAttrsScope(this).attrs() }
        }
    }
}

@Composable
fun ElementScope<SVGFilterElement>.ConvolveMatrix(
    attrs: SVGFEConvolveMatrixAttrsScope.() -> Unit
) {
    GenericTag(
        "feConvolveMatrix",
        "http://www.w3.org/2000/svg", SVGFEConvolveMatrixAttrsScope(attrs)
    )
}

/**
 * Exposes the JavaScript [SVGFEDiffuseLightingElement](https://developer.mozilla.org/en/docs/Web/API/SVGFEDiffuseLightingElement) to Kotlin
 */
abstract external class SVGFEDiffuseLightingElement : SVGElement, SVGFECommon, SVGFEInput1 {
    val diffuseConstant: SVGAnimatedNumber
    val surfaceScale: SVGAnimatedNumber
}

class SVGFEDiffuseLightingAttrsScope private constructor(attrs: AttrsScope<SVGFEDiffuseLightingElement>) :
    SVGFilterElementAttrsScope<SVGFEDiffuseLightingElement>(attrs),
    SvgFilterInput1Attrs<SVGFEDiffuseLightingElement> {

    fun diffuseConstant(value: Number) {
        attr("diffuseConstant", value.toString())
    }

    fun surfaceScale(value: Number) {
        attr("surfaceScale", value.toString())
    }

    companion object {
        operator fun invoke(attrs: SVGFEDiffuseLightingAttrsScope.() -> Unit): AttrBuilderContext<SVGFEDiffuseLightingElement> {
            return { SVGFEDiffuseLightingAttrsScope(this).attrs() }
        }
    }
}

@Composable
fun ElementScope<SVGFilterElement>.DiffuseLighting(
    attrs: SVGFEDiffuseLightingAttrsScope.() -> Unit,
    content: ContentBuilder<SVGFEDiffuseLightingElement>
) {
    GenericTag(
        "feDiffuseLighting",
        "http://www.w3.org/2000/svg", SVGFEDiffuseLightingAttrsScope(attrs), content
    )
}

/**
 * Exposes the JavaScript [SVGFEDistantLightElement](https://developer.mozilla.org/en/docs/Web/API/SVGFEDistantLightElement) to Kotlin
 */
abstract external class SVGFEDistantLightElement : SVGElement {
    val azimuth: SVGAnimatedNumber
    val elevation: SVGAnimatedNumber
}

class SVGFEDistantLightAttrsScope private constructor(attrs: AttrsScope<SVGFEDistantLightElement>) :
    SVGElementAttrsScope<SVGFEDistantLightElement>(attrs) {

    fun azimuth(value: Number) {
        attr("azimuth", value.toString())
    }

    fun elevation(value: Number) {
        attr("elevation", value.toString())
    }

    companion object {
        operator fun invoke(attrs: SVGFEDistantLightAttrsScope.() -> Unit): AttrBuilderContext<SVGFEDistantLightElement> {
            return { SVGFEDistantLightAttrsScope(this).attrs() }
        }
    }
}

@Composable
private fun UnscopedDistantLight(
    attrs: SVGFEDistantLightAttrsScope.() -> Unit,
) {
    GenericTag(
        "feDistantLight",
        "http://www.w3.org/2000/svg", SVGFEDistantLightAttrsScope(attrs)
    )
}

@Composable
fun ElementScope<SVGFEDiffuseLightingElement>.DistantLight(attrs: SVGFEDistantLightAttrsScope.() -> Unit) {
    UnscopedDistantLight(attrs)
}

@Composable
fun ElementScope<SVGFESpecularLightingElement>.DistantLight(attrs: SVGFEDistantLightAttrsScope.() -> Unit) {
    UnscopedDistantLight(attrs)
}

/**
 * Exposes the JavaScript [SVGFEPointLightElement](https://developer.mozilla.org/en/docs/Web/API/SVGFEPointLightElement) to Kotlin
 */
abstract external class SVGFEPointLightElement : SVGElement {
    val x: SVGAnimatedNumber
    val y: SVGAnimatedNumber
    val z: SVGAnimatedNumber
}

class SVGFEPointLightAttrsScope private constructor(attrs: AttrsScope<SVGFEPointLightElement>) :
    SVGElementAttrsScope<SVGFEPointLightElement>(attrs) {

    fun x(value: Number) {
        attr("x", value.toString())
    }

    fun y(value: Number) {
        attr("y", value.toString())
    }

    fun z(value: Number) {
        attr("z", value.toString())
    }

    companion object {
        operator fun invoke(attrs: SVGFEPointLightAttrsScope.() -> Unit): AttrBuilderContext<SVGFEPointLightElement> {
            return { SVGFEPointLightAttrsScope(this).attrs() }
        }
    }
}

@Composable
private fun UnscopedPointLight(
    attrs: SVGFEPointLightAttrsScope.() -> Unit,
) {
    GenericTag(
        "fePointLight",
        "http://www.w3.org/2000/svg", SVGFEPointLightAttrsScope(attrs)
    )
}

@Composable
fun ElementScope<SVGFEDiffuseLightingElement>.PointLight(attrs: SVGFEPointLightAttrsScope.() -> Unit) {
    UnscopedPointLight(attrs)
}

@Composable
fun ElementScope<SVGFESpecularLightingElement>.PointLight(attrs: SVGFEPointLightAttrsScope.() -> Unit) {
    UnscopedPointLight(attrs)
}

/**
 * Exposes the JavaScript [SVGFESpotLightElement](https://developer.mozilla.org/en/docs/Web/API/SVGFESpotLightElement) to Kotlin
 */
abstract external class SVGFESpotLightElement : SVGElement {
    val x: SVGAnimatedNumber
    val y: SVGAnimatedNumber
    val z: SVGAnimatedNumber

    val pointsAtX: SVGAnimatedNumber
    val pointsAtY: SVGAnimatedNumber
    val pointsAtZ: SVGAnimatedNumber

    val specularExponent: SVGAnimatedNumber
    val limitingConeAngle: SVGAnimatedNumber
}

class SVGFESpotLightAttrsScope private constructor(attrs: AttrsScope<SVGFESpotLightElement>) :
    SVGElementAttrsScope<SVGFESpotLightElement>(attrs) {

    fun x(value: Number) {
        attr("x", value.toString())
    }

    fun y(value: Number) {
        attr("y", value.toString())
    }

    fun z(value: Number) {
        attr("z", value.toString())
    }

    fun pointsAtX(value: Number) {
        attr("pointsAtX", value.toString())
    }

    fun pointsAtY(value: Number) {
        attr("pointsAtY", value.toString())
    }

    fun pointsAtZ(value: Number) {
        attr("pointsAtZ", value.toString())
    }

    fun specularExponent(value: Number) {
        attr("specularExponent", value.toString())
    }

    fun limitingConeAngle(value: Number) {
        attr("limitingConeAngle", value.toString())
    }

    companion object {
        operator fun invoke(attrs: SVGFESpotLightAttrsScope.() -> Unit): AttrBuilderContext<SVGFESpotLightElement> {
            return { SVGFESpotLightAttrsScope(this).attrs() }
        }
    }
}

@Composable
private fun UnscopedSpotLight(
    attrs: SVGFESpotLightAttrsScope.() -> Unit,
) {
    GenericTag(
        "feSpotLight",
        "http://www.w3.org/2000/svg", SVGFESpotLightAttrsScope(attrs)
    )
}

@Composable
fun ElementScope<SVGFEDiffuseLightingElement>.SpotLight(attrs: SVGFESpotLightAttrsScope.() -> Unit) {
    UnscopedSpotLight(attrs)
}

@Composable
fun ElementScope<SVGFESpecularLightingElement>.SpotLight(attrs: SVGFESpotLightAttrsScope.() -> Unit) {
    UnscopedSpotLight(attrs)
}

/**
 * Exposes the JavaScript [SVGFETurbulenceElement](https://developer.mozilla.org/en/docs/Web/API/SVGFETurbulenceElement) to Kotlin
 */
abstract external class SVGFEDisplacementMapElement : SVGElement, SVGFECommon, SVGFEInput2 {
    companion object {
        val SVG_CHANNEL_UNKNOWN: Short
        val SVG_CHANNEL_R: Short
        val SVG_CHANNEL_G: Short
        val SVG_CHANNEL_B: Short
        val SVG_CHANNEL_A: Short
    }

    // SVGFEDisplacementMapElement.SVG_CHANNEL_...
    val xChannelSelector: SVGAnimatedEnumeration
    val yChannelSelector: SVGAnimatedEnumeration

    val scale: SVGAnimatedNumber
}

enum class SVGColorChannel {
    R, G, B, A;

    override fun toString() = this.name
}

class SVGFEDisplacementMapAttrsScope private constructor(attrs: AttrsScope<SVGFEDisplacementMapElement>) :
    SVGFilterElementAttrsScope<SVGFEDisplacementMapElement>(attrs),
    SvgFilterInput2Attrs<SVGFEDisplacementMapElement> {

    fun xChannelSelector(value: SVGColorChannel) {
        attr("xChannelSelector", value.toString())
    }

    fun yChannelSelector(value: SVGColorChannel) {
        attr("yChannelSelector", value.toString())
    }

    fun scale(value: Number) {
        attr("scale", value.toString())
    }

    companion object {
        operator fun invoke(attrs: SVGFEDisplacementMapAttrsScope.() -> Unit): AttrBuilderContext<SVGFEDisplacementMapElement> {
            return { SVGFEDisplacementMapAttrsScope(this).attrs() }
        }
    }
}

@Composable
fun ElementScope<SVGFilterElement>.DisplacementMap(
    attrs: SVGFEDisplacementMapAttrsScope.() -> Unit,
) {
    GenericTag(
        "feDisplacementMap",
        "http://www.w3.org/2000/svg", SVGFEDisplacementMapAttrsScope(attrs)
    )
}


/**
 * Exposes the JavaScript [SVGFEDropShadowElement](https://developer.mozilla.org/en/docs/Web/API/SVGFEDropShadowElement) to Kotlin
 */
abstract external class SVGFEDropShadowElement : SVGElement, SVGFECommon, SVGFEInput1, SVGFEStdDeviation {
    val dx: SVGAnimatedLength
    val dy: SVGAnimatedLength
}

class SVGFEDropShadowAttrsScope private constructor(attrs: AttrsScope<SVGFEDropShadowElement>) :
    SVGFilterElementAttrsScope<SVGFEDropShadowElement>(attrs),
    SvgOffsetAttrs<SVGFEDropShadowElement>, SvgStdDeviationAttrs<SVGFEDropShadowElement> {

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
abstract external class SVGFEFloodElement : SVGElement, SVGFECommon

class SVGFEFloodAttrsScope private constructor(attrs: AttrsScope<SVGFEFloodElement>) :
    SVGFilterElementAttrsScope<SVGFEFloodElement>(attrs) {

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
abstract external class SVGFEGaussianBlurElement : SVGElement, SVGFECommon, SVGFEInput1, SVGFEStdDeviation {
    companion object {} // Empty companion object declaration necessary so we can extend it

    // SVGFEGaussianBlurElement.SVG_EDGEMODE_...
    val edgeMode: SVGAnimatedEnumeration
}

// EDGEMODE constants are declared on SVGFEConvolveMatrixElement but also relevant to SVGFEGaussianBlurElement
val SVGFEGaussianBlurElement.Companion.SVG_EDGEMODE_UNKNOWN get() = SVGFEConvolveMatrixElement.SVG_EDGEMODE_UNKNOWN
val SVGFEGaussianBlurElement.Companion.SVG_EDGEMODE_DUPLICATE get() = SVGFEConvolveMatrixElement.SVG_EDGEMODE_DUPLICATE
val SVGFEGaussianBlurElement.Companion.SVG_EDGEMODE_WRAP get() = SVGFEConvolveMatrixElement.SVG_EDGEMODE_WRAP
val SVGFEGaussianBlurElement.Companion.SVG_EDGEMODE_NONE get() = SVGFEConvolveMatrixElement.SVG_EDGEMODE_NONE

enum class SVGEdgeMode {
    Duplicate,
    Wrap,
    None;

    override fun toString() = this.toSvgValue()
}

class SVGFEGaussianBlurAttrsScope private constructor(attrs: AttrsScope<SVGFEGaussianBlurElement>) :
    SVGFilterElementAttrsScope<SVGFEGaussianBlurElement>(attrs),
    SvgFilterInput1Attrs<SVGFEGaussianBlurElement>, SvgStdDeviationAttrs<SVGFEGaussianBlurElement> {

    fun edgeMode(edgeMode: SVGEdgeMode) {
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
 * Exposes the JavaScript [SVGFEImageElement](https://developer.mozilla.org/en/docs/Web/API/SVGFEImageElement) to Kotlin
 */
abstract external class SVGFEImageElement : SVGElement, SVGFECommon {
    val href: SVGAnimatedString

    val crossOrigin: SVGAnimatedString
    val preserveAspectRatio: SVGAnimatedPreserveAspectRatio
}

class SVGFEImageAttrsScope private constructor(attrs: AttrsScope<SVGFEImageElement>) :
    SVGFilterElementAttrsScope<SVGFEImageElement>(attrs), SvgPreserveAspectRatioAttrs<SVGFEImageElement> {

    fun href(value: String) {
        attr("href", value)
    }

    companion object {
        operator fun invoke(attrs: SVGFEImageAttrsScope.() -> Unit): AttrBuilderContext<SVGFEImageElement> {
            return { SVGFEImageAttrsScope(this).attrs() }
        }
    }
}

@Composable
fun ElementScope<SVGFilterElement>.Image(
    attrs: SVGFEImageAttrsScope.() -> Unit
) {
    GenericTag(
        "feImage",
        "http://www.w3.org/2000/svg", SVGFEImageAttrsScope(attrs)
    )
}

/**
 * Exposes the JavaScript [SVGFEMergeElement](https://developer.mozilla.org/en/docs/Web/API/SVGFEMergeElement) to Kotlin
 */
abstract external class SVGFEMergeElement : SVGElement, SVGFECommon

class SVGFEMergeAttrsScope private constructor(attrs: AttrsScope<SVGFEMergeElement>) :
    SVGFilterElementAttrsScope<SVGFEMergeElement>(attrs) {

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
abstract external class SVGFEMergeNodeElement : SVGElement, SVGFEInput1

class SVGFEMergeNodeAttrsScope private constructor(attrs: AttrsScope<SVGFEMergeNodeElement>) :
    SVGFilterElementAttrsScope<SVGFEMergeNodeElement>(attrs), SvgFilterInput1Attrs<SVGFEMergeNodeElement> {

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
 * Exposes the JavaScript [SVGFEComponentTransferElement](https://developer.mozilla.org/en/docs/Web/API/SVGFEComponentTransferElement) to Kotlin
 */
abstract external class SVGFEComponentTransferElement : SVGElement, SVGFECommon

class SVGFEComponentTransferAttrsScope private constructor(attrs: AttrsScope<SVGFEComponentTransferElement>) :
    SVGFilterElementAttrsScope<SVGFEComponentTransferElement>(attrs), SvgFilterInput1Attrs<SVGFEComponentTransferElement> {

    companion object {
        operator fun invoke(attrs: SVGFEComponentTransferAttrsScope.() -> Unit): AttrBuilderContext<SVGFEComponentTransferElement> {
            return { SVGFEComponentTransferAttrsScope(this).attrs() }
        }
    }
}

@Composable
fun ElementScope<SVGFilterElement>.ComponentTransfer(
    attrs: (SVGFEComponentTransferAttrsScope.() -> Unit)? = null,
    content: ContentBuilder<SVGFEComponentTransferElement>
) {
    GenericTag(
        "feComponentTransfer",
        "http://www.w3.org/2000/svg", attrs?.let { SVGFEComponentTransferAttrsScope(it) }, content
    )
}

/**
 * Exposes the JavaScript [SVGComponentTransferFunctionElement](https://developer.mozilla.org/en/docs/Web/API/SVGComponentTransferFunctionElement) to Kotlin
 */
abstract external class SVGComponentTransferFunctionElement : SVGElement {
    companion object {
        val SVG_FECOMPONENTTRANSFER_TYPE_UNKNOWN: Short
        val SVG_FECOMPONENTTRANSFER_TYPE_IDENTITY: Short
        val SVG_FECOMPONENTTRANSFER_TYPE_TABLE: Short
        val SVG_FECOMPONENTTRANSFER_TYPE_DISCRETE: Short
        val SVG_FECOMPONENTTRANSFER_TYPE_LINEAR: Short
        val SVG_FECOMPONENTTRANSFER_TYPE_GAMMA: Short
    }

    // SVGComponentTransferFunctionElement.SVG_FECOMPONENTTRANSFER_TYPE_...
    val type: SVGAnimatedEnumeration

    val tableValues: SVGAnimatedNumberList
    val slope: SVGAnimatedNumber
    val intercept: SVGAnimatedNumber
    val amplitude: SVGAnimatedNumber
    val exponent: SVGAnimatedNumber
    val offset: SVGAnimatedNumber
}

/**
 * Exposes the JavaScript [SVGFEFuncAElement](https://developer.mozilla.org/en/docs/Web/API/SVGFEFuncAElement) to Kotlin
 */
abstract external class SVGFEFuncAElement : SVGComponentTransferFunctionElement

/**
 * Exposes the JavaScript [SVGFEFuncRElement](https://developer.mozilla.org/en/docs/Web/API/SVGFEFuncRElement) to Kotlin
 */
abstract external class SVGFEFuncRElement : SVGComponentTransferFunctionElement

/**
 * Exposes the JavaScript [SVGFEFuncGElement](https://developer.mozilla.org/en/docs/Web/API/SVGFEFuncGElement) to Kotlin
 */
abstract external class SVGFEFuncGElement : SVGComponentTransferFunctionElement

/**
 * Exposes the JavaScript [SVGFEFuncBElement](https://developer.mozilla.org/en/docs/Web/API/SVGFEFuncBElement) to Kotlin
 */
abstract external class SVGFEFuncBElement : SVGComponentTransferFunctionElement


enum class SVGComponentTransferFunctionType {
    Identity,
    Table,
    Discrete,
    Linear,
    Gamma;

    override fun toString() = this.toSvgValue()
}

class SVGComponentTransferFunctionAttrsScope<E : SVGComponentTransferFunctionElement> private constructor(attrs: AttrsScope<E>) :
    SVGFilterElementAttrsScope<E>(attrs) {
    fun type(type: SVGComponentTransferFunctionType) {
        attr("type", type.toString())
    }

    fun tableValues(vararg values: Number) {
        attr("tableValues", values.joinToString(" "))
    }

    /**
     * Used in the equation "slope * C + intercept".
     *
     * Relevant when [type] is set to [SVGComponentTransferFunctionType.Linear].
     */
    fun slope(value: Number) {
        attr("slope", value.toString())
    }

    /**
     * Used in the equation "slope * C + intercept".
     *
     * Relevant when [type] is set to [SVGComponentTransferFunctionType.Linear].
     */
    fun intercept(value: Number) {
        attr("intercept", value.toString())
    }

    /**
     * Used in the equation "amplitude * pow(C, exponent) + offset".
     *
     * Relevant when [type] is set to [SVGComponentTransferFunctionType.Gamma].
     */
    fun amplitude(value: Number) {
        attr("amplitude", value.toString())
    }

    /**
     * Used in the equation "amplitude * pow(C, exponent) + offset".
     *
     * Relevant when [type] is set to [SVGComponentTransferFunctionType.Gamma].
     */
    fun exponent(value: Number) {
        attr("exponent", value.toString())
    }

    /**
     * Used in the equation "amplitude * pow(C, exponent) + offset".
     *
     * Relevant when [type] is set to [SVGComponentTransferFunctionType.Gamma].
     */
    fun offset(value: Number) {
        attr("offset", value.toString())
    }

    companion object {
        operator fun <E : SVGComponentTransferFunctionElement> invoke(attrs: SVGComponentTransferFunctionAttrsScope<E>.() -> Unit): AttrBuilderContext<E> {
            return { SVGComponentTransferFunctionAttrsScope(this).attrs() }
        }
    }
}

@Composable
fun ElementScope<SVGFEComponentTransferElement>.FuncA(attrs: SVGComponentTransferFunctionAttrsScope<SVGFEFuncAElement>.() -> Unit) {
    GenericTag("feFuncA", "http://www.w3.org/2000/svg", SVGComponentTransferFunctionAttrsScope(attrs))
}

@Composable
fun ElementScope<SVGFEComponentTransferElement>.FuncR(attrs: SVGComponentTransferFunctionAttrsScope<SVGFEFuncRElement>.() -> Unit) {
    GenericTag("feFuncR", "http://www.w3.org/2000/svg", SVGComponentTransferFunctionAttrsScope(attrs))
}

@Composable
fun ElementScope<SVGFEComponentTransferElement>.FuncG(attrs: SVGComponentTransferFunctionAttrsScope<SVGFEFuncGElement>.() -> Unit) {
    GenericTag("feFuncG", "http://www.w3.org/2000/svg", SVGComponentTransferFunctionAttrsScope(attrs))
}

@Composable
fun ElementScope<SVGFEComponentTransferElement>.FuncB(attrs: SVGComponentTransferFunctionAttrsScope<SVGFEFuncBElement>.() -> Unit) {
    GenericTag("feFuncB", "http://www.w3.org/2000/svg", SVGComponentTransferFunctionAttrsScope(attrs))
}

/**
 * Exposes the JavaScript [SVGFEMorphologyElement](https://developer.mozilla.org/en/docs/Web/API/SVGFEMorphologyElement) to Kotlin
 */
abstract external class SVGFEMorphologyElement : SVGElement, SVGFECommon, SVGFEInput1 {
    companion object {
        val SVG_MORPHOLOGY_OPERATOR_UNKNOWN: Short
        val SVG_MORPHOLOGY_OPERATOR_ERODE: Short
        val SVG_MORPHOLOGY_OPERATOR_DILATE: Short
    }

    // SVGFEMorphologyElement.SVG_MORPHOLOGY_OPERATOR_...
    val operator: SVGAnimatedEnumeration

    val radiusX: SVGAnimatedNumber
    val radiusY: SVGAnimatedNumber
}

enum class SVGMorphologyOperator {
    Erode,
    Dilate;

    override fun toString() = this.toSvgValue()
}

class SVGFEMorphologyAttrsScope private constructor(attrs: AttrsScope<SVGFEMorphologyElement>) :
    SVGFilterElementAttrsScope<SVGFEMorphologyElement>(attrs),
    SvgFilterInput1Attrs<SVGFEMorphologyElement> {

    fun operator(value: SVGMorphologyOperator) {
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
abstract external class SVGFEOffsetElement : SVGElement, SVGFECommon, SVGFEInput1 {
    val dx: SVGAnimatedLength
    val dy: SVGAnimatedLength
}

class SVGFEOffsetAttrsScope private constructor(attrs: AttrsScope<SVGFEOffsetElement>) :
    SVGFilterElementAttrsScope<SVGFEOffsetElement>(attrs),
    SvgFilterInput1Attrs<SVGFEOffsetElement>, SvgOffsetAttrs<SVGFEOffsetElement> {

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

/**
 * Exposes the JavaScript [SVGFESpecularLightingElement](https://developer.mozilla.org/en/docs/Web/API/SVGFESpecularLightingElement) to Kotlin
 */
abstract external class SVGFESpecularLightingElement : SVGElement, SVGFECommon, SVGFEInput1 {
    val specularConstant: SVGAnimatedNumber
    val specularExponent: SVGAnimatedNumber

    val surfaceScale: SVGAnimatedNumber
}

class SVGFESpecularLightingAttrsScope private constructor(attrs: AttrsScope<SVGFESpecularLightingElement>) :
    SVGFilterElementAttrsScope<SVGFESpecularLightingElement>(attrs),
    SvgFilterInput1Attrs<SVGFESpecularLightingElement> {

    fun specularConstant(value: Number) {
        attr("specularConstant", value.toString())
    }

    fun specularExponent(value: Number) {
        attr("specularExponent", value.toString())
    }

    fun surfaceScale(value: Number) {
        attr("surfaceScale", value.toString())
    }

    companion object {
        operator fun invoke(attrs: SVGFESpecularLightingAttrsScope.() -> Unit): AttrBuilderContext<SVGFESpecularLightingElement> {
            return { SVGFESpecularLightingAttrsScope(this).attrs() }
        }
    }
}

@Composable
fun ElementScope<SVGFilterElement>.SpecularLighting(
    attrs: SVGFESpecularLightingAttrsScope.() -> Unit,
    content: ContentBuilder<SVGFESpecularLightingElement>
) {
    GenericTag(
        "feSpecularLighting",
        "http://www.w3.org/2000/svg", SVGFESpecularLightingAttrsScope(attrs), content
    )
}

/**
 * Exposes the JavaScript [SVGFETileElement](https://developer.mozilla.org/en/docs/Web/API/SVGFETileElement) to Kotlin
 */
abstract external class SVGFETileElement : SVGElement, SVGFECommon, SVGFEInput1

class SVGFETileAttrsScope private constructor(attrs: AttrsScope<SVGFETileElement>) :
    SVGFilterElementAttrsScope<SVGFETileElement>(attrs), SvgFilterInput1Attrs<SVGFETileElement> {

    companion object {
        operator fun invoke(attrs: SVGFETileAttrsScope.() -> Unit): AttrBuilderContext<SVGFETileElement> {
            return { SVGFETileAttrsScope(this).attrs() }
        }
    }
}

@Composable
fun ElementScope<SVGFilterElement>.Tile(
    attrs: (SVGFETileAttrsScope.() -> Unit)? = null
) {
    GenericTag(
        "feTile",
        "http://www.w3.org/2000/svg", attrs?.let { SVGFETileAttrsScope(it) }
    )
}

/**
 * Exposes the JavaScript [SVGFETurbulenceElement](https://developer.mozilla.org/en/docs/Web/API/SVGFETurbulenceElement) to Kotlin
 */
abstract external class SVGFETurbulenceElement : SVGElement, SVGFECommon {
    companion object {
        val SVG_TURBULENCE_TYPE_UNKNOWN: Short
        val SVG_TURBULENCE_TYPE_FRACTALNOISE: Short
        val SVG_TURBULENCE_TYPE_TURBULENCE: Short

        val SVG_STITCHTYPE_UNKNOWN: Short
        val SVG_STITCHTYPE_STITCH: Short
        val SVG_STITCHTYPE_NOSTITCH: Short
    }

    // SVGFETurbulenceElement.SVG_TURBULENCE_TYPE_...
    val type: SVGAnimatedEnumeration

    val radiusX: SVGAnimatedNumber
    val radiusY: SVGAnimatedNumber

    // SVGFETurbulenceElement.SVG_STITCHTYPE_...
    val stitchTiles: SVGAnimatedEnumeration

    val baseFrequencyX: SVGAnimatedNumber
    val baseFrequencyY: SVGAnimatedNumber
    val numOctaves: SVGAnimatedInteger
    val seed: SVGAnimatedNumber
}

enum class SVGTurbulenceType {
    FractalNoise,
    Turbulence;

    override fun toString() = this.toSvgValue()
}

enum class SVGStitchType {
    Stitch,
    NoStitch;

    override fun toString() = this.toSvgValue()
}

class SVGFETurbulenceAttrsScope private constructor(attrs: AttrsScope<SVGFETurbulenceElement>) :
    SVGFilterElementAttrsScope<SVGFETurbulenceElement>(attrs),
    SvgFilterInput1Attrs<SVGFETurbulenceElement> {

    fun type(value: SVGTurbulenceType) {
        attr("type", value.toString())
    }

    fun stitchTiles(value: SVGStitchType) {
        attr("stitchTiles", value.toString())
    }

    fun baseFrequency(value: Number) {
        attr("baseFrequency", value.toString())
    }

    fun baseFrequency(x: Number, y: Number) {
        attr("baseFrequency", "$x $y")
    }

    fun numOctaves(value: Int) {
        attr("numOctaves", value.toString())
    }

    fun seed(value: Number) {
        attr("seed", value.toString())
    }

    companion object {
        operator fun invoke(attrs: SVGFETurbulenceAttrsScope.() -> Unit): AttrBuilderContext<SVGFETurbulenceElement> {
            return { SVGFETurbulenceAttrsScope(this).attrs() }
        }
    }
}

@Composable
fun ElementScope<SVGFilterElement>.Turbulence(
    attrs: SVGFETurbulenceAttrsScope.() -> Unit,
) {
    GenericTag(
        "feTurbulence",
        "http://www.w3.org/2000/svg", SVGFETurbulenceAttrsScope(attrs)
    )
}

// end region
