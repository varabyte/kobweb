package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*

// region Rotate

fun StyleScope.rotate(a: CSSAngleValue) {
    property("rotate", a)
}

fun StyleScope.rotate(x: Number, y: Number, z: Number, a: CSSAngleValue) {
    property("rotate", "$x $y $z $a")
}

fun StyleScope.rotateX(a: CSSAngleValue) {
    property("rotate", "x $a")
}

fun StyleScope.rotateY(a: CSSAngleValue) {
    property("rotate", "y $a")
}

fun StyleScope.rotateZ(a: CSSAngleValue) {
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

fun StyleScope.scale(s: CSSPercentageValue) {
    property("scale", s)
}

fun StyleScope.scale(sx: CSSPercentageValue, sy: CSSPercentageValue) {
    property("scale", "$sx $sy")
}

fun StyleScope.scale(sx: CSSPercentageValue, sy: CSSPercentageValue, sz: CSSPercentageValue) {
    property("scale", "$sx $sy $sz")
}

fun StyleScope.scaleX(s: CSSPercentageValue) {
    scale(s, 100.percent)
}

fun StyleScope.scaleY(s: CSSPercentageValue) {
    scale(100.percent, s)
}

fun StyleScope.scaleZ(s: CSSPercentageValue) {
    scale(100.percent, 100.percent, s)
}

// endregion

// region Translate

fun StyleScope.translate(tx: CSSLengthOrPercentageValue) {
    property("translate", tx)
}

fun StyleScope.translate(tx: CSSLengthOrPercentageValue, ty: CSSLengthOrPercentageValue) {
    property("translate", "$tx $ty")
}

fun StyleScope.translate(
    tx: CSSLengthOrPercentageValue,
    ty: CSSLengthOrPercentageValue,
    tz: CSSLengthOrPercentageValue
) {
    property("translate", "$tx $ty $tz")
}

fun StyleScope.translateX(tx: CSSLengthOrPercentageValue) {
    translate(tx)
}

fun StyleScope.translateY(ty: CSSLengthOrPercentageValue) {
    translate(0.percent, ty)
}

fun StyleScope.translateZ(tz: CSSLengthOrPercentageValue) {
    translate(0.percent, 0.percent, tz)
}

// endregion
