package com.varabyte.kobweb.silk.theme

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.silk.init.SilkConfig
import com.varabyte.kobweb.silk.init.SilkStylesheet
import com.varabyte.kobweb.silk.style.ComponentKind
import com.varabyte.kobweb.silk.style.CssKind
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.CssStyleBaseScope
import com.varabyte.kobweb.silk.style.CssStyleScope
import com.varabyte.kobweb.silk.style.CssStyleVariant
import com.varabyte.kobweb.silk.style.ExtendingCssStyle
import com.varabyte.kobweb.silk.style.ExtendingCssStyleVariant
import com.varabyte.kobweb.silk.style.GeneralKind
import com.varabyte.kobweb.silk.style.ImmutableCssStyle
import com.varabyte.kobweb.silk.style.RestrictedKind
import com.varabyte.kobweb.silk.style.SimpleCssStyleVariant
import com.varabyte.kobweb.silk.style.addVariant
import com.varabyte.kobweb.silk.style.animation.Keyframes
import com.varabyte.kobweb.silk.style.breakpoint.BreakpointSizes
import com.varabyte.kobweb.silk.style.breakpoint.BreakpointValues
import com.varabyte.kobweb.silk.style.layer.SilkLayer
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import com.varabyte.kobweb.silk.theme.colors.palette.MutablePalettes
import com.varabyte.kobweb.silk.theme.colors.palette.Palette
import com.varabyte.kobweb.silk.theme.colors.palette.Palettes
import org.jetbrains.compose.web.css.*
import kotlin.reflect.KClass

/**
 * Theme values that will get frozen at initialization time.
 *
 * Unlike [SilkConfig] values, theme values are expected to be accessible in user projects via the [SilkTheme] object.
 */
class MutableSilkTheme {
    // Map of name to style
    private val _cssStyles = mutableMapOf<String, CssStyle<*>>()
    internal val cssStyles: Map<String, CssStyle<*>> = _cssStyles

    // Map of name to variant
    private val _cssStyleVariants = mutableMapOf<String, CssStyleVariant<*>>()
    internal val cssStyleVariants: Map<String, CssStyleVariant<*>> = _cssStyleVariants

    private val _cssStyleNames = mutableMapOf<CssStyle<*>, String>()
    internal val cssStyleNames: Map<CssStyle<*>, String> = _cssStyleNames
    private val _cssLayersFor = mutableMapOf<String, String>()
    internal val cssLayersFor: Map<String, String> = _cssLayersFor

    private val _replacedCssStyles = mutableMapOf<CssStyle<*>, CssStyle<*>>()
    internal val replacedCssStyles: Map<CssStyle<*>, CssStyle<*>> = _replacedCssStyles

    // If present in the map, "A to [B, C, D]" means that A depends on B, C, and D (and must be registered in the
    // stylesheet after them to ensure its styles take precedence)
    private val _cssStyleDependencies = mutableMapOf<CssStyle<*>, MutableList<CssStyle<*>>>()
    internal val cssStyleDependencies: Map<CssStyle<*>, List<CssStyle<*>>> = _cssStyleDependencies

    // Map of name to keyframes
    private val _keyframes = mutableMapOf<String, Keyframes>()
    internal val keyframes: Map<String, Keyframes> = _keyframes

    private val _cssKeyframesNames = mutableMapOf<Keyframes, String>()
    internal val cssKeyframesNames: Map<Keyframes, String> = _cssKeyframesNames

    val palettes = MutablePalettes()

    var breakpoints: BreakpointValues<CSSLengthNumericValue> = BreakpointSizes(
        30.cssRem,
        48.cssRem,
        62.cssRem,
        80.cssRem,
    )

    private fun _registerStyle(name: String, style: CssStyle<*>, kind: KClass<out CssKind>, layer: String?) {
        check(cssStyles[name].let { it == null || it === style }) {
            """
                Attempting to register a second CssStyle with a name that's already used: "$name"

                If this was an intentional override, you should use `replaceStyle` instead.
            """.trimIndent()
        }
        _cssStyles[name] = style
        _cssStyleNames[style] = name

        val finalLayer = layer ?: when (kind) {
            ComponentKind::class -> SilkLayer.COMPONENT_STYLES
            RestrictedKind::class -> SilkLayer.RESTRICTED_STYLES
            GeneralKind::class -> SilkLayer.GENERAL_STYLES
            else -> error("Unknown kind: $kind")
        }.layerName
        finalLayer
            .takeIf { it.isNotEmpty() } // If the user passes in "", no layer should be registered
            ?.let { _cssLayersFor[name] = it }

        if (style is ExtendingCssStyle) {
            _cssStyleDependencies.getOrPut(style) { mutableListOf() }.add(style.baseStyle)
        }
    }

