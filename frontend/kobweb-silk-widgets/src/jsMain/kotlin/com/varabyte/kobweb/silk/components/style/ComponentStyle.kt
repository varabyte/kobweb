package com.varabyte.kobweb.silk.components.style

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.StyleModifier
import com.varabyte.kobweb.compose.ui.modifiers.classNames
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.compose.ui.toStyles
import com.varabyte.kobweb.silk.components.style.CssModifier.Companion.BaseKey
import com.varabyte.kobweb.silk.components.style.breakpoint.Breakpoint
import com.varabyte.kobweb.silk.theme.SilkTheme
import com.varabyte.kobweb.silk.theme.breakpoint.toMinWidthQuery
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import com.varabyte.kobweb.silk.theme.colors.getColorMode
import com.varabyte.kobweb.silk.theme.colors.suffixedWith
import com.varabyte.kobweb.silk.util.titleCamelCaseToKebabCase
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.css.*
import org.w3c.dom.Element
import kotlin.reflect.KProperty

/**
 * Represents a [Modifier] entry that is tied to a css rule, e.g. the modifier for ".myclass:hover" for example.
 */
internal class CssModifier(
    val modifier: Modifier,
    val mediaQuery: CSSMediaQuery? = null,
    val suffix: String? = null,
) {
    init {
        modifier.fold(Unit) { _, currModifier ->
            if (currModifier !is StyleModifier) {
                throw IllegalArgumentException(
                    """
                        You are attempting to construct a ComponentStyle or ComponentVariant with a non-style Modifier
                        (e.g. `id`, `tabIndex`, etc.). Due to technical limitations in html / css, only `StyleModifier`s
                        are allowed in this context.

                        Unfortunately, at the point this exception is getting thrown, information about the offending
                        attribute is not known. Please audit your project's ComponentStyle and ComponentVariant
                        Modifiers, perhaps commenting out recently added ones, until this exception goes away.

                        Once the offending modifier is identified, to fix this, you can either call attribute modifiers
                        directly on the Modifier you pass into some widget, or you can extend this Style or Variant with
                        extra modifiers by passing them in the `extraModifier` parameter:

                        ```
                        // Approach #1: Call Attribute Modifiers later

                        val ExampleStyle = ComponentStyle("ex") {
                           ...
                        }

                        ExampleWidget(ExampleStyle.toModifier().tabIndex(0))

                        // Approach #2: Use `extraModifiers`:

                        val ExampleStyle = ComponentStyle("ex", extraModifiers = Modifier.tabIndex(0)) {
                           ...
                        }

                        ExampleWidget(ExampleStyle.toModifier())
                        ```
                    """.trimIndent()
                )
            }
        }
    }

    companion object {
        val BaseKey = Key(null, null)
    }
    data class Key(val mediaQuery: String?, val suffix: String?)

    /**
     * A key useful for storing this entry into a map.
     *
     * If two [CssModifier] instances have the same key, that means they would evaluate to the same CSS rule. This
     * can indicate if a css rule was applied redundantly (where the latter would overrule the former) or allow us to
     * compare modifiers across dark and light color modes.
     */
    // Note: We have to convert mediaQuery toString for now because it CSSMediaQuery.MediaFeature is not itself defined
    // correctly for equality checking (for some reason, they don't define the hashcode)
    val key get() = Key(mediaQuery?.toString(), suffix)
}

/**
 * Class used as the receiver to a callback, allowing the user to define various state-dependent styles (defined via
 * [Modifier]s).
 *
 * See also [ComponentModifiers] for places where you can define component styles which have a few extra features
 * enabled for them.
 */
abstract class StyleModifiers {
    private val _cssModifiers = mutableListOf<CssModifier>()
    internal val cssModifiers: List<CssModifier> = _cssModifiers

    /** Define base styles for this component. This will always be applied first. */
    fun base(createModifier: () -> Modifier) {
        _cssModifiers.add(CssModifier(createModifier()))
    }

