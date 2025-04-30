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
sealed interface MixBlendMode : StylePropertyValue {
    companion object : CssBlendModeValues<MixBlendMode>, CssGlobalValues<MixBlendMode> {
        // Keywords
        override val Normal get() = "normal".unsafeCast<MixBlendMode>()
        override val Multiply get() = "multiply".unsafeCast<MixBlendMode>()
        override val Screen get() = "screen".unsafeCast<MixBlendMode>()
        override val Overlay get() = "overlay".unsafeCast<MixBlendMode>()
        override val Darken get() = "darken".unsafeCast<MixBlendMode>()
        override val Lighten get() = "lighten".unsafeCast<MixBlendMode>()
        override val ColorDodge get() = "color-dodge".unsafeCast<MixBlendMode>()
        override val ColorBurn get() = "color-burn".unsafeCast<MixBlendMode>()
        override val HardLight get() = "hard-light".unsafeCast<MixBlendMode>()
        override val SoftLight get() = "soft-light".unsafeCast<MixBlendMode>()
        override val Difference get() = "difference".unsafeCast<MixBlendMode>()
        override val Exclusion get() = "exclusion".unsafeCast<MixBlendMode>()
        override val Hue get() = "hue".unsafeCast<MixBlendMode>()
        override val Saturation get() = "saturation".unsafeCast<MixBlendMode>()
        override val Color get() = "color".unsafeCast<MixBlendMode>()
        override val Luminosity get() = "luminosity".unsafeCast<MixBlendMode>()
        override val PlusDarker get() = "plus-darker".unsafeCast<MixBlendMode>()
        override val PlusLighter get() = "plus-lighter".unsafeCast<MixBlendMode>()
    }
}

fun StyleScope.mixBlendMode(blendMode: MixBlendMode) {
    property("mix-blend-mode", blendMode)
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/object-fit
sealed interface ObjectFit : StylePropertyValue {
    companion object : CssGlobalValues<ObjectFit> {
        // Keywords
        val Contain get() = "contain".unsafeCast<ObjectFit>()
        val Cover get() = "cover".unsafeCast<ObjectFit>()
        val Fill get() = "fill".unsafeCast<ObjectFit>()
        val None get() = "none".unsafeCast<ObjectFit>()
        val ScaleDown get() = "scale-down".unsafeCast<ObjectFit>()
    }
}

fun StyleScope.objectFit(objectFit: ObjectFit) {
    property("object-fit", objectFit)
}
