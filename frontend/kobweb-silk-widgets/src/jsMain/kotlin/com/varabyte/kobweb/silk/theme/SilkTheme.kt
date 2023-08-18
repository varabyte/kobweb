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
import com.varabyte.kobweb.silk.components.style.breakpoint.BreakpointSizes
import com.varabyte.kobweb.silk.components.style.breakpoint.BreakpointValues
import com.varabyte.kobweb.silk.theme.colors.*
import org.jetbrains.compose.web.css.*

/**
 * Theme values that will get frozen at initialization time.
 *
 * Unlike [SilkConfig] values, theme values are expected to be accessible in user projects via the [SilkTheme] object.
 */
class MutableSilkTheme {
    internal val componentStyles = mutableMapOf<String, ComponentStyle>()
    internal val overiddenStyles = mutableSetOf<String>()
    internal val componentVariants = mutableMapOf<String, ComponentVariant>()
    internal val overiddenVariants = mutableSetOf<String>()

    var palettes = MutableSilkPalettes()

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
     * val SomeStyle by ComponentStyle { // Registered automatically by Kobweb
     *   base { Modifier.background(Colors.Grey) }
     * }
     *
     * // Later...
     * SomeWidget(SomeStyle.toModifier()) // <-- Pass Style to the target widget
     * ```
     *
     * @see [replaceComponentStyle]
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
    fun replaceComponentStyle(
        style: ComponentStyle,
        extraModifiers: Modifier = Modifier,
        init: ComponentModifiers.() -> Unit
    ) {
        replaceComponentStyle(style, { extraModifiers }, init)
    }

    fun replaceComponentStyle(
        style: ComponentStyle,
        extraModifiers: @Composable () -> Modifier,
        init: ComponentModifiers.() -> Unit
    ) {
        check(componentStyles.contains(style.name)) { "Attempting to replace a style that was never registered: \"${style.name}\"" }
        check(overiddenStyles.add(style.name)) { "Attempting to override style \"${style.name}\" twice" }
        componentStyles[style.name] = ComponentStyle(style.nameWithoutPrefix, extraModifiers, style.prefix, init)
    }

    /**
     * Register variants associated with a base style.
     *
     * **NOTE:** Most of the time, you don't have to call this yourself, as the Gradle plugin will call it for you.
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
    fun replaceComponentVariant(
        variant: ComponentVariant,
        extraModifiers: Modifier = Modifier,
        init: ComponentModifiers.() -> Unit
    ) {
        replaceComponentVariant(variant, { extraModifiers }, init)
    }

    fun replaceComponentVariant(
        variant: ComponentVariant,
        extraModifiers: @Composable () -> Modifier,
        init: ComponentModifiers.() -> Unit
    ) {
        @Suppress("NAME_SHADOWING")
        val variant = variant as? SimpleComponentVariant
            ?: error("You can only replace variants created by `addVariant` or `addVariantBase`.")

        check(componentVariants.contains(variant.style.name)) { "Attempting to replace a variant that was never registered: \"${variant.style.name}\"" }
        check(overiddenVariants.add(variant.style.name)) { "Attempting to override variant \"${variant.style.name}\" twice" }
        componentVariants[variant.style.name] = variant.baseStyle.addVariant(variant.name, extraModifiers, init)
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
    replaceComponentStyleBase(style, { extraModifiers }, init)
}

fun MutableSilkTheme.replaceComponentStyleBase(
    style: ComponentStyle,
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
fun MutableSilkTheme.replaceComponentVariantBase(
    variant: ComponentVariant,
    extraModifiers: Modifier = Modifier,
    init: ComponentModifier.() -> Modifier
) {
    replaceComponentVariantBase(variant, { extraModifiers }, init)
}

/**
 * Convenience method when you want to replace an upstream variant but only need to define a base style.
 */
fun MutableSilkTheme.replaceComponentVariantBase(
    variant: ComponentVariant,
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
fun MutableSilkTheme.modifyComponentStyle(
    style: ComponentStyle,
    extraModifiers: Modifier = Modifier,
    init: ComponentModifiers.() -> Unit
) {
    modifyComponentStyle(style, { extraModifiers }, init)
}

fun MutableSilkTheme.modifyComponentStyle(
    style: ComponentStyle,
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

fun MutableSilkTheme.modifyComponentStyleBase(
    style: ComponentStyle,
    extraModifiers: Modifier = Modifier,
    init: ComponentModifier.() -> Modifier
) {
    modifyComponentStyleBase(style, { extraModifiers }, init)
}

fun MutableSilkTheme.modifyComponentStyleBase(
    style: ComponentStyle,
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
fun MutableSilkTheme.modifyComponentVariant(
    variant: ComponentVariant,
    extraModifiers: Modifier = Modifier,
    init: ComponentModifiers.() -> Unit
) {
    modifyComponentVariant(variant, { extraModifiers }, init)
}

fun MutableSilkTheme.modifyComponentVariant(
    variant: ComponentVariant,
    extraModifiers: @Composable () -> Modifier,
    init: ComponentModifiers.() -> Unit
) {
    @Suppress("NAME_SHADOWING")
    val variant = variant as? SimpleComponentVariant
        ?: error("You can only replace variants created by `addVariant` or `addVariantBase`.")

    check(componentVariants.contains(variant.style.name)) { "Attempting to modify a variant that was never registered: \"${variant.style.name}\"" }
    val existingExtraModifiers = variant.style.extraModifiers
    val existingInit = variant.style.init

    replaceComponentVariant(variant, {
        existingExtraModifiers().then(extraModifiers())
    }) {
        existingInit.invoke(this)
        init.invoke(this)
    }
}

fun MutableSilkTheme.modifyComponentVariantBase(
    variant: ComponentVariant,
    extraModifiers: Modifier = Modifier,
    init: ComponentModifier.() -> Modifier
) {
    modifyComponentVariantBase(variant, { extraModifiers }, init)
}

fun MutableSilkTheme.modifyComponentVariantBase(
    variant: ComponentVariant,
    extraModifiers: @Composable () -> Modifier,
    init: ComponentModifier.() -> Modifier
) {
    modifyComponentVariant(variant, extraModifiers) {
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
        get() = palettes[ColorMode.current]

    val breakpoints = mutableSilkTheme.breakpoints

    private val _componentStyles = mutableMapOf<String, ImmutableComponentStyle>()
    val componentStyles: Map<String, ImmutableComponentStyle> = _componentStyles

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
    get() {
        return _SilkTheme ?: error("You can't access SilkTheme before first calling SilkApp")
    }

/**
 * Convenience method for fetching the silk palette associated with the target color mode, useful for when you aren't
 * in a `@Composable` scope (which is common when defining ComponentStyles).
 */
fun ColorMode.toSilkPalette() = SilkTheme.palettes[this]
