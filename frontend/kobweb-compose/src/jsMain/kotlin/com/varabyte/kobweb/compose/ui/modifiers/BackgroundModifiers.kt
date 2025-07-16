package com.varabyte.kobweb.compose.ui.modifiers

import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.css.functions.CSSUrl
import com.varabyte.kobweb.compose.css.functions.Gradient
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.styleModifier
import org.jetbrains.compose.web.css.*

@Suppress("FunctionName") // leading underscore to emphasize the internal nature of the method
class BackgroundPositionScope internal constructor(private val styleScope: StyleScope) {
    private fun _x(value: StylePropertyValue) = styleScope.property("background-position-x", value)
    private fun _y(value: StylePropertyValue) = styleScope.property("background-position-y", value)

    fun x(value: CSSLengthOrPercentageNumericValue) = _x(value)
    fun x(value: EdgeXOrCenter) = _x(value)
    fun x(value: EdgeXOffset) = _x(value)
    fun y(value: CSSLengthOrPercentageNumericValue) = _y(value)
    fun y(value: EdgeYOrCenter) = _y(value)
    fun y(value: EdgeYOffset) = _y(value)
}

class BackgroundScope internal constructor(private val styleScope: StyleScope) {
    fun attachment(backgroundAttachment: BackgroundAttachment) = styleScope.backgroundAttachment(backgroundAttachment)
    fun clip(backgroundClip: BackgroundClip) = styleScope.backgroundClip(backgroundClip)
    fun color(color: CSSColorValue) = styleScope.backgroundColor(color)
    fun color(backgroundColor: BackgroundColor) = styleScope.backgroundColor(backgroundColor)
    fun image(backgroundImage: BackgroundImage) = styleScope.backgroundImage(backgroundImage)
    fun image(url: CSSUrl) = styleScope.backgroundImage(url)
    fun image(gradient: Gradient) = styleScope.backgroundImage(gradient)
    fun origin(backgroundOrigin: BackgroundOrigin) = styleScope.backgroundOrigin(backgroundOrigin)
    fun position(backgroundPosition: BackgroundPosition) = styleScope.backgroundPosition(backgroundPosition)
    fun position(position: CSSPosition) = styleScope.backgroundPosition(BackgroundPosition.of(position))
    fun position(scope: BackgroundPositionScope.() -> Unit) = BackgroundPositionScope(styleScope).scope()
    fun repeat(backgroundRepeat: BackgroundRepeat) = styleScope.backgroundRepeat(backgroundRepeat)
    fun repeat(horizontalRepeat: BackgroundRepeat.Mode, verticalRepeat: BackgroundRepeat.Mode) =
        styleScope.backgroundRepeat(horizontalRepeat, verticalRepeat)
    fun size(backgroundSize: BackgroundSize) = styleScope.backgroundSize(backgroundSize)
}

fun Modifier.background(scope: BackgroundScope.() -> Unit) = styleModifier {
    BackgroundScope(this).apply(scope)
}

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

@Deprecated("Use `Modifier.background { attachment(...) }` instead.", ReplaceWith("background { attachment(backgroundAttachment) }"))
fun Modifier.backgroundAttachment(backgroundAttachment: BackgroundAttachment) =
    background { attachment(backgroundAttachment) }

fun Modifier.backgroundBlendMode(blendMode: BackgroundBlendMode) = styleModifier {
    backgroundBlendMode(blendMode)
}

fun Modifier.backgroundBlendMode(vararg blendModes: BackgroundBlendMode.Listable) =
    backgroundBlendMode(BackgroundBlendMode.list(*blendModes))

fun Modifier.backgroundBlendMode(blendModes: List<BackgroundBlendMode.Listable>) =
    backgroundBlendMode(BackgroundBlendMode.list(*blendModes.toTypedArray()))

@Deprecated("Use `Modifier.background { clip(...) }` instead.", ReplaceWith("background { clip(backgroundClip) }"))
fun Modifier.backgroundClip(backgroundClip: BackgroundClip) = background { clip(backgroundClip) }

fun Modifier.backgroundColor(color: CSSColorValue) = background { color(color) }
fun Modifier.backgroundColor(backgroundColor: BackgroundColor) = background { color(backgroundColor) }

fun Modifier.backgroundImage(backgroundImage: BackgroundImage) = background { image(backgroundImage) }
fun Modifier.backgroundImage(url: CSSUrl) = background { image(url) }
fun Modifier.backgroundImage(gradient: Gradient) = background { image(gradient) }

@Deprecated("Use `Modifier.background { origin(...) }` instead.", ReplaceWith("background { origin(backgroundOrigin) }"))
fun Modifier.backgroundOrigin(backgroundOrigin: BackgroundOrigin) = background { origin(backgroundOrigin) }

@Deprecated("Use `Modifier.background { position(...) }` instead.", ReplaceWith("background { position(backgroundPosition) }"))
fun Modifier.backgroundPosition(backgroundPosition: BackgroundPosition) = background { position(backgroundPosition) }

@Deprecated("Use `Modifier.background { repeat(...) }` instead.", ReplaceWith("background { repeat(backgroundRepeat) }"))
fun Modifier.backgroundRepeat(backgroundRepeat: BackgroundRepeat) = background { repeat(backgroundRepeat) }

@Deprecated("Use `Modifier.background { repeat(...) }` instead.", ReplaceWith("background { repeat(horizontalRepeat, verticalRepeat) }"))
fun Modifier.backgroundRepeat(
    horizontalRepeat: BackgroundRepeat.Mode,
    verticalRepeat: BackgroundRepeat.Mode
) = background { repeat(horizontalRepeat, verticalRepeat) }

@Deprecated("Use `Modifier.background { size(...) }` instead.", ReplaceWith("background { size(backgroundSize) }"))
fun Modifier.backgroundSize(backgroundSize: BackgroundSize) = background { size(backgroundSize) }
