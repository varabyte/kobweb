package com.varabyte.kobweb.compose.ui.modifiers

import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.styleModifier
import org.jetbrains.compose.web.ExperimentalComposeWebApi
import org.jetbrains.compose.web.css.*

@ExperimentalComposeWebApi
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

fun Modifier.scale(s: CSSPercentageValue) = styleModifier {
    scale(s)
}

fun Modifier.scale(sx: CSSPercentageValue, sy: CSSPercentageValue) = styleModifier {
    scale(sx, sy)
}

fun Modifier.scale(sx: CSSPercentageValue, sy: CSSPercentageValue, sz: CSSPercentageValue) = styleModifier {
    scale(sx, sy, sz)
}

fun Modifier.scaleX(s: CSSPercentageValue) = styleModifier {
    scaleX(s)
}

fun Modifier.scaleY(s: CSSPercentageValue) = styleModifier {
    scaleY(s)
}

fun Modifier.scaleZ(s: CSSPercentageValue) = styleModifier {
    scaleZ(s)
}

fun Modifier.translate(tx: CSSLengthOrPercentageValue) = styleModifier {
    translate(tx)
}

fun Modifier.translate(tx: CSSLengthOrPercentageValue, ty: CSSLengthOrPercentageValue) = styleModifier {
    translate(tx, ty)
}

fun Modifier.translate(tx: CSSLengthOrPercentageValue, ty: CSSLengthOrPercentageValue, tz: CSSLengthOrPercentageValue) =
    styleModifier {
        translate(tx, ty, tz)
    }

fun Modifier.translateX(tx: CSSLengthOrPercentageValue) = styleModifier {
    translateX(tx)
}

fun Modifier.translateY(ty: CSSLengthOrPercentageValue) = styleModifier {
    translateY(ty)
}

fun Modifier.translateZ(tz: CSSLengthOrPercentageValue) = styleModifier {
    translateZ(tz)
}
