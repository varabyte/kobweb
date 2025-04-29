// Sealed class private constructors are useful, actually!
@file:Suppress("RedundantVisibilityModifier")

package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*

// region Rotate

fun StyleScope.rotate(a: CSSAngleNumericValue) {
    property("rotate", a)
}

fun StyleScope.rotate(x: Number, y: Number, z: Number, a: CSSAngleNumericValue) {
    property("rotate", "$x $y $z $a")
}

fun StyleScope.rotateX(a: CSSAngleNumericValue) {
    property("rotate", "x $a")
}

fun StyleScope.rotateY(a: CSSAngleNumericValue) {
    property("rotate", "y $a")
}

fun StyleScope.rotateZ(a: CSSAngleNumericValue) {
    property("rotate", "z $a")
}

// endregion

// region Scale

fun StyleScope.scale(s: Number) {
    property("scale", s)
}

fun StyleScope.scale(sx: Number, sy: Number) {
    property("scale", "$sx $sy")
}

fun StyleScope.scale(sx: Number, sy: Number, sz: Number) {
    property("scale", "$sx $sy $sz")
}

fun StyleScope.scaleX(s: Number) {
    scale(s, 1)
}

fun StyleScope.scaleY(s: Number) {
    scale(1, s)
}

fun StyleScope.scaleZ(s: Number) {
    scale(1, 1, s)
}

fun StyleScope.scale(s: CSSPercentageNumericValue) {
    property("scale", s)
}

fun StyleScope.scale(sx: CSSPercentageNumericValue, sy: CSSPercentageNumericValue) {
    property("scale", "$sx $sy")
}

fun StyleScope.scale(sx: CSSPercentageNumericValue, sy: CSSPercentageNumericValue, sz: CSSPercentageNumericValue) {
    property("scale", "$sx $sy $sz")
}

fun StyleScope.scaleX(s: CSSPercentageNumericValue) {
    scale(s, 100.percent)
}

fun StyleScope.scaleY(s: CSSPercentageNumericValue) {
    scale(100.percent, s)
}

fun StyleScope.scaleZ(s: CSSPercentageNumericValue) {
    scale(100.percent, 100.percent, s)
}

// endregion

// https://developer.mozilla.org/en-US/docs/Web/CSS/backface-visibility
class BackfaceVisibility private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object {
        // Keyword values
        val Visible get() = BackfaceVisibility("visible")
        val Hidden get() = BackfaceVisibility("hidden")

        // Global values
        val Inherit get() = BackfaceVisibility("inherit")
        val Initial get() = BackfaceVisibility("initial")
        val Revert get() = BackfaceVisibility("revert")
        val Unset get() = BackfaceVisibility("unset")
    }
}

fun StyleScope.backfaceVisibility(backFaceVisibility: BackfaceVisibility) {
    property("backface-visibility", backFaceVisibility)
}


// https://developer.mozilla.org/en-US/docs/Web/CSS/transform-box
class TransformBox private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object {
        // Keyword
        val BorderBox get() = TransformBox("border-box")
        val ContentBox get() = TransformBox("content-box")
        val FillBox get() = TransformBox("fill-box")
        val StrokeBox get() = TransformBox("stroke-box")
        val ViewBox get() = TransformBox("view-box")

        // Global
        val Inherit get() = TransformBox("inherit")
        val Initial get() = TransformBox("initial")
        val Revert get() = TransformBox("revert")
        val Unset get() = TransformBox("unset")
    }
}

fun StyleScope.transformBox(transformBox: TransformBox) {
    property("transform-box", transformBox.toString())
}

