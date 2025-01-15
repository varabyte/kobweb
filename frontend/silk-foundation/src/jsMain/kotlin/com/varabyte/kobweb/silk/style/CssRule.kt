package com.varabyte.kobweb.silk.style

import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.silk.style.breakpoint.BreakpointQueryProvider
import org.jetbrains.compose.web.css.*

/**
 * A class which can be used to create type-safe CSS rules that can be applied to a [StyleScope] instance.
 *
 * A CSS rule can consist of an optional media query, zero or more pseudo-classes, and an optional trailing
 * pseudo-element.
 *
 * For example, this class enables:
 *
 * ```
 * CssStyle {
 *   hover { ... } // Creates CssRule(this, ":hover") under the hood
 *   (hover + after) { ... } // Creates CssRule(this, ":hover::after)
 *   (Breakpoint.MD + hover) { ... } // Creates ":hover" style within a medium-sized media query
 * }
 * ```
 *
 * It's not expected for an end user to use this class directly. It's provided for libraries that want to provide
 * additional extension properties to the [StyleScope] class (like `hover` and `after`)
 */
sealed class CssRule {
    companion object {
        /**
         * A CSS rule that represents a functional pseudo-class.
         *
         * For example, passing in "not" would result in: `:not(...)`
         */
        fun OfFunctionalPseudoClass(pseudoClass: String, vararg params: NonMediaCssRule) =
            OfPseudoClass("$pseudoClass(${params.mapNotNull { it.toSelectorText() }.joinToString()})")
    }

    // Note: This is used by the `StyleScope` class to create css rules without leaking CssRule's implementation details.
    // If/when kotlin supports context parameters, the logic can be entirely contained within this class, taking a
    // `StyleScope` instance as a context parameter.
    internal fun cssRule(target: StyleScope, createModifier: () -> Modifier) {
        target.cssRule(mediaQuery, toSelectorText(), createModifier)
    }

    protected open val mediaQuery: CSSMediaQuery? = null
    protected open fun toSelectorText(): String? = null


    protected fun buildSelectorText(
        attributeSelectors: List<OfAttributeSelector>,
        pseudoClasses: List<OfPseudoClass>,
        pseudoElement: OfPseudoElement?,
    ): String? {
        return buildString {
            attributeSelectors.forEach { append("[${it.attributeSelector}]") }
            pseudoClasses.forEach { append(":${it.pseudoClass}") }
            if (pseudoElement != null) {
                append("::${pseudoElement.pseudoElement}")
            }
        }.takeIf { it.isNotEmpty() }
    }

    /**
     * A CSS rule (or rule part) that represents a media query.
     *
     * For example, could result in: `@media (max-width: 1234px)`
     */
    class OfMedia(override val mediaQuery: CSSMediaQuery) : CssRule() {
        operator fun plus(other: OfPseudoClass) =
            CompositeOpen(mediaQuery, emptyList(), listOf(other))

        operator fun plus(other: OfPseudoElement) =
            CompositeClosed(mediaQuery, emptyList(), emptyList(), other)
    }

    sealed class NonMediaCssRule() : CssRule()

    /**
     * A CSS rule that represents an attribute selector.
     *
     * For example, passing in "aria-disabled" would result in: `[aria-disabled]`
     */
    class OfAttributeSelector(val attributeSelector: String) : NonMediaCssRule() {
        override fun toSelectorText() = buildSelectorText(listOf(this), emptyList(), null)

        operator fun plus(other: OfAttributeSelector) =
            CompositeOpen(null, listOf(this, other), emptyList())

        operator fun plus(other: OfPseudoClass) =
            CompositeOpen(null, listOf(this), listOf(other))

        operator fun plus(other: OfPseudoElement) =
            CompositeClosed(null, listOf(this), emptyList(), other)

    }

    /**
     * A CSS rule that represents a pseudo-class selector.
     *
     * For example, passing in "hover" would result in: `:hover`
     */
    class OfPseudoClass(val pseudoClass: String) : NonMediaCssRule() {
        override fun toSelectorText() = buildSelectorText(emptyList(), listOf(this), null)

        operator fun plus(other: OfPseudoClass) =
            CompositeOpen(null, emptyList(), listOf(this, other))

        operator fun plus(other: OfPseudoElement) =
            CompositeClosed(null, emptyList(), listOf(this), other)
    }

    /**
     * A CSS rule that represents a pseudo-element selector.
     *
     * For example, passing in "after" would result in: `::after`
     */
    class OfPseudoElement(val pseudoElement: String) : NonMediaCssRule() {
        override fun toSelectorText() = buildSelectorText(emptyList(), emptyList(), this)
    }

    /**
     * A composite CSS rule that is a chain of subparts and still open to accepting more pseudo-classes and/or a
     * pseudo-element.
     */
    class CompositeOpen(
        override val mediaQuery: CSSMediaQuery?,
        val attributeSelectors: List<OfAttributeSelector>,
        val pseudoClasses: List<OfPseudoClass>
    ) : NonMediaCssRule() {
        override fun toSelectorText() = buildSelectorText(attributeSelectors, pseudoClasses, null)

        operator fun plus(other: OfPseudoClass) =
            CompositeOpen(null, attributeSelectors, pseudoClasses + other)

        operator fun plus(other: OfAttributeSelector) =
            CompositeOpen(null, attributeSelectors + other, pseudoClasses)

        operator fun plus(other: OfPseudoElement) =
            CompositeClosed(null, attributeSelectors, pseudoClasses, other)
    }

    /**
     * A composite CSS rule that is a chain of subparts which is terminated - it cannot grow any further but can only
     * be invoked at this point.
     */
    class CompositeClosed(
        override val mediaQuery: CSSMediaQuery?,
        val attributeSelectors: List<OfAttributeSelector>,
        val pseudoClasses: List<OfPseudoClass>,
        val pseudoElement: OfPseudoElement
    ) : NonMediaCssRule() {
        override fun toSelectorText() = buildSelectorText(attributeSelectors, pseudoClasses, pseudoElement)
    }
}

// Breakpoint extensions to allow adding styles to normal breakpoint values, e.g. "Breakpoint.MD + hover"
operator fun BreakpointQueryProvider.plus(other: CssRule.OfPseudoClass) =
    CssRule.OfMedia(this.toCSSMediaQuery()) + other

operator fun BreakpointQueryProvider.plus(other: CssRule.OfPseudoElement) =
    CssRule.OfMedia(this.toCSSMediaQuery()) + other

/**
 * A way you can define multiple rules which all result in the same style.
 */
// TODO: Simplify this using selector lists instead?
//  See also: https://developer.mozilla.org/en-US/docs/Learn/CSS/Building_blocks/Selectors#selector_lists
fun StyleScope.cssRules(vararg rules: CssRule, createModifier: () -> Modifier) {
    rules.forEach { rule -> rule.invoke(createModifier) }
}
