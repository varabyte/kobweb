package com.varabyte.kobweb.silk.style.animation

import androidx.compose.runtime.*
import com.varabyte.kobweb.browser.dom.css.CssIdent
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.toStyles
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.CssStyleScopeBase
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import com.varabyte.kobweb.silk.theme.colors.suffixedWith
import com.varabyte.kobweb.silk.theme.name
import org.jetbrains.compose.web.css.*

class KeyframesBuilder internal constructor(override val colorMode: ColorMode) : CssStyleScopeBase {
    private val keyframeStyles = mutableMapOf<CSSKeyframe, Modifier>()

    private val comparableKeyframeStyles
        get() = keyframeStyles.mapValues { (_, modifier) ->
            ComparableStyleScope().apply {
                modifier.toStyles().invoke(this)
            }
        }

    /** Describe the style of the element when this animation starts. */
    fun from(createStyle: () -> Modifier) {
        keyframeStyles += CSSKeyframe.From to createStyle()
    }

    /** Describe the style of the element when this animation ends. */
    fun to(createStyle: () -> Modifier) {
        keyframeStyles += CSSKeyframe.To to createStyle()
    }

    /** Describe the style of the element when the animation reaches some percent completion. */
    operator fun CSSSizeValue<CSSUnit.percent>.invoke(createStyle: () -> Modifier) {
        keyframeStyles += CSSKeyframe.Percentage(this) to createStyle()
    }

    /**
     * A way to assign multiple percentage values with the same style.
     *
     * For example, this can be useful if you have an animation that changes, then stops for a bit, and then continues
     * to change again.
     *
     * ```
     * val Example = Keyframes {
     *    from { Modifier.opacity(0) }
     *    each(20.percent, 80.percent) { Modifier.opacity(1) }
     *    to { Modifier.opacity(1) }
     * }
     * ```
     */
    fun each(vararg keys: CSSSizeValue<CSSUnit.percent>, createStyle: () -> Modifier) {
        keyframeStyles += CSSKeyframe.Combine(keys.toList()) to createStyle()
    }

    override fun equals(other: Any?): Boolean {
        if (other !is KeyframesBuilder) return false
        return this === other || this.comparableKeyframeStyles == other.comparableKeyframeStyles
    }

    override fun hashCode(): Int {
        return comparableKeyframeStyles.hashCode()
    }

    internal fun addKeyframesIntoStylesheet(stylesheet: StyleSheet, keyframesName: String) {
        val keyframeRules = keyframeStyles.map { (keyframe, modifier) ->
            val styles = modifier.toStyles()

            val cssRuleBuilder = StyleScopeBuilder()
            styles.invoke(cssRuleBuilder)

            CSSKeyframeRuleDeclaration(keyframe, cssRuleBuilder)
        }

        stylesheet.add(CSSKeyframesRuleDeclaration(keyframesName, keyframeRules))
    }
}

/**
 * Define a set of keyframes that can later be referenced in animations.
 *
 * For example,
 *
 * ```
 * val BounceKeyframes = Keyframes {
 *   from { Modifier.translateX((-50).percent) }
 *   to { Modifier.translateX((50).percent) }
 * }
 *
 * // Later
 * Div(
 *   Modifier
 *     .size(100.px).backgroundColor(Colors.Red)
 *     .animation(BounceKeyframes.toAnimation(
 *       duration = 2.s,
 *       timingFunction = AnimationTimingFunction.EaseIn,
 *       direction = AnimationDirection.Alternate,
 *       iterationCount = AnimationIterationCount.Infinite
 *     ))
 *     .toAttrs()
 * )
 * ```
 *
 * If you are not using Kobweb, e.g. if you're using these widgets as a standalone library, you will have to manually
 * register your keyframes:
 *
 * ```
 * SilkFoundationStyles(
 *   initSilk = { ctx ->
 *     /*...*/
 *     ctx.theme.registerKeyframes("bounce", BounceKeyframes)
 *   }
 * )
 * ```
 *
 * Otherwise, the Kobweb Gradle plugin will do this for you.
 */
class Keyframes(internal val init: KeyframesBuilder.() -> Unit) {
    companion object {
        internal fun isColorModeAgnostic(build: KeyframesBuilder.() -> Unit): Boolean {
            // A user can use colorMode checks to change the keyframes builder, either by completely changing what sort
            // of keyframes show up across the light version and the dark version, or (more commonly) keeping the same
            // keyframes but changing some color values in the styles.
            return listOf(ColorMode.LIGHT, ColorMode.DARK)
                .map { colorMode -> KeyframesBuilder(colorMode).apply(build) }
                .distinct().count() == 1
        }
    }

    // Note: Need to postpone checking this value, because color modes aren't ready until after a certain point in
    // Silk's initialization.
    val usesColorMode by lazy { !isColorModeAgnostic(init) }
}

/**
 * A convenience method to convert this [Keyframes] instance into an object that can be passed into [Modifier.animation].
 *
 * This version of the method is [Composable] because it's aware of the site's current color mode.
 *
 * @see ColorMode.currentState
 */
@Composable
fun Keyframes.toAnimation(
    duration: CSSTimeNumericValue? = null,
    timingFunction: AnimationTimingFunction? = null,
    delay: CSSTimeNumericValue? = null,
    iterationCount: AnimationIterationCount? = null,
    direction: AnimationDirection? = null,
    fillMode: AnimationFillMode? = null,
    playState: AnimationPlayState? = null
): Animation.Listable {
    val colorMode = if (this.usesColorMode) ColorMode.current else null
    return toAnimation(colorMode, duration, timingFunction, delay, iterationCount, direction, fillMode, playState)
}

/**
 * A convenience method to convert this [Keyframes] instance into an object that can be passed into [Modifier.animation].
 *
 * This version of the method is not [Composable] and requires the user pass in a [ColorMode] explicitly, especially to
 * distinguish it from the other [toAnimation] method.
 *
 * If you defined a [Keyframes] that uses references the site's color mode, it is an error if you pass in [colorMode] is
 * null. Alternately, if the [Keyframes] doesn't reference the site's color mode in its definition, then whatever color
 * mode is passed in is ignored.
 *
 * It can be useful to call this method from within a [CssStyle]. For example:
 *
 * ```
 * val MyAnimatedStyle = CssStyle {
 *   after {
 *     Modifier.animation(AnimOut.toAnimation(colorMode, ...))
 *   }
 * }
 * ```
 */
fun Keyframes.toAnimation(
    colorMode: ColorMode?,
    duration: CSSTimeNumericValue? = null,
    timingFunction: AnimationTimingFunction? = null,
    delay: CSSTimeNumericValue? = null,
    iterationCount: AnimationIterationCount? = null,
    direction: AnimationDirection? = null,
    fillMode: AnimationFillMode? = null,
    playState: AnimationPlayState? = null,
): Animation.Listable {
    val name = this.name

    @Suppress("NAME_SHADOWING")
    val colorMode = if (this.usesColorMode) {
        colorMode ?: error("Animation $name depends on the site's color mode but no color mode was specified.")
    } else {
        null
    }

    val nameWithColorMode = if (colorMode != null) CssIdent(name).suffixedWith(colorMode).asStr else name
    return Animation.of(
        nameWithColorMode,
        duration,
        timingFunction,
        delay,
        iterationCount,
        direction,
        fillMode,
        playState
    )
}
