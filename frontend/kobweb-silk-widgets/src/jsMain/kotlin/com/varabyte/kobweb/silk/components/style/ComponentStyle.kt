package com.varabyte.kobweb.silk.components.style

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.asStyleBuilder
import com.varabyte.kobweb.compose.ui.modifiers.classNames
import com.varabyte.kobweb.silk.components.style.CssModifier.Companion.BaseKey
import com.varabyte.kobweb.silk.components.style.breakpoint.Breakpoint
import com.varabyte.kobweb.silk.theme.SilkTheme
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import com.varabyte.kobweb.silk.theme.colors.getColorMode
import com.varabyte.kobweb.silk.theme.toSize
import org.jetbrains.compose.web.css.CSSMediaQuery
import org.jetbrains.compose.web.css.GenericStyleSheetBuilder
import org.jetbrains.compose.web.css.StyleBuilder
import org.jetbrains.compose.web.css.StylePropertyValue
import org.jetbrains.compose.web.css.StyleSheet
import org.jetbrains.compose.web.css.media

// We need our own implementation of StyleBuilder, so we can both test equality and pull values out of it later
private class ComparableStyleBuilder : StyleBuilder {
    val properties = LinkedHashMap<String, String>() // Preserve insertion order
    val variables = LinkedHashMap<String, String>() // Preserve insertion order

    override fun property(propertyName: String, value: StylePropertyValue) {
        properties[propertyName] = value.toString()
    }

    override fun variable(variableName: String, value: StylePropertyValue) {
        variables[variableName] = value.toString()
    }

    override fun equals(other: Any?): Boolean {
        return (other is ComparableStyleBuilder) && properties == other.properties && variables == other.variables
    }

    override fun hashCode(): Int {
        return properties.hashCode() + variables.hashCode()
    }
}

private fun ComparableStyleBuilder.isNotEmpty(): Boolean {
    return properties.isNotEmpty() || variables.isNotEmpty()
}

private fun Breakpoint.toMinWidthQuery() = CSSMediaQuery.MediaFeature("min-width", this.toSize())

/**
 * Represents a [Modifier] entry that is tied to a css rule, e.g. the modifier for ".myclass:hover" for example.
 */
