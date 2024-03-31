package com.varabyte.kobweb.silk.components.style

import androidx.compose.runtime.*
import com.varabyte.kobweb.browser.util.kebabCaseToTitleCamelCase
import com.varabyte.kobweb.browser.util.titleCamelCaseToKebabCase
import com.varabyte.kobweb.compose.attributes.ComparableAttrsScope
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.compose.ui.toStyles
import com.varabyte.kobweb.silk.components.util.internal.CacheByPropertyNameDelegate
import com.varabyte.kobweb.silk.theme.SilkTheme
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import com.varabyte.kobweb.silk.theme.colors.suffixedWith
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.css.*
import org.w3c.dom.Element

abstract class CssStyle(
    internal val init: ComponentModifiers.() -> Unit,
    internal val extraModifiers: @Composable () -> Modifier = { Modifier },
) {

    abstract class Base(
        init: ComponentBaseModifier.() -> Modifier,
        extraModifiers: @Composable () -> Modifier = { Modifier },
    ) : CssStyle({ base { ComponentBaseModifier(colorMode).init() } }, extraModifiers)

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

    // Collect all CSS selectors (e.g. all base, hover, breakpoints, etc. modifiers) and, if we ever find multiple
    // definitions for the same selector, just combine them together. One way this is useful is you can use
    // `MutableSilkTheme.modifyComponentStyle` to layer additional styles on top of a base style. In almost all
    // practical cases, however, there will only ever be a single selector of each type per component style.
    private fun ComponentModifiers.mergeCssModifiers(init: ComponentModifiers.() -> Unit): Map<CssModifier.Key, CssModifier> {
        return apply(init).cssModifiers
            .groupBy { it.key }
            .mapValues { (_, group) ->
                group.reduce { acc, curr -> acc.mergeWith(curr) }
            }
    }

    private fun Map<CssModifier.Key, CssModifier>.assertNoAttributeModifiers(selectorName: String): Map<CssModifier.Key, CssModifier> {
        return this.onEach { (_, cssModifier) ->
            val attrsScope = ComparableAttrsScope<Element>()
            cssModifier.modifier.toAttrs<AttrsScope<Element>>().invoke(attrsScope)
            if (attrsScope.attributes.isEmpty()) return@onEach

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
                appendLine("\tAttribute(s): ${attrsScope.attributes.keys.joinToString(", ") { "\"$it\"" }}")
                appendLine()
                appendLine("An example of how to fix this:")
                appendLine(
                    """
                    |   // Before
                    |   val ExampleStyle by ComponentStyle {
                    |       base {
                    |          Modifier
                    |              .backgroundColor(Colors.Magenta))
                    |              .tabIndex(0) // <-- The offending attribute modifier
                    |       }
                    |   }
                    |   
                    |   // After
                    |   val ExampleStyle by ComponentStyle(extraModifiers = Modifier.tabIndex(0)) {
                    |       base {
                    |           Modifier.backgroundColor(Colors.Magenta)
                    |       }
                    |   }
                    """.trimMargin()
                )
            })
        }
    }

    // TODO: docs
    internal fun addStylesInto(selector: String, styleSheet: StyleSheet): ClassSelectors {
        // Always add the base selector name, even if the ComponentStyle is empty. Callers may use empty
        // component styles as classnames, which can still be useful for targeting one element from another, or
        // searching for all elements tagged with a certain class.
        val classNames = mutableListOf(selector)

        val lightModifiers = ComponentModifiers(ColorMode.LIGHT).mergeCssModifiers(init)
            .assertNoAttributeModifiers(selector)
        val darkModifiers = ComponentModifiers(ColorMode.DARK).mergeCssModifiers(init)
            .assertNoAttributeModifiers(selector)

        StyleGroup.from(lightModifiers[CssModifier.BaseKey]?.modifier, darkModifiers[CssModifier.BaseKey]?.modifier)
            ?.let { group ->
                withFinalSelectorName(selector, group) { name, styles ->
                    if (styles.isNotEmpty()) {
                        classNames.add(name)
                        styleSheet.addStyles(name, styles)
                    }
                }
            }

        val allCssRuleKeys = (lightModifiers.keys + darkModifiers.keys).filter { it != CssModifier.BaseKey }
        for (cssRuleKey in allCssRuleKeys) {
            val group = StyleGroup.from(lightModifiers[cssRuleKey]?.modifier, darkModifiers[cssRuleKey]?.modifier)
                ?: continue
            withFinalSelectorName(selector, group) { name, styles ->
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
        return ClassSelectors(classNames)
    }

    internal fun intoImmutableStyle(classSelectors: ClassSelectors) =
        ImmutableCssStyle(classSelectors, extraModifiers)

    @Composable
    fun toModifier(): Modifier = SilkTheme.cssStyles.getValue(this).toModifier()

    companion object // for extensions
}

internal class SimpleCssStyle(
    val selector: String,
    init: ComponentModifiers.() -> Unit,
    extraModifiers: @Composable () -> Modifier,
) : CssStyle(init, extraModifiers) {
    internal fun addStylesInto(styleSheet: StyleSheet): ClassSelectors {
        return addStylesInto(selector, styleSheet)
    }
}

/**
 * A [CssStyle] pared down to read-only data only, which should happen shortly after Silk initializes.
 *
 * @param classSelectors The CSS class selectors associated with this style, including the base class and any
 *  color mode specific classes, used to determine the exact classnames to apply when this style is used.
 * @param extraModifiers Additional modifiers that can be tacked onto this component style, convenient for including
 *   non-style attributes whenever this style is applied.
 */
internal class ImmutableCssStyle(
    classSelectors: ClassSelectors,
    private val extraModifiers: @Composable () -> Modifier
) {
    private val classNames = classSelectors.classNames.toSet()

    @Composable
    fun toModifier(): Modifier {
        val currentClassNames = classNames.filterNot { it.endsWith(ColorMode.current.opposite.name.lowercase()) }
        return (if (currentClassNames.isNotEmpty()) Modifier.classNames(*currentClassNames.toTypedArray()) else Modifier)
            .then(extraModifiers())
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

class ComponentModifiers internal constructor(override val colorMode: ColorMode) : ComponentModifier, StyleModifiers()

/**
 * Class provided for cases where you only generate a single style (e.g. base), unlike [ComponentModifiers] where you
 * can define a collection of styles.
 */
class ComponentBaseModifier internal constructor(override val colorMode: ColorMode) : ComponentModifier

private sealed interface StyleGroup {
    class Light(val styles: ComparableStyleScope) : StyleGroup
    class Dark(val styles: ComparableStyleScope) : StyleGroup
    class ColorAgnostic(val styles: ComparableStyleScope) : StyleGroup
    class ColorAware(val lightStyles: ComparableStyleScope, val darkStyles: ComparableStyleScope) : StyleGroup

    companion object {
        //        @Suppress("NAME_SHADOWING") // Shadowing used to turn nullable into non-null
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

class SimpleCssStyleProvider internal constructor(
    private val extraModifiers: @Composable () -> Modifier,
    private val prefix: String? = null,
    private val init: ComponentModifiers.() -> Unit,
) : CacheByPropertyNameDelegate<CssStyle>() {
    override fun create(propertyName: String): CssStyle {
        val prefix = prefix?.let { "$it-" } ?: ""
        // e.g. "TitleTextStyle" to "title-text"
        val name = prefix + propertyName.removeSuffix("Style").titleCamelCaseToKebabCase()
        return SimpleCssStyle(".$name", init, extraModifiers)
    }
}

fun CssStyle(extraModifiers: Modifier = Modifier, prefix: String? = null, init: ComponentModifiers.() -> Unit) =
    CssStyle({ extraModifiers }, prefix, init)

fun CssStyle(
    extraModifiers: @Composable () -> Modifier,
    prefix: String? = null,
    init: ComponentModifiers.() -> Unit
) = SimpleCssStyleProvider(extraModifiers, prefix, init)

fun CssStyle.Companion.base(
    extraModifiers: Modifier = Modifier,
    prefix: String? = null,
    init: ComponentBaseModifier.() -> Modifier
) = base({ extraModifiers }, prefix, init)

fun CssStyle.Companion.base(
    extraModifiers: @Composable () -> Modifier,
    prefix: String? = null,
    init: ComponentBaseModifier.() -> Modifier
) = SimpleCssStyleProvider(extraModifiers, prefix, init = { base { ComponentBaseModifier(colorMode).let(init) } })