    /**
     * Add a CSS rule that is applied to this component class, passing in a [suffix] (which represents a pseudo-class
     * or pseudo-element) and a [mediaQuery] entry if the style should be defined within a media rule.
     *
     * CSS rules will always be applied in the order they were registered in.
     *
     * See also:
     *   https://developer.mozilla.org/en-US/docs/Web/CSS/@media
     *   https://developer.mozilla.org/en-US/docs/Web/CSS/Pseudo-classes
     *   https://developer.mozilla.org/en-US/docs/Web/CSS/Pseudo-elements
     */
    fun cssRule(mediaQuery: CSSMediaQuery?, suffix: String?, createModifier: () -> Modifier) {
        _cssModifiers.add(CssModifier(createModifier(), mediaQuery, suffix))
    }

    fun cssRule(suffix: String, createModifier: () -> Modifier) {
        _cssModifiers.add(CssModifier(createModifier(), null, suffix))
    }

    fun cssRule(mediaQuery: CSSMediaQuery, createModifier: () -> Modifier) {
        _cssModifiers.add(CssModifier(createModifier(), mediaQuery))
    }

    /**
     * Convenience function for associating a modifier directly against a breakpoint enum.
     *
     * For example, you can call
     *
     * ```
     * Breakpoint.MD { Modifier.color(...) }
     * ```
     *
     * which is identical to:
     *
     * ```
     * cssRule(CSSMediaQuery.MediaFeature("min-width", ...)) { Modifier.color(...) }
     * ```
     *
     * Note: This probably would have been an extension method except Kotlin doesn't support multiple receivers yet
     * (here, we'd need to access both "Breakpoint" and "ComponentModifiers")
     */
    operator fun Breakpoint.invoke(createModifier: () -> Modifier) {
        cssRule(this.toMinWidthQuery(), createModifier)
    }
}

/**
 * State specific to [ComponentStyle] initialization but not the more general [StyleModifiers] case.
 *
 * For example, color mode is supported here:
 *
 * ```
 * val MyWidgetStyle = ComponentStyle("my-widget") {
 *    ...
 * }
 * ```
 *
 * but not here:
 *
 * ```
 * @InitSilk
 * fun initSilk(ctx: InitSilkContext) {
 *   ctx.config.registerStyle("body") {
 *     ...
 *   }
 * }
 * ```
 */
interface ComponentModifier {
    /**
     * The current color mode, which may impact the look and feel of the current component style.
     */
    val colorMode: ColorMode
}

class ComponentModifiers(override val colorMode: ColorMode): ComponentModifier, StyleModifiers()
/**
 * Class provided for cases where you only generate a single style (e.g. base), unlike [ComponentModifiers] where you
 * can define a collection of styles.
 */
class ComponentBaseModifier(override val colorMode: ColorMode): ComponentModifier

/**
 * A [ComponentStyle] pared down to read-only data only, which should happen shortly after Silk initializes.
 *
 * @param extraModifiers Additional modifiers that can be tacked onto this component style, convenient for including
 *   non-style attributes whenever this style is applied.
 */
class ImmutableComponentStyle internal constructor(
    private val name: String,
    private val extraModifiers: Modifier = Modifier
) {
    @Composable
    fun toModifier(): Modifier {
        val classNames = listOf(name, name.suffixedWith(getColorMode()))
            .filter { name -> ComponentStyle.registeredClasses.contains(name) }

        return (if (classNames.isNotEmpty()) Modifier.classNames(*classNames.toTypedArray()) else Modifier)
            .then(extraModifiers)
    }
}

/**
 * Convenience method when you only care about registering the base style, which can help avoid a few extra lines.
 *
 * So this:
 *
 * ```
 * ComponentStyle.base("custom-widget") {
 *   Modifier.fontSize(48.px)
 * }
 * ```
 *
 * replaces this:
 *
 * ```
 * ComponentStyle("custom-widget") {
 *   base {
 *     Modifier.fontSize(48.px)
 *   }
 * }
 * ```
 *
 * You may still wish to construct a [ComponentStyle] directly instead if you expect that at some point in the future
 * you'll want to add additional, non-base styles.
 */