// https://developer.mozilla.org/en-US/docs/Web/CSS/transform-origin
sealed class TransformOrigin private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    private class Keyword(value: String) : TransformOrigin(value)
    private class Origin(value: String) : TransformOrigin(value)

    companion object {
        // We cannot use CssPosition as transform-origin does not support offsets from edges, e.g. Edge.Right(10.px)

        @Suppress("FunctionName")
        private fun _of(xOffset: Any? = null, yOffset: Any? = null, zOffset: Any? = null): TransformOrigin =
            Origin(listOfNotNull(xOffset, yOffset, zOffset).joinToString(" "))

        // NOTE: the following 2 functions do not take a `zOffset` parameter as `of(Edge.Right, 5.px)` would then apply
        // the `5.px` to the z-axis despite intuitively appearing to refer to the y-axis
        fun of(xOffset: EdgeXOrCenter): TransformOrigin = of(xOffset, Edge.CenterY)
        fun of(yOffset: EdgeYOrCenter): TransformOrigin = of(Edge.CenterX, yOffset)

        fun of(xOffset: EdgeXOrCenter, yOffset: EdgeYOrCenter, zOffset: CSSLengthNumericValue? = null): TransformOrigin =
            _of(xOffset, yOffset, zOffset)

        fun of(
            xOffset: EdgeXOrCenter,
            yOffset: CSSLengthOrPercentageNumericValue,
            zOffset: CSSLengthNumericValue? = null,
        ): TransformOrigin = _of(xOffset, yOffset, zOffset)

        fun of(
            xOffset: CSSLengthOrPercentageNumericValue,
            yOffset: EdgeYOrCenter,
            zOffset: CSSLengthNumericValue? = null,
        ): TransformOrigin = _of(xOffset, yOffset, zOffset)

        fun of(
            xOffset: CSSLengthOrPercentageNumericValue,
            yOffset: CSSLengthOrPercentageNumericValue,
            zOffset: CSSLengthNumericValue? = null,
        ): TransformOrigin = _of(xOffset, yOffset, zOffset)

        // Duplicate the values provided by CssPosition
        val Top get() = of(Edge.Top)
        val TopRight get() = of(Edge.Right, Edge.Top)
        val Right get() = of(Edge.Right)
        val BottomRight get() = of(Edge.Right, Edge.Bottom)
        val Bottom get() = of(Edge.Bottom)
        val BottomLeft get() = of(Edge.Left, Edge.Bottom)
        val Left get() = of(Edge.Left)
        val TopLeft get() = of(Edge.Left, Edge.Top)
        val Center get() = of(Edge.CenterX, Edge.CenterY)

        // Global values
        val Inherit get(): TransformOrigin = Keyword("inherit")
        val Initial get(): TransformOrigin = Keyword("initial")
        val Revert get(): TransformOrigin = Keyword("revert")
        val Unset get(): TransformOrigin = Keyword("unset")
    }
}

fun StyleScope.transformOrigin(transformOrigin: TransformOrigin) {
    property("transform-origin", transformOrigin.toString())
}

// https://developer.mozilla.org/en-US/docs/Web/CSS/transform-style
class TransformStyle private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object {
        // Keyword
        val Flat get() = TransformStyle("flat")
        val Preserve3d get() = TransformStyle("preserve-3d")

        // Global
        val Inherit get() = TransformStyle("inherit")
        val Initial get() = TransformStyle("initial")
        val Revert get() = TransformStyle("revert")
        val Unset get() = TransformStyle("unset")
    }
}

fun StyleScope.transformStyle(transformStyle: TransformStyle) {
    property("transform-style", transformStyle.toString())
}

// region Translate

fun StyleScope.translate(tx: CSSLengthOrPercentageNumericValue) {
    property("translate", tx)
}

fun StyleScope.translate(tx: CSSLengthOrPercentageNumericValue, ty: CSSLengthOrPercentageNumericValue) {
    property("translate", "$tx $ty")
}

fun StyleScope.translate(
    tx: CSSLengthOrPercentageNumericValue,
    ty: CSSLengthOrPercentageNumericValue,
    tz: CSSLengthOrPercentageNumericValue
) {
    property("translate", "$tx $ty $tz")
}

fun StyleScope.translateX(tx: CSSLengthOrPercentageNumericValue) {
    translate(tx)
}

fun StyleScope.translateY(ty: CSSLengthOrPercentageNumericValue) {
    translate(0.percent, ty)
}

fun StyleScope.translateZ(tz: CSSLengthOrPercentageNumericValue) {
    translate(0.percent, 0.percent, tz)
}

// endregion
