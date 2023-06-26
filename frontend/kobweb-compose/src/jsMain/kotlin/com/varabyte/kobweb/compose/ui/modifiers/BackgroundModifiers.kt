package com.varabyte.kobweb.compose.ui.modifiers

import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.css.functions.CSSUrl
import com.varabyte.kobweb.compose.css.functions.Gradient
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.styleModifier
import org.jetbrains.compose.web.css.*

fun Modifier.background(vararg backgrounds: CSSBackground) = styleModifier {
    background(*backgrounds)
}

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
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/background
 */
fun Modifier.background(color: CSSColorValue?, vararg backgrounds: CSSBackground) = styleModifier {
    background(color, *backgrounds)
}

// TODO(#168): Remove before v1.0
@Deprecated(
    "All stringly-typed Kobweb Modifiers will be removed before v1.0. Use a richly-typed version or use styleModifier as a fallback.",
    ReplaceWith(
        "styleModifier { background(value) }",
        "com.varabyte.kobweb.compose.ui.styleModifier",
        "org.jetbrains.compose.web.css.background"
    ),
)
fun Modifier.background(value: String) = styleModifier {
    background(value)
}

fun Modifier.backgroundAttachment(backgroundAttachment: BackgroundAttachment) = styleModifier {
    backgroundAttachment(backgroundAttachment)
}

// TODO(#168): Remove before v1.0
@Deprecated(
    "All stringly-typed Kobweb Modifiers will be removed before v1.0. Use a richly-typed version or use styleModifier as a fallback.",
    ReplaceWith(
        "styleModifier { backgroundAttachment(value) }",
        "com.varabyte.kobweb.compose.ui.styleModifier",
        "org.jetbrains.compose.web.css.backgroundAttachment"
    ),
)
fun Modifier.backgroundAttachment(value: String) = styleModifier {
    backgroundAttachment(value)
}

fun Modifier.backgroundBlendMode(blendMode: BackgroundBlendMode) = styleModifier {
    backgroundBlendMode(blendMode)
}

fun Modifier.backgroundClip(backgroundClip: BackgroundClip) = styleModifier {
    backgroundClip(backgroundClip)
}

// TODO(#168): Remove before v1.0
@Deprecated(
    "All stringly-typed Kobweb Modifiers will be removed before v1.0. Use a richly-typed version or use styleModifier as a fallback.",
    ReplaceWith(
        "styleModifier { backgroundClip(value) }",
        "com.varabyte.kobweb.compose.ui.styleModifier",
        "org.jetbrains.compose.web.css.backgroundClip"
    ),
)
fun Modifier.backgroundClip(value: String) = styleModifier {
    backgroundClip(value)
}

// TODO(#168): Remove before v1.0
@Deprecated(
    "All stringly-typed Kobweb Modifiers will be removed before v1.0. Use a richly-typed version or use styleModifier as a fallback.",
    ReplaceWith(
        "styleModifier { property(\"background-color\", value) }",
        "com.varabyte.kobweb.compose.ui.styleModifier"
    ),
)
fun Modifier.backgroundColor(value: String) = styleModifier {
    property("background-color", value)
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

// TODO(#168): Remove before v1.0
@Deprecated(
    "All stringly-typed Kobweb Modifiers will be removed before v1.0. Use a richly-typed version or use styleModifier as a fallback.",
    ReplaceWith(
        "styleModifier { backgroundImage(value) }",
        "com.varabyte.kobweb.compose.ui.styleModifier",
        "org.jetbrains.compose.web.css.backgroundImage"
    ),
)
fun Modifier.backgroundImage(value: String) = styleModifier {
    backgroundImage(value)
}

fun Modifier.backgroundOrigin(backgroundOrigin: BackgroundOrigin) = styleModifier {
    backgroundOrigin(backgroundOrigin)
}

// TODO(#168): Remove before v1.0
@Deprecated(
    "All stringly-typed Kobweb Modifiers will be removed before v1.0. Use a richly-typed version or use styleModifier as a fallback.",
    ReplaceWith(
        "styleModifier { backgroundOrigin(value) }",
        "com.varabyte.kobweb.compose.ui.styleModifier",
        "org.jetbrains.compose.web.css.backgroundOrigin"
    ),
)
fun Modifier.backgroundOrigin(value: String) = styleModifier {
    backgroundOrigin(value)
}

fun Modifier.backgroundPosition(backgroundPosition: BackgroundPosition) = styleModifier {
    backgroundPosition(backgroundPosition)
}

// TODO(#168): Remove before v1.0
@Deprecated(
    "All stringly-typed Kobweb Modifiers will be removed before v1.0. Use a richly-typed version or use styleModifier as a fallback.",
    ReplaceWith(
        "styleModifier { backgroundPosition(value) }",
        "com.varabyte.kobweb.compose.ui.styleModifier",
        "org.jetbrains.compose.web.css.backgroundPosition"
    ),
)
fun Modifier.backgroundPosition(value: String) = styleModifier {
    backgroundPosition(value)
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

// TODO(#168): Remove before v1.0
@Deprecated(
    "All stringly-typed Kobweb Modifiers will be removed before v1.0. Use a richly-typed version or use styleModifier as a fallback.",
    ReplaceWith(
        "styleModifier { backgroundRepeat(value) }",
        "com.varabyte.kobweb.compose.ui.styleModifier",
        "org.jetbrains.compose.web.css.backgroundRepeat"
    ),
)
fun Modifier.backgroundRepeat(value: String) = styleModifier {
    backgroundRepeat(value)
}

fun Modifier.backgroundSize(backgroundSize: BackgroundSize) = styleModifier {
    backgroundSize(backgroundSize)
}

// TODO(#168): Remove before v1.0
@Deprecated(
    "All stringly-typed Kobweb Modifiers will be removed before v1.0. Use a richly-typed version or use styleModifier as a fallback.",
    ReplaceWith(
        "styleModifier { backgroundSize(value) }",
        "com.varabyte.kobweb.compose.ui.styleModifier",
        "org.jetbrains.compose.web.css.backgroundSize"
    ),
)
fun Modifier.backgroundSize(value: String) = styleModifier {
    backgroundSize(value)
}