fun ComponentStyle.Companion.base(className: String, extraModifiers: Modifier = Modifier, init: ComponentModifier.() -> Modifier): ComponentStyle {
    return ComponentStyle(className, extraModifiers) {
        base {
            ComponentBaseModifier(colorMode).let(init)
        }
    }
}


private sealed interface StyleGroup {
    class Light(val styles: ComparableStyleScope) : StyleGroup
    class Dark(val styles: ComparableStyleScope) : StyleGroup
    class ColorAgnostic(val styles: ComparableStyleScope) : StyleGroup
    class ColorAware(val lightStyles: ComparableStyleScope, val darkStyles: ComparableStyleScope) : StyleGroup

    companion object {
        @Suppress("NAME_SHADOWING") // Shadowing used to turn nullable into non-null
        fun from(lightModifiers: Modifier?, darkModifiers: Modifier?): StyleGroup? {
            val lightStyles = lightModifiers?.let { lightModifiers ->
                ComparableStyleScope().apply { lightModifiers.toStyles().invoke(this) }
            }
            val darkStyles = darkModifiers?.let { darkModifiers ->
                ComparableStyleScope().apply { darkModifiers.toStyles().invoke(this) }
            }

            if (lightStyles == null && darkStyles == null) return null
            if (lightStyles != null && darkStyles == null) return Light(lightStyles)
            if (lightStyles == null && darkStyles != null) return Dark(darkStyles)
            check(lightStyles != null && darkStyles != null)
            return if (lightStyles == darkStyles) {
                ColorAgnostic(lightStyles)
            } else {
                ColorAware(lightStyles, darkStyles)
            }
        }
    }
}

/**
 * A class which allows a user to define styles that get added to the page's stylesheet, instead of inline styles.
 *
 * This is important because some functionality is only available when defined in the stylesheet, e.g. link colors,
 * media queries, and psuedo classes.
 *
 * If defining a style for a custom widget, you should call the [toModifier] method to apply it:
 *
 * ```
 * val CustomStyle = ComponentStyle("my-style") { ... }
 *
 * @Composable
 * fun CustomWidget(..., variant: ComponentVariant? = null, ...) {
 *   val modifier = CustomStyle.toModifier(variant).then(...)
 *   // ^ This modifier is now set with your registered styles.
 * }
 * ```
 *
 * @param extraModifiers Additional modifiers that can be tacked onto this component style, convenient for including
 *   non-style attributes whenever this style is applied.
 */
