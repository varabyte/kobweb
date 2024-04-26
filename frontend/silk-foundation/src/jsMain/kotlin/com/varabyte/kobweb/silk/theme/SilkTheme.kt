package com.varabyte.kobweb.silk.theme

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.silk.components.style.ComponentBaseModifier
import com.varabyte.kobweb.silk.components.style.ComponentKind
import com.varabyte.kobweb.silk.components.style.ComponentModifier
import com.varabyte.kobweb.silk.components.style.ComponentModifiers
import com.varabyte.kobweb.silk.components.style.ComponentStyle
import com.varabyte.kobweb.silk.components.style.ComponentVariant
import com.varabyte.kobweb.silk.components.style.CssStyle
import com.varabyte.kobweb.silk.components.style.ImmutableCssStyle
import com.varabyte.kobweb.silk.components.style.SimpleComponentVariant
import com.varabyte.kobweb.silk.components.style.SimpleCssStyle
import com.varabyte.kobweb.silk.components.style.breakpoint.BreakpointSizes
import com.varabyte.kobweb.silk.components.style.breakpoint.BreakpointValues
import com.varabyte.kobweb.silk.init.SilkConfig
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import com.varabyte.kobweb.silk.theme.colors.palette.MutablePalettes
import com.varabyte.kobweb.silk.theme.colors.palette.Palette
import com.varabyte.kobweb.silk.theme.colors.palette.Palettes
import org.jetbrains.compose.web.css.*

/**
 * Theme values that will get frozen at initialization time.
 *
 * Unlike [SilkConfig] values, theme values are expected to be accessible in user projects via the [SilkTheme] object.
 */
class MutableSilkTheme {
    private val _componentStyles = mutableMapOf<String, ComponentStyle<*>>()
    internal val componentStyles: Map<String, ComponentStyle<*>> = _componentStyles
    private val overriddenComponentStyles = mutableSetOf<String>()

    private val _componentVariants = mutableMapOf<String, ComponentVariant<*>>()
    internal val componentVariants: Map<String, ComponentVariant<*>> = _componentVariants
    private val overriddenComponentVariants = mutableSetOf<String>()

    private val _cssStyles = mutableMapOf<String, CssStyle>()
    internal val cssStyles: Map<String, CssStyle> = _cssStyles
    internal val _cssStyleNames = mutableMapOf<CssStyle, String>()
    internal val cssStyleNames: Map<CssStyle, String> = _cssStyleNames
    private val overriddenCssStyles = mutableSetOf<String>()

    val palettes = MutablePalettes()

