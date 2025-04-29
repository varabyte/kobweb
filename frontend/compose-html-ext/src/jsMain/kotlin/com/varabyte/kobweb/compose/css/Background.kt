// Sealed class private constructors are useful, actually!
@file:Suppress("RedundantVisibilityModifier")

package com.varabyte.kobweb.compose.css

import com.varabyte.kobweb.compose.css.functions.CSSImage
import com.varabyte.kobweb.compose.css.functions.CSSUrl
import com.varabyte.kobweb.compose.css.functions.Gradient
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.keywords.CSSAutoKeyword

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/background-attachment
class BackgroundAttachment private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object : CssGlobalValues<BackgroundAttachment> {
        // Keywords
        val Scroll get() = BackgroundAttachment("scroll")
        val Fixed get() = BackgroundAttachment("fixed")
        val Local get() = BackgroundAttachment("local")
    }
}

fun StyleScope.backgroundAttachment(backgroundAttachment: BackgroundAttachment) {
    backgroundAttachment(backgroundAttachment.toString())
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/background-blend-mode
sealed class BackgroundBlendMode private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    class Listable(value: String) : BackgroundBlendMode(value)
    private class ValueList(values: List<Listable>) : BackgroundBlendMode(values.joinToString())

    companion object : CssBlendModeValues<Listable>, CssGlobalValues<BackgroundBlendMode> {
        fun list(vararg blendModes: Listable): BackgroundBlendMode = ValueList(blendModes.toList())

        override val Normal get() = Listable("normal")
        override val Multiply get() = Listable("multiply")
        override val Screen get() = Listable("screen")
        override val Overlay get() = Listable("overlay")
        override val Darken get() = Listable("darken")
        override val Lighten get() = Listable("lighten")
        override val ColorDodge get() = Listable("color-dodge")
        override val ColorBurn get() = Listable("color-burn")
        override val HardLight get() = Listable("hard-light")
        override val SoftLight get() = Listable("soft-light")
        override val Difference get() = Listable("difference")
        override val Exclusion get() = Listable("exclusion")
        override val Hue get() = Listable("hue")
        override val Saturation get() = Listable("saturation")
        override val Color get() = Listable("color")
        override val Luminosity get() = Listable("luminosity")
        override val PlusDarker get() = Listable("plus-darker")
        override val PlusLighter get() = Listable("plus-lighter")
    }
}

fun StyleScope.backgroundBlendMode(blendMode: BackgroundBlendMode) {
    property("background-blend-mode", blendMode)
}

// Needed temporarily until we can remove the deprecated `vararg` version
fun StyleScope.backgroundBlendMode(blendMode: BackgroundBlendMode.Listable) {
    backgroundBlendMode(blendMode as BackgroundBlendMode)
}
// Remove the previous method too after removing this method
@Deprecated("Use `backgroundBlendMode(BackgroundBlendMode.list(...))` instead.", ReplaceWith("backgroundBlendMode(BackgroundBlendMode.list(*blendModes))"))
fun StyleScope.backgroundBlendMode(vararg blendModes: BackgroundBlendMode.Listable) {
    backgroundBlendMode(BackgroundBlendMode.list(*blendModes))
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/background-clip
class BackgroundClip private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object : CssGlobalValues<BackgroundClip> {
        // Keywords
        val BorderBox get() = BackgroundClip("border-box")
        val PaddingBox get() = BackgroundClip("padding-box")
        val ContentBox get() = BackgroundClip("content-box")
        val Text get() = BackgroundClip("text")
    }
}

