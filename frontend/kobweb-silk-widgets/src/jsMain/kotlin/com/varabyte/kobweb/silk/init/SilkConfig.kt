package com.varabyte.kobweb.silk.init

import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.silk.components.animation.KeyframesBuilder
import com.varabyte.kobweb.silk.components.style.StyleModifiers
import com.varabyte.kobweb.silk.theme.colors.ColorMode

/**
 * Configuration values which are frozen at initialization time and accessed globally within Silk after that point.
 */
interface SilkConfig {
    var initialColorMode: ColorMode

    // TODO(#168): Remove in v1.0
    @Deprecated(message = "Calls to `ctx.config.registerStyle` should be changed to `ctx.stylesheet.registerStyle`")
    fun registerStyle(cssSelector: String, extraModifiers: Modifier = Modifier, init: StyleModifiers.() -> Unit)
    // TODO(#168): Remove in v1.0
    @Deprecated(message = "Calls to `ctx.config.registerKeyframes` should be changed to `ctx.stylesheet.registerKeyframes`")
    fun registerKeyframes(name: String, build: KeyframesBuilder.() -> Unit)
}

@Suppress("DeprecatedCallableAddReplaceWith")
internal object SilkConfigInstance : SilkConfig {
    override var initialColorMode = ColorMode.LIGHT

    // TODO(#168): Remove in v1.0
    @Deprecated(message = "Calls to `ctx.config.registerStyle` should be changed to `ctx.stylesheet.registerStyle`")
    override fun registerStyle(cssSelector: String, extraModifiers: Modifier, init: StyleModifiers.() -> Unit) {
        SilkStylesheetInstance.registerStyle(cssSelector, extraModifiers, init)
    }

    // TODO(#168): Remove in v1.0
    @Deprecated(message = "Calls to `ctx.config.registerKeyframes` should be changed to `ctx.stylesheet.registerKeyframes`")
    override fun registerKeyframes(name: String, build: KeyframesBuilder.() -> Unit) {
        SilkStylesheetInstance.registerKeyframes(name, build)
    }
}
