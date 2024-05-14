package com.varabyte.kobweb.silk.style.selectors

import com.varabyte.kobweb.silk.style.CssRule
import com.varabyte.kobweb.silk.style.StyleScope
import org.jetbrains.compose.web.css.*

//region Pseudo classes

//region Location

/**
 * Styles to apply to components that would be matched by both "link" or "visited".
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/:any-link">:any-link</a>
 */
val StyleScope.anyLink get() = CssRule.OfPseudoClass(this, "any-link")

/**
 * Styles to apply to components that represent navigation links which have not yet been visited.
 *
 * Be aware that you should use the LVHA order if using link, visited, hover, and/or active pseudo classes.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/:link">:link</a>
 */
val StyleScope.link get() = CssRule.OfPseudoClass(this, "link")

/**
 * Styles to apply to elements that are targets of links in the same document.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/:target">:target</a>
 */
val StyleScope.target get() = CssRule.OfPseudoClass(this, "target")

/**
 * Styles to apply to components that represent navigation links which have previously been visited.
 *
 * Be aware that you should use the LVHA order if using link, visited, hover, and/or active pseudo classes.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/:visited">:visited</a>
 */
val StyleScope.visited get() = CssRule.OfPseudoClass(this, "visited")

//endregion

//region User action

/**
 * Styles to apply to components when a cursor is pointing at them.
 *
 * Be aware that you should use the LVHA order if using link, visited, hover, and/or active pseudo classes.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/:hover">:hover</a>
 */
val StyleScope.hover get() = CssRule.OfPseudoClass(this, "hover")

/**
 * Styles to apply to components when a cursor is interacting with them.
 *
 * Be aware that you should use the LVHA order if using link, visited, hover, and/or active pseudo classes.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/:active">:active</a>
 */
val StyleScope.active: CssRule.OfPseudoClass get() = CssRule.OfPseudoClass(this, "active")

/**
 * Styles to apply to components when they have focus.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/:focus">:focus</a>
 */
val StyleScope.focus get() = CssRule.OfPseudoClass(this, "focus")

/**
 * Styles to apply to components when they have keyboard / a11y-assisted focus.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/:focus-visible">:focus-visible</a>
 */
val StyleScope.focusVisible get() = CssRule.OfPseudoClass(this, "focus-visible")

/**
 * Styles to apply to components when they or any descendants have focus.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/:focus-within">:focus-within</a>
 */
val StyleScope.focusWithin get() = CssRule.OfPseudoClass(this, "focus-within")

//endregion

//region Input

/**
 * Matches when an input element has been autofilled by the browser.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/:autofill">:autofill</a>
 */
val StyleScope.autofill get() = CssRule.OfPseudoClass(this, "autofill")

/**
 * Represents a user interface element that is in an enabled state.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/:enabled">:enabled</a>
 */
val StyleScope.enabled get() = CssRule.OfPseudoClass(this, "enabled")

/**
 * Represents a user interface element that is in a disabled state.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/:disabled">:disabled</a>
 */
val StyleScope.disabled get() = CssRule.OfPseudoClass(this, "disabled")

/**
 * Represents any element that cannot be changed by the user.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/:read-only">:read-only</a>
 */
val StyleScope.readOnly get() = CssRule.OfPseudoClass(this, "read-only")

/**
 * Represents any element that is user-editable.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/:read-write">:read-write</a>
 */
val StyleScope.readWrite get() = CssRule.OfPseudoClass(this, "read-write")

/**
 * Matches an input element that is displaying placeholder text.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/:placeholder-shown">:placeholder-shown</a>
 */
val StyleScope.placeholderShown get() = CssRule.OfPseudoClass(this, "placeholder-shown")

/**
 * Matches one or more UI elements that are the default among a set of elements.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/:default">:default</a>
 */
val StyleScope.default get() = CssRule.OfPseudoClass(this, "default")

/**
 * Matches an element, such as checkboxes and radio buttons, that are checked or toggled to an `on` state.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/:checked">:checked</a>
 */
val StyleScope.checked get() = CssRule.OfPseudoClass(this, "checked")

/**
 * Matches when elements, such as checkboxes and radio buttons, are in an indeterminate state.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/:indeterminate">:indeterminate</a>
 */
val StyleScope.indeterminate get() = CssRule.OfPseudoClass(this, "indeterminate")

/**
 * Matches an element with valid contents. For example, an input element with type 'email' which contains a validly formed email address.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/:valid">:valid</a>
 */
val StyleScope.valid get() = CssRule.OfPseudoClass(this, "valid")

/**
 * Matches an element with invalid contents. For example, an input element with type 'email' with a name entered.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/:invalid">:invalid</a>
 */
val StyleScope.invalid get() = CssRule.OfPseudoClass(this, "invalid")

/**
 * Applies to elements with range limitations, for example a slider control, when the selected value is in the allowed range.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/:in-range">:in-range</a>
 */
val StyleScope.inRange get() = CssRule.OfPseudoClass(this, "in-range")

/**
 * Applies to elements with range limitations, for example a slider control, when the selected value is outside the
 * allowed range.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/:out-of-range">:out-of-range</a>
 */
val StyleScope.outOfRange get() = CssRule.OfPseudoClass(this, "out-of-range")

/**
 * Matches when a form element is required.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/:required">:required</a>
 */
val StyleScope.required get() = CssRule.OfPseudoClass(this, "required")

