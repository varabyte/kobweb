@file:Suppress("FunctionName")

package com.varabyte.kobweb.silk.components.style

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.StyleModifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.compose.ui.toStyles
import com.varabyte.kobweb.compose.util.titleCamelCaseToKebabCase
import com.varabyte.kobweb.silk.components.style.CssModifier.Companion.BaseKey
import com.varabyte.kobweb.silk.components.style.breakpoint.Breakpoint
import com.varabyte.kobweb.silk.components.util.internal.CacheByPropertyNameDelegate
import com.varabyte.kobweb.silk.theme.SilkTheme
import com.varabyte.kobweb.silk.theme.breakpoint.toMinWidthQuery
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import com.varabyte.kobweb.silk.theme.colors.suffixedWith
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.css.*
import org.w3c.dom.Element

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
        // We use this key to represent the base CSS rule, which is always applied first
        internal val BaseKey = Key(null, null)
    }

    data class Key(val mediaQuery: String?, val suffix: String?)

    /**
     * A key useful for storing this entry into a map.
     *
     * If two [CssModifier] instances have the same key, that means they would evaluate to the same CSS rule. Although
     * we don't expect this to happen in practice, if it does, then both selectors can be merged. We can also use this
     * key to test a light and dark version of the same component style to see if this particular selector is the same
     * or not across the two.
     */
    // Note: We have to convert mediaQuery toString for now because CSSMediaQuery.MediaFeature is not itself defined
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
 * val MyWidgetStyle by ComponentStyle {
 *    ...
 * }
 * ```
 *
 * but not here:
 *
 * ```
 * @InitSilk
 * fun initSilk(ctx: InitSilkContext) {
 *   ctx.stylesheet.registerStyle("body") {
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

class ComponentModifiers(override val colorMode: ColorMode) : ComponentModifier, StyleModifiers()

/**
 * Class provided for cases where you only generate a single style (e.g. base), unlike [ComponentModifiers] where you
 * can define a collection of styles.
 */
class ComponentBaseModifier(override val colorMode: ColorMode) : ComponentModifier

/**
 * A [ComponentStyle] pared down to read-only data only, which should happen shortly after Silk initializes.
 *
 * @param extraModifiers Additional modifiers that can be tacked onto this component style, convenient for including
 *   non-style attributes whenever this style is applied.
 */
class ImmutableComponentStyle internal constructor(
    private val name: String,
    private val extraModifiers: @Composable () -> Modifier
) {
    @Composable
    fun toModifier(): Modifier {
        val classNames = listOf(name, name.suffixedWith(ColorMode.current))
            .filter { name -> ComponentStyle.registeredClasses.contains(name) }

        return (if (classNames.isNotEmpty()) Modifier.classNames(*classNames.toTypedArray()) else Modifier)
            .then(extraModifiers())
    }
}

/**
 * Convenience method when you only care about registering the base style, which can help avoid a few extra lines.
 *
 * So this:
 *
 * ```
 * ComponentStyle.base {
 *   Modifier.fontSize(48.px)
 * }
 * ```
 *
 * replaces this:
 *
 * ```
 * ComponentStyle {
 *   base {
 *     Modifier.fontSize(48.px)
 *   }
 * }
 * ```
 *
 * You may still wish to construct a [ComponentStyle] directly instead if you expect that at some point in the future
 * you'll want to add additional, non-base styles.
 */
fun ComponentStyle.Companion.base(
    className: String,
    extraModifiers: Modifier = Modifier,
    init: ComponentModifier.() -> Modifier
): ComponentStyle {
    return base(className, { extraModifiers }, init)
}

fun ComponentStyle.Companion.base(
    className: String,
    extraModifiers: @Composable () -> Modifier,
    init: ComponentModifier.() -> Modifier
): ComponentStyle {
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
 * media queries, and pseudo classes.
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
 * @param name The name of the style, which will be used as the CSS class name.
 * @param extraModifiers Additional modifiers that can be tacked onto this component style, convenient for including
 *   non-style attributes whenever this style is applied.
 * @param prefix An optional prefix to prepend in front of the style name, as a helpful tool for reducing the chance of
 *   style name collisions. (Note: unless you are a library author, it's not expected you'll set this.) Why not just put
 *   the prefix directly in the name itself? We allow separating it out since you can use delegation to create a style,
 *   at which point the name will be derived from the style's property name. In contrast, a prefix will be manually
 *   chosen. For a concrete example, `val ButtonStyle by ComponentStyle(prefix = "silk")` creates the full name
 *   `"silk-button"`). Also, when creating a variant by delegation, it is useful to know the non-prefixed name of the
 *   style it is based on when creating a name for it.
 */
class ComponentStyle(
    name: String,
    internal val extraModifiers: @Composable () -> Modifier,
    val prefix: String? = null,
    internal val init: ComponentModifiers.() -> Unit,
) {
    init {
        require(name.isNotEmpty()) { "ComponentStyle name must not be empty" }
    }

    internal val nameWithoutPrefix = name
    val name = prefix?.let { "$it-$name" } ?: name

    constructor(
        name: String,
        extraModifiers: Modifier = Modifier,
        prefix: String? = null,
        init: ComponentModifiers.() -> Unit
    )
        : this(name, { extraModifiers }, prefix, init)

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
         *     Modifier.color(if (colorMode.isLight) Colors.Red else Colors.Pink)
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
        return addVariant(name, { extraModifiers }, init)
    }

    /**
     * Create a new variant that builds on top of this style.
     *
     *  @param extraModifiers Additional modifiers that can be tacked onto this component style, convenient for
     *    including non-style attributes that should always get applied whenever this variant style is applied.
     */
    fun addVariant(
        name: String,
        extraModifiers: @Composable () -> Modifier,
        init: ComponentModifiers.() -> Unit
    ): ComponentVariant {
        return SimpleComponentVariant(
            ComponentStyle("${this.name}-$name", extraModifiers, prefix = null, init),
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
    private fun withFinalSelectorName(
        selectorBaseName: String,
        group: StyleGroup,
        handler: (String, ComparableStyleScope) -> Unit
    ) {
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
        // Always add the base selector name, even if the ComponentStyle is empty. Callers may use empty
        // component styles as classnames, which can still be useful for targeting one element from another, or
        // searching for all elements tagged with a certain class.
        notifySelectorName(selectorName)

        // Collect all CSS selectors (e.g. all base, hover, breakpoints, etc. modifiers) and, if we ever find multiple
        // definitions for the same selector, just combine them together. One way this is useful is you can use
        // `MutableSilkTheme.modifyComponentStyle` to layer additional styles on top of a base style. In almost all
        // practical cases, however, there will only ever be a single selector of each type per component style.
        fun ComponentModifiers.mergeCssModifiers(init: ComponentModifiers.() -> Unit): Map<CssModifier.Key, CssModifier> {
            return apply(init).cssModifiers
                .groupBy { it.key }
                .mapValues { (_, group) ->
                    val first = group.first()
                    if (group.size == 1) return@mapValues first

                    // Weird "Modifier as Modifier" casting trick required to get around type ambiguity
                    var mergedModifier = Modifier as Modifier
                    group.forEach { curr ->
                        check(curr.mediaQuery == first.mediaQuery && curr.suffix == first.suffix)
                        mergedModifier = mergedModifier.then(curr.modifier)
                    }

                    CssModifier(mergedModifier, first.mediaQuery, first.suffix)
                }
        }

        val lightModifiers = ComponentModifiers(ColorMode.LIGHT).mergeCssModifiers(init)
        val darkModifiers = ComponentModifiers(ColorMode.DARK).mergeCssModifiers(init)

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
    private val extraModifiers: @Composable () -> Modifier,
    private val prefix: String? = null,
    private val init: ComponentModifiers.() -> Unit,
) : CacheByPropertyNameDelegate<ComponentStyle>() {
    override fun create(propertyName: String): ComponentStyle {
        // e.g. "TitleTextStyle" to "title-text"
        val name = propertyName.removeSuffix("Style").titleCamelCaseToKebabCase()
        return ComponentStyle(name, extraModifiers, prefix, init)
    }
}

fun ComponentStyle(extraModifiers: Modifier = Modifier, prefix: String? = null, init: ComponentModifiers.() -> Unit) =
    ComponentStyle({ extraModifiers }, prefix, init)

fun ComponentStyle(
    extraModifiers: @Composable () -> Modifier,
    prefix: String? = null,
    init: ComponentModifiers.() -> Unit
) = ComponentStyleProvider(extraModifiers, prefix, init)

fun ComponentStyle.Companion.base(
    extraModifiers: Modifier = Modifier,
    prefix: String? = null,
    init: ComponentBaseModifier.() -> Modifier
) = base({ extraModifiers }, prefix, init)

fun ComponentStyle.Companion.base(
    extraModifiers: @Composable () -> Modifier,
    prefix: String? = null,
    init: ComponentBaseModifier.() -> Modifier
) = ComponentStyleProvider(extraModifiers, prefix, init = { base { ComponentBaseModifier(colorMode).let(init) } })


/**
 * A delegate provider class which allows you to create a [ComponentVariant] via the `by` keyword.
 */
class ComponentVariantProvider internal constructor(
    private val style: ComponentStyle,
    private val extraModifiers: @Composable () -> Modifier,
    private val init: ComponentModifiers.() -> Unit,
) : CacheByPropertyNameDelegate<ComponentVariant>() {
    override fun create(propertyName: String): ComponentVariant {
        // Given a style called "ExampleStyle", we want to support the following variant name simplifications:
        // - "OutlinedExampleVariant" -> "outlined" // Preferred variant naming style
        // - "ExampleOutlinedVariant" -> "outlined" // Acceptable variant naming style
        // - "OutlinedVariant"        -> "outlined" // But really the user should have kept "Example" in the name
        // - "ExampleVariant"         -> "example" // In other words, protect against empty strings!
        // - "ExampleExampleVariant"  -> "example"

        // Step 1, remove variant suffix and turn the code style into CSS sytle,
        // e.g. "OutlinedExampleVariant" -> "outlined-example"
        val withoutSuffix = propertyName.removeSuffix("Variant").titleCamelCaseToKebabCase()

        val name =
            withoutSuffix.removePrefix("${style.nameWithoutPrefix}-").removeSuffix("-${style.nameWithoutPrefix}")
                .takeIf { it.isNotEmpty() } ?: withoutSuffix
        return style.addVariant(name, extraModifiers, init)
    }
}

fun ComponentStyle.addVariant(extraModifiers: Modifier = Modifier, init: ComponentModifiers.() -> Unit) =
    addVariant({ extraModifiers }, init)

fun ComponentStyle.addVariant(extraModifiers: @Composable () -> Modifier, init: ComponentModifiers.() -> Unit) =
    ComponentVariantProvider(this, extraModifiers, init)

fun ComponentStyle.addVariantBase(extraModifiers: Modifier = Modifier, init: ComponentBaseModifier.() -> Modifier) =
    addVariantBase({ extraModifiers }, init)

fun ComponentStyle.addVariantBase(
    extraModifiers: @Composable () -> Modifier,
    init: ComponentBaseModifier.() -> Modifier
) = ComponentVariantProvider(this, extraModifiers, init = { base { ComponentBaseModifier(colorMode).let(init) } })

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
    internal abstract fun toModifier(): Modifier
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

private class CompositeComponentVariant(private val head: ComponentVariant, private val tail: ComponentVariant) :
    ComponentVariant() {
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
 * This is useful if you need to convert a style into something directly consumable by a Compose HTML widget.
 */
@Composable
fun <T : Element, A : AttrsScope<T>> ComponentStyle.toAttrs(
    variant: ComponentVariant? = null,
    finalHandler: (A.() -> Unit)? = null
): A.() -> Unit {
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
fun <T : Element, A : AttrsScope<T>> Iterable<ComponentStyle>.toAttrs(finalHandler: (A.() -> Unit)? = null): A.() -> Unit {
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
