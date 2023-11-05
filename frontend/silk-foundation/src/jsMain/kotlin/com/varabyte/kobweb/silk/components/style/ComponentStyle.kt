@file:Suppress("FunctionName")

package com.varabyte.kobweb.silk.components.style

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.attributes.ComparableAttrsScope
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.compose.ui.toStyles
import com.varabyte.kobweb.compose.util.kebabCaseToTitleCamelCase
import com.varabyte.kobweb.compose.util.titleCamelCaseToKebabCase
import com.varabyte.kobweb.silk.components.style.CssModifier.Companion.BaseKey
import com.varabyte.kobweb.silk.components.util.internal.CacheByPropertyNameDelegate
import com.varabyte.kobweb.silk.theme.SilkTheme
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import com.varabyte.kobweb.silk.theme.colors.suffixedWith
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.css.*
import org.w3c.dom.Element

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

    companion object // for extensions

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
        )
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

    internal fun addStylesInto(styleSheet: StyleSheet, selectorName: String): List<String> {
        // Always add the base selector name, even if the ComponentStyle is empty. Callers may use empty
        // component styles as classnames, which can still be useful for targeting one element from another, or
        // searching for all elements tagged with a certain class.
        val classNames = mutableListOf(selectorName)

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
                    group.reduce { acc, curr -> acc.mergeWith(curr) }
                }
        }

        fun Map<CssModifier.Key, CssModifier>.assertNoAttributeModifiers(): Map<CssModifier.Key, CssModifier> {
            this.forEach { (_, cssModifier) ->
                ComparableAttrsScope<Element>().apply {
                    cssModifier.modifier.toAttrs<AttrsScope<Element>>().invoke(this)

                    if (attributes.isNotEmpty()) {
                        error(buildString {
                            appendLine("ComponentStyle declarations cannot contain Modifiers that specify attributes. Please move Modifiers associated with attributes to the ComponentStyle's `extraModifiers` parameter.")
                            appendLine()
                            appendLine("Details:")

                            append("\tCSS rule: ")
                            append("\"$selectorName")
                            if (cssModifier.mediaQuery != null) append(cssModifier.mediaQuery)
                            if (cssModifier.suffix != null) append(cssModifier.suffix)
                            append("\"")

                            append(" (do you declare a property called ")
                            // ".example" likely comes from `ExampleStyle` while ".example.example-outlined" likely
                            // comes from ExampleOutlinedVariant or OutlinedExampleVariant
                            val isStyle = selectorName.count { it == '.' } == 1// "Variant" else "Style"
                            val styleName = selectorName.substringAfter(".").substringBefore(".")

                            if (isStyle) {
                                append("`${styleName.kebabCaseToTitleCamelCase()}Style`")
                            } else {
                                // Convert ".example.example-outlined" to "outlined". This could come from a variant
                                // property called OutlinedExampleVariant or ExampleOutlinedVariant
                                val variantPart = selectorName.substringAfterLast(".").removePrefix("$styleName-")
                                append("`${"$styleName-$variantPart".kebabCaseToTitleCamelCase()}Variant`")
                                append(" or ")
                                append("`${"$variantPart-$styleName".kebabCaseToTitleCamelCase()}Variant`")
                            }
                            appendLine("?)")
                            appendLine("\tAttribute(s): ${attributes.keys.joinToString(", ") { "\"$it\"" }}")
                            appendLine()
                            appendLine("An example of how to fix this:")
                            appendLine(
                                """
                                    // Before
                                    val ExampleStyle by ComponentStyle {
                                        base {
                                           Modifier
                                               .backgroundColor(Colors.Magenta))
                                               .tabIndex(0) // <-- The offending attribute modifier
                                        }
                                    }
                                    
                                    // After
                                    val ExampleStyle by ComponentStyle(extraModifiers = Modifier.tabIndex(0)) {
                                        base {
                                            Modifier.backgroundColor(Colors.Magenta)
                                        }
                                    }
                                """.trimIndent().split("\n").joinToString(separator = "\n") { "\t$it" }
                            )
                        })
                    }
                }
            }
            return this
        }

        val lightModifiers = ComponentModifiers(ColorMode.LIGHT).mergeCssModifiers(init).assertNoAttributeModifiers()
        val darkModifiers = ComponentModifiers(ColorMode.DARK).mergeCssModifiers(init).assertNoAttributeModifiers()

        StyleGroup.from(lightModifiers[BaseKey]?.modifier, darkModifiers[BaseKey]?.modifier)?.let { group ->
            withFinalSelectorName(selectorName, group) { name, styles ->
                if (styles.isNotEmpty()) {
                    classNames.add(name)
                    styleSheet.addStyles(name, styles)
                }
            }
        }

        val allCssRuleKeys = (lightModifiers.keys + darkModifiers.keys).filter { it != BaseKey }
        for (cssRuleKey in allCssRuleKeys) {
            StyleGroup.from(lightModifiers[cssRuleKey]?.modifier, darkModifiers[cssRuleKey]?.modifier)?.let { group ->
                withFinalSelectorName(selectorName, group) { name, styles ->
                    if (styles.isNotEmpty()) {
                        classNames.add(name)

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
        // Selectors may be ".someStyle" or ".someStyle.someVariant" - only the last part is relevant to this style
        return classNames.map { it.substringAfterLast('.') }
    }

    /**
     * Add this [ComponentStyle]'s styles to the target [StyleSheet].
     *
     * @return The list of CSS class selectors associated with this style.
     */
    internal fun addStylesInto(styleSheet: StyleSheet): List<String> {
        // Register styles associated with this style's classname
        return addStylesInto(styleSheet, ".$name")
    }

    internal fun intoImmutableStyle(classNames: List<String>) = ImmutableComponentStyle(classNames, extraModifiers)
}

/**
 * A [ComponentStyle] pared down to read-only data only, which should happen shortly after Silk initializes.
 *
 * @param classNames The list of CSS class selectors associated with this style.
 * @param extraModifiers Additional modifiers that can be tacked onto this component style, convenient for including
 *   non-style attributes whenever this style is applied.
 */
internal class ImmutableComponentStyle(
    classNames: List<String>,
    private val extraModifiers: @Composable () -> Modifier
) {
    private val classNames = classNames.toSet()

    @Composable
    fun toModifier(): Modifier {
        val currentClassNames = classNames.filterNot { it.endsWith(ColorMode.current.opposite.name.lowercase()) }
        return (if (currentClassNames.isNotEmpty()) Modifier.classNames(*currentClassNames.toTypedArray()) else Modifier)
            .then(extraModifiers())
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
 * Convert a user's component style into a [Modifier].
 *
 * @param variants 0 or more variants that can potentially extend the base style. Although it may seem odd at first that
 *   nullable values are accepted here, that's because Silk widgets all default their `variant` parameter to null, so
 *   it's easier to just accept null here rather than require users to branch based on whether the variant is null or
 *   not.
 */
@Composable
fun ComponentStyle.toModifier(vararg variants: ComponentVariant?): Modifier {
    return SilkTheme.componentStyles.getValue(name).toModifier()
        .then(variants.toList().combine()?.toModifier() ?: Modifier)
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
    return fold<_, Modifier>(Modifier) { acc, style -> acc.then(style.toModifier()) }
}

/**
 * A convenience method for chaining a collection of styles into a single [AttrsScope] builder.
 */
@Composable
fun <T : Element, A : AttrsScope<T>> Iterable<ComponentStyle>.toAttrs(finalHandler: (A.() -> Unit)? = null): A.() -> Unit {
    return this.toModifier().toAttrs(finalHandler)
}