    fun <K : ComponentKind> registerStyle(name: String, style: CssStyle<K>, layer: String? = null) =
        _registerStyle(name, style, ComponentKind::class, layer)

    fun registerStyle(name: String, style: CssStyle<RestrictedKind>, layer: String? = null) =
        _registerStyle(name, style, RestrictedKind::class, layer)

    fun registerStyle(name: String, style: CssStyle<GeneralKind>, layer: String? = null) =
        _registerStyle(name, style, GeneralKind::class, layer)

    private fun updateReplaced(originalStyle: CssStyle<*>, newStyle: CssStyle<*>) {
        _replacedCssStyles[originalStyle] = newStyle

        _cssStyleDependencies.remove(originalStyle)?.let { dependencies ->
            _cssStyleDependencies[newStyle] = dependencies
        }
        _cssStyleDependencies.values.forEach {
            if (it.remove(originalStyle)) it.add(newStyle)
        }
    }

    fun replaceStyle(
        style: CssStyle<*>,
        extraModifier: @Composable () -> Modifier,
        init: CssStyleScope.() -> Unit
    ) {
        val name = cssStyleNames[style] ?: error("Attempting to replace a CSS style that was never registered.")
        check(!replacedCssStyles.contains(style)) { "Attempting to override style \"${name}\" twice" }
        val newStyle = object : CssStyle<GeneralKind>(init, extraModifier) {}
        _cssStyles[name] = newStyle
        _cssStyleNames[newStyle] = name
        updateReplaced(style, newStyle)
    }

    fun replaceStyle(
        style: CssStyle<*>,
        extraModifier: Modifier = Modifier,
        init: CssStyleScope.() -> Unit
    ) {
        replaceStyle(style, { extraModifier }, init)
    }

    /**
     * Register variants associated with a base style.
     *
     * Since variants are always based on top of some target style, we allow the name parameter to start with a dash,
     * which indicates that the variant name will be appended to the base style's name.
     *
     * For example,
     *
     * ```
     * val ButtonStyle = CssStyle<ButtonKind> { ... }
     *
     * // Here, the final name --> "button-brand"
     * // Without a CssName, the final name would be "button-brand-aware-orange-red"
     * // If the CssName was just "brand", then the final name would be, well, "brand"
     * @CssName("-brand")
     * val BrandAwareOrangeRedButtonVariant = ButtonStyle.addVariant { ... }
     * ```
     *
     * **NOTE:** Most of the time, you don't have to call this yourself, as the Gradle plugin will call it for you.
     */
    fun <K : ComponentKind> registerVariant(name: String, variant: CssStyleVariant<K>, layer: String? = null) {
        val simpleVariant = variant as? SimpleCssStyleVariant<K>
            ?: error("You can only register variants created by `addVariant` or `addVariantBase`.")

        @Suppress("NAME_SHADOWING")
        val name = if (name.startsWith('-')) {
            val baseStyleName = cssStyleNames[simpleVariant.baseStyle]
                ?: error("When registering variant \"$name\", somehow its base style was not registered correctly. This is not expected, so please report the issue.")

            baseStyleName + name
        } else name

        check(cssStyleVariants[name].let { it == null || it === variant }) {
            """
            Attempting to register a second variant with a name that's already used: "$name"

            This isn't allowed. Please choose a different name. If there's a usecase for this I'm unaware of,
            consider filing an issue at https://github.com/varabyte/kobweb/issues
        """.trimIndent()
        }
        _cssStyleVariants[name] = variant
        _cssStyleNames[variant.cssStyle] = name

        val finalLayer = (layer ?: SilkLayer.COMPONENT_VARIANTS.layerName)
            .takeIf { it.isNotEmpty() } // In case user passes in ""
        finalLayer?.let { _cssLayersFor[name] = it }

        if (variant is ExtendingCssStyleVariant) {
            _cssStyleDependencies.getOrPut(variant.cssStyle) { mutableListOf() }.add(variant.baseVariant.cssStyle)
        }
    }

    fun <K : ComponentKind> replaceVariant(
        variant: CssStyleVariant<K>,
        extraModifier: Modifier = Modifier,
        init: CssStyleScope.() -> Unit
    ) {
        replaceVariant(variant, { extraModifier }, init)
    }

