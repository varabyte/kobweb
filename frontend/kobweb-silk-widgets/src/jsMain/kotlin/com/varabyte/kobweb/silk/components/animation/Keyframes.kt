package com.varabyte.kobweb.silk.components.animation

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.css.CSSAnimation
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.toStyles
import com.varabyte.kobweb.compose.util.titleCamelCaseToKebabCase
import com.varabyte.kobweb.silk.components.style.ComponentStyle
import com.varabyte.kobweb.silk.components.util.internal.CacheByPropertyNameDelegate
import com.varabyte.kobweb.silk.init.SilkStylesheet
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import com.varabyte.kobweb.silk.theme.colors.suffixedWith
import org.jetbrains.compose.web.css.*

private val KeyframesBuilder.comparableKeyframeStyles
    get() = keyframeStyles.mapValues { (_, create) ->
        ComparableStyleScope().apply {
            create().toStyles().invoke(this)
        }
    }

class KeyframesBuilder internal constructor(val colorMode: ColorMode) {
    internal val keyframeStyles = mutableMapOf<CSSKeyframe, () -> Modifier>()

    /** Describe the style of the element when this animation starts. */
    fun from(createStyle: () -> Modifier) {
        keyframeStyles += CSSKeyframe.From to createStyle
    }

    /** Describe the style of the element when this animation ends. */
    fun to(createStyle: () -> Modifier) {
        keyframeStyles += CSSKeyframe.To to createStyle
    }

    /** Describe the style of the element when the animation reaches some percent completion. */
    operator fun CSSSizeValue<CSSUnit.percent>.invoke(createStyle: () -> Modifier) {
        keyframeStyles += CSSKeyframe.Percentage(this) to createStyle
    }

    /**
     * A way to assign multiple percentage values with the same style.
     *
     * For example, this can be useful if you have an animation that changes, then stops for a bit, and then continues
     * to change again.
     *
     * ```
     * val Example by Keyframes {
     *    from { Modifier.opacity(0) }
     *    each(20.percent, 80.percent) { Modifier.opacity(1) }
     *    to { Modifier.opacity(1) }
     * }
     * ```
     */
    fun each(vararg keys: CSSSizeValue<CSSUnit.percent>, createStyle: () -> Modifier) {
        keyframeStyles += CSSKeyframe.Combine(keys.toList()) to createStyle
    }

    override fun equals(other: Any?): Boolean {
        if (other !is KeyframesBuilder) return false
        return this === other || this.comparableKeyframeStyles == other.comparableKeyframeStyles
    }

    override fun hashCode(): Int {
        return comparableKeyframeStyles.hashCode()
    }

    internal fun addKeyframesIntoStylesheet(stylesheet: StyleSheet, keyframesName: String) {
        val keyframeRules = keyframeStyles.map { (keyframe, create) ->
            val styles = create().toStyles()

            val cssRuleBuilder = StyleScopeBuilder()
            styles.invoke(cssRuleBuilder)

            CSSKeyframeRuleDeclaration(keyframe, cssRuleBuilder)
        }

        stylesheet.add(CSSKeyframesRuleDeclaration(keyframesName, keyframeRules))
    }
}

/**
 * Define a set of keyframes that can later be references in animations.
 *
 * For example,
 *
 * ```
 * val Bounce = Keyframes("bounce") {
 *   from { Modifier.translateX((-50).percent) }
 *   to { Modifier.translateX((50).percent) }
 * }
 *
 * // Later
 * Div(
 *   Modifier
 *     .size(100.px).backgroundColor(Colors.Red)
 *     .animation(Bounce.toAnimation(
 *       duration = 2.s,
 *       timingFunction = AnimationTimingFunction.EaseIn,
 *       direction = AnimationDirection.Alternate,
 *       iterationCount = AnimationIterationCount.Infinite
 *     )
 *     .toAttrs()
 * )
 * ```
 *
 * Note: You should prefer to create keyframes using the [keyframes] delegate method to avoid needing to duplicate the
 * property name, e.g.
 *
 * ```
 * val Bounce by Keyframes {
 *   from { Modifier.translateX((-50).percent) }
 *   to { Modifier.translateX((50).percent) }
 * }
 * ```
 *
 * If you are not using Kobweb, e.g. if you're using these widgets as a standalone library, you will have to use an
 * `@InitSilk` block to register your keyframes:
 *
 * ```
 * val Bounce = Keyframes("bounce") { ... }
 * @InitSilk
 * fun initSilk(ctx: InitSilkContext) {
 *   ctx.stylesheet.registerKeyframes(Bounce)
 * }
 * ```
 *
 * Otherwise, the Kobweb Gradle plugin will do this for you.
 */
