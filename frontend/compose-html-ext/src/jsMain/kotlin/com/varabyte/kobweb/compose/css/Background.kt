package com.varabyte.kobweb.compose.css

import com.varabyte.kobweb.compose.css.functions.CSSImage
import com.varabyte.kobweb.compose.css.functions.CSSUrl
import com.varabyte.kobweb.compose.css.functions.Gradient
import com.varabyte.kobweb.compose.css.functions.toImage
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.keywords.CSSAutoKeyword

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/background-attachment
class BackgroundAttachment private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object {
        // Keywords
        val Scroll get() = BackgroundAttachment("scroll")
        val Fixed get() = BackgroundAttachment("fixed")
        val Local get() = BackgroundAttachment("local")

        // Global values
        val Inherit get() = BackgroundAttachment("inherit")
        val Initial get() = BackgroundAttachment("initial")
        val Revert get() = BackgroundAttachment("revert")
        val Unset get() = BackgroundAttachment("unset")
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

    companion object {
        // Keywords
        val BorderBox get() = BackgroundClip("border-box")
        val PaddingBox get() = BackgroundClip("padding-box")
        val ContentBox get() = BackgroundClip("content-box")
        val Text get() = BackgroundClip("text")

        // Global values
        val Inherit get() = BackgroundClip("inherit")
        val Initial get() = BackgroundClip("initial")
        val Revert get() = BackgroundClip("revert")
        val Unset get() = BackgroundClip("unset")
    }
}