class ComponentStyle(
    val name: String,
    private val extraModifiers: Modifier = Modifier,
    private val init: ComponentModifiers.() -> Unit,
) {
    companion object {
        /**
         * A set of all classnames we have styles registered against.
         *
         * This lets us avoid applying unnecessary classnames, which really helps cut down on the number of class tags
         * we use, making it easier to debug CSS issues in the browser.
         *
         * An example can help clarify here. Let's say you define a color-aware style:
         *
         * ```
         * ComponentStyle("custom") { colorMode ->
         *   base {
         *     Modifier.color(if (colorMode.isLight()) Colors.Red else Colors.Pink)
         *   }
         * }
         * ```
         *
         * For this, you only need the class name "custom-dark" (in dark mode) and "custom-light"; if instead it was
         * color-agnostic, we would only need "custom". Before, we were applying all color modes because we weren't sure
         * what was defined and what wasn't.
         *
         * In some cases, we have empty styles too, as hooks that user's can optionally replace if they want to.
         * Now, as long as the style remains empty, we won't add any tagging at all to the user's elements.
         */
        internal val registeredClasses = mutableSetOf<String>()

        /**
         * Handle being notified of a selector name, e.g. ".someStyle" or ".someStyle.someVariant"
         */
        // Note: This is kind of a hack, but since we can't currently query the StyleSheet for selector names, what we
        // do instead is update our internal list as we run through all ComponentStyles.
        private fun notifySelectorName(selectorName: String) {
            selectorName.split('.').filter { it.isNotEmpty() }.forEach { registeredClasses.add(it) }
        }
    }

    internal val variants = mutableListOf<ComponentVariant>()

    /**
     * Create a new variant that builds on top of this style.
     *
     *  @param extraModifiers Additional modifiers that can be tacked onto this component style, convenient for
     *    including non-style attributes that should always get applied whenever this variant style is applied.
     */
    fun addVariant(
        name: String,
        extraModifiers: Modifier = Modifier,
        init: ComponentModifiers.() -> Unit
    ): ComponentVariant {
        return SimpleComponentVariant(
            ComponentStyle("${this.name}-$name", extraModifiers, init),
            baseStyle = this
        ).also {
            variants.add(it)
        }
    }

    /**
     * @param cssRule A selector plus an optional pseudo keyword (e.g. "a", "a:link", and "a::selection")
     */
    private fun <T : StyleScope> GenericStyleSheetBuilder<T>.addStyles(cssRule: String, styles: ComparableStyleScope) {
        cssRule style {
            styles.properties.forEach { entry -> property(entry.key, entry.value) }
            styles.variables.forEach { entry -> variable(entry.key, entry.value) }
        }
    }

    /**
     * Shared logic for using an initial selector name and triggering a callback with the final selector name and
     * CSS styles to be associated with it.
     */
    private fun withFinalSelectorName(selectorBaseName: String, group: StyleGroup, handler: (String, ComparableStyleScope) -> Unit) {
        when (group) {
            is StyleGroup.Light -> handler(selectorBaseName.suffixedWith(ColorMode.LIGHT), group.styles)
            is StyleGroup.Dark -> handler(selectorBaseName.suffixedWith(ColorMode.DARK), group.styles)
            is StyleGroup.ColorAgnostic -> handler(selectorBaseName, group.styles)
            is StyleGroup.ColorAware -> {
                handler(selectorBaseName.suffixedWith(ColorMode.LIGHT), group.lightStyles)
                handler(selectorBaseName.suffixedWith(ColorMode.DARK), group.darkStyles)
            }
        }
    }

    internal fun addStylesInto(styleSheet: StyleSheet, selectorName: String) {
        val lightModifiers = ComponentModifiers(ColorMode.LIGHT).apply(init).cssModifiers.associateBy { it.key }
        val darkModifiers = ComponentModifiers(ColorMode.DARK).apply(init).cssModifiers.associateBy { it.key }

        StyleGroup.from(lightModifiers[BaseKey]?.modifier, darkModifiers[BaseKey]?.modifier)?.let { group ->
            withFinalSelectorName(selectorName, group) { name, styles ->
                if (styles.isNotEmpty()) {
                    notifySelectorName(name)
                    styleSheet.addStyles(name, styles)
                }
            }
        }

        val allCssRuleKeys = (lightModifiers.keys + darkModifiers.keys).filter { it != BaseKey }
        for (cssRuleKey in allCssRuleKeys) {
            StyleGroup.from(lightModifiers[cssRuleKey]?.modifier, darkModifiers[cssRuleKey]?.modifier)?.let { group ->
                withFinalSelectorName(selectorName, group) { name, styles ->
                    if (styles.isNotEmpty()) {
                        notifySelectorName(name)

                        val cssRule = "$name${cssRuleKey.suffix.orEmpty()}"
                        if (cssRuleKey.mediaQuery != null) {
                            styleSheet.apply {
                                media(cssRuleKey.mediaQuery) {
                                    addStyles(cssRule, styles)
                                }
                            }
                        } else {
                            styleSheet.addStyles(cssRule, styles)
                        }
                    }
                }
            }
        }
    }

    /**
     * Add this [ComponentStyle]'s styles to the target [StyleSheet]
     */
    internal fun addStylesInto(styleSheet: StyleSheet) {
        // Register styles associated with this style's classname
        addStylesInto(styleSheet, ".$name")
    }

    internal fun intoImmutableStyle() = ImmutableComponentStyle(name, extraModifiers)
}