/**
 * Matches when a form element is optional.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/:optional">:optional</a>
 */
val StyleScope.optional get() = CssRule.OfPseudoClass(this, "optional")

/**
 * Represents an element with correct input, but only when the user has interacted with it.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/:user-valid">:user-valid</a>
 */
val StyleScope.userValid get() = CssRule.OfPseudoClass(this, "user-valid")

/**
 * Represents an element with incorrect input, but only when the user has interacted with it.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/:user-invalid">:user-invalid</a>
 */
val StyleScope.userInvalid get() = CssRule.OfPseudoClass(this, "user-invalid")

//endregion

//region Tree

/**
 * Represents an element that is the root of the document. In HTML this is usually the `<html>` element.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/:root">:root</a>
 */
val StyleScope.root get() = CssRule.OfPseudoClass(this, "root")

/**
 * Represents an element with no children other than white-space characters.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/:empty">:empty</a>
 */
val StyleScope.empty get() = CssRule.OfPseudoClass(this, "empty")

/**
 * Matches an element that is the first of its siblings.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/:first-child">:first-child</a>
 */
val StyleScope.firstChild get() = CssRule.OfPseudoClass(this, "first-child")

/**
 * Matches an element that is the last of its siblings.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/:last-child">:last-child</a>
 */
val StyleScope.lastChild get() = CssRule.OfPseudoClass(this, "last-child")

/**
 * Matches an element that has no siblings. For example, a list item with no other list items in that list.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/:only-child">:only-child</a>
 */
val StyleScope.onlyChild get() = CssRule.OfPseudoClass(this, "only-child")

/**
 * Matches an element that is the first of its siblings, and also matches a certain type selector.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/:first-of-type">:first-of-type</a>
 */
val StyleScope.firstOfType get() = CssRule.OfPseudoClass(this, "first-of-type")

/**
 * Matches an element that is the last of its siblings, and also matches a certain type selector.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/:last-of-type">:last-of-type</a>
 */
val StyleScope.lastOfType get() = CssRule.OfPseudoClass(this, "last-of-type")

/**
 * Matches an element that has no siblings of the chosen type selector.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/:only-of-type">:only-of-type</a>
 */
val StyleScope.onlyOfType get() = CssRule.OfPseudoClass(this, "only-of-type")

//endregion

//endregion

//region Pseudo elements

/**
 * Styles to apply to a virtual element that is created before the first element in some container.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/::before">::before</a>
 */
val StyleScope.before get() = CssRule.OfPseudoElement(this, "before")

/**
 * Styles to apply to a virtual element that is created after the last element in some container.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/::after">::after</a>
 */
val StyleScope.after get() = CssRule.OfPseudoElement(this, "after")

/**
 * Styles to apply to the selected part of a document.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/::selection">::selection</a>
 */
val StyleScope.selection get() = CssRule.OfPseudoElement(this, "selection")

/**
 * Styles to apply to the first letter in a block of text.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/::first-letter">::first-letter</a>
 */
val StyleScope.firstLetter get() = CssRule.OfPseudoElement(this, "first-letter")

/**
 * Styles to apply to the first line in a block of text.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/::first-line">::first-line</a>
 */
val StyleScope.firstLine get() = CssRule.OfPseudoElement(this, "first-line")

/**
 * Matches the placeholder text within an input element that is displaying placeholder text.
 *
 * Note that if you override the color of the placeholder, you should consider setting its `opacity` to `1`.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/::placeholder">::placeholder</a>
 */
val StyleScope.placeholder get() = CssRule.OfPseudoElement(this, "placeholder")

//endregion

// region Media queries

/**
 * Used to indicate styles which should only be applied when the page is being printed.
 */
val StyleScope.mediaPrint get() = CssRule.OfMedia(this, CSSMediaQuery.MediaType(CSSMediaQuery.MediaType.Enum.Print))

//endregion

// region Functional pseudo classes

fun StyleScope.not(vararg params: CssRule.NonMediaCssRule) = CssRule.OfFunctionalPseudoClass(this, "not", *params)

//endregion

//region Aria attributes

/**
 * A way to select elements that have been tagged with an `aria-disabled` attribute.
 *
 * This is different from the `:disabled` pseudo-class selector! There are various reasons to use the ARIA version over
 * the HTML version; for example, some elements don't support `disabled` and also `disabled` elements don't fire
 * mouse events, which can be useful e.g. when implementing tooltips.
 */
val StyleScope.ariaDisabled get() = CssRule.OfAttributeSelector(this, """aria-disabled="true"""")

/**
 * A way to select elements that have been tagged with an `aria-invalid` attribute.
 *
 * This is different from the `:invalid` pseudo-class selector! The `invalid` pseudo-class is useful when the standard
 * widget supports a general invalidation algorithm (like an email type input with an invalid email address), but the
 * `ariaInvalid` version can be used to support custom invalidation strategies.
 */
val StyleScope.ariaInvalid get() = CssRule.OfAttributeSelector(this, """aria-invalid="true"""")

/**
 * A way to select elements that have been tagged with an `aria-required` attribute.
 *
 * This is different from the `:required` pseudo-class selector! It can be useful to use the `ariaRequired` tag when
 * using elements that don't support the `required` attribute, like elements created from divs, as a way to communicate
 * their required state to accessibility readers.
 */
val StyleScope.ariaRequired get() = CssRule.OfAttributeSelector(this, """aria-required="true"""")

//endregion
