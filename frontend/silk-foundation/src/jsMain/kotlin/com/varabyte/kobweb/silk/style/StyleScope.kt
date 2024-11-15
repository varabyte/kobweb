package com.varabyte.kobweb.silk.style

import com.varabyte.kobweb.compose.attributes.ComparableAttrsScope
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.silk.style.breakpoint.Breakpoint
import com.varabyte.kobweb.silk.theme.breakpoint.toMinWidthQuery
import com.varabyte.kobweb.silk.theme.breakpoint.toWidth
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.css.*
import org.w3c.dom.Element

/**
 * Class used as the receiver to a callback, allowing the user to define various state-dependent styles (defined via
 * [Modifier]s).
 *
 * See also [CssStyleScope] which extends this class to provide additional functionality for defining `CssStyle` blocks.
 */
abstract class StyleScope {
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
     * Declare a style that appears starting at the current breakpoint.
     *
     * That means this style will be visible from the current breakpoint and all larger breakpoints. For example,
     * `Breakpoint.MD` will apply to desktops, wide screens, and ultra wide screens.
     *
     * You can use `Breakpoint.MD.only { ... }` to apply styles only to the MD breakpoint.
     *
     * Note: This probably would have been an extension method except Kotlin doesn't support multiple receivers yet
     * (here, we'd need to access both "Breakpoint" and "StyleScope")
     */
    operator fun Breakpoint.invoke(createModifier: () -> Modifier) {
        cssRule(this.toMinWidthQuery(), createModifier)
    }

    inner class BreakpointRange(
        private val lower: Breakpoint,
        private val upper: Breakpoint,
        private val upperExclusive: Boolean,
    ) {
        init {
            require(lower.ordinal != upper.ordinal) { "Breakpoint range should not be called with the same breakpoint twice ($lower)." }
            require(lower.ordinal < upper.ordinal) { "Breakpoint range breakpoints passed in wrong order: $lower should be smaller than $upper" }
        }

        private fun toCSSMediaQuery(lowerInclusive: Breakpoint, upperExclusive: Breakpoint): CSSMediaQuery.Raw {
            // see also: https://www.miragecraft.com/blog/polyfill-for-media-range-syntax
            return CSSMediaQuery.Raw("(min-width: ${lowerInclusive.toWidth()}) and (max-width: ${upperExclusive.toWidth()}) and (not (width: ${upperExclusive.toWidth()}))")
        }

        operator fun invoke(createModifier: () -> Modifier) {
            if (upperExclusive) {
                cssRule(toCSSMediaQuery(lower, upper), createModifier)
            } else {
                if (upper.ordinal < Breakpoint.entries.lastIndex) {
                    cssRule(toCSSMediaQuery(lower, Breakpoint.entries[upper.ordinal + 1]), createModifier)
                } else {
                    lower.invoke(createModifier)
                }
            }
        }
    }

    /**
     * Declare a style that appears between a lower-bound breakpoint (inclusive) and an upper-bound breakpoint (inclusive).
     *
     * For example, `(SM .. MD) { ... }` will appear for tablets through desktops but not for mobile devices nor wide screens;
     * `(ZERO .. ZERO) { ... }` will only apply to mobile devices.
     *
     * Note: `(ZERO .. ZERO) { ... }` doesn't read intuitively, and we would recommend `(ZERO until SM) { ... }` or even
     * just `until(SM) { ... }` in this case.
     *
     * Note: This probably would have been an extension method except Kotlin doesn't support multiple receivers yet
     * (here, we'd need to access both "Breakpoint" and "StyleScope")
     */
    operator fun Breakpoint.rangeTo(upper: Breakpoint): BreakpointRange {
        return BreakpointRange(this, upper, upperExclusive = false)
    }

    /**
     * Declare a style that appears between a lower-bound breakpoint (inclusive) and an upper-bound breakpoint (exclusive).
     *
     * For example, `(SM ..< LG) { ... }` will appear for tablets through desktops but not for mobile devices nor wide
     * screens; `(ZERO ..< SM) { ... }` will only apply to mobile devices.
     *
     * Note: `(ZERO ..< SM) { ... }` reads OK, but we would probably recommend `until(SM) { ... }` in this case.
     *
     * Note: This probably would have been an extension method except Kotlin doesn't support multiple receivers yet
     * (here, we'd need to access both "Breakpoint" and "StyleScope")
     */
    operator fun Breakpoint.rangeUntil(upper: Breakpoint): BreakpointRange {
        return BreakpointRange(this, upper, upperExclusive = true)
    }

    /**
     * Convenience method replacing the [rangeUntil] operator syntax with something more readable.
     */
    infix fun Breakpoint.until(upper: Breakpoint): BreakpointRange {
        return this.rangeUntil(upper)
    }
}

/**
 * Convenience method for tying a general CSS rule to a breakpoint.
 *
 * This is equivalent to:
 *
 * ```
 * cssRule(breakpoint.toMinWidthQuery(), suffix, createModifier)
 * ```
 *
 * but when people use a breakpoint, like Breakpoint.MD, they almost always want to use the min-width query.
 */
fun StyleScope.cssRule(breakpoint: Breakpoint, suffix: String?, createModifier: () -> Modifier) {
    cssRule(breakpoint.toMinWidthQuery(), suffix, createModifier)
}

/**
 * Declare a style that appears up until the current breakpoint.
 *
 * That means this style will NOT be visible at the current breakpoint, but will be for all smaller breakpoints. For
 *  example, `until(Breakpoint.MD)` will apply to mobile and tablet devices.
 */
fun StyleScope.until(breakpoint: Breakpoint, createModifier: () -> Modifier) {
    (Breakpoint.ZERO ..< breakpoint).invoke(createModifier)
}

/**
 * Declare a style that appears between a lower breakpoint (inclusive) and an upper breakpoint (exclusive).
 *
 * This is a convenience method which might be easier to discover than requiring parentheses (that is,
 * `between(SM, LG)` vs `(SM ..< LG)` or `(SM until LG)`)
 *
 * @see StyleScope.rangeUntil
 */
fun StyleScope.between(lower: Breakpoint, upper: Breakpoint, createModifier: () -> Modifier) {
    (lower ..< upper).invoke(createModifier)
}


// The
// For example, ".myclass:hover" separates ".myclass" from ":hover".
// See: https://www.w3schools.com/cssref/css_selectors.php and https://www.w3schools.com/cssref/trysel.php
private val selectorSeparators = setOf(' ', '>', '+', '~', '.', ':', ',', '[')

/**
 * Represents a [Modifier] entry that is tied to a css rule, e.g. the modifier for ".myclass:hover" for example.
 */
internal class CssModifier(
    val modifier: Modifier,
    val mediaQuery: CSSMediaQuery? = null,
    suffix: String? = null,
) {
    // People might use e.g. "h1" as a suffix, but it has to be " h1" (leading space) to avoid running into the previous
    // part of the selector (e.g. ".myclass h1", not ".myclassh1"). Let's detect this ourselves and add the space, since
    // we understand the user's intentions (and forgetting the space is really hard to debug).
    val suffix: String? =
        suffix?.takeIf { it.isNotBlank() }?.let { if (it.first() !in selectorSeparators ) " $it" else it }

    internal fun mergeWith(other: CssModifier): CssModifier {
        check(this !== other && mediaQuery == other.mediaQuery && suffix == other.suffix)
        return CssModifier(modifier.then(other.modifier), mediaQuery, suffix)
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

internal fun CssModifier.assertNoAttributes(selectorName: String, extraContext: String) {
    val attrsScope = ComparableAttrsScope<Element>()
    this.modifier.toAttrs<AttrsScope<Element>>().invoke(attrsScope)

    if (attrsScope.attributes.isEmpty()) return

    error(buildString {
        appendLine("Style block declarations cannot contain Modifiers that specify attributes. Only style modifiers are allowed here.")
        appendLine()
        appendLine("Details:")

        append("\tCSS rule: ")
        append("\"$selectorName")
        if (mediaQuery != null) append(mediaQuery)
        if (suffix != null) append(suffix)
        appendLine("\"")
        appendLine("\tAttribute(s): ${attrsScope.attributes.keys.joinToString(", ") { "\"$it\"" }}")
        appendLine()
        appendLine(extraContext)
    })
}