fun StyleScope.backgroundClip(backgroundClip: BackgroundClip) {
    backgroundClip(backgroundClip.toString())
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/background-color
class BackgroundColor private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object {
        // Keywords
        val CurrentColor get() = BackgroundColor("currentcolor")
        val Transparent get() = BackgroundColor("transparent")

        // Global values
        val Inherit get() = BackgroundColor("inherit")
        val Initial get() = BackgroundColor("initial")
        val Revert get() = BackgroundColor("revert")
        val Unset get() = BackgroundColor("unset")
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

// Provided for backwards compatibility. We've since refactored out a common CSSImage class, so use that instead.
@Deprecated(
    "Use Gradient.toImage() instead",
    ReplaceWith("this.toImage()", "com.varabyte.kobweb.compose.css.functions.toImage")
)
fun Gradient.toBackgroundImage() = this.toImage()

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/background-origin
class BackgroundOrigin private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object {
        // Keywords
        val BorderBox get() = BackgroundOrigin("border-box")
        val PaddingBox get() = BackgroundOrigin("padding-box")
        val ContentBox get() = BackgroundOrigin("content-box")

        // Global values
        val Inherit get() = BackgroundOrigin("inherit")
        val Initial get() = BackgroundOrigin("initial")
        val Revert get() = BackgroundOrigin("revert")
        val Unset get() = BackgroundOrigin("unset")
    }
}

fun StyleScope.backgroundOrigin(backgroundOrigin: BackgroundOrigin) {
    backgroundOrigin(backgroundOrigin.toString())
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/background-position
// Suppress: We only have deprecated methods calling deprecated methods, so no need to warn about them. They'll all get
// removed at the same time.
@Suppress("DeprecatedCallableAddReplaceWith", "DEPRECATION")
sealed class BackgroundPosition private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    private class Keyword(value: String) : BackgroundPosition(value)
    private class Position(position: CSSPosition) : BackgroundPosition("$position")

    // TODO(#168): Remove before v1.0, these were replaced by CSSPosition
    sealed class LegacyEdge(value: String) : BackgroundPosition(value)
    class LegacyEdgeX internal constructor(value: String) : LegacyEdge(value)
    class LegacyEdgeY internal constructor(value: String) : LegacyEdge(value)
    private class LegacyEdgeOffset(edge: LegacyEdge, offset: CSSLengthOrPercentageValue) :
        BackgroundPosition("$edge $offset")

    private class LegacyPosition(x: LegacyEdgeOffset, y: LegacyEdgeOffset) : BackgroundPosition("$x $y")

    companion object {
        @Deprecated("Use CSSPosition instead (e.g. BackgroundPosition.of(CSSPosition(Left, 50.px)))")
        fun of(xAnchor: LegacyEdgeX, x: CSSLengthOrPercentageValue): BackgroundPosition = LegacyEdgeOffset(xAnchor, x)

        @Deprecated("Use CSSPosition instead (e.g. BackgroundPosition.of(CSSPosition(Top, 20.percent)))")
        fun of(yAnchor: LegacyEdgeY, y: CSSLengthOrPercentageValue): BackgroundPosition = LegacyEdgeOffset(yAnchor, y)

        @Deprecated("Use CSSPosition instead (e.g. BackgroundPosition.of(CSSPosition(Left, 50.px, Top, 20.percent)))")
        fun of(
            xAnchor: LegacyEdgeX,
            x: CSSLengthOrPercentageValue,
            yAnchor: LegacyEdgeY,
            y: CSSLengthOrPercentageValue
        ): BackgroundPosition =
            LegacyPosition(LegacyEdgeOffset(xAnchor, x), LegacyEdgeOffset(yAnchor, y))

        @Deprecated("Use CSSPosition instead (e.g. BackgroundPosition.of(CSSPosition(50.px, 20.percent)))")
        fun of(x: CSSLengthOrPercentageValue, y: CSSLengthOrPercentageValue) = this.of(Left, x, Top, y)

        fun of(position: CSSPosition): BackgroundPosition = Position(position)

        // Edges
        @Deprecated("Use CSSPosition instead (e.g. CSSPosition.Top)")
        val Top get() = LegacyEdgeY("top")

        @Deprecated("Use CSSPosition instead (e.g. CSSPosition.Bottom)")
        val Bottom get() = LegacyEdgeY("bottom")

        @Deprecated("Use CSSPosition instead (e.g. CSSPosition.Left)")
        val Left get() = LegacyEdgeX("left")

        @Deprecated("Use CSSPosition instead (e.g. CSSPosition.Right)")
        val Right get() = LegacyEdgeX("right")

        // Keyword
        @Deprecated("Use CSSPosition instead (e.g. CSSPosition.Center)")
        val Center get(): BackgroundPosition = Keyword("center")

        // Global values
        val Inherit get(): BackgroundPosition = Keyword("inherit")
        val Initial get(): BackgroundPosition = Keyword("initial")
        val Revert get(): BackgroundPosition = Keyword("revert")
        val Unset get(): BackgroundPosition = Keyword("unset")
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

    companion object {
        fun of(horizontal: RepeatStyle, vertical: RepeatStyle): BackgroundRepeat = TwoValue(horizontal, vertical)

        // Keywords
        val RepeatX get(): BackgroundRepeat = Keyword("repeat-x")
        val RepeatY get(): BackgroundRepeat = Keyword("repeat-y")

        val Repeat get() = RepeatStyle("repeat")
        val Space get() = RepeatStyle("space")
        val Round get() = RepeatStyle("round")
        val NoRepeat get() = RepeatStyle("no-repeat")

        // Global values
        val Inherit get(): BackgroundRepeat = Keyword("inherit")
        val Initial get(): BackgroundRepeat = Keyword("initial")
        val Revert get(): BackgroundRepeat = Keyword("revert")
        val Unset get(): BackgroundRepeat = Keyword("unset")
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

    companion object {
        fun of(width: CSSLengthOrPercentageValue): BackgroundSize = Size("$width")
        fun of(width: CSSAutoKeyword): BackgroundSize = Size("$width")
        fun of(width: CSSLengthOrPercentageValue, height: CSSLengthOrPercentageValue): BackgroundSize =
            Size("$width $height")

        fun of(width: CSSAutoKeyword, height: CSSLengthOrPercentageValue): BackgroundSize = Size("$width $height")
        fun of(width: CSSLengthOrPercentageValue, height: CSSAutoKeyword): BackgroundSize = Size("$width $height")

        // Keywords
        val Cover get(): BackgroundSize = Keyword("cover")
        val Contain get(): BackgroundSize = Keyword("contain")

        // Global values
        val Inherit get(): BackgroundSize = Keyword("inherit")
        val Initial get(): BackgroundSize = Keyword("initial")
        val Revert get(): BackgroundSize = Keyword("revert")
        val Unset get(): BackgroundSize = Keyword("unset")
    }
}

fun StyleScope.backgroundSize(backgroundSize: BackgroundSize) {
    backgroundSize(backgroundSize.toString())
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/background
// Note: Color is actually a separate property and intentionally not included here.
// Note: blend mode *is* specified here but needs to be handled externally, since
//   (probably for legacy reasons?) the `background` property does not accept it.
data class CSSBackground(
    val image: BackgroundImage? = null,
    val repeat: BackgroundRepeat? = null,
    val size: BackgroundSize? = null,
    val position: BackgroundPosition? = null,
    val blend: BackgroundBlendMode? = null,
    val origin: BackgroundOrigin? = null,
    val clip: BackgroundClip? = null,
    val attachment: BackgroundAttachment? = null,
) : CSSStyleValue {
    override fun toString() = buildList {
        image?.let { add(it.toString()) }
        repeat?.let { add(it) }
        position?.let { add(it.toString()) }
        this@CSSBackground.size?.let {
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
}

fun StyleScope.background(vararg backgrounds: CSSBackground) {
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
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/background
 */
fun StyleScope.background(color: CSSColorValue?, vararg backgrounds: CSSBackground) {
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