    /**
     * Use this method to override a variant previously registered using [registerVariant].
     *
     * This is particularly useful if you want to change variants provided by Silk.
     *
     * ```
     * @InitSilk
     * fun initSilk(ctx: InitSilkContext) {
     *   // UndecoratedLinkVariant comes from Silk
     *   ctx.theme.replaceVariant(UndecoratedLinkVariant) {
     *     base { Modifier.fontStyle(FontStyle.Italic) }
     *     hover { Modifier.textDecorationLine(TextDecorationLine.None) }
     *   }
     * }
     * ```
     */
    fun <K : ComponentKind> replaceVariant(
        variant: CssStyleVariant<K>,
        extraModifier: @Composable () -> Modifier,
        init: CssStyleScope.() -> Unit
    ) {
        @Suppress("NAME_SHADOWING")
        val variant = variant as? SimpleCssStyleVariant<K>
            ?: error("You can only replace variants created by `addVariant` or `addVariantBase`.")

        val name = cssStyleNames[variant.cssStyle]
            ?: error("Attempting to replace a variant that was never registered.")

        check(!replacedCssStyles.contains(variant.cssStyle)) { "Attempting to override variant \"${name}\" twice" }
        val newVariant = variant.baseStyle.addVariant(extraModifier, init) as SimpleCssStyleVariant<K>
        _cssStyleVariants[name] = newVariant
        _cssStyleNames[variant.cssStyle] = name
        updateReplaced(variant.cssStyle, newVariant.cssStyle)
    }

    fun registerKeyframes(name: String, keyframes: Keyframes) {
        check(_keyframes[name].let { it == null || it === keyframes }) {
            """
                Attempting to register a second keyframes with a name that's already used: "$name"
            """.trimIndent()
        }
        _keyframes[name] = keyframes
        _cssKeyframesNames[keyframes] = name
    }
}

/**
 * Use this method to tweak a style previously registered using [MutableSilkTheme.registerStyle].
 *
 * This is particularly useful if you want to supplement changes to styles provided by Silk.
 *
 * ```
 * @InitSilk
 * fun initSilk(ctx: InitSilkContext) {
 *   ctx.theme.modifyStyle(ButtonSize.MD) {
 *     base { Modifier.fontWeight(FontWeight.Bold) }
 *   }
 * }
 * ```
 */
fun MutableSilkTheme.modifyStyle(
    style: CssStyle<*>,
    extraModifier: Modifier = Modifier,
    init: CssStyleScope.() -> Unit
) {
    modifyStyle(style, { extraModifier }, init)
}

fun MutableSilkTheme.modifyStyle(
    style: CssStyle<*>,
    extraModifier: @Composable () -> Modifier,
    init: CssStyleScope.() -> Unit
) {
    val styleName = cssStyleNames[style] ?: error("Attempting to modify a style that was never registered.")
    check(cssStyles.contains(styleName)) { "Attempting to modify a style that was never registered: \"${styleName}\"" }
    val existingExtraModifier = style.extraModifier
    val existingInit = style.init

    replaceStyle(style, {
        existingExtraModifier().then(extraModifier())
    }) {
        existingInit.invoke(this)
        init.invoke(this)
    }
}

fun MutableSilkTheme.modifyStyleBase(
    style: CssStyle<*>,
    extraModifier: Modifier = Modifier,
    init: CssStyleBaseScope.() -> Modifier
) {
    modifyStyleBase(style, { extraModifier }, init)
}

fun MutableSilkTheme.modifyStyleBase(
    style: CssStyle<*>,
    extraModifier: @Composable () -> Modifier,
    init: CssStyleBaseScope.() -> Modifier
) {
    modifyStyle(style, extraModifier) {
        base {
            CssStyleBaseScope(colorMode).let(init)
        }
    }
}

fun MutableSilkTheme.replaceStyleBase(
    style: CssStyle<*>,
    extraModifier: Modifier = Modifier,
    init: CssStyleBaseScope.() -> Modifier
) {
    replaceStyleBase(style, { extraModifier }, init)
}

/**
 * Convenience method when you want to replace an upstream style but only need to define a base style.
 */
fun MutableSilkTheme.replaceStyleBase(
    style: CssStyle<*>,
    extraModifier: @Composable () -> Modifier,
    init: CssStyleBaseScope.() -> Modifier
) {
    replaceStyle(style, extraModifier) {
        base {
            CssStyleBaseScope(colorMode).let(init)
        }
    }
}

