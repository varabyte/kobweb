package com.varabyte.kobweb.compose.ui.modifiers

import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.css.functions.CSSUrl
import com.varabyte.kobweb.compose.css.functions.Gradient
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.styleModifier
import org.jetbrains.compose.web.css.*

fun Modifier.background(background: Background) = styleModifier {
    background(background)
}

fun Modifier.background(vararg backgrounds: Background.Listable) = styleModifier {
    background(Background.list(*backgrounds))
}

fun Modifier.background(backgrounds: List<Background.Listable>) = background(*backgrounds.toTypedArray())

/**
 * Configure an element's background appearance.
 *
 * Background layers are specified in bottom-to-top order. Note that this is the *opposite* of how CSS does it, which
 * for this property expects a top-to-bottom order. However, we decided to deviate from the standard here for the
 * following reasons:
 *
 * * Everything else in HTML uses a bottom-to-top order (e.g. declaring elements on a page).
 * * This method accepts a color parameter first (in front of the vararg background layers), which renders on the bottom
 *   of everything else. This sets the expectation that "bottom" values come first.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/background">background</a>
 */
fun Modifier.background(color: CSSColorValue, vararg backgrounds: Background.Listable) = styleModifier {
    background(Background.list(color, *backgrounds))
}

fun Modifier.background(color: CSSColorValue, backgrounds: List<Background.Listable>) =
    background(color, *backgrounds.toTypedArray())

fun Modifier.backgroundAttachment(backgroundAttachment: BackgroundAttachment) = styleModifier {
    backgroundAttachment(backgroundAttachment)
}

fun Modifier.backgroundBlendMode(blendMode: BackgroundBlendMode) = styleModifier {
    backgroundBlendMode(blendMode)
}

fun Modifier.backgroundClip(backgroundClip: BackgroundClip) = styleModifier {
    backgroundClip(backgroundClip)
}

fun Modifier.backgroundColor(color: CSSColorValue) = styleModifier {
    backgroundColor(color)
}

fun Modifier.backgroundColor(backgroundColor: BackgroundColor) = styleModifier {
    backgroundColor(backgroundColor)
}

fun Modifier.backgroundImage(backgroundImage: BackgroundImage) = styleModifier {
    backgroundImage(backgroundImage)
}

fun Modifier.backgroundImage(url: CSSUrl) = styleModifier {
    backgroundImage(url)
}

fun Modifier.backgroundImage(gradient: Gradient) = styleModifier {
    backgroundImage(gradient)
}

fun Modifier.backgroundOrigin(backgroundOrigin: BackgroundOrigin) = styleModifier {
    backgroundOrigin(backgroundOrigin)
}

fun Modifier.backgroundPosition(backgroundPosition: BackgroundPosition) = styleModifier {
    backgroundPosition(backgroundPosition)
}

fun Modifier.backgroundRepeat(backgroundRepeat: BackgroundRepeat) = styleModifier {
    backgroundRepeat(backgroundRepeat)
}

fun Modifier.backgroundRepeat(
    horizontalRepeat: BackgroundRepeat.RepeatStyle,
    verticalRepeat: BackgroundRepeat.RepeatStyle
) = styleModifier {
    backgroundRepeat(horizontalRepeat, verticalRepeat)
}

fun Modifier.backgroundSize(backgroundSize: BackgroundSize) = styleModifier {
    backgroundSize(backgroundSize)
}