/**
 * A delegate provider class which allows you to create a [ComponentStyle] via the `by` keyword.
 */
class ComponentStyleProvider internal constructor(
    private val extraModifiers: Modifier = Modifier,
    private val init: ComponentModifiers.() -> Unit,
) {
    operator fun getValue(
        thisRef: Any?,
        property: KProperty<*>
    ): ComponentStyle {
        // e.g. "TitleTextStyle" to "title-text"
        val name = property.name.removeSuffix("Style").titleCamelCaseToKebabCase()
        return ComponentStyle(name, extraModifiers, init)
    }
}

fun ComponentStyle(extraModifiers: Modifier = Modifier, init: ComponentModifiers.() -> Unit)
    = ComponentStyleProvider(extraModifiers, init)

fun ComponentStyle.Companion.base(extraModifiers: Modifier = Modifier, init: ComponentBaseModifier.() -> Modifier)
    = ComponentStyleProvider(extraModifiers, init = { base { ComponentBaseModifier(colorMode).let(init) }})


/**
 * A delegate provider class which allows you to create a [ComponentVariant] via the `by` keyword.
 */
class ComponentVariantProvider internal constructor(
    private val style: ComponentStyle,
    private val extraModifiers: Modifier = Modifier,
    private val init: ComponentModifiers.() -> Unit,
) {
    operator fun getValue(
        thisRef: Any?,
        property: KProperty<*>
    ): ComponentVariant {
        // e.g. "OutlinedTitleTextVariant" to "outlined", assuming it's a variant for "TitleTextStyle"
        val withoutSuffix = property.name.removeSuffix("Variant").titleCamelCaseToKebabCase()
        // Unlikely, but protect against "val TextVariant by TextStyle.addVariant { ... }" ending up with an empty
        // string. (The user should have called it *Something*TextVariant but no guarantee a user won't be lazy)
        val name = withoutSuffix.removeSuffix("-${style.name}").takeIf { it.isNotEmpty() } ?: withoutSuffix
        return style.addVariant(name, extraModifiers, init)
    }
}

fun ComponentStyle.addVariant(extraModifiers: Modifier = Modifier, init: ComponentModifiers.() -> Unit)
    = ComponentVariantProvider(this, extraModifiers, init)

fun ComponentStyle.addVariantBase(extraModifiers: Modifier = Modifier, init: ComponentBaseModifier.() -> Modifier)
    = ComponentVariantProvider(this, extraModifiers, init = { base { ComponentBaseModifier(colorMode).let(init) }})

/**
 * Convenience method when you only care about registering the base style, which can help avoid a few extra lines.
 *
 * You may still wish to use [ComponentStyle.addVariant] instead if you expect that at some point in the future
 * you'll want to add additional, non-base styles.
 */
fun ComponentStyle.addVariantBase(
    name: String,
    extraModifiers: Modifier = Modifier,
    init: ComponentBaseModifier.() -> Modifier
): ComponentVariant {
    return addVariant(name, extraModifiers) {
        base {
            ComponentBaseModifier(colorMode).let(init)
        }
    }
}

sealed class ComponentVariant {
    object Empty : ComponentVariant() {
        override fun addStylesInto(styleSheet: StyleSheet) = Unit
        @Composable
        override fun toModifier() = Modifier
    }

    infix fun then(next: ComponentVariant): ComponentVariant {
        return if (next === Empty) this
        else if (this === Empty) next
        else CompositeComponentVariant(this, next)
    }

    internal abstract fun addStylesInto(styleSheet: StyleSheet)
    @Composable
    abstract fun toModifier(): Modifier
}

fun ComponentVariant.thenIf(condition: Boolean, produce: () -> ComponentVariant): ComponentVariant {
    return this
        .then(if (condition) produce() else ComponentVariant.Empty)
}

fun ComponentVariant.thenUnless(condition: Boolean, produce: () -> ComponentVariant): ComponentVariant {
    return this.thenIf(!condition, produce)
}

