package com.varabyte.kobweb.silk.theme

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.silk.components.style.ComponentBaseModifier
import com.varabyte.kobweb.silk.components.style.ComponentModifier
import com.varabyte.kobweb.silk.components.style.ComponentModifiers
import com.varabyte.kobweb.silk.components.style.ComponentStyle
import com.varabyte.kobweb.silk.components.style.ComponentVariant
import com.varabyte.kobweb.silk.components.style.ImmutableComponentStyle
import com.varabyte.kobweb.silk.components.style.SimpleComponentVariant
import com.varabyte.kobweb.silk.components.style.StyleModifiers
import com.varabyte.kobweb.silk.components.style.breakpoint.BreakpointSizes
import com.varabyte.kobweb.silk.components.style.breakpoint.BreakpointValues
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import com.varabyte.kobweb.silk.theme.colors.DarkSilkPalette
import com.varabyte.kobweb.silk.theme.colors.LightSilkPalette
import com.varabyte.kobweb.silk.theme.colors.SilkPalette
import com.varabyte.kobweb.silk.theme.colors.SilkPalettes
import com.varabyte.kobweb.silk.theme.colors.getColorMode
import org.jetbrains.compose.web.css.*

/**
 * Configuration values which are frozen at initialization time and accessed globally within Silk after that point.
 */
interface SilkConfig {
    var initialColorMode: ColorMode

    /**
     * An alternate way to register global styles with Silk instead of using a Compose for Web StyleSheet directly.
     *
     * So this:
     *
     * ```
     * @InitSilk
     * fun initStyles(ctx: InitSilkContext) {
     *   ctx.registerStyle("*") {
     *     base {
     *       Modifier.fontSize(48.px)
     *     }
     *     Breakpoint.MD {
     *       ...
     *     }
     *   }
     * }
     * ```
     *
     * is a replacement for all of this:
     *
     * ```
     * object MyStyleSheet : StyleSheet() {
     *   init {
     *     "*" style {
     *       fontSize(48.px)
     *
     *       media(minWidth(...)) {
     *         self style {
     *           ...
     *         }
     *       }
     *     }
     *   }
     * }
     *
     * @App
     * @Composable
     * fun MyApp(content: @Composable () -> Unit) {
     *   SilkApp {
     *     Style(TodoStyleSheet)
     *     ...
     *   }
     * }
     * ```
     */
    fun registerStyle(className: String, extraModifiers: Modifier = Modifier, init: StyleModifiers.() -> Unit)
}

/**
 * Convenience method when you only care about registering the base method, which can help avoid a few extra lines.
 *
 * So this:
 *
 * ```
 * config.registerBaseStyle("*") {
 *   Modifier.fontSize(48.px)
 * }
 * ```
 *
 * replaces this:
 *
 * ```
 * config.registerStyle("*") {
 *   base {
 *     Modifier.fontSize(48.px)
 *   }
 * }
 * ```
 *
 * You may still wish to use [SilkConfig.registerStyle] instead if you expect that at some point in the future
 * you'll want to add additional, non-base styles.
 */
fun SilkConfig.registerBaseStyle(className: String, extraModifiers: Modifier = Modifier, init: () -> Modifier) {
    registerStyle(className, extraModifiers) {
        base {
            init()
        }
    }
}

internal object SilkConfigInstance : SilkConfig {
    override var initialColorMode = ColorMode.LIGHT

    private val styles = mutableListOf<ComponentStyle>()

    override fun registerStyle(className: String, extraModifiers: Modifier, init: StyleModifiers.() -> Unit) {
        styles.add(ComponentStyle(className, extraModifiers, init))
    }

    // This method is not part of the public API and should be called by Silk at initialization time
    fun registerStyles(siteStyleSheet: StyleSheet) {
        styles.forEach { componentStyle ->
            componentStyle.addStylesInto(siteStyleSheet, componentStyle.name)
        }
    }

}

/**
 * Theme values that will get frozen at initialization time.
 *
 * Unlike [SilkConfig] values, theme values are expected to be accessible in user projects via the [SilkTheme] object.
 */
class MutableSilkTheme {
    internal val componentStyles = mutableMapOf<String, ComponentStyle>()
    internal val overiddenStyles = mutableSetOf<String>()
    internal val componentVariants = mutableMapOf<String, ComponentVariant>()

    var palettes = SilkPalettes(LightSilkPalette, DarkSilkPalette)

    var breakpoints: BreakpointValues<CSSUnitValue> = BreakpointSizes(
        30.cssRem,
        48.cssRem,
        62.cssRem,
        80.cssRem,
    )

    /**
     * Register a new component style with this theme.
     *
     * **NOTE:** You shouldn't have to call this yourself. Kobweb detects styles in your code at compile and calls this
     * method for you.
     *
     * Once a style is registered, you can reference it in your Composable widget by calling `toModifier` on it:
     *
     * ```
     * // Your widget code
     * @Composable
     * fun SomeWidget(modifier: Modifier = Modifier) { ... }
     *
     * // Your view code:
     * val SomeStyle = ComponentStyle(...) { // Registered automatically by Kobweb
     *   base { Modifier.background(Colors.Grey) }
     * }
     *
     * // Later...
     * SomeWidget(SomeStyle.toModifier()) // <-- Pass Style to the target widget
     * ```
     *
     * See also: [replaceComponentStyle]
     */
    fun registerComponentStyle(style: ComponentStyle) {
        check(componentStyles[style.name].let { it == null || it === style }) {
            """
                Attempting to register a second style with a name that's already used: "${style.name}"

                If this was an intentional override, you should use `replaceComponentStyle` instead.
            """.trimIndent()
        }
        componentStyles[style.name] = style
        registerComponentVariants(*style.variants.toTypedArray())
    }

