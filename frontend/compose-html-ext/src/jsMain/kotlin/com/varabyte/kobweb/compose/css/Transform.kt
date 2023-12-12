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
