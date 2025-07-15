package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*

interface CssBlendModeValues<T: StylePropertyValue> {
    val Normal get() = "normal".unsafeCast<T>()
    val Multiply get() = "multiply".unsafeCast<T>()
    val Screen get() = "screen".unsafeCast<T>()
    val Overlay get() = "overlay".unsafeCast<T>()
    val Darken get() = "darken".unsafeCast<T>()
    val Lighten get() = "lighten".unsafeCast<T>()
    val ColorDodge get() = "color-dodge".unsafeCast<T>()
    val ColorBurn get() = "color-burn".unsafeCast<T>()
    val HardLight get() = "hard-light".unsafeCast<T>()
    val SoftLight get() = "soft-light".unsafeCast<T>()
    val Difference get() = "difference".unsafeCast<T>()
    val Exclusion get() = "exclusion".unsafeCast<T>()
    val Hue get() = "hue".unsafeCast<T>()
    val Saturation get() = "saturation".unsafeCast<T>()
    val Color get() = "color".unsafeCast<T>()
    val Luminosity get() = "luminosity".unsafeCast<T>()
    // Not widely supported: https://caniuse.com/mdn-css_properties_mix-blend-mode_plus-darker
    // val PlusDarker get() = "plus-darker".unsafeCast<T>()
    // Not widely supported in `background-blend-mode` (added below in `MixBlendMode`):
    // https://wpt.fyi/results/css/compositing/background-blending?q=plus-lighter
    // val PlusLighter get() = "plus-lighter".unsafeCast<T>()
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/mix-blend-mode
sealed interface MixBlendMode : StylePropertyValue {
    companion object : CssBlendModeValues<MixBlendMode>, CssGlobalValues<MixBlendMode> {
        val PlusLighter get() = "plus-lighter".unsafeCast<MixBlendMode>()
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
