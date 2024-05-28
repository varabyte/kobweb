package com.varabyte.kobweb.compose.ui.modifiers

import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.styleModifier
import org.jetbrains.compose.web.ExperimentalComposeWebApi
import org.jetbrains.compose.web.css.*

@OptIn(ExperimentalComposeWebApi::class)
fun Modifier.transform(transformContext: TransformBuilder.() -> Unit) = styleModifier {
    transform(transformContext)
}

fun Modifier.rotate(a: CSSAngleValue) = styleModifier {
    rotate(a)
}

fun Modifier.rotate(x: Number, y: Number, z: Number, a: CSSAngleValue) = styleModifier {
    rotate(x, y, z, a)
}

fun Modifier.rotateX(ax: CSSAngleValue) = styleModifier {
    rotateX(ax)
}

fun Modifier.rotateY(ay: CSSAngleValue) = styleModifier {
    rotateY(ay)
}

fun Modifier.rotateZ(az: CSSAngleValue) = styleModifier {
    rotateZ(az)
}

fun Modifier.scale(s: Number) = styleModifier {
    scale(s)
}

fun Modifier.scale(sx: Number, sy: Number) = styleModifier {
    scale(sx, sy)
}

fun Modifier.scale(sx: Number, sy: Number, sz: Number) = styleModifier {
    scale(sx, sy, sz)
}

fun Modifier.scaleX(s: Number) = styleModifier {
    scaleX(s)
}

fun Modifier.scaleY(s: Number) = styleModifier {
    scaleY(s)
}

fun Modifier.scaleZ(s: Number) = styleModifier {
    scaleZ(s)
}

fun Modifier.scale(s: CSSPercentageNumericValue) = styleModifier {
    scale(s)
}

fun Modifier.scale(sx: CSSPercentageNumericValue, sy: CSSPercentageNumericValue) = styleModifier {
    scale(sx, sy)
}

fun Modifier.scale(sx: CSSPercentageNumericValue, sy: CSSPercentageNumericValue, sz: CSSPercentageNumericValue) =
    styleModifier {
    scale(sx, sy, sz)
}

fun Modifier.scaleX(s: CSSPercentageNumericValue) = styleModifier {
    scaleX(s)
}

fun Modifier.scaleY(s: CSSPercentageNumericValue) = styleModifier {
    scaleY(s)
}

fun Modifier.scaleZ(s: CSSPercentageNumericValue) = styleModifier {
    scaleZ(s)
}

fun Modifier.transformBox(transformBox: TransformBox) = styleModifier {
    transformBox(transformBox)
}

fun Modifier.transformOrigin(transformOrigin: TransformOrigin) = styleModifier {
    transformOrigin(transformOrigin)
}

fun Modifier.transformStyle(transformStyle: TransformStyle) = styleModifier {
    transformStyle(transformStyle)
}

fun Modifier.translate(tx: CSSLengthOrPercentageNumericValue) = styleModifier {
    translate(tx)
}

fun Modifier.translate(tx: CSSLengthOrPercentageNumericValue, ty: CSSLengthOrPercentageNumericValue) = styleModifier {
    translate(tx, ty)
}

fun Modifier.translate(
    tx: CSSLengthOrPercentageNumericValue,
    ty: CSSLengthOrPercentageNumericValue,
    tz: CSSLengthOrPercentageNumericValue
) =
    styleModifier {
        translate(tx, ty, tz)
    }

fun Modifier.translateX(tx: CSSLengthOrPercentageNumericValue) = styleModifier {
    translateX(tx)
}

fun Modifier.translateY(ty: CSSLengthOrPercentageNumericValue) = styleModifier {
    translateY(ty)
}

fun Modifier.translateZ(tz: CSSLengthOrPercentageNumericValue) = styleModifier {
    translateZ(tz)
}