fun ComponentVariant.thenIf(condition: Boolean, other: ComponentVariant): ComponentVariant {
    return this.thenIf(condition) { other }
}

fun ComponentVariant.thenUnless(condition: Boolean, other: ComponentVariant): ComponentVariant {
    return this.thenUnless(condition) { other }
}

/**
 * A default [ComponentVariant] implementation that represents a single variant style.
 */
class SimpleComponentVariant(
    internal val style: ComponentStyle,
    internal val baseStyle: ComponentStyle,
) : ComponentVariant() {
    /**
     * The raw variant name, unqualified by its parent base style.
     *
     * This name is not guaranteed to be unique across all variants. If you need that, check `style.name` instead.
     */
    val name: String
        get() = style.name.removePrefix("${baseStyle.name}-")

    override fun addStylesInto(styleSheet: StyleSheet) {
        // If you are using a variant, require it be associated with a tag already associated with the base style
        // e.g. if you have a link variant ("silk-link-undecorated") it should only be applied if the tag is also
        // a link (so this would be registered as ".silk-link.silk-link-undecorated").
        // To put it another way, if you use a link variant with a surface widget, it won't be applied.
        style.addStylesInto(styleSheet, ".${baseStyle.name}.${style.name}")
    }

    @Composable
    override fun toModifier() = style.toModifier()
    internal fun intoImmutableStyle() = style.intoImmutableStyle()
}

private class CompositeComponentVariant(private val head: ComponentVariant, private val tail: ComponentVariant): ComponentVariant() {
    override fun addStylesInto(styleSheet: StyleSheet) {
        head.addStylesInto(styleSheet)
        tail.addStylesInto(styleSheet)
    }

    @Composable
    override fun toModifier() = head.toModifier().then(tail.toModifier())
}

/**
 * Convert a user's component style into a [Modifier].
 *
 * @param variants 0 or more variants that can potentially extend the base style. Although it may seem odd at first that
 *   nullable values are accepted here, that's because Silk widgets all default their `variant` parameter to null, so
 *   it's easier to just accept null here rather than require users to branch based on whether the variant is null or
 *   not.
 */
@Composable
fun ComponentStyle.toModifier(vararg variants: ComponentVariant?): Modifier {
    return SilkTheme.componentStyles.getValue(name).toModifier().then(variants.toList().combine().toModifier())
}

/**
 * Convert a user's component style into an [AttrsScope] builder.
 *
 * This is useful if you need to convert a style into something directly consumable by a Compose for Web widget.
 */
@Composable
fun <T: Element, A: AttrsScope<T>> ComponentStyle.toAttrs(variant: ComponentVariant? = null, finalHandler: (A.() -> Unit)? = null): A.() -> Unit {
    return this.toModifier(variant).toAttrs(finalHandler)
}

/**
 * A convenience method for chaining a collection of styles into a single modifier.
 *
 * This can be useful as sometimes you might break up many css rules across multiple styles for re-usability, and it's
 * much easier to type `listOf(Style1, Style2, Style3).toModifier()` than
 * `Style1.toModifier().then(Style2.toModifier())...`
 */
@Composable
fun Iterable<ComponentStyle>.toModifier(): Modifier {
    var finalModifier: Modifier = Modifier
    for (style in this) {
        finalModifier = finalModifier.then(style.toModifier())
    }
    return finalModifier
}

/**
 * A convenience method for chaining a collection of styles into a single [AttrsScope] builder.
 */
@Composable
fun <T: Element, A: AttrsScope<T>> Iterable<ComponentStyle>.toAttrs(finalHandler: (A.() -> Unit)? = null): A.() -> Unit {
    return this.toModifier().toAttrs(finalHandler)
}

/**
 * A convenience method for folding a list of component variants into one single one that represents all of them.
 */
@Composable
fun Iterable<ComponentVariant?>.combine(): ComponentVariant {
    var finalVariant: ComponentVariant = ComponentVariant.Empty
    for (variant in this) {
        finalVariant = finalVariant.then(variant ?: ComponentVariant.Empty)
    }
    return finalVariant
}
