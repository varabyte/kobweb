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
sealed interface BackfaceVisibility : StylePropertyValue {
    companion object : CssGlobalValues<BackfaceVisibility> {
        // Keyword values
        val Visible get() = "visible".unsafeCast<BackfaceVisibility>()
        val Hidden get() = "hidden".unsafeCast<BackfaceVisibility>()
    }
}

fun StyleScope.backfaceVisibility(backFaceVisibility: BackfaceVisibility) {
    property("backface-visibility", backFaceVisibility)
}


// https://developer.mozilla.org/en-US/docs/Web/CSS/transform-box
sealed interface TransformBox : StylePropertyValue {
    companion object : CssGlobalValues<TransformBox> {
        // Keyword
        val BorderBox get() = "border-box".unsafeCast<TransformBox>()
        val ContentBox get() = "content-box".unsafeCast<TransformBox>()
        val FillBox get() = "fill-box".unsafeCast<TransformBox>()
        val StrokeBox get() = "stroke-box".unsafeCast<TransformBox>()
        val ViewBox get() = "view-box".unsafeCast<TransformBox>()
    }
}

fun StyleScope.transformBox(transformBox: TransformBox) {
    property("transform-box", transformBox.toString())
}

// https://developer.mozilla.org/en-US/docs/Web/CSS/transform-origin
sealed interface TransformOrigin : StylePropertyValue {
    companion object : CssGlobalValues<TransformOrigin> {
        // We cannot use CssPosition as transform-origin does not support offsets from edges, e.g. Edge.Right(10.px)

        @Suppress("FunctionName")
        private fun _of(xOffset: Any? = null, yOffset: Any? = null, zOffset: Any? = null): TransformOrigin =
            listOfNotNull(xOffset, yOffset, zOffset).joinToString(" ").unsafeCast<TransformOrigin>()

        // NOTE: the following 2 functions do not take a `zOffset` parameter as `of(Edge.Right, 5.px)` would then apply
        // the `5.px` to the z-axis despite intuitively appearing to refer to the y-axis
        fun of(xOffset: EdgeXOrCenter) = of(xOffset, Edge.CenterY)
        fun of(yOffset: EdgeYOrCenter) = of(Edge.CenterX, yOffset)

        fun of(xOffset: EdgeXOrCenter, yOffset: EdgeYOrCenter, zOffset: CSSLengthNumericValue? = null) =
            _of(xOffset, yOffset, zOffset)

        fun of(
            xOffset: EdgeXOrCenter,
            yOffset: CSSLengthOrPercentageNumericValue,
            zOffset: CSSLengthNumericValue? = null,
        ) = _of(xOffset, yOffset, zOffset)

        fun of(
            xOffset: CSSLengthOrPercentageNumericValue,
            yOffset: EdgeYOrCenter,
            zOffset: CSSLengthNumericValue? = null,
        ) = _of(xOffset, yOffset, zOffset)

        fun of(
            xOffset: CSSLengthOrPercentageNumericValue,
            yOffset: CSSLengthOrPercentageNumericValue,
            zOffset: CSSLengthNumericValue? = null,
        ) = _of(xOffset, yOffset, zOffset)

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
    }
}

fun StyleScope.transformOrigin(transformOrigin: TransformOrigin) {
    property("transform-origin", transformOrigin.toString())
}

// https://developer.mozilla.org/en-US/docs/Web/CSS/transform-style
sealed interface TransformStyle : StylePropertyValue {
    companion object : CssGlobalValues<TransformStyle> {
        // Keyword
        val Flat get() = "flat".unsafeCast<TransformStyle>()
        val Preserve3d get() = "preserve-3d".unsafeCast<TransformStyle>()
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