internal class CssModifier(
    val modifier: Modifier,
    val mediaQuery: CSSMediaQuery? = null,
    val suffix: String? = null,
) {
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
 * A class which can be used to set CSS rules on a target [StyleModifiers] instance using types to prevent
 * invalid combinations.
 *
 * A CSS rule can consist of an optional breakpoint, zero or more pseudo classes, and an optional trailing pseudo
 * element.
 *
 * For example, this class enables:
 *
 * ```
 * ComponentStyle("css-rule-example") {
 *   hover { ... } // Creates CssRule(this, ":hover") under the hood
 *   (hover + after) { ... } // Creates CssRule(this, ":hover::after)
 *   (Breakpoint.MD + hover) { ... } // Creates ":hover" style within a medium-sized media query
 * }
 * ```
 *
 * It's not expected for an end user to use this class directly. It's provided for libraries that want to provide
 * additional extension properties to the [StyleModifiers] class (like `hover` and `after`)
 */
sealed class CssRule(val target: StyleModifiers) {
    abstract operator fun invoke(createModifier: () -> Modifier)

    protected fun addCssRule(
        mediaQuery: CSSMediaQuery?,
        pseudoClasses: List<String>,
        pseudoElement: String?,
        createModifier: () -> Modifier
    ) {
        val suffix = buildString {
            pseudoClasses.forEach { append(":$it") }
            if (pseudoElement != null) {
                append("::$pseudoElement")
            }
        }.takeIf { it.isNotEmpty() }

        target.cssRule(mediaQuery, suffix, createModifier)
    }

    /** A simple CSS rule that represents only setting a single breakpoint */
    class OfMedia(target: StyleModifiers, val mediaQuery: CSSMediaQuery) : CssRule(target) {
        override fun invoke(createModifier: () -> Modifier) {
            addCssRule(mediaQuery, emptyList(), null, createModifier)
        }

        operator fun plus(other: OfPseudoClass) =
            CompositeOpen(target, mediaQuery, listOf(other.pseudoClass))

        operator fun plus(other: OfPseudoElement) =
            CompositeClosed(target, mediaQuery, emptyList(), other.pseudoElement)
    }

    class OfPseudoClass(target: StyleModifiers, val pseudoClass: String) : CssRule(target) {
        override fun invoke(createModifier: () -> Modifier) {
            addCssRule(null, listOf(pseudoClass), null, createModifier)
        }

        operator fun plus(other: OfPseudoClass) =
            CompositeOpen(target, null, listOf(pseudoClass, other.pseudoClass))

        operator fun plus(other: OfPseudoElement) =
            CompositeClosed(target, null, listOf(pseudoClass), other.pseudoElement)
    }

    class OfPseudoElement(target: StyleModifiers, val pseudoElement: String) : CssRule(target) {
        override fun invoke(createModifier: () -> Modifier) {
            addCssRule(null, emptyList(), pseudoElement, createModifier)
        }
    }

    /**
     * A composite CSS rule that is a chain of subparts and still open to accepting more pseudo classes and/or a
     * pseudo element.
     */
    class CompositeOpen(target: StyleModifiers, val mediaQuery: CSSMediaQuery?, val pseudoClasses: List<String>) : CssRule(target) {
        override fun invoke(createModifier: () -> Modifier) {
            addCssRule(mediaQuery, pseudoClasses, null, createModifier)
        }

        operator fun plus(other: OfPseudoClass) =
            CompositeOpen(target, null, pseudoClasses + other.pseudoClass)

        operator fun plus(other: OfPseudoElement) =
            CompositeClosed(target, null, pseudoClasses, other.pseudoElement)
    }

    /**
     * A composite CSS rule that is a chain of subparts which is terminated - it cannot grow any further but can only
     * be invoked at this point
     */
    class CompositeClosed(
        target: StyleModifiers,
        val mediaQuery: CSSMediaQuery?,
        val pseudoClasses: List<String>,
        val pseudoElement: String
    ) : CssRule(target) {
        override fun invoke(createModifier: () -> Modifier) {
            addCssRule(mediaQuery, pseudoClasses, pseudoElement, createModifier)
        }
    }
}

// Breakpoint extensions to allow adding styles to normal breakpoint values, e.g. "Breakpoint.MD + hover"
operator fun Breakpoint.plus(other: CssRule.OfPseudoClass) = CssRule.OfMedia(other.target, this.toMinWidthQuery()) + other
operator fun Breakpoint.plus(other: CssRule.OfPseudoElement) = CssRule.OfMedia(other.target, this.toMinWidthQuery()) + other

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
 * [StyleModifiers] with enhanced features enabled for [ComponentStyle] support.
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
 *
 * @param colorMode What color mode these modifiers should be designed around. This is passed in so users defining
 *   a component style can use it if relevant.
 */
// Note: If you change this class's constructor, you should update the `ComponentStyleState` constructor to match
class ComponentModifiers(val colorMode: ColorMode): StyleModifiers()

/**
 * Class provided for cases where you only generate a single style (e.g. base), unlike [StyleModifiers] where you
 * can define a collection of styles.
 */
class ComponentStyleState(val colorMode: ColorMode)

/**
 * A [ComponentStyle] pared down to read-only data only, which should happen shortly after Silk initializes.
 */
class ImmutableComponentStyle internal constructor(private val name: String) {
    @Composable
    fun toModifier(): Modifier {
        val classNames = listOf(name, "$name-${getColorMode().name.lowercase()}")
            .filter { name -> ComponentStyle.registeredClasses.contains(name) }

        return if (classNames.isNotEmpty()) Modifier.classNames(*classNames.toTypedArray()) else Modifier
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
 * You may still wish to use [ComponentStyle.Companion.invoke] instead if you expect that at some point in the future
 * you'll want to add additional, non-base styles.
 */
fun ComponentStyle.Companion.base(className: String, init: ComponentStyleState.() -> Modifier): ComponentStyle {
    return ComponentStyle(className) {
        base {
            ComponentStyleState(colorMode).let(init)
        }
    }
}


private sealed interface StyleGroup {
    class Light(val styles: ComparableStyleBuilder) : StyleGroup
    class Dark(val styles: ComparableStyleBuilder) : StyleGroup
    class ColorAgnostic(val styles: ComparableStyleBuilder) : StyleGroup
    class ColorAware(val lightStyles: ComparableStyleBuilder, val darkStyles: ComparableStyleBuilder) : StyleGroup

    companion object {
        @Suppress("NAME_SHADOWING") // Shadowing used to turn nullable into non-null
        fun from(lightModifiers: Modifier?, darkModifiers: Modifier?): StyleGroup? {
            val lightStyles = lightModifiers?.let { lightModifiers ->
                ComparableStyleBuilder().apply { lightModifiers.asStyleBuilder().invoke(this) }
            }
            val darkStyles = darkModifiers?.let { darkModifiers ->
                ComparableStyleBuilder().apply { darkModifiers.asStyleBuilder().invoke(this) }
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
 */
class ComponentStyle(
    val name: String,
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

    fun addVariant(name: String, init: ComponentModifiers.() -> Unit): ComponentVariant {
        return ComponentVariant(ComponentStyle("${this.name}-$name", init), baseStyle = this).also {
            variants.add(it)
        }
    }

    /**
     * @param cssRule A selector plus an optional pseudo keyword (e.g. "a", "a:link", and "a::selection")
     */
    private fun <T: StyleBuilder> GenericStyleSheetBuilder<T>.addStyles(cssRule: String, styles: ComparableStyleBuilder) {
        cssRule style {
            styles.properties.forEach { entry -> property(entry.key, entry.value) }
            styles.variables.forEach { entry -> variable(entry.key, entry.value) }
        }
    }

    /**
     * Shared logic for using an initial selector name and triggering a callback with the final selector name and
     * CSS styles to be associated with it.
     */
    private fun withFinalSelectorName(selectorBaseName: String, group: StyleGroup, handler: (String, ComparableStyleBuilder) -> Unit) {
        when (group) {
            is StyleGroup.Light -> handler("$selectorBaseName-light", group.styles)
            is StyleGroup.Dark -> handler("$selectorBaseName-dark", group.styles)
            is StyleGroup.ColorAgnostic -> handler(selectorBaseName, group.styles)
            is StyleGroup.ColorAware -> {
                handler("$selectorBaseName-light", group.lightStyles)
                handler("$selectorBaseName-dark", group.darkStyles)
            }
        }
    }

    internal fun addStyles(styleSheet: StyleSheet, selectorName: String) {
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
    internal fun addStyles(styleSheet: StyleSheet) {
        // Register styles associated with this style's classname
        addStyles(styleSheet, ".$name")
    }
}

/**
 * Convenience method when you only care about registering the base style, which can help avoid a few extra lines.
 *
 * You may still wish to use [ComponentStyle.addVariant] instead if you expect that at some point in the future
 * you'll want to add additional, non-base styles.
 */
fun ComponentStyle.addBaseVariant(name: String, init: ComponentStyleState.() -> Modifier): ComponentVariant {
    return addVariant(name) {
        base {
            ComponentStyleState(colorMode).let(init)
        }
    }
}

class ComponentVariant(internal val style: ComponentStyle, private val baseStyle: ComponentStyle) {
    fun addStyles(styleSheet: StyleSheet) {
        // If you are using a variant, require it be associated with a tag already associated with the base style
        // e.g. if you have a link variant ("silk-link-undecorated") it should only be applied if the tag is also
        // a link (so this would be registered as ".silk-link.silk-link-undecorated").
        // To put it another way, if you use a link variant with a surface widget, it won't be applied.
        style.addStyles(styleSheet, ".${baseStyle.name}.${style.name}")
    }
}

/**
 * Convert a user's component style into a [Modifier].
 *
 * This can then be passed into Silk widgets directly, or to Compose for Web widgets
 * by calling `attrs = style.toModifier.asAttributeBuilder()`
 */
@Composable
fun ComponentStyle.toModifier(variant: ComponentVariant? = null): Modifier {
    return SilkTheme.componentStyles.getValue(name).toModifier().then(
        variant?.style?.toModifier() ?: Modifier
    )
}

/**
 * A convenience method for converting a collection of styles into a single modifier.
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