package com.varabyte.kobweb.silk.style

import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.silk.style.breakpoint.Breakpoint
import com.varabyte.kobweb.silk.theme.breakpoint.toMinWidthQuery
import org.jetbrains.compose.web.css.*

/**
 * A class which can be used to set CSS rules on a target [StyleScope] instance using types to prevent
 * invalid combinations.
 *
 * A CSS rule can consist of an optional breakpoint, zero or more pseudo-classes, and an optional trailing
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
sealed class CssRule(val target: StyleScope) {
    companion object {
        /**
         * A CSS rule that represents a functional pseudo-class.
         *
         * For example, passing in "not" would result in: `:not(...)`
         */
        fun OfFunctionalPseudoClass(target: StyleScope, pseudoClass: String, vararg params: NonMediaCssRule) =
            OfPseudoClass(target, "$pseudoClass(${params.mapNotNull { it.toSelectorText() }.joinToString()})")
    }

    operator fun invoke(createModifier: () -> Modifier) {
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
    class OfMedia(target: StyleScope, override val mediaQuery: CSSMediaQuery) : CssRule(target) {
        operator fun plus(other: OfPseudoClass) =
            CompositeOpen(target, mediaQuery, emptyList(), listOf(other))

        operator fun plus(other: OfPseudoElement) =
            CompositeClosed(target, mediaQuery, emptyList(), emptyList(), other)
    }

    sealed class NonMediaCssRule(target: StyleScope) : CssRule(target)

    /**
     * A CSS rule that represents an attribute selector.
     *
     * For example, passing in "aria-disabled" would result in: `[aria-disabled]`
     */
    class OfAttributeSelector(target: StyleScope, val attributeSelector: String) : NonMediaCssRule(target) {
        override fun toSelectorText() = buildSelectorText(listOf(this), emptyList(), null)

        operator fun plus(other: OfAttributeSelector) =
            CompositeOpen(target, null, listOf(this, other), emptyList())

        operator fun plus(other: OfPseudoClass) =
            CompositeOpen(target, null, listOf(this), listOf(other))

        operator fun plus(other: OfPseudoElement) =
            CompositeClosed(target, null, listOf(this), emptyList(), other)

    }

    /**
     * A CSS rule that represents a pseudo-class selector.
     *
     * For example, passing in "hover" would result in: `:hover`
     */
    class OfPseudoClass(target: StyleScope, val pseudoClass: String) : NonMediaCssRule(target) {
        override fun toSelectorText() = buildSelectorText(emptyList(), listOf(this), null)

        operator fun plus(other: OfPseudoClass) =
            CompositeOpen(target, null, emptyList(), listOf(this, other))

        operator fun plus(other: OfPseudoElement) =
            CompositeClosed(target, null, emptyList(), listOf(this), other)
    }

    /**
     * A CSS rule that represents a pseudo-element selector.
     *
     * For example, passing in "after" would result in: `::after`
     */
    class OfPseudoElement(target: StyleScope, val pseudoElement: String) : NonMediaCssRule(target) {
        override fun toSelectorText() = buildSelectorText(emptyList(), emptyList(), this)
    }

    /**
     * A composite CSS rule that is a chain of subparts and still open to accepting more pseudo-classes and/or a
     * pseudo-element.
     */
    class CompositeOpen(
        target: StyleScope,
        override val mediaQuery: CSSMediaQuery?,
        val attributeSelectors: List<OfAttributeSelector>,
        val pseudoClasses: List<OfPseudoClass>
    ) : NonMediaCssRule(target) {
        override fun toSelectorText() = buildSelectorText(attributeSelectors, pseudoClasses, null)

        operator fun plus(other: OfPseudoClass) =
            CompositeOpen(target, null, attributeSelectors, pseudoClasses + other)

        operator fun plus(other: OfAttributeSelector) =
            CompositeOpen(target, null, attributeSelectors + other, pseudoClasses)

        operator fun plus(other: OfPseudoElement) =
            CompositeClosed(target, null, attributeSelectors, pseudoClasses, other)
    }

    /**
     * A composite CSS rule that is a chain of subparts which is terminated - it cannot grow any further but can only
     * be invoked at this point.
     */
    class CompositeClosed(
        target: StyleScope,
        override val mediaQuery: CSSMediaQuery?,
        val attributeSelectors: List<OfAttributeSelector>,
        val pseudoClasses: List<OfPseudoClass>,
        val pseudoElement: OfPseudoElement
    ) : NonMediaCssRule(target) {
        override fun toSelectorText() = buildSelectorText(attributeSelectors, pseudoClasses, pseudoElement)
    }
}

// Breakpoint extensions to allow adding styles to normal breakpoint values, e.g. "Breakpoint.MD + hover"
operator fun Breakpoint.plus(other: CssRule.OfPseudoClass) =
    CssRule.OfMedia(other.target, this.toMinWidthQuery()) + other

operator fun Breakpoint.plus(other: CssRule.OfPseudoElement) =
    CssRule.OfMedia(other.target, this.toMinWidthQuery()) + other

/**
 * A way you can define multiple rules which all result in the same style.
 */
// TODO: Simplify this using selector lists instead?
//  See also: https://developer.mozilla.org/en-US/docs/Learn/CSS/Building_blocks/Selectors#selector_lists
fun cssRules(vararg rules: CssRule, createModifier: () -> Modifier) {
    rules.forEach { rule -> rule.invoke(createModifier) }
}
