package com.varabyte.kobweb.silk.style

import com.varabyte.kobweb.silk.init.SilkStylesheet

/**
 * An annotation that can be applied to a [CssStyle] property to override its default layer.
 *
 * For example, let's say you were having issues with two styles colliding with each other, and you always wanted one to
 * be applied after the other. You could introduce an "important" layer to one of them to ensure that it always takes
 * precedence:
 *
 * ```
 * val RegularStyle = CssStyle.base { /* ... */ }
 *
 * @CssLayer("important")
 * val ImportantStyle = CssStyle.base { /* ... */ }
 * ```
 *
 * Once done, you are expected to additionally register the layer with Silk:
 *
 * ```
 * @InitSilk
 * fun initSilk(ctx: InitSilkContext) {
 *   ctx.stylesheet.cssLayers.add("important")
 * }
 * ```
 *
 * If not done, CSS will make a best-effort guess, adding the layer at a high priority location, but registering the
 * layer explicitly gives you control, especially if you declare multiple layers:
 *
 * ```
 * @InitSilk
 * fun initSilk(ctx: InitSilkContext) {
 *   ctx.stylesheet.cssLayers.add("important", "more-important", "most-important")
 * }
 * ```
 *
 * In general, it is not recommended for users to override a style's layer. It can make style issues that are difficult
 * to debug and understand. However, it can be useful in certain cases.
 *
 * @see [SilkStylesheet.cssLayers]
 * @see [CssStyle]
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY)
annotation class CssLayer(val name: String)