fun StyleScope.backgroundClip(backgroundClip: BackgroundClip) {
    backgroundClip(backgroundClip.toString())
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/background-color
class BackgroundColor private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object : CssGlobalValues<BackgroundColor> {
        // Keywords
        val CurrentColor get() = BackgroundColor("currentcolor")
        val Transparent get() = BackgroundColor("transparent")
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
class BackgroundOrigin private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object : CssGlobalValues<BackgroundOrigin> {
        // Keywords
        val BorderBox get() = BackgroundOrigin("border-box")
        val PaddingBox get() = BackgroundOrigin("padding-box")
        val ContentBox get() = BackgroundOrigin("content-box")
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
sealed class BackgroundRepeat private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    private class Keyword(value: String) : BackgroundRepeat(value)
    class RepeatStyle internal constructor(value: String) : BackgroundRepeat(value)
    private class TwoValue(horizontal: RepeatStyle, vertical: RepeatStyle) :
        BackgroundRepeat("$horizontal $vertical")

    companion object : CssGlobalValues<BackgroundRepeat> {
        fun of(horizontal: RepeatStyle, vertical: RepeatStyle): BackgroundRepeat = TwoValue(horizontal, vertical)

        // Keywords
        val RepeatX get(): BackgroundRepeat = Keyword("repeat-x")
        val RepeatY get(): BackgroundRepeat = Keyword("repeat-y")

        val Repeat get() = RepeatStyle("repeat")
        val Space get() = RepeatStyle("space")
        val Round get() = RepeatStyle("round")
        val NoRepeat get() = RepeatStyle("no-repeat")
    }
}

fun StyleScope.backgroundRepeat(backgroundRepeat: BackgroundRepeat) {
    backgroundRepeat(backgroundRepeat.toString())
}

fun StyleScope.backgroundRepeat(horizontal: BackgroundRepeat.RepeatStyle, vertical: BackgroundRepeat.RepeatStyle) {
    backgroundRepeat(BackgroundRepeat.of(horizontal, vertical))
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/background-size
sealed class BackgroundSize private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    private class Keyword(value: String) : BackgroundSize(value)
    private class Size(value: String) : BackgroundSize(value)

    companion object : CssGlobalValues<BackgroundSize> {
        fun of(width: CSSLengthOrPercentageNumericValue): BackgroundSize = Size("$width")
        fun of(width: CSSAutoKeyword): BackgroundSize = Size("$width")
        fun of(width: CSSLengthOrPercentageNumericValue, height: CSSLengthOrPercentageNumericValue): BackgroundSize =
            Size("$width $height")

        fun of(width: CSSAutoKeyword, height: CSSLengthOrPercentageNumericValue): BackgroundSize =
            Size("$width $height")

        fun of(width: CSSLengthOrPercentageNumericValue, height: CSSAutoKeyword): BackgroundSize =
            Size("$width $height")

        // Keywords
        val Cover get(): BackgroundSize = Keyword("cover")
        val Contain get(): BackgroundSize = Keyword("contain")
    }
}

fun StyleScope.backgroundSize(backgroundSize: BackgroundSize) {
    backgroundSize(backgroundSize.toString())
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/background
sealed class Background private constructor(private val value: String) : StylePropertyValue {
    override fun toString(): String = value

    private class Keyword(value: String) : Background(value)
    internal class ValueList(color: CSSColorValue?, internal val backgrounds: List<Listable>) : Background(
        buildString {
            if (color == null && backgrounds.isEmpty()) return@buildString
            // CSS order is backwards (IMO). We attempt to fix that in Kobweb.
            append(backgrounds.reversed().joinToString())
            // Backgrounds only allow you to specify a single color. If provided, it must be included with
            // the final layer.
            if (color != null) {
                if (this.isNotEmpty()) append(' ')
                append(color)
            }
        }
    )

    // Note: Color is actually a separate property and intentionally not included here.
    // Note: blend mode *is* specified here but needs to be handled externally, since
    //   (probably for legacy reasons?) the `background` property does not accept it.
    class Listable internal constructor(
        val image: BackgroundImage?,
        val repeat: BackgroundRepeat?,
        val size: BackgroundSize?,
        val position: BackgroundPosition?,
        @Deprecated("Due to technical limitations, we will be removing blend mode support from Background. Set the `backgroundBlendMode` property instead.")
        val blend: BackgroundBlendMode?, // See StyleScope.background for where this is used
        val origin: BackgroundOrigin?,
        val clip: BackgroundClip?,
        val attachment: BackgroundAttachment?,
    ) : Background(
        buildList {
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
        }.joinToString(" ")
    )

    companion object : CssGlobalValues<Background> {
        // Keyword
        val None: Background get() = Keyword("none")

        // NOTE: If you also want to support blending between images, see `BackgroundBlendMode`
        fun of(
            image: BackgroundImage? = null,
            repeat: BackgroundRepeat? = null,
            size: BackgroundSize? = null,
            position: BackgroundPosition? = null,
            origin: BackgroundOrigin? = null,
            clip: BackgroundClip? = null,
            attachment: BackgroundAttachment? = null,
        ): Listable = Listable(image, repeat, size, position, blend = null, origin, clip, attachment)

        @Deprecated("Unfortunately, we need to deprecate supporting `blend` in `Background`. It was a nice idea but we hit technical limitations. Instead, CSS offers a separate `backgroundBlendMode` property you should set directly.")
        fun of(
            image: BackgroundImage? = null,
            repeat: BackgroundRepeat? = null,
            size: BackgroundSize? = null,
            position: BackgroundPosition? = null,
            blend: BackgroundBlendMode?,
            origin: BackgroundOrigin? = null,
            clip: BackgroundClip? = null,
            attachment: BackgroundAttachment? = null,
        ): Listable = Listable(image, repeat, size, position, blend, origin, clip, attachment)

        fun list(vararg backgrounds: Listable): Background = ValueList(null, backgrounds.toList())

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
        fun list(color: CSSColorValue, vararg backgrounds: Listable): Background = ValueList(color, backgrounds.toList())
    }
}


@Suppress("DEPRECATION") // We can remove this after `blend` goes away
fun StyleScope.background(background: Background) {
    property("background", background)
    when (background) {
        is Background.Listable -> if (background.blend != null) {
            property("background-blend-mode", background.blend)
        }
        is Background.ValueList -> {
            val defaultBlendMode = BackgroundBlendMode.Normal
            val blendModes = background.backgrounds
                .map { it.blend ?: defaultBlendMode }
                // Use toString comparison because otherwise equality checks are against instance
                .takeIf { blendModes -> blendModes.any { it.toString() != defaultBlendMode.toString() } }
            if (blendModes != null) {
                property("background-blend-mode", blendModes.joinToString())
            }
        }
        else -> {} // No other types of background implementations have a blend mode
    }
}

// Needed temporarily until we can remove the deprecated `vararg` version
fun StyleScope.background(background: Background.Listable) {
    background(background as Background)
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
