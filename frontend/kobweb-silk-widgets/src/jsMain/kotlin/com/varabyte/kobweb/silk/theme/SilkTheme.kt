package com.varabyte.kobweb.silk.theme

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.silk.components.style.ComponentModifiers
import com.varabyte.kobweb.silk.components.style.ComponentStyle
import com.varabyte.kobweb.silk.components.style.ComponentStyleState
import com.varabyte.kobweb.silk.components.style.ComponentVariant
import com.varabyte.kobweb.silk.components.style.ImmutableComponentStyle
import com.varabyte.kobweb.silk.components.style.breakpoint.Breakpoint
import com.varabyte.kobweb.silk.components.style.breakpoint.BreakpointSizes
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import com.varabyte.kobweb.silk.theme.colors.DarkSilkPalette
import com.varabyte.kobweb.silk.theme.colors.LightSilkPalette
import com.varabyte.kobweb.silk.theme.colors.SilkPalette
import com.varabyte.kobweb.silk.theme.colors.SilkPalettes
import com.varabyte.kobweb.silk.theme.colors.getColorMode
import org.jetbrains.compose.web.css.CSSUnitValue
import org.jetbrains.compose.web.css.StyleSheet
import org.jetbrains.compose.web.css.cssRem

/**
 * Configuration values which are frozen at initialization time and accessed globally within Silk after that point.
 */
interface SilkConfig {
    var initialColorMode: ColorMode

    /**
     * An alternate way to register global styles with Silk instead of using a Web Compose StyleSheet directly.
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
    fun registerStyle(className: String, init: ComponentModifiers.() -> Unit)
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
fun SilkConfig.registerBaseStyle(className: String, init: ComponentStyleState.() -> Modifier) {
    registerStyle(className) {
        base {
            ComponentStyleState(colorMode).let(init)
        }
    }
}

internal object SilkConfigInstance : SilkConfig {
    override var initialColorMode = ColorMode.LIGHT

    private val styles = mutableListOf<ComponentStyle>()

    override fun registerStyle(className: String, init: ComponentModifiers.() -> Unit) {
        styles.add(ComponentStyle(className, init))
    }

    // This method is not part of the public API and should be called by Silk at initialization time
    fun registerStyles(siteStyleSheet: StyleSheet) {
        styles.forEach { styleBuilder ->
            styleBuilder.addStyles(siteStyleSheet, styleBuilder.name)
        }
    }

}

/**
 * Theme values that will get frozen at initialization time.
 *
 * Unlike [SilkConfig] values, theme values are expected to be accessible in user projects via the [SilkTheme] object.
 */
class MutableSilkTheme {
    internal val componentStyles = LinkedHashMap<String, ComponentStyle>() // Preserve insertion order
    internal val componentVariants = LinkedHashMap<String, ComponentVariant>() // Preserve insertion order

    var palettes = SilkPalettes(LightSilkPalette, DarkSilkPalette)

    var breakpoints = BreakpointSizes(
        30.cssRem,
        48.cssRem,
        62.cssRem,
        80.cssRem,
    )

    /**
     * Register a new component style with this theme.
     *
     * **NOTE:** Most of the time, you don't have to call this yourself, as the Gradle plugin will call it for you.
     *
     * If this style has defined additional variants, they will also be registered automatically at this time.
     *
     * Once a style is registered, you can reference it in your Composable widget via the following code:
     */
    fun registerComponentStyle(style: ComponentStyle) {
        componentStyles[style.name] = style
        registerComponentVariants(*style.variants.toTypedArray())
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
        variants.forEach { variant -> componentVariants[variant.style.name] = variant }
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
        mutableSilkTheme.componentStyles.values.forEach { styleBuilder ->
            styleBuilder.addStyles(componentStyleSheet)
            _componentStyles[styleBuilder.name] = ImmutableComponentStyle(styleBuilder.name)
        }
        // Variants should be defined after base styles to make sure they take priority if used
        mutableSilkTheme.componentVariants.values.forEach { variant ->
            variant.addStyles(componentStyleSheet)
            _componentStyles[variant.style.name] = ImmutableComponentStyle(variant.style.name)
        }
    }
}

internal var _SilkTheme: ImmutableSilkTheme? = null
val SilkTheme: ImmutableSilkTheme
    get() { return _SilkTheme ?: error("You can't access SilkTheme before first calling SilkApp") }

/**
 * Convenience method for fetching the associated `SilkTheme.breakpoints` value for the current [Breakpoint] value.
 */
fun Breakpoint.toSize(): CSSUnitValue {
    return when (this) {
        Breakpoint.SM -> SilkTheme.breakpoints.sm
        Breakpoint.MD -> SilkTheme.breakpoints.md
        Breakpoint.LG -> SilkTheme.breakpoints.lg
        Breakpoint.XL -> SilkTheme.breakpoints.xl
    }
}

/**
 * Convenience method for fetching the silk palette associated with the target color mode, useful for when you aren't
 * in a `@Composable` scope (which is common when defining ComponentStyles).
 */
fun ColorMode.toSilkPalette() = SilkTheme.palettes[this]