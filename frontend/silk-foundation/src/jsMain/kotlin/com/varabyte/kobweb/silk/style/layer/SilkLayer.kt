package com.varabyte.kobweb.silk.style.layer

import com.varabyte.kobweb.compose.style.KOBWEB_COMPOSE_LAYER
import com.varabyte.kobweb.silk.init.SilkStylesheet

/**
 * A collection of all CSS layers managed by Silk.
 *
 * The precedence order is: reset < kobweb-compose < base < component styles < component variants < restricted styles < general styles
 *
 * The **reset** layer is meant for any style that exists to reset / override default styles provided by browsers,
 * sometimes in order to make behavior consistent when different browsers provide diverging styles, or because browsers
 * default to legacy styles that exist for backwards compatibility but that most modern sites change (particularly
 * `box-sizing`). Most users are not expected to use this layer.
 *
 * The **kobweb-compose** layer is used by the `kobweb-compose` module, which defines various layout widgets inspired by
 * Jetpack Compose. When using Silk, we place it here as they are meant to have a fairly low precedence.
 *
 * The **base** layer is meant for general low precedence styles, things that the user might want to define as
 * global styles for their site but that should be overridable by anything else that also targets the same element.
 *
 * Silk does not use the base layer itself but exposes it for users. The recommended code pattern for applying styles to
 * a base layer is:
 *
 * ```
 * @InitSilk
 * fun initSilk(ctx: InitSilkContext) {
 *    ctx.stylesheet.apply {
 *      layer(SilkLayer.BASE) {
 *        registerStyle("a")
 *      }
 *    }
 * }
 * ```
 *
 *
 * The remaining styles apply to the different cases of CSS style blocks.
 *
 * Imagine code like this:
 * ```
 * interface WidgetKind
 * val WidgetStyle = CssStyle<WidgetKind> { ... }
 * class SomeParam(...) : CssStyle.Restricted(...)
 * fun Widget(modifier: Modifier, variant: CssStyleVariant<WidgetKind>, someParam: SomeParam) {
 *   val finalModifier = WidgetStyle.toModifier(variant)
 *      .then(someParam.toModifier())
 *      .then(modifier)
 * }
 * ```
 *
 * Called like this:
 * ```
 * val MyStyle = CssStyle { ... }
 * val MyWidgetVariant = WidgetStyle.addVariant { ... }
 * Widget(MyStyle.toModifier(), MyWidgetVariant, SomeParam.Value)
 * ```
 *
 * Here, we would expect any variant to override the style, any parameter to override the variant, and any
 * user style passed into the modifier value to override everything else.
 *
 * Finally, note that users can add their own custom layers, using the [SilkStylesheet.cssLayers] property.
 */
enum class SilkLayer(val layerName: String) {
    /**
     * A layer for resetting default CSS values provided by browsers.
     *
     * This layer is useful for cleaning up inconsistencies in different browser styles or overriding legacy styles
     * that most modern sites don't use but browsers supply for compatibility with older sites.
     */
    RESET("reset"),

    /**
     * A layer reserved for Kobweb Compose widget styles (like `Box`, `Column`, and `Row`).
     */
    KOBWEB_COMPOSE(KOBWEB_COMPOSE_LAYER),

    /**
     * A fairly low-precedence layer provided to users for specifying styles that should not override CssStyle blocks.
     */
    BASE("base"),

    /**
     * A layer used for styles created by `CssStyle<ComponentKind> { ... }` blocks.
     */
    COMPONENT_STYLES("component-styles"),

    /**
     * A layer used for styles created by `SomeComponentStyle.addVariant { ... }` blocks.
     */
    COMPONENT_VARIANTS("component-variants"),

    /**
     * A layer used for styles created by classes that implement `CssStyle.Restricted`
     */
    RESTRICTED_STYLES("restricted-styles"),

    /**
     * A layer used for styles created by `CssStyle { ... }` blocks.
     */
    GENERAL_STYLES("general-styles");
}
