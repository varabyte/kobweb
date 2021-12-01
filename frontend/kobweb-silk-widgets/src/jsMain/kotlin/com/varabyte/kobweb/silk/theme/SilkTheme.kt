package com.varabyte.kobweb.silk.theme

import androidx.compose.runtime.*
import com.varabyte.kobweb.silk.components.style.breakpoint.Breakpoint
import com.varabyte.kobweb.silk.components.style.breakpoint.BreakpointValues
import com.varabyte.kobweb.silk.components.style.ComponentStyle
import com.varabyte.kobweb.silk.components.style.ComponentStyleBuilder
import com.varabyte.kobweb.silk.components.style.ComponentVariant
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

    fun registerBreakpoints(values: BreakpointValues<CSSUnitValue>)
}

internal object SilkConfigInstance : SilkConfig {
    override var initialColorMode = ColorMode.LIGHT

    val breakpoints: MutableMap<Breakpoint, CSSUnitValue> = mutableMapOf(
        Breakpoint.SM to 30.cssRem,
        Breakpoint.MD to 48.cssRem,
        Breakpoint.LG to 62.cssRem,
        Breakpoint.XL to 80.cssRem,
        Breakpoint.XXL to 96.cssRem,
    )

    override fun registerBreakpoints(values: BreakpointValues<CSSUnitValue>) {
        breakpoints[Breakpoint.SM] = values.sm
        breakpoints[Breakpoint.MD] = values.md
        breakpoints[Breakpoint.LG] = values.lg
        breakpoints[Breakpoint.XL] = values.xl
        breakpoints[Breakpoint.XXL] = values.xxl
    }
}

/**
 * Theme values that will get frozen at initialization time.
 *
 * Unlike [SilkConfig] values, theme values are expected to be accessible in user projects via the [SilkTheme] object.
 */
class MutableSilkTheme {
    internal val componentStyles = LinkedHashMap<String, ComponentStyleBuilder>() // Preserve insertion order
    internal val componentVariants = LinkedHashMap<String, ComponentVariant>() // Preserve insertion order

    var palettes = SilkPalettes(LightSilkPalette, DarkSilkPalette)

    /**
     * Register a new component style with this theme.
     *
     * **NOTE:** Most of the time, you don't have to call this yourself, as the Gradle plugin will call it for you.
     *
     * If this style has defined additional variants, they will also be registered automatically at this time.
     *
     * Once a style is registered, you can reference it in your Composable widget via the following code:
     */
    fun registerComponentStyle(style: ComponentStyleBuilder) {
        componentStyles[style.name] = style
        registerComponentVariants(*style.variants.toTypedArray())
    }

    /**
     * Register variants associated with a base style.
     *
     * **NOTE:** Most of the time, you don't have to call this yourself, as the Gradle plugin will call it for you.
     * Additionally, any variants created by [ComponentStyleBuilder.addVariant] will be automatically registered when
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

    private val _componentStyles = mutableMapOf<String, ComponentStyle>()
    val componentStyles: Map<String, ComponentStyle> = _componentStyles

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
        check(::SilkTheme.isInitialized)
        mutableSilkTheme.componentStyles.values.forEach { styleBuilder ->
            styleBuilder.addStyles(componentStyleSheet)
            _componentStyles[styleBuilder.name] = ComponentStyle(styleBuilder.name)
        }
        // Variants should be defined after base styles to make sure they take priority if used
        mutableSilkTheme.componentVariants.values.forEach { variant ->
            variant.addStyles(componentStyleSheet)
            _componentStyles[variant.style.name] = ComponentStyle(variant.style.name)
        }
    }
}

lateinit var SilkTheme: ImmutableSilkTheme
    internal set