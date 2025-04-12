package com.varabyte.kobweb.compose.css

import com.varabyte.kobweb.compose.css.functions.CSSImage
import com.varabyte.kobweb.compose.css.functions.CSSUrl
import com.varabyte.kobweb.compose.css.functions.Gradient
import com.varabyte.kobweb.compose.css.global.GlobalStylePropertyValues
import com.varabyte.kobweb.compose.css.global.globalStyleValues
import org.jetbrains.compose.web.css.CSSColorValue
import org.jetbrains.compose.web.css.StylePropertyValue
import org.jetbrains.compose.web.css.StyleScope
import org.jetbrains.compose.web.css.backgroundAttachment
import org.jetbrains.compose.web.css.backgroundClip
import org.jetbrains.compose.web.css.backgroundImage
import org.jetbrains.compose.web.css.backgroundOrigin
import org.jetbrains.compose.web.css.backgroundPosition
import org.jetbrains.compose.web.css.backgroundRepeat
import org.jetbrains.compose.web.css.backgroundSize
import org.jetbrains.compose.web.css.keywords.CSSAutoKeyword

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/background-attachment
class BackgroundAttachment private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object {
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
typealias BackgroundBlendMode = MixBlendMode

fun StyleScope.backgroundBlendMode(blendMode: BackgroundBlendMode) {
    property("background-blend-mode", blendMode)
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/background-clip
class BackgroundClip private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object : GlobalStyleValues<BackgroundClip> by globalStyleValues(::BackgroundClip) {
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

    companion object : GlobalStyleValues<BackgroundColor> by globalStyleValues(::BackgroundColor) {
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

    companion object : GlobalStylePropertyValues<BackgroundOrigin> by globalStyleValues(::BackgroundOrigin){
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
sealed class BackgroundPosition private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    private class Keyword(value: String) : BackgroundPosition(value)
    private class Position(position: CSSPosition) : BackgroundPosition("$position")

    companion object : GlobalStylePropertyValues<BackgroundPosition> by globalStyleValues(::BackgroundPosition) {
        fun of(position: CSSPosition): BackgroundPosition = Position(position)
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

    companion object : GlobalStylePropertyValues<BackgroundRepeat> by globalStyleValues(::BackgroundRepeat) {
        fun of(horizontal: RepeatStyle, vertical: RepeatStyle): BackgroundRepeat =
            TwoValue(horizontal, vertical)

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

    companion object : GlobalStylePropertyValues<BackgroundSize> by globalStyleValues(::BackgroundSize) {
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

    // Note: Color is actually a separate property and intentionally not included here.
    // Note: blend mode *is* specified here but needs to be handled externally, since
    //   (probably for legacy reasons?) the `background` property does not accept it.
    class Repeatable internal constructor(
        val image: BackgroundImage?,
        val repeat: BackgroundRepeat?,
        val size: BackgroundSize?,
        val position: BackgroundPosition?,
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

    companion object : GlobalStyleValues<Background> by globalStyleValues(::Background){
        // Keyword
        val None: Background get() = Keyword("none")

        fun of(
            image: BackgroundImage? = null,
            repeat: BackgroundRepeat? = null,
            size: BackgroundSize? = null,
            position: BackgroundPosition? = null,
            blend: BackgroundBlendMode? = null,
            origin: BackgroundOrigin? = null,
            clip: BackgroundClip? = null,
            attachment: BackgroundAttachment? = null,
        ): Repeatable = Repeatable(image, repeat, size, position, blend, origin, clip, attachment)
    }
}


fun StyleScope.background(background: Background) {
    property("background", background)
}

fun StyleScope.background(vararg backgrounds: Background.Repeatable) {
    background(null, *backgrounds)
}

/**
 * A Kotlin-idiomatic API to configure the `background` CSS property.
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
fun StyleScope.background(color: CSSColorValue?, vararg backgrounds: Background.Repeatable) {
    if (color == null && backgrounds.isEmpty()) return

    // CSS order is backwards (IMO). We attempt to fix that in Kobweb.
    @Suppress("NAME_SHADOWING") val backgrounds = backgrounds.reversed()
    property("background", buildString {
        append(backgrounds.joinToString(", "))
        // backgrounds only allow you to specify a single color. If provided, it must be included with
        // the final layer.
        if (color != null) {
            if (this.isNotEmpty()) append(' ')
            append(color)
        }
    })
    val defaultBlendMode = BackgroundBlendMode.Normal
    val blendModes = backgrounds
        .map { it.blend ?: defaultBlendMode }
        // Use toString comparison because otherwise equality checks are against instance
        .takeIf { blendModes -> blendModes.any { it.toString() != defaultBlendMode.toString() } }
    if (blendModes != null) {
        property("background-blend-mode", blendModes.joinToString())
    }
}