fun <K : ComponentKind> MutableSilkTheme.replaceVariantBase(
    variant: CssStyleVariant<K>,
    extraModifier: Modifier = Modifier,
    init: CssStyleBaseScope.() -> Modifier
) {
    replaceVariantBase(variant, { extraModifier }, init)
}

/**
 * Convenience method when you want to replace an upstream variant but only need to define a base style.
 */
fun <K : ComponentKind> MutableSilkTheme.replaceVariantBase(
    variant: CssStyleVariant<K>,
    extraModifier: @Composable () -> Modifier,
    init: CssStyleBaseScope.() -> Modifier
) {
    replaceVariant(variant, extraModifier) {
        base {
            CssStyleBaseScope(colorMode).let(init)
        }
    }
}

fun <K : ComponentKind> MutableSilkTheme.modifyVariant(
    variant: CssStyleVariant<K>,
    extraModifier: Modifier = Modifier,
    init: CssStyleScope.() -> Unit
) {
    modifyVariant(variant, { extraModifier }, init)
}

/**
 * Use this method to tweak a variant previously registered using [MutableSilkTheme.registerVariants].
 *
 * This is particularly useful if you want to change variants provided by Silk.
 *
 * ```
 * @InitSilk
 * fun initSilk(ctx: InitSilkContext) {
 *   // UndecoratedLinkVariant comes from Silk
 *   ctx.theme.modifyVariant(UndecoratedLinkVariant) {
 *     base { Modifier.fontStyle(FontStyle.Italic) }
 *   }
 * }
 * ```
 */
fun <K : ComponentKind> MutableSilkTheme.modifyVariant(
    variant: CssStyleVariant<K>,
    extraModifier: @Composable () -> Modifier,
    init: CssStyleScope.() -> Unit
) {
    @Suppress("NAME_SHADOWING")
    val variant = variant as? SimpleCssStyleVariant<K>
        ?: error("You can only replace variants created by `addVariant` or `addVariantBase`.")

    val variantName =
        cssStyleNames[variant.cssStyle] ?: error("Attempting to modify a variant that was never registered.")
    check(cssStyleVariants.contains(variantName)) { "Attempting to modify a style that was never registered: \"${variantName}\"" }
    val existingExtraModifier = variant.cssStyle.extraModifier
    val existingInit = variant.cssStyle.init

    replaceVariant(variant, {
        existingExtraModifier().then(extraModifier())
    }) {
        existingInit.invoke(this)
        init.invoke(this)
    }
}

fun <K : ComponentKind> MutableSilkTheme.modifyVariantBase(
    variant: CssStyleVariant<K>,
    extraModifier: Modifier = Modifier,
    init: CssStyleBaseScope.() -> Modifier
) {
    modifyVariantBase(variant, { extraModifier }, init)
}

