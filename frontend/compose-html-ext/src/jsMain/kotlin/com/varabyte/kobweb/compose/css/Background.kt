package com.varabyte.kobweb.compose.css

import com.varabyte.kobweb.compose.css.functions.CSSUrl
import com.varabyte.kobweb.compose.css.functions.Gradient
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.keywords.CSSAutoKeyword

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/background-attachment
class BackgroundAttachment private constructor(private val value: String): StylePropertyValue {
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
fun StyleScope.backgroundBlendMode(blendMode: MixBlendMode) {
    property("background-blend-mode", blendMode)
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/background-clip
class BackgroundClip private constructor(private val value: String): StylePropertyValue {
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
class BackgroundColor private constructor(private val value: String): StylePropertyValue {
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
sealed class BackgroundImage private constructor(private val value: String): StylePropertyValue {
    override fun toString() = value

    private class Keyword(value: String) : BackgroundImage(value)
    private class Url(url: CSSUrl) : BackgroundImage(url.toString())
    private class Gradient(gradient: com.varabyte.kobweb.compose.css.functions.Gradient) :
        BackgroundImage(gradient.toString())


    companion object {
        fun of(url: CSSUrl): BackgroundImage = Url(url)

        /**
         * See also: [com.varabyte.kobweb.compose.css.functions.Gradient.toBackgroundImage]
         */
        fun of(gradient: com.varabyte.kobweb.compose.css.functions.Gradient): BackgroundImage = Gradient(gradient)

        // Global values
        val Inherit get(): BackgroundImage = Keyword("inherit")
        val Initial get(): BackgroundImage = Keyword("initial")
        val Revert get(): BackgroundImage = Keyword("revert")
        val Unset get(): BackgroundImage = Keyword("unset")
    }
}

fun StyleScope.backgroundImage(backgroundImage: BackgroundImage) {
    backgroundImage(backgroundImage.toString())
}

// Convenience methods for common cases

fun StyleScope.backgroundImage(url: CSSUrl) = backgroundImage(BackgroundImage.of(url))
fun StyleScope.backgroundImage(gradient: Gradient) = backgroundImage(BackgroundImage.of(gradient))

/**
 * Convenience method for converting a gradient into a background image.
 *
 * As gradients often span multiple lines, using this method can help avoid some indentations.
 *
 * For example, the normal way:
 *
 * ```
 * BackgroundImage.of(
 *     radialGradient(/*...*/) {
 *         addColor(Colors.Red)
 *         addColor(Colors.Blue)
 *     }
 * )
 * ```
 *
 * And the same code using this helper method:
 *
 * ```
 * radialGradient(/*...*/) {
 *     addColor(Colors.Red)
 *     addColor(Colors.Blue)
 * }.toBackgroundImage()
 * ```
 */
fun Gradient.toBackgroundImage() = BackgroundImage.of(this)


// See: https://developer.mozilla.org/en-US/docs/Web/CSS/background-origin
class BackgroundOrigin private constructor(private val value: String): StylePropertyValue {
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
sealed class BackgroundPosition private constructor(private val value: String): StylePropertyValue {
    override fun toString() = value

    private class Keyword internal constructor(value: String) : BackgroundPosition(value)
    private class Position internal constructor(position: CSSPosition) : BackgroundPosition("$position")

    // TODO(#168): Remove before v1.0, these were replaced by CSSPosition
    sealed class LegacyEdge(value: String) : BackgroundPosition(value)
    class LegacyEdgeX internal constructor(value: String) : LegacyEdge(value)
    class LegacyEdgeY internal constructor(value: String) : LegacyEdge(value)
    private class LegacyEdgeOffset(edge: LegacyEdge? = null, offset: CSSLengthOrPercentageValue) : BackgroundPosition("$edge $offset")
    private class LegacyPosition(x: LegacyEdgeOffset, y: LegacyEdgeOffset) : BackgroundPosition("$x $y")

    companion object {
        @Deprecated("Use CSSPosition instead (e.g. BackgroundPosition.of(CSSPosition(Left, 50.px)))")
        fun of(xAnchor: LegacyEdgeX, x: CSSLengthOrPercentageValue): BackgroundPosition = LegacyEdgeOffset(xAnchor, x)
        @Deprecated("Use CSSPosition instead (e.g. BackgroundPosition.of(CSSPosition(Top, 20.percent)))")
        fun of(yAnchor: LegacyEdgeY, y: CSSLengthOrPercentageValue): BackgroundPosition = LegacyEdgeOffset(yAnchor, y)
        @Deprecated("Use CSSPosition instead (e.g. BackgroundPosition.of(CSSPosition(Left, 50.px, Top, 20.percent)))")
        fun of(xAnchor: LegacyEdgeX, x: CSSLengthOrPercentageValue, yAnchor: LegacyEdgeY, y: CSSLengthOrPercentageValue): BackgroundPosition =
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
sealed class BackgroundRepeat private constructor(private val value: String): StylePropertyValue {
    override fun toString() = value

    open class Keyword internal constructor(value: String) : BackgroundRepeat(value)
    class RepeatStyle internal constructor(value: String) : Keyword(value)

    companion object {
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
    backgroundRepeat("$horizontal $vertical")
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/background-size
sealed class BackgroundSize private constructor(private val value: String): StylePropertyValue {
    override fun toString() = value

    private class Keyword internal constructor(value: String) : BackgroundSize(value)
    private class Size internal constructor(value: String) : BackgroundSize(value)

    companion object {
        fun of(width: CSSLengthOrPercentageValue): BackgroundSize = Size("$width")
        fun of(width: CSSAutoKeyword): BackgroundSize = Size("$width")
        fun of(width: CSSLengthOrPercentageValue, height: CSSLengthOrPercentageValue): BackgroundSize = Size("$width $height")
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
data class CSSBackground(
    val image: BackgroundImage? = null,
    val color: CSSColorValue? = null,
    val repeat: BackgroundRepeat? = null,
    val position: BackgroundPosition? = null,
    val size: BackgroundSize? = null,
    val origin: BackgroundOrigin? = null,
    val clip: BackgroundClip? = null,
    val attachment: BackgroundAttachment? = null
) : CSSStyleValue {
    override fun toString() = buildList {
        image?.let { add(it.toString()) }
        color?.let { add(it.toString()) }
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
    if (backgrounds.isNotEmpty()) {
        property("background", backgrounds.joinToString(", "))
    }
}