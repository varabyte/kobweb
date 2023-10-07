package com.varabyte.kobweb.silk.components.style

import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.StyleModifier
import com.varabyte.kobweb.silk.components.style.breakpoint.Breakpoint
import com.varabyte.kobweb.silk.theme.breakpoint.toMinWidthQuery
import org.jetbrains.compose.web.css.*

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
