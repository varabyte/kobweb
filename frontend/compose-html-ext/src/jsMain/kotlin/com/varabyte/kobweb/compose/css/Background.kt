// Sealed class private constructors are useful, actually!
@file:Suppress("RedundantVisibilityModifier")

package com.varabyte.kobweb.compose.css

import com.varabyte.kobweb.compose.css.functions.CSSImage
import com.varabyte.kobweb.compose.css.functions.CSSUrl
import com.varabyte.kobweb.compose.css.functions.Gradient
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.keywords.CSSAutoKeyword

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/background-attachment
sealed interface BackgroundAttachment : StylePropertyValue {
    companion object : CssGlobalValues<BackgroundAttachment> {
        // Keywords
        val Scroll get() = "scroll".unsafeCast<BackgroundAttachment>()
        val Fixed get() = "fixed".unsafeCast<BackgroundAttachment>()
        val Local get() = "local".unsafeCast<BackgroundAttachment>()
    }
}

fun StyleScope.backgroundAttachment(backgroundAttachment: BackgroundAttachment) {
    backgroundAttachment(backgroundAttachment.toString())
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/background-blend-mode
sealed interface BackgroundBlendMode : StylePropertyValue {
    sealed interface Listable : BackgroundBlendMode

    companion object : CssBlendModeValues<Listable>, CssGlobalValues<BackgroundBlendMode> {
        fun list(vararg blendModes: Listable): BackgroundBlendMode = blendModes.toList().joinToString().unsafeCast<BackgroundBlendMode>()

        override val Normal get() = "normal".unsafeCast<Listable>()
        override val Multiply get() = "multiply".unsafeCast<Listable>()
        override val Screen get() = "screen".unsafeCast<Listable>()
        override val Overlay get() = "overlay".unsafeCast<Listable>()
        override val Darken get() = "darken".unsafeCast<Listable>()
        override val Lighten get() = "lighten".unsafeCast<Listable>()
        override val ColorDodge get() = "color-dodge".unsafeCast<Listable>()
        override val ColorBurn get() = "color-burn".unsafeCast<Listable>()
        override val HardLight get() = "hard-light".unsafeCast<Listable>()
        override val SoftLight get() = "soft-light".unsafeCast<Listable>()
        override val Difference get() = "difference".unsafeCast<Listable>()
        override val Exclusion get() = "exclusion".unsafeCast<Listable>()
        override val Hue get() = "hue".unsafeCast<Listable>()
        override val Saturation get() = "saturation".unsafeCast<Listable>()
        override val Color get() = "color".unsafeCast<Listable>()
        override val Luminosity get() = "luminosity".unsafeCast<Listable>()
        override val PlusDarker get() = "plus-darker".unsafeCast<Listable>()
        override val PlusLighter get() = "plus-lighter".unsafeCast<Listable>()
    }
}

fun StyleScope.backgroundBlendMode(blendMode: BackgroundBlendMode) {
    property("background-blend-mode", blendMode)
}

// Needed temporarily until we can remove the deprecated `vararg` version
fun StyleScope.backgroundBlendMode(blendMode: BackgroundBlendMode.Listable) {
    backgroundBlendMode(blendMode.unsafeCast<BackgroundBlendMode>())
}
// Remove the previous method too after removing this method
@Deprecated("Use `backgroundBlendMode(BackgroundBlendMode.list(...))` instead.", ReplaceWith("backgroundBlendMode(BackgroundBlendMode.list(*blendModes))"))
fun StyleScope.backgroundBlendMode(vararg blendModes: BackgroundBlendMode.Listable) {
    backgroundBlendMode(BackgroundBlendMode.list(*blendModes))
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/background-clip
sealed interface BackgroundClip : StylePropertyValue {
    companion object : CssGlobalValues<BackgroundClip> {
        // Keywords
        val BorderBox get() = "border-box".unsafeCast<BackgroundClip>()
        val PaddingBox get() = "padding-box".unsafeCast<BackgroundClip>()
        val ContentBox get() = "content-box".unsafeCast<BackgroundClip>()
        val Text get() = "text".unsafeCast<BackgroundClip>()
    }
}

fun StyleScope.backgroundClip(backgroundClip: BackgroundClip) {
    backgroundClip(backgroundClip.toString())
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/background-color
sealed interface BackgroundColor : StylePropertyValue {
    companion object : CssGlobalValues<BackgroundColor> {
        // Keywords
        val CurrentColor get() = "currentcolor".unsafeCast<BackgroundColor>()
        val Transparent get() = "transparent".unsafeCast<BackgroundColor>()
    }
}

fun StyleScope.backgroundColor(backgroundColor: BackgroundColor) {
    property("background-color", backgroundColor)
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/background-image
typealias BackgroundImage = CSSImage

fun StyleScope.backgroundImage(backgroundImage: BackgroundImage) {
    backgroundImage(backgroundImage.toString())
}

// Convenience methods for common cases

fun StyleScope.backgroundImage(url: CSSUrl) = backgroundImage(BackgroundImage.of(url))
fun StyleScope.backgroundImage(gradient: Gradient) = backgroundImage(BackgroundImage.of(gradient))

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/background-origin
sealed interface BackgroundOrigin : StylePropertyValue {
    companion object : CssGlobalValues<BackgroundOrigin> {
        // Keywords
        val BorderBox get() = "border-box".unsafeCast<BackgroundOrigin>()
        val PaddingBox get() = "padding-box".unsafeCast<BackgroundOrigin>()
        val ContentBox get() = "content-box".unsafeCast<BackgroundOrigin>()
    }
}
fun StyleScope.backgroundOrigin(backgroundOrigin: BackgroundOrigin) {
    backgroundOrigin(backgroundOrigin.toString())
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/background-position
class BackgroundPosition private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object : CssGlobalValues<BackgroundPosition> {
        fun of(position: CSSPosition): BackgroundPosition = BackgroundPosition("$position")
    }
}

fun StyleScope.backgroundPosition(backgroundPosition: BackgroundPosition) {
    backgroundPosition(backgroundPosition.toString())
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/background-repeat
sealed interface BackgroundRepeat : StylePropertyValue {
    sealed interface Mode : BackgroundRepeat

    companion object : CssGlobalValues<BackgroundRepeat> {
        fun of(horizontal: Mode, vertical: Mode): BackgroundRepeat =
            "$horizontal $vertical".unsafeCast<BackgroundRepeat>()

        // Keywords
        val RepeatX get() = "repeat-x".unsafeCast<BackgroundRepeat>()
        val RepeatY get() = "repeat-y".unsafeCast<BackgroundRepeat>()

        val Repeat get() = "repeat".unsafeCast<Mode>()
        val Space get() = "space".unsafeCast<Mode>()
        val Round get() = "round".unsafeCast<Mode>()
        val NoRepeat get() = "no-repeat".unsafeCast<Mode>()
    }
}
fun StyleScope.backgroundRepeat(backgroundRepeat: BackgroundRepeat) {
    backgroundRepeat(backgroundRepeat.toString())
}

fun StyleScope.backgroundRepeat(horizontal: BackgroundRepeat.Mode, vertical: BackgroundRepeat.Mode) {
    backgroundRepeat(BackgroundRepeat.of(horizontal, vertical))
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/background-size
sealed interface BackgroundSize : StylePropertyValue {
    companion object : CssGlobalValues<BackgroundSize> {
        fun of(width: CSSLengthOrPercentageNumericValue): BackgroundSize = "$width".unsafeCast<BackgroundSize>()
        fun of(width: CSSAutoKeyword): BackgroundSize = "$width".unsafeCast<BackgroundSize>()
        fun of(width: CSSLengthOrPercentageNumericValue, height: CSSLengthOrPercentageNumericValue): BackgroundSize =
            "$width $height".unsafeCast<BackgroundSize>()

        fun of(width: CSSAutoKeyword, height: CSSLengthOrPercentageNumericValue): BackgroundSize =
            "$width $height".unsafeCast<BackgroundSize>()

        fun of(width: CSSLengthOrPercentageNumericValue, height: CSSAutoKeyword): BackgroundSize =
            "$width $height".unsafeCast<BackgroundSize>()

        // Keywords
        val Cover get() = "cover".unsafeCast<BackgroundSize>()
        val Contain get() = "contain".unsafeCast<BackgroundSize>()
    }
}
fun StyleScope.backgroundSize(backgroundSize: BackgroundSize) {
    backgroundSize(backgroundSize.toString())
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/background
sealed interface Background : StylePropertyValue {
    sealed interface Listable

    companion object : CssGlobalValues<Background> {
        // Keyword
        val None get() = "none".unsafeCast<Background>()

        // NOTE: If you also want to support blending between images, see `BackgroundBlendMode`
        fun of(
            image: BackgroundImage? = null,
            repeat: BackgroundRepeat? = null,
            size: BackgroundSize? = null,
            position: BackgroundPosition? = null,
            origin: BackgroundOrigin? = null,
            clip: BackgroundClip? = null,
            attachment: BackgroundAttachment? = null,
        ) = buildList {
            image?.let { add(it.toString()) }
            repeat?.let { add(it) }
            position?.let { add(it.toString()) }
            size?.let {
                // Size must ALWAYS follow position with a slash
                // See: https://developer.mozilla.org/en-US/docs/Web/CSS/background#syntax
                if (position == null) add(BackgroundPosition.of(CSSPosition.TopLeft))
                add("/")
                add(it.toString())
            }
            origin?.let {
                add(it)
                // See: https://developer.mozilla.org/en-US/docs/Web/CSS/background#values
                if (clip == null) add(BackgroundClip.BorderBox.toString())
            }
            clip?.let {
                // See: https://developer.mozilla.org/en-US/docs/Web/CSS/background#values
                if (origin == null) add(BackgroundOrigin.PaddingBox.toString())
                add(it)
            }
            attachment?.let { add(it) }
        }.joinToString(" ").unsafeCast<Listable>()

        @Deprecated(
            "Unfortunately, we need to deprecate supporting `blend` in `Background`. Moving forward, the value is ignored. It was a nice idea but we hit technical limitations. Instead, CSS offers a separate `backgroundBlendMode` property you should set directly.",
            level = DeprecationLevel.ERROR
        )
        fun of(
            image: BackgroundImage? = null,
            repeat: BackgroundRepeat? = null,
            size: BackgroundSize? = null,
            position: BackgroundPosition? = null,
            blend: BackgroundBlendMode?,
            origin: BackgroundOrigin? = null,
            clip: BackgroundClip? = null,
            attachment: BackgroundAttachment? = null,
        ): Listable = of(image, repeat, size, position, origin, clip, attachment)

        @Suppress("FunctionName")
        private fun _list(color: CSSColorValue?, backgrounds: List<Listable>) = buildString {
            if (color == null && backgrounds.isEmpty()) return@buildString
            // CSS order is backwards (IMO). We attempt to fix that in Kobweb.
            append(backgrounds.reversed().joinToString())
            // Backgrounds only allow you to specify a single color. If provided, it must be included with
            // the final layer.
            if (color != null) {
                if (this.isNotEmpty()) append(' ')
                append(color)
            }
        }.unsafeCast<Background>()

        fun list(vararg backgrounds: Listable) = _list(null, backgrounds.toList())

        /**
         * A Kotlin-idiomatic API to configure the `background` CSS property with multiple backgrounds.
         *
         * Background layers are specified in bottom-to-top order. Note that this is the *opposite* of how CSS does it,
         * which for this property expects a top-to-bottom order. However, we decided to deviate from the standard here for
         * the following reasons:
         *
         * * Everything else in HTML uses a bottom-to-top order (e.g. declaring elements on a page).
         * * This method accepts a color parameter first (in front of the vararg background layers), which renders on the bottom
         *   of everything else. This sets the expectation that "bottom" values come first.
         *
         * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/background">background</a>
         */
        fun list(color: CSSColorValue, vararg backgrounds: Listable) = _list(color, backgrounds.toList())
    }
}

fun StyleScope.background(background: Background) {
    property("background", background)
}

// Needed temporarily until we can remove the deprecated `vararg` version
fun StyleScope.background(background: Background.Listable) {
    background(background.unsafeCast<Background>())
}
// Remove the previous method too after removing this method
@Deprecated("Use `background(Background.list(...))` instead.", ReplaceWith("background(Background.list(*backgrounds))"))
fun StyleScope.background(vararg backgrounds: Background.Listable) {
    background(Background.list(*backgrounds))
}

@Deprecated("Use `background(Background.list(...))` instead.", ReplaceWith("background(Background.list(color, *backgrounds))"))
fun StyleScope.background(color: CSSColorValue, vararg backgrounds: Background.Listable) {
    background(Background.list(color, *backgrounds))
}
