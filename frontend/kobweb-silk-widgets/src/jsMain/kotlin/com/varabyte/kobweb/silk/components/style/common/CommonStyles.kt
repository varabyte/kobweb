package com.varabyte.kobweb.silk.components.style.common

import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.components.style.ComponentStyle
import com.varabyte.kobweb.silk.components.style.CssRule
import com.varabyte.kobweb.silk.components.style.StyleModifiers
import com.varabyte.kobweb.silk.components.style.base
import org.jetbrains.compose.web.css.*

// Note: CSS provides a `disabled` selector, but disabling elements using HTML properties prevents mouse events from
// firing, and this is bad because you might want to show tooltips even for a disabled element. Some solutions online
// solve this by wrapping disabled elements in a parent element, but this can screw up things like flexbox rows and
// columns, which act on their direct children and *not* children of children (e.g. missing a `flex-grow` setting on
// an element just because you wrapped it with a tooltip).
// Instead, we just immitate disabled behavior ourselves in silk.
val DisabledStyle by ComponentStyle.base(
    prefix = "silk",
    extraModifiers = { Modifier.ariaDisabled().tabIndex(-1) }
) {
    Modifier.opacity(0.5).cursor(Cursor.NotAllowed)
}

/**
 * A way to select elements that have been tagged with an `aria-disabled` attribute.
 *
 * This is different from the `:disabled` pseudo-class selector! There are various reasons to use the ARIA version over
 * the HTML version; for example, some elements don't support `disabled` and also `disabled` elements don't fire
 * mouse events, which can be useful e.g. when implementing tooltips.
 */
val StyleModifiers.ariaDisabled get() = CssRule.OfAttributeSelector(this, """aria-disabled="true"""")

/**
 * A style which opts an element into background color transitions, which looks better than color snapping when the
 * color mode changes.
 *
 * This is recommended to be used with your app's root `Surface`:
 *
 * ```
 * Surface(SmoothColorStyle.toModifier()) { ... }
 * ```
 *
 * but you may need to additionally apply it on children which themselves modify their own background colors in response
 * to color mode changes, since in CSS, transitions are not inherited.
 *
 * Pro-tip: If you are defining your own ComponentStyle that updates the background color and want it to also apply
 * smooth styles, you can use this `extraModifiers` pattern:
 *
 * ```
 * val ExampleStyle by ComponentStyle(extraModifiers = { SmoothColorStyle.toModifier() }) {
 *    ...
 * }
 * ```
 *
 * Note: This is shared as a style instead of a simple modifier so that a user can tweak the timing in their own site by
 * overriding the style if they'd like.
 */
val SmoothColorStyle by ComponentStyle.base(prefix = "silk") {
    Modifier.transition(CSSTransition("background-color", 200.ms))
}
