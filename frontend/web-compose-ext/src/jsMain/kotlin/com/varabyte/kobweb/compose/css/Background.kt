package com.varabyte.kobweb.compose.css

import com.varabyte.kobweb.compose.css.functions.CSSUrl
import com.varabyte.kobweb.compose.css.functions.LinearGradient
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
        val RevertLayer get() = BackgroundAttachment("revert")
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
        val RevertLayer get() = BackgroundClip("revert")
        val Unset get() = BackgroundClip("unset")
    }
}

fun StyleScope.backgroundClip(backgroundClip: BackgroundClip) {
    backgroundClip(backgroundClip.toString())
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/background-image
sealed class BackgroundImage private constructor(private val value: String): StylePropertyValue {
    override fun toString() = value

    private class Keyword(value: String) : BackgroundImage(value)
    private class Url(url: CSSUrl) : BackgroundImage(url.toString())
    private class LinearGradient(linearGradient: com.varabyte.kobweb.compose.css.functions.LinearGradient) :
        BackgroundImage(linearGradient.toString())


    companion object {
        fun of(url: CSSUrl): BackgroundImage = Url(url)
        fun of(gradient: com.varabyte.kobweb.compose.css.functions.LinearGradient): BackgroundImage = LinearGradient(gradient)

        // Global values
        val Inherit get(): BackgroundImage = Keyword("inherit")
        val Initial get(): BackgroundImage = Keyword("initial")
        val Revert get(): BackgroundImage = Keyword("revert")
        val RevertLayer get(): BackgroundImage = Keyword("revert")
        val Unset get(): BackgroundImage = Keyword("unset")
    }
}

fun StyleScope.backgroundImage(backgroundImage: BackgroundImage) {
    backgroundImage(backgroundImage.toString())
}

// Convenience methods for common cases

fun StyleScope.backgroundImage(url: CSSUrl) = backgroundImage(BackgroundImage.of(url))
fun StyleScope.backgroundImage(linearGradient: LinearGradient) = backgroundImage(BackgroundImage.of(linearGradient))

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
        val RevertLayer get() = BackgroundOrigin("revert")
        val Unset get() = BackgroundOrigin("unset")
    }
}

fun StyleScope.backgroundOrigin(backgroundOrigin: BackgroundOrigin) {
    backgroundOrigin(backgroundOrigin.toString())
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/background-position
sealed class BackgroundPosition(private val value: String): StylePropertyValue {
    override fun toString() = value

    class Keyword internal constructor(value: String) : BackgroundPosition(value)
    sealed class Edge(value: String) : BackgroundPosition(value)
    class EdgeX internal constructor(value: String) : Edge(value)
    class EdgeY internal constructor(value: String) : Edge(value)
    class EdgeOffset internal constructor(edge: Edge? = null, offset: CSSLengthOrPercentageValue) : BackgroundPosition("$edge $offset")
    class Position internal constructor(x: EdgeOffset, y: EdgeOffset) : BackgroundPosition("$x $y")

    companion object {
        fun of(xAnchor: EdgeX, x: CSSLengthOrPercentageValue) = EdgeOffset(xAnchor, x)
        fun of(yAnchor: EdgeY, y: CSSLengthOrPercentageValue) = EdgeOffset(yAnchor, y)
        fun of(xAnchor: EdgeX, x: CSSLengthOrPercentageValue, yAnchor: EdgeY, y: CSSLengthOrPercentageValue) =
            Position(EdgeOffset(xAnchor, x), EdgeOffset(yAnchor, y))
        fun of(x: CSSLengthOrPercentageValue, y: CSSLengthOrPercentageValue) = this.of(Left, x, Top, y)

        // Edges
        val Top get() = EdgeY("top")
        val Bottom get() = EdgeY("bottom")
        val Left get() = EdgeX("left")
        val Right get() = EdgeX("right")

        // Keyword
        val Center get() = Keyword("center")

        // Global values
        val Inherit get() = Keyword("inherit")
        val Initial get() = Keyword("initial")
        val Revert get() = Keyword("revert")
        val RevertLayer get() = Keyword("revert")
        val Unset get() = Keyword("unset")
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
        val RepeatX get() = Keyword("repeat-x")
        val RepeatY get() = Keyword("repeat-y")

        val Repeat get() = RepeatStyle("repeat")
        val Space get() = RepeatStyle("space")
        val Round get() = RepeatStyle("round")
        val NoRepeat get() = RepeatStyle("no-repeat")

        // Global values
        val Inherit get() = Keyword("inherit")
        val Initial get() = Keyword("initial")
        val Revert get() = Keyword("revert")
        val RevertLayer get() = Keyword("revert")
        val Unset get() = Keyword("unset")
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
        val RevertLayer get(): BackgroundSize = Keyword("revert")
        val Unset get(): BackgroundSize = Keyword("unset")
    }
}

fun StyleScope.backgroundSize(backgroundSize: BackgroundSize) {
    backgroundSize(backgroundSize.toString())
}
