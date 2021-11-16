package com.varabyte.kobweb.silk.theme

import androidx.compose.runtime.*
import com.varabyte.kobweb.silk.components.style.ComponentStyle
import com.varabyte.kobweb.silk.components.style.ComponentStyleBuilder
import com.varabyte.kobweb.silk.components.style.ComponentVariant
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import com.varabyte.kobweb.silk.theme.colors.DarkSilkPalette
import com.varabyte.kobweb.silk.theme.colors.LightSilkPalette
import com.varabyte.kobweb.silk.theme.colors.SilkPalette
import com.varabyte.kobweb.silk.theme.colors.SilkPalettes
import com.varabyte.kobweb.silk.theme.colors.getColorMode
import org.jetbrains.compose.web.css.StyleSheet

interface SilkConfig {
    var initialColorMode: ColorMode
}

internal object SilkConfigInstance : SilkConfig {
    override var initialColorMode = ColorMode.LIGHT
}

class MutableSilkTheme {
    internal val componentStyles = LinkedHashMap<String, ComponentStyleBuilder>() // Preserve insertion order
    internal val componentVariants = LinkedHashMap<String, ComponentVariant>() // Preserve insertion order

    var palettes = SilkPalettes(LightSilkPalette, DarkSilkPalette)

    /**
     * Register a new component style with this theme.
     *
     * If this style has defined additional variants, they will also be registered automatically at this time.
     *
     * Once a style is registered, you can reference it in your Composable widget via the following code:
     *
     * ```
     * val CustomStyle = ComponentStyle("my-style") { ... }
     *
     * @InitSilk
     * fun initCustomStyle(ctx: InitSilkContext) {
     *   ctx.theme.registerComponentStyle(CustomStyle)
     * }
     *
     * @Composable
     * fun CustomWidget(..., variant: ComponentVariant? = null, ...) {
     *   val modifier = CustomStyle.toModifier(variant).then(...)
     *   // ^ This modifier is now set with your registered styles.
     * }
     * ```
     */
    fun registerComponentStyle(style: ComponentStyleBuilder) {
        componentStyles[style.name] = style
        registerComponentVariants(*style.variants.toTypedArray())
    }

    /**
     * Register variants associated with a base style.
     *
     * **NOTE:** Most of the time, you don't have to call this yourself, as if you create a custom style with a bunch of
     * variants on it, calling [registerComponentStyle] will automatically register them for you.
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

    internal fun registerStyles(componentStyleSheet: StyleSheet) {
        check(::SilkTheme.isInitialized) // Call only after SilkTheme is set
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