    /**
     * Use this method to override a style previously registered using [registerComponentStyle].
     *
     * This is particularly if you want to change styles provided by Silk.
     *
     * ```
     * @InitSilk
     * fun initSilk(ctx: InitSilkContext) {
     *   // TextStyle comes from Silk
     *   ctx.theme.replaceComponentStyle(TextStyle) {
     *     base { Modifier.lineHeight(2) }
     *   }
     * }
     * ```
     */
    fun replaceComponentStyle(
        style: ComponentStyle,
        extraModifiers: Modifier = Modifier,
        init: ComponentModifiers.() -> Unit
    ) {
        check(componentStyles.contains(style.name)) { "Attempting to replace a style that was never registered: \"${style.name}\"" }
        check(overiddenStyles.add(style.name)) { "Attempting to override style \"${style.name}\" twice" }
        componentStyles[style.name] = ComponentStyle(style.name, extraModifiers, init)
    }

    /**
     * Register variants associated with a base style.
     *
     * **NOTE:** Most of the time, you don't have to call this yourself, as the Gradle plugin will call it for you.
     * Additionally, any variants created by [ComponentStyle.addVariant] will be automatically registered when
     * [registerComponentStyle] is called (in which case, calling this is essentially a no-op).
     *
     * However, if you are defining variants on top of base Silk styles, e.g. maybe some new button variants, then they
     * would normally be missed so you'll have to register them yourself in that case:
     *
     * ```
     * package not.in.silk
     * import silk.ButtonStyle
     *
     * val MyButtonVariant = ButtonStyle.addVariant(...)
     *
     * @InitSilk
     * fun initCustomStyle(ctx: InitSilkContext) {
     *   ctx.theme.registerComponentVariants(MyButtonVariant)
     * }
     *
     * @Composable
     * fun CustomWidget(...) {
     *   Button(..., variant = MyButtonVariant, ...)
     * }
     * ```
     */
    fun registerComponentVariants(vararg variants: ComponentVariant) {
        variants.filterIsInstance<SimpleComponentVariant>().forEach { variant ->
            check(componentVariants[variant.style.name].let { it == null || it === variant }) {
                """
                Attempting to register a second variant with a name that's already used: "${variant.style.name}"

                This isn't allowed. Please choose a different name. If there's a usecase for this I'm unaware of,
                consider filing an issue at https://github.com/varabyte/kobweb/issues
            """.trimIndent()
            }
            componentVariants[variant.style.name] = variant
        }
    }
}

/**
 * Convenience method when you want to replace an upstream style but only need to define a base style.
 */
fun MutableSilkTheme.replaceComponentStyleBase(
    style: ComponentStyle,
    extraModifiers: Modifier = Modifier,
    init: ComponentModifier.() -> Modifier
) {
    replaceComponentStyle(style, extraModifiers) {
        base {
            ComponentBaseModifier(colorMode).let(init)
        }
    }
}

class ImmutableSilkTheme(private val mutableSilkTheme: MutableSilkTheme) {
    val palettes = mutableSilkTheme.palettes

    val palette: SilkPalette
        @Composable
        @ReadOnlyComposable
        get() = palettes[getColorMode()]

    val breakpoints = mutableSilkTheme.breakpoints

    private val _componentStyles = mutableMapOf<String, ImmutableComponentStyle>()
    val componentStyles: Map<String, ImmutableComponentStyle> = _componentStyles

    // Note: We separate this function out from the SilkTheme constructor so we can construct it first and then call
    // this later. This allows ComponentStyles to reference SilkTheme in their logic, e.g. TextStyle:
    //  val TextStyle = ComponentStyle("silk-text") {
    //    base {
    //      Modifier.color(SilkTheme.palettes[colorMode].color)
    //                     ^^^^^^^^^
    //     }
    //  }
    // Silk must make sure to set the SilkTheme lateinit var (below) and then call this method right after
    internal fun registerStyles(componentStyleSheet: StyleSheet) {
        // We shouldn't have called this if we didn't set _SilkTheme already. This being true means ComponentStyle
        // initialization blocks can reference `SilkTheme`.
        check(_SilkTheme != null)
        mutableSilkTheme.componentStyles.values.forEach { componentStyle ->
            componentStyle.addStylesInto(componentStyleSheet)
            _componentStyles[componentStyle.name] = componentStyle.intoImmutableStyle()
        }
        // Variants should be defined after base styles to make sure they take priority if used
        mutableSilkTheme.componentVariants.values.filterIsInstance<SimpleComponentVariant>().forEach { variant ->
            variant.addStylesInto(componentStyleSheet)
            _componentStyles[variant.style.name] = variant.intoImmutableStyle()
        }
    }
}

internal var _SilkTheme: ImmutableSilkTheme? = null
val SilkTheme: ImmutableSilkTheme
    get() { return _SilkTheme ?: error("You can't access SilkTheme before first calling SilkApp") }

/**
 * Convenience method for fetching the silk palette associated with the target color mode, useful for when you aren't
 * in a `@Composable` scope (which is common when defining ComponentStyles).
 */
fun ColorMode.toSilkPalette() = SilkTheme.palettes[this]