class Keyframes(name: String, prefix: String? = null, internal val init: KeyframesBuilder.() -> Unit) {
    val name = prefix?.let { "$it-$name" } ?: name

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
 * A delegate provider class which allows you to create a [Keyframes] instance via the `by` keyword.
 */
class KeyframesProvider internal constructor(
    private val prefix: String?,
    private val init: KeyframesBuilder.() -> Unit
) : CacheByPropertyNameDelegate<Keyframes>() {
    override fun create(propertyName: String): Keyframes {
        val name = propertyName.titleCamelCaseToKebabCase()
        return Keyframes(name, prefix, init)
    }
}

fun SilkStylesheet.registerKeyframes(keyframes: Keyframes) = registerKeyframes(keyframes.name, keyframes.init)

/**
 * Construct a [Keyframes] instance where the name comes from the variable name.
 *
 * For example,
 *
 * ```
 * val Bounce by Keyframes { ... }
 * ```
 *
 * creates a keyframe entry into the site stylesheet (provided by Silk) with the name "bounce".
 *
 * Title camel case gets converted to snake case, so if the variable was called "AnimBounce", the final name added to
 * the style sheet would be "anim-bounce"
 *
 * Note: You can always construct a [Keyframes] object directly if you need to control the name, e.g.
 *
 * ```
 * // Renamed "Bounce" to "LegacyBounce" but don't want to break some old code.
 * val LegacyBounce = Keyframes("bounce") { ... }
 * ```
 */
@Suppress("FunctionName") // name chosen to look like a constructor intentionally
fun Keyframes(prefix: String? = null, init: KeyframesBuilder.() -> Unit) = KeyframesProvider(prefix, init)

// TODO(#168): Delete this before 1.0
@Deprecated(
    "`keyframes` has been replaced with `Keyframes` (capitalized) for consistency with `ComponentStyle` behavior.",
    ReplaceWith("Keyframes(init)")
)
fun keyframes(prefix: String? = null, init: KeyframesBuilder.() -> Unit) = Keyframes(prefix, init)

/**
 * A convenience method to convert this [Keyframes] instance into an object that can be passed into [Modifier.animation].
 *
 * This version of the method is [Composable] because it's aware of the site's current color mode.
 *
 * @see [ColorMode.currentState]
 */
@Composable
fun Keyframes.toAnimation(
    duration: CSSSizeValue<out CSSUnitTime>? = null,
    timingFunction: AnimationTimingFunction? = null,
    delay: CSSSizeValue<out CSSUnitTime>? = null,
    iterationCount: AnimationIterationCount? = null,
    direction: AnimationDirection? = null,
    fillMode: AnimationFillMode? = null,
    playState: AnimationPlayState? = null
): CSSAnimation {
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
 * It can be useful to call this method from within a [ComponentStyle]. For example:
 *
 * ```
 * val MyAnimatedStyle by ComponentStyle {
 *   after {
 *     Modifier.animation(AnimOut.toAnimation(colorMode, ...))
 *   }
 * }
 * ```
 */
fun Keyframes.toAnimation(
    colorMode: ColorMode?,
    duration: CSSSizeValue<out CSSUnitTime>? = null,
    timingFunction: AnimationTimingFunction? = null,
    delay: CSSSizeValue<out CSSUnitTime>? = null,
    iterationCount: AnimationIterationCount? = null,
    direction: AnimationDirection? = null,
    fillMode: AnimationFillMode? = null,
    playState: AnimationPlayState? = null,
): CSSAnimation {
    @Suppress("NAME_SHADOWING")
    val colorMode = if (this.usesColorMode) {
        colorMode ?: error("Animation $name depends on the site's color mode but no color mode was specified.")
    } else {
        null
    }

    val finalName = if (colorMode != null) {
        this.name.suffixedWith(colorMode)
    } else {
        this.name
    }

    return CSSAnimation(
        finalName,
        duration,
        timingFunction,
        delay,
        iterationCount,
        direction,
        fillMode,
        playState
    )
}