fun <K : ComponentKind> MutableSilkTheme.modifyVariantBase(
    variant: CssStyleVariant<K>,
    extraModifier: @Composable () -> Modifier,
    init: CssStyleBaseScope.() -> Modifier
) {
    modifyVariant(variant, extraModifier) {
        base {
            CssStyleBaseScope(colorMode).let(init)
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

    private val _cssStyles = mutableMapOf<CssStyle<*>, ImmutableCssStyle>()
    internal val cssStyles: Map<CssStyle<*>, ImmutableCssStyle> = _cssStyles

    fun nameFor(style: CssStyle<*>): String? = mutableSilkTheme.cssStyleNames[style]
    fun nameFor(keyframes: Keyframes): String? = mutableSilkTheme.cssKeyframesNames[keyframes]

    // Note: We separate these function out from the SilkTheme constructor so we can construct it first and then call
    // them later. This allows ComponentStyles to reference SilkTheme in their logic, e.g. TextStyle:
    //  val TextStyle = CssStyle {
    //    base {
    //      Modifier.color(SilkTheme.palettes[colorMode].color)
    //                     ^^^^^^^^^
    //     }
    //  }
    // Silk must make sure to set the SilkTheme lateinit var (below) and then call this method right after

    internal fun registerKeyframesInto(silkStyleSheet: SilkStylesheet) {
        // We shouldn't have called this if we didn't set _SilkTheme already. This being true means ComponentStyle
        // initialization blocks can reference `SilkTheme`.
        check(_SilkTheme != null)

        mutableSilkTheme.keyframes.forEach { (name, keyframes) ->
            silkStyleSheet.registerKeyframes(name, keyframes.init)
        }
    }

    internal fun registerStylesInto(stylesheet: StyleSheet) {
        // We shouldn't have called this if we didn't set _SilkTheme already. This being true means ComponentStyle
        // initialization blocks can reference `SilkTheme`.
        check(_SilkTheme != null)

        // For a list like [E, D, B, C, A] where D, B, and C all depend on A, we get a final ordering: [E, A, D, B, C]
        fun orderStyles(
            styles: List<CssStyle<*>>,
            dependencies: Map<CssStyle<*>, List<CssStyle<*>>>
        ): List<CssStyle<*>> {
            if (dependencies.isEmpty()) return styles

            val orderedStyles = mutableListOf<CssStyle<*>>()
            val visited = mutableSetOf<CssStyle<*>>()

            fun visit(style: CssStyle<*>) {
                if (style in visited) return
                visited.add(style)
                dependencies[style]?.forEach { visit(it) }
                orderedStyles.add(style)
            }

            styles.forEach { visit(it) }
            return orderedStyles
        }

        val allCssStyles =
            mutableSilkTheme.cssStyles.values +
                mutableSilkTheme.cssStyleVariants.values
                    .filterIsInstance<SimpleCssStyleVariant<*>>()
                    .map { it.cssStyle }

        // Do a sorting pass (useful for ensuring that extending styles are always declared with later styles appearing
        // after extended-from styles)
        val allCssStylesSorted = orderStyles(allCssStyles, mutableSilkTheme.cssStyleDependencies)
        allCssStylesSorted.forEach { style ->
            val className = nameFor(style)
            val layer = mutableSilkTheme.cssLayersFor[className]
            val classSelectors = style.addStylesInto(".$className", stylesheet, layer)
            _cssStyles[style] = style.intoImmutableStyle(classSelectors)
        }

        mutableSilkTheme.replacedCssStyles.forEach { (originalStyle, overrideStyle) ->
            // Register styles against original style values. This is useful so someone can replace the original
            // style property and not have the code break, e.g.
            //
            // ```
            // val SomeStyle = CssStyle { ... }
            // // Later...
            // ctx.theme.replaceStyle(SomeStyle) { ... }
            // // Later still...
            // val modifier = SomeStyle.toModifier() // This style has an obsolete style associated with it
            _cssStyles[originalStyle] = _cssStyles.getValue(overrideStyle)
        }
    }
}

/**
 * Return the class name associated with the given [CssStyle].
 *
 * While it is technically possible for this to crash (create a `CssStyle` and never register it), it is safe to
 * dereference this (i.e. `!!`) on any style that has been declared as a
 * property (e.g. `val MyStyle = CssStyle { ... }`).
 */
val CssStyle<*>.name
    get() = SilkTheme.nameFor(this)
        ?: error("Name requested for invalid CssStyle. This should only be called on top-level public styles or styles that got manually registered")

/**
 * Return the class name associated with the given [CssStyleVariant].
 *
 * While this can crash if used on a composite variant (e.g. `FirstVariant.then(SecondVariant).name`, it is safe to
 * dereference this (i.e. `!!`) on any variant that has been declared as a
 * property (e.g. `val MyVariant = SomeStyle.addVariant { ... }`).
 */
val CssStyleVariant<*>.name
    get() = (this as? SimpleCssStyleVariant<*>)?.let { simpleVariant ->
        SilkTheme.nameFor(simpleVariant.cssStyle)
    }
        ?: error("Name requested for invalid CssStyleVariant. Did you call this on a composite variant (e.g. `FirstVariant.then(SecondVariant)`?)")

/**
 * Return the class name associated with the given [Keyframes].
 *
 * While it is technically possible for this to crash (create a `Keyframes` and never register it), it is safe to
 * dereference this (i.e. `!!`) on any keyframes that has been declared as a
 * property (e.g. `val MyKeyframes = Keyframes { ... }`).
 */
val Keyframes.name
    get() = SilkTheme.nameFor(this)
        ?: error("Name requested for invalid Keyframes. This should only be called on top-level public keyframes or keyframes that got manually registered")

internal var _SilkTheme: ImmutableSilkTheme? = null
val SilkTheme: ImmutableSilkTheme
    get() {
        return _SilkTheme ?: error("You can't access SilkTheme before first calling `prepareSilkFoundation` (or `SilkApp`, which calls it)")
    }
