package com.varabyte.kobweb.silk.components.style

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.asStyleBuilder
import com.varabyte.kobweb.compose.ui.modifiers.classNames
import com.varabyte.kobweb.silk.SilkStyleSheet.style
import com.varabyte.kobweb.silk.components.style.breakpoint.Breakpoint
import com.varabyte.kobweb.silk.theme.SilkConfigInstance
import com.varabyte.kobweb.silk.theme.SilkTheme
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import com.varabyte.kobweb.silk.theme.colors.getColorMode
import org.jetbrains.compose.web.css.StyleBuilder
import org.jetbrains.compose.web.css.StylePropertyValue
import org.jetbrains.compose.web.css.StyleSheet
import org.jetbrains.compose.web.css.media
import org.jetbrains.compose.web.css.mediaMinWidth

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

/**
 * Class used as the receiver to a callback, allowing the user to define various state-dependent styles (defined via
 * [Modifier]s).
 *
 * @param colorMode What color mode these modifiers should be designed around. This is passed in so users defining
 *   a component style can use it if relevant.
 */
class ComponentModifiers(val colorMode: ColorMode) {
    internal var base: Modifier? = null

    internal val pseudoClasses = LinkedHashMap<String, Modifier>() // LinkedHashMap preserves insertion order

    internal val pseudoElements = LinkedHashMap<String, Modifier>() // LinkedHashMap preserves insertion order

    internal val breakpoints = mutableMapOf<Breakpoint, Modifier>()

    /** Define base styles for this component, will always be applied first. */
    fun base(createModifier: () -> Modifier) {
        base = createModifier()
    }

    /**
     * Register styles associated with pseudo classes like "hover".
     *
     * Pseudo classes will be applied in the order inserted. Be aware that you should use the LVHA order if using link,
     * visited, hover, and/or active pseudo classes.
     *
     * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/Pseudo-classes
     */
    fun pseudoClass(name: String, createModifier: () -> Modifier) {
        pseudoClasses[name] = createModifier()
    }

    /**
     * Register styles associated with pseudo elements like "after".
     *
     * Pseudo-elements will be applied in the order registered, and after all pseudo classes.
     *
     * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/Pseudo-elements
     */
    fun pseudoElement(name: String, createModifier: () -> Modifier) {
        pseudoElements[name] = createModifier()
    }

    /**
     * Register layout styles which are dependent on the current window width.
     *
     * Breakpoints will be applied in order from smallest to largest, and after all pseudo classes and pseudo-elements.
     */
    fun breakpoint(breakpoint: Breakpoint, createModifier: () -> Modifier) {
        breakpoints[breakpoint] = createModifier()
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
     * which is identical to
     *
     * ```
     * breakpoint(Breakpoint.MD) { Modifier.color(...) }
     * ```
     */
    operator fun Breakpoint.invoke(createModifier: () -> Modifier) {
        breakpoints[this] = createModifier()
    }

}

/**
 * A class which allows a user to define styles that get added to the page's stylesheet, instead of just using
 * inline styles.
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
class ComponentStyle internal constructor(private val name: String) {
    companion object {
        operator fun invoke(name: String, init: ComponentModifiers.() -> Unit) =
            ComponentStyleBuilder(name, init)
    }

    @Composable
    fun toModifier(): Modifier {
        return Modifier.classNames(name, "$name-${getColorMode().name.lowercase()}")
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

class ComponentStyleBuilder internal constructor(
    val name: String,
    private val init: ComponentModifiers.() -> Unit,
) {
    internal val variants = mutableListOf<ComponentVariant>()

    fun addVariant(name: String, init: ComponentModifiers.() -> Unit): ComponentVariant {
        return ComponentVariant(ComponentStyleBuilder("${this.name}-$name", init), baseStyle = this).also {
            variants.add(it)
        }
    }

    /**
     * @param cssRule A selector plus an optional pseudo keyword (e.g. "a", "a:link", and "a::selection")
     */
    private fun addStyles(cssRule: String, styles: ComparableStyleBuilder) {
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

    /**
     * Add a css rule that is part selector and part (optional) pseudoSuffix.
     *
     * Note: The name and suffix are separated intentionally, since we may tweak the base name based on the color mode.
     */
    private fun addStyles(selectorName: String, pseudoSuffix: String?, group: StyleGroup) {
        withFinalSelectorName(selectorName, group) { name, styles ->
            val cssRule = "$name${pseudoSuffix.orEmpty()}"
            addStyles(cssRule, styles)
        }
    }

    /**
     * Add styles which will only be applied based on the width of the screen, associated with the passed in
     * [breakpoint].
     */
    private fun StyleSheet.addResponsiveStyles(selectorName: String, breakpoint: Breakpoint, styles: ComparableStyleBuilder) {
        media(mediaMinWidth(SilkConfigInstance.breakpoints.getValue(breakpoint))) {
            selectorName style {
                styles.properties.forEach { entry -> property(entry.key, entry.value) }
                styles.variables.forEach { entry -> variable(entry.key, entry.value) }
            }
        }
    }

    private fun StyleSheet.addResponsiveStyles(selectorName: String, breakpoint: Breakpoint, group: StyleGroup) {
        withFinalSelectorName(selectorName, group) { name, styles ->
            addResponsiveStyles(name, breakpoint, styles)
        }
    }

    internal fun addStyles(styleSheet: StyleSheet, selectorName: String) {
        val lightModifiers = ComponentModifiers(ColorMode.LIGHT).apply(init)
        val darkModifiers = ComponentModifiers(ColorMode.DARK).apply(init)

        StyleGroup.from(lightModifiers.base, darkModifiers.base)?.let { group ->
            addStyles(selectorName, null, group)
        }

        val allPseudoClasses = lightModifiers.pseudoClasses.keys + darkModifiers.pseudoClasses.keys
        for (pseudoClass in allPseudoClasses) {
            StyleGroup.from(lightModifiers.pseudoClasses[pseudoClass], darkModifiers.pseudoClasses[pseudoClass])?.let { group ->
                addStyles(selectorName, ":$pseudoClass", group)
            }
        }

        val allPseudoElements = lightModifiers.pseudoElements.keys + darkModifiers.pseudoElements.keys
        for (pseudoElement in allPseudoElements) {
            StyleGroup.from(lightModifiers.pseudoElements[pseudoElement], darkModifiers.pseudoElements[pseudoElement])?.let { group ->
                addStyles(selectorName, "::$pseudoElement", group)
            }
        }

        for (breakpoint in Breakpoint.values()) {
            StyleGroup.from(lightModifiers.breakpoints[breakpoint], darkModifiers.breakpoints[breakpoint])?.let { group ->
                styleSheet.addResponsiveStyles(selectorName, breakpoint, group)
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

class ComponentVariant(internal val style: ComponentStyleBuilder, private val baseStyle: ComponentStyleBuilder) {
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
 * This can then be passed into Silk widgets directly, or to Web Compose widgets
 * by calling `attrs = style.toModifier.asAttributeBuilder()`
 */
@Composable
fun ComponentStyleBuilder.toModifier(variant: ComponentVariant? = null): Modifier {
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
fun Iterable<ComponentStyleBuilder>.toModifier(): Modifier {
    var finalModifier: Modifier = Modifier
    for (style in this) {
        finalModifier = finalModifier.then(style.toModifier())
    }
    return finalModifier
}