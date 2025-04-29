package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*

interface CssBlendModeValues<T: StylePropertyValue> {
    val Normal: T
    val Multiply: T
    val Screen: T
    val Overlay: T
    val Darken: T
    val Lighten: T
    val ColorDodge: T
    val ColorBurn: T
    val HardLight: T
    val SoftLight: T
    val Difference: T
    val Exclusion: T
    val Hue: T
    val Saturation: T
    val Color: T
    val Luminosity: T
    val PlusDarker: T
    val PlusLighter: T
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/mix-blend-mode
class MixBlendMode private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object : CssBlendModeValues<MixBlendMode>, CssGlobalValues<MixBlendMode> {
        // Keywords
        override val Normal get() = MixBlendMode("normal")
        override val Multiply get() = MixBlendMode("multiply")
        override val Screen get() = MixBlendMode("screen")
        override val Overlay get() = MixBlendMode("overlay")
        override val Darken get() = MixBlendMode("darken")
        override val Lighten get() = MixBlendMode("lighten")
        override val ColorDodge get() = MixBlendMode("color-dodge")
        override val ColorBurn get() = MixBlendMode("color-burn")
        override val HardLight get() = MixBlendMode("hard-light")
        override val SoftLight get() = MixBlendMode("soft-light")
        override val Difference get() = MixBlendMode("difference")
        override val Exclusion get() = MixBlendMode("exclusion")
        override val Hue get() = MixBlendMode("hue")
        override val Saturation get() = MixBlendMode("saturation")
        override val Color get() = MixBlendMode("color")
        override val Luminosity get() = MixBlendMode("luminosity")
        override val PlusDarker get() = MixBlendMode("plus-darker")
        override val PlusLighter get() = MixBlendMode("plus-lighter")
    }
}

fun StyleScope.mixBlendMode(blendMode: MixBlendMode) {
    property("mix-blend-mode", blendMode)
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/object-fit
class ObjectFit private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object : CssGlobalValues<ObjectFit> {
        // Keywords
        val Contain get() = ObjectFit("contain")
        val Cover get() = ObjectFit("cover")
        val Fill get() = ObjectFit("fill")
        val None get() = ObjectFit("none")
        val ScaleDown get() = ObjectFit("scale-down")
    }
}

fun StyleScope.objectFit(objectFit: ObjectFit) {
    property("object-fit", objectFit)
}