    var breakpoints: BreakpointValues<CSSLengthNumericValue> = BreakpointSizes(
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
     * val SomeStyle by ComponentStyle { // Registered automatically by Kobweb
     *   base { Modifier.background(Colors.Grey) }
     * }
     *
     * // Later...
     * SomeWidget(SomeStyle.toModifier()) // <-- Pass Style to the target widget
     * ```
     *
     * @see replaceComponentStyle
     */
    fun registerComponentStyle(style: ComponentStyle<*>) {
        check(componentStyles[style.name].let { it == null || it === style }) {
            """
                Attempting to register a second style with a name that's already used: "${style.name}"

                If this was an intentional override, you should use `replaceComponentStyle` instead.
            """.trimIndent()
        }
        _componentStyles[style.name] = style
    }

    /**
     * Use this method to override a style previously registered using [registerComponentStyle].
     *
     * This is particularly useful if you want to change styles provided by Silk.
     *
     * ```
     * @InitSilk
     * fun initSilk(ctx: InitSilkContext) {
     *   // TextStyle comes from Silk
     *   ctx.theme.replaceComponentStyle(SpanTextStyle) {
     *     base { Modifier.lineHeight(2) }
     *   }
     * }
     * ```
     */
    fun <T : ComponentKind> replaceComponentStyle(
        style: ComponentStyle<T>,
        extraModifiers: Modifier = Modifier,
        init: ComponentModifiers.() -> Unit
    ) {
        replaceComponentStyle(style, { extraModifiers }, init)
    }

    fun <T : ComponentKind> replaceComponentStyle(
        style: ComponentStyle<T>,
        extraModifiers: @Composable () -> Modifier,
        init: ComponentModifiers.() -> Unit
    ) {
        check(componentStyles.contains(style.name)) { "Attempting to replace a style that was never registered: \"${style.name}\"" }
        check(overriddenComponentStyles.add(style.name)) { "Attempting to override style \"${style.name}\" twice" }
        _componentStyles[style.name] = ComponentStyle<T>(style.nameWithoutPrefix, extraModifiers, style.prefix, init)
    }

    /**
     * Register variants associated with a base style.
     *
     * **NOTE:** Most of the time, you don't have to call this yourself, as the Gradle plugin will call it for you.
     */
    fun registerComponentVariants(vararg variants: ComponentVariant<*>) {
        variants.filterIsInstance<SimpleComponentVariant<*>>().forEach { variant ->
            check(componentVariants[variant.cssStyle.selector].let { it == null || it === variant }) {
                """
                Attempting to register a second variant with a name that's already used: "${variant.cssStyle.selector}"

                This isn't allowed. Please choose a different name. If there's a usecase for this I'm unaware of,
                consider filing an issue at https://github.com/varabyte/kobweb/issues
            """.trimIndent()
            }
            _componentVariants[variant.cssStyle.selector] = variant
        }
    }

    /**
     * Use this method to override a variant previously registered using [registerComponentVariants].
     *
     * This is particularly useful if you want to change variants provided by Silk.
     *
     * ```
     * @InitSilk
     * fun initSilk(ctx: InitSilkContext) {
     *   // UndecoratedLinkVariant comes from Silk
     *   ctx.theme.replaceComponentVariant(UndecoratedLinkVariant) {
     *     base { Modifier.fontStyle(FontStyle.Italic) }
     *     hover { Modifier.textDecorationLine(TextDecorationLine.None) }
     *   }
     * }
     * ```
     */
    fun <T : ComponentKind> replaceComponentVariant(
        variant: ComponentVariant<T>,
        extraModifiers: Modifier = Modifier,
        init: ComponentModifiers.() -> Unit
    ) {
        replaceComponentVariant(variant, { extraModifiers }, init)
    }

    fun <T : ComponentKind> replaceComponentVariant(
        variant: ComponentVariant<T>,
        extraModifiers: @Composable () -> Modifier,
        init: ComponentModifiers.() -> Unit
    ) {
        @Suppress("NAME_SHADOWING")
        val variant = variant as? SimpleComponentVariant
            ?: error("You can only replace variants created by `addVariant` or `addVariantBase`.")

        check(componentVariants.contains(variant.cssStyle.selector)) { "Attempting to replace a variant that was never registered: \"${variant.cssStyle.selector}\"" }
        check(overriddenComponentVariants.add(variant.cssStyle.selector)) { "Attempting to override variant \"${variant.cssStyle.selector}\" twice" }
        _componentVariants[variant.cssStyle.selector] =
            variant.baseStyle.addVariant(variant.name, extraModifiers, init)
    }

    fun registerCssStyle(name: String, style: CssStyle) {
        val selector = (style as? SimpleCssStyle)?.selector ?: ".$name"
        check(cssStyles[selector].let { it == null || it === style }) {
            """
                Attempting to register a second CssStyle with a name that's already used: "$name"

                If this was an intentional override, you should use `replaceCssStyle` instead.
            """.trimIndent()
        }
        _cssStyles[selector] = style
        _cssStyleNames[style] = name
    }

    fun MutableSilkTheme.replaceCssStyle(
        style: CssStyle,
        extraModifiers: @Composable () -> Modifier,
        init: ComponentModifiers.() -> Unit
    ) {
        val selector = cssStyles.entries.find { it.value == style }?.key ?: error("Attempting to replace a CSS style that was never registered.")
        check(overriddenCssStyles.add(selector)) { "Attempting to override style \"${selector}\" twice" }
        _cssStyles[selector] = SimpleCssStyle(selector, init, extraModifiers)
        // No need to add to `_cssStyleNames` here, as we only will ever need to return names for the originally registered Style
    }
}
/**
 * Convenience method when you want to replace an upstream style but only need to define a base style.
 */
fun <T : ComponentKind> MutableSilkTheme.replaceComponentStyleBase(
    style: ComponentStyle<T>,
    extraModifiers: Modifier = Modifier,
    init: ComponentModifier.() -> Modifier
) {
    replaceComponentStyleBase(style, { extraModifiers }, init)
}

fun <T : ComponentKind> MutableSilkTheme.replaceComponentStyleBase(
    style: ComponentStyle<T>,
    extraModifiers: @Composable () -> Modifier,
    init: ComponentModifier.() -> Modifier
) {
    replaceComponentStyle(style, extraModifiers) {
        base {
            ComponentBaseModifier(colorMode).let(init)
        }
    }
}

/**
 * Convenience method when you want to replace an upstream variant but only need to define a base style.
 */
fun <T : ComponentKind> MutableSilkTheme.replaceComponentVariantBase(
    variant: ComponentVariant<T>,
    extraModifiers: Modifier = Modifier,
    init: ComponentModifier.() -> Modifier
) {
    replaceComponentVariantBase(variant, { extraModifiers }, init)
}

/**
 * Convenience method when you want to replace an upstream variant but only need to define a base style.
 */
fun <T : ComponentKind> MutableSilkTheme.replaceComponentVariantBase(
    variant: ComponentVariant<T>,
    extraModifiers: @Composable () -> Modifier,
    init: ComponentModifier.() -> Modifier
) {
    replaceComponentVariant(variant, extraModifiers) {
        base {
            ComponentBaseModifier(colorMode).let(init)
        }
    }
}
/**
 * Use this method to tweak a style previously registered using [MutableSilkTheme.registerComponentStyle].
 *
 * This is particularly useful if you want to supplement changes to styles provided by Silk.
 *
 * ```
 * @InitSilk
 * fun initSilk(ctx: InitSilkContext) {
 *   // TextStyle comes from Silk
 *   ctx.theme.modifyComponentStyle(SpanTextStyle) {
 *     base { Modifier.fontWeight(FontWeight.Bold) }
 *   }
 * }
 * ```
 */
fun <T : ComponentKind> MutableSilkTheme.modifyComponentStyle(
    style: ComponentStyle<T>,
    extraModifiers: Modifier = Modifier,
    init: ComponentModifiers.() -> Unit
) {
    modifyComponentStyle(style, { extraModifiers }, init)
}

fun <T : ComponentKind> MutableSilkTheme.modifyComponentStyle(
    style: ComponentStyle<T>,
    extraModifiers: @Composable () -> Modifier,
    init: ComponentModifiers.() -> Unit
) {
    check(componentStyles.contains(style.name)) { "Attempting to modify a style that was never registered: \"${style.name}\"" }
    val existingExtraModifiers = style.extraModifiers
    val existingInit = style.init

    replaceComponentStyle(style, {
        existingExtraModifiers().then(extraModifiers())
    }) {
        existingInit.invoke(this)
        init.invoke(this)
    }
}

fun <T : ComponentKind> MutableSilkTheme.modifyComponentStyleBase(
    style: ComponentStyle<T>,
    extraModifiers: Modifier = Modifier,
    init: ComponentModifier.() -> Modifier
) {
    modifyComponentStyleBase(style, { extraModifiers }, init)
}

fun <T : ComponentKind> MutableSilkTheme.modifyComponentStyleBase(
    style: ComponentStyle<T>,
    extraModifiers: @Composable () -> Modifier,
    init: ComponentModifier.() -> Modifier
) {
    modifyComponentStyle(style, extraModifiers) {
        base {
            ComponentBaseModifier(colorMode).let(init)
        }
    }
}

/**
 * Use this method to tweak a variant previously registered using [MutableSilkTheme.registerComponentVariants].
 *
 * This is particularly useful if you want to change variants provided by Silk.
 *
 * ```
 * @InitSilk
 * fun initSilk(ctx: InitSilkContext) {
 *   // UndecoratedLinkVariant comes from Silk
 *   ctx.theme.modifyComponentVariant(UndecoratedLinkVariant) {
 *     base { Modifier.fontStyle(FontStyle.Italic) }
 *   }
 * }
 * ```
 */
fun <T : ComponentKind> MutableSilkTheme.modifyComponentVariant(
    variant: ComponentVariant<T>,
    extraModifiers: Modifier = Modifier,
    init: ComponentModifiers.() -> Unit
) {
    modifyComponentVariant(variant, { extraModifiers }, init)
}

fun <T : ComponentKind> MutableSilkTheme.modifyComponentVariant(
    variant: ComponentVariant<T>,
    extraModifiers: @Composable () -> Modifier,
    init: ComponentModifiers.() -> Unit
) {
    @Suppress("NAME_SHADOWING")
    val variant = variant as? SimpleComponentVariant
        ?: error("You can only replace variants created by `addVariant` or `addVariantBase`.")

    check(componentVariants.contains(variant.cssStyle.selector)) { "Attempting to modify a variant that was never registered: \"${variant.cssStyle.selector}\"" }
    val existingExtraModifiers = variant.cssStyle.extraModifiers
    val existingInit = variant.cssStyle.init

    replaceComponentVariant(variant, {
        existingExtraModifiers().then(extraModifiers())
    }) {
        existingInit.invoke(this)
        init.invoke(this)
    }
}

fun <T : ComponentKind> MutableSilkTheme.modifyComponentVariantBase(
    variant: ComponentVariant<T>,
    extraModifiers: Modifier = Modifier,
    init: ComponentModifier.() -> Modifier
) {
    modifyComponentVariantBase(variant, { extraModifiers }, init)
}

fun <T : ComponentKind> MutableSilkTheme.modifyComponentVariantBase(
    variant: ComponentVariant<T>,
    extraModifiers: @Composable () -> Modifier,
    init: ComponentModifier.() -> Modifier
) {
    modifyComponentVariant(variant, extraModifiers) {
        base {
            ComponentBaseModifier(colorMode).let(init)
        }
    }
}

/**
 * Use this method to tweak a style previously registered using [MutableSilkTheme.registerCssStyle].
 *
 * This is particularly useful if you want to supplement changes to styles provided by Silk.
 *
 * ```
 * @InitSilk
 * fun initSilk(ctx: InitSilkContext) {
 *   ctx.theme.modifyCssStyle(ButtonSize.MD) {
 *     base { Modifier.fontWeight(FontWeight.Bold) }
 *   }
 * }
 * ```
 */
fun MutableSilkTheme.modifyCssStyle(
    style: CssStyle,
    extraModifiers: Modifier = Modifier,
    init: ComponentModifiers.() -> Unit
) {
    modifyCssStyle(style, { extraModifiers }, init)
}

fun MutableSilkTheme.modifyCssStyle(
    style: CssStyle,
    extraModifiers: @Composable () -> Modifier,
    init: ComponentModifiers.() -> Unit
) {
    val existingExtraModifiers = style.extraModifiers
    val existingInit = style.init

    replaceCssStyle(style, {
        existingExtraModifiers().then(extraModifiers())
    }) {
        existingInit.invoke(this)
        init.invoke(this)
    }
}

fun MutableSilkTheme.modifyCssStyleBase(
    style: CssStyle,
    extraModifiers: Modifier = Modifier,
    init: ComponentModifier.() -> Modifier
) {
    modifyCssStyleBase(style, { extraModifiers }, init)
}

fun MutableSilkTheme.modifyCssStyleBase(
    style: CssStyle,
    extraModifiers: @Composable () -> Modifier,
    init: ComponentModifier.() -> Modifier
) {
    modifyCssStyle(style, extraModifiers) {
        base {
            ComponentBaseModifier(colorMode).let(init)
        }
    }
}


class ImmutableSilkTheme(private val mutableSilkTheme: MutableSilkTheme) {
    val palettes = mutableSilkTheme.palettes as Palettes

    val palette: Palette
        @Composable
        @ReadOnlyComposable
        get() = palettes[ColorMode.current]

    val breakpoints = mutableSilkTheme.breakpoints

    private val _cssStyles = mutableMapOf<CssStyle, ImmutableCssStyle>()
    internal val cssStyles: Map<CssStyle, ImmutableCssStyle> = _cssStyles

    /**
     * Return the name associated with the given [CssStyle].
     *
     * Any CSS style that has been declared as a property (e.g. `val MyStyle = CssStyle { ... }`), even as a subclass of
     * one (e.g. `val SM = ButtonSize()`) will have a name.
     *
     * A handful of cases are not named, however. Users shouldn't have to worry about this too much because these are
     * generally internal cases (e.g. `stylesheet.registerStyleBase("#some-id") { ... }` creates a nameless CssStyle
     * behind the scenes).
     *
     * As a result, it is possible for this method to crash if triggered with such a transient CssStyle. However, the
     * user would have to go out of their way to make this happen in practice, which is why the API was designed to
     * assume this won't crash.
     */
    fun nameFor(style: CssStyle): String = mutableSilkTheme.cssStyleNames.getValue(style)

    // Note: We separate this function out from the SilkTheme constructor so we can construct it first and then call
    // this later. This allows ComponentStyles to reference SilkTheme in their logic, e.g. TextStyle:
    //  val TextStyle by ComponentStyle {
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
        val componentStyles = mutableSilkTheme.componentStyles.values
            .associate { it.cssStyle.selector to it.cssStyle }
        // Variants should be defined after base styles to make sure they take priority if used
        val componentVariants = mutableSilkTheme.componentVariants.values.filterIsInstance<SimpleComponentVariant<*>>()
            .associate { it.cssStyle.selector to it.cssStyle }

        val allCssStyles = componentStyles + componentVariants + mutableSilkTheme.cssStyles

        allCssStyles.forEach { (selector, style) ->
            val classSelectors = style.addStylesInto(selector, componentStyleSheet)
            _cssStyles[style] = style.intoImmutableStyle(classSelectors)
        }
    }
}

internal var _SilkTheme: ImmutableSilkTheme? = null
val SilkTheme: ImmutableSilkTheme
    get() {
        return _SilkTheme ?: error("You can't access SilkTheme before first calling SilkApp")
    }
