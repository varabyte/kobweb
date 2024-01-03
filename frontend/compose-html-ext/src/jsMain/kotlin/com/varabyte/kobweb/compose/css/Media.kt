package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/object-fit
class ObjectFit private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object {
        // Keywords
        val Contain get() = ObjectFit("contain")
        val Cover get() = ObjectFit("cover")
        val Fill get() = ObjectFit("fill")
        val None get() = ObjectFit("none")
        val ScaleDown get() = ObjectFit("scale-down")

        // Global values
        val Inherit get() = ObjectFit("inherit")
        val Initial get() = ObjectFit("initial")
        val Revert get() = ObjectFit("revert")
        val Unset get() = ObjectFit("unset")
    }
}

fun StyleScope.objectFit(objectFit: ObjectFit) {
    property("object-fit", objectFit)
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/mix-blend-mode
class MixBlendMode private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object {
        // Keywords
        val Normal get() = MixBlendMode("normal")
        val Multiply get() = MixBlendMode("multiply")
        val Screen get() = MixBlendMode("screen")
        val Overlay get() = MixBlendMode("overlay")
        val Darken get() = MixBlendMode("darken")
        val Lighten get() = MixBlendMode("lighten")
        val ColorDodge get() = MixBlendMode("color-dodge")
        val ColorBurn get() = MixBlendMode("color-burn")
        val HardLight get() = MixBlendMode("hard-light")
        val SoftLight get() = MixBlendMode("soft-light")
        val Difference get() = MixBlendMode("difference")
        val Exclusion get() = MixBlendMode("exclusion")
        val Hue get() = MixBlendMode("hue")
        val Saturation get() = MixBlendMode("saturation")
        val Color get() = MixBlendMode("color")
        val Luminosity get() = MixBlendMode("luminosity")
        val PlusDarker get() = MixBlendMode("plus-darker")
        val PlusLighter get() = MixBlendMode("plus-lighter")

        // Global values
        val Inherit get() = MixBlendMode("inherit")
        val Initial get() = MixBlendMode("initial")
        val Revert get() = MixBlendMode("revert")
        val Unset get() = MixBlendMode("unset")
    }
}

fun StyleScope.mixBlendMode(blendMode: MixBlendMode) {
    property("mix-blend-mode", blendMode)
}
