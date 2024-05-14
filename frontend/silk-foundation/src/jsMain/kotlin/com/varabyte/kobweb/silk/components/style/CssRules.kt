@file:Suppress("DeprecatedCallableAddReplaceWith") // ReplaceWith doesn't work great for extension methods

package com.varabyte.kobweb.silk.components.style

import com.varabyte.kobweb.silk.style.CssRule
import com.varabyte.kobweb.silk.style.StyleScope
import com.varabyte.kobweb.silk.style.selectors.active
import com.varabyte.kobweb.silk.style.selectors.after
import com.varabyte.kobweb.silk.style.selectors.anyLink
import com.varabyte.kobweb.silk.style.selectors.ariaDisabled
import com.varabyte.kobweb.silk.style.selectors.ariaInvalid
import com.varabyte.kobweb.silk.style.selectors.ariaRequired
import com.varabyte.kobweb.silk.style.selectors.autofill
import com.varabyte.kobweb.silk.style.selectors.before
import com.varabyte.kobweb.silk.style.selectors.checked
import com.varabyte.kobweb.silk.style.selectors.default
import com.varabyte.kobweb.silk.style.selectors.disabled
import com.varabyte.kobweb.silk.style.selectors.empty
import com.varabyte.kobweb.silk.style.selectors.enabled
import com.varabyte.kobweb.silk.style.selectors.firstChild
import com.varabyte.kobweb.silk.style.selectors.firstLetter
import com.varabyte.kobweb.silk.style.selectors.firstLine
import com.varabyte.kobweb.silk.style.selectors.firstOfType
import com.varabyte.kobweb.silk.style.selectors.focus
import com.varabyte.kobweb.silk.style.selectors.focusVisible
import com.varabyte.kobweb.silk.style.selectors.focusWithin
import com.varabyte.kobweb.silk.style.selectors.hover
import com.varabyte.kobweb.silk.style.selectors.inRange
import com.varabyte.kobweb.silk.style.selectors.indeterminate
import com.varabyte.kobweb.silk.style.selectors.invalid
import com.varabyte.kobweb.silk.style.selectors.lastChild
import com.varabyte.kobweb.silk.style.selectors.lastOfType
import com.varabyte.kobweb.silk.style.selectors.link
import com.varabyte.kobweb.silk.style.selectors.mediaPrint
import com.varabyte.kobweb.silk.style.selectors.not
import com.varabyte.kobweb.silk.style.selectors.onlyChild
import com.varabyte.kobweb.silk.style.selectors.onlyOfType
import com.varabyte.kobweb.silk.style.selectors.optional
import com.varabyte.kobweb.silk.style.selectors.outOfRange
import com.varabyte.kobweb.silk.style.selectors.placeholder
import com.varabyte.kobweb.silk.style.selectors.placeholderShown
import com.varabyte.kobweb.silk.style.selectors.readOnly
import com.varabyte.kobweb.silk.style.selectors.readWrite
import com.varabyte.kobweb.silk.style.selectors.required
import com.varabyte.kobweb.silk.style.selectors.root
import com.varabyte.kobweb.silk.style.selectors.selection
import com.varabyte.kobweb.silk.style.selectors.target
import com.varabyte.kobweb.silk.style.selectors.userInvalid
import com.varabyte.kobweb.silk.style.selectors.userValid
import com.varabyte.kobweb.silk.style.selectors.valid
import com.varabyte.kobweb.silk.style.selectors.visited

//region Pseudo classes

//region Location

/**
 * Styles to apply to components that would be matched by both "link" or "visited".
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/:any-link">:any-link</a>
 */
@Deprecated("Please change your import to `com.varabyte.kobweb.silk.style.selectors.anyLink`")
val StyleScope.anyLink get() = anyLink

/**
 * Styles to apply to components that represent navigation links which have not yet been visited.
 *
 * Be aware that you should use the LVHA order if using link, visited, hover, and/or active pseudo classes.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/:link">:link</a>
 */
@Deprecated("Please change your import to `com.varabyte.kobweb.silk.style.selectors.link`")
val StyleScope.link get() = link

/**
 * Styles to apply to elements that are targets of links in the same document.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/:target">:target</a>
 */
@Deprecated("Please change your import to `com.varabyte.kobweb.silk.style.selectors.target`")
val StyleScope.target get() = target

/**
 * Styles to apply to components that represent navigation links which have previously been visited.
 *
 * Be aware that you should use the LVHA order if using link, visited, hover, and/or active pseudo classes.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/:visited">:visited</a>
 */
@Deprecated("Please change your import to `com.varabyte.kobweb.silk.style.selectors.visited`")
val StyleScope.visited get() = visited

//endregion

//region User action

/**
 * Styles to apply to components when a cursor is pointing at them.
 *
 * Be aware that you should use the LVHA order if using link, visited, hover, and/or active pseudo classes.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/:hover">:hover</a>
 */
@Deprecated("Please change your import to `com.varabyte.kobweb.silk.style.selectors.hover`")
val StyleScope.hover get() = hover

/**
 * Styles to apply to components when a cursor is interacting with them.
 *
 * Be aware that you should use the LVHA order if using link, visited, hover, and/or active pseudo classes.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/:active">:active</a>
 */
@Deprecated("Please change your import to `com.varabyte.kobweb.silk.style.selectors.active`")
val StyleScope.active get() = active

/**
 * Styles to apply to components when they have focus.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/:focus">:focus</a>
 */
@Deprecated("Please change your import to `com.varabyte.kobweb.silk.style.selectors.focus`")
val StyleScope.focus get() = focus

/**
 * Styles to apply to components when they have keyboard / a11y-assisted focus.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/:focus-visible">:focus-visible</a>
 */
@Deprecated("Please change your import to `com.varabyte.kobweb.silk.style.selectors.focusVisible`")
val StyleScope.focusVisible get() = focusVisible

/**
 * Styles to apply to components when they or any descendants have focus.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/:focus-within">:focus-within</a>
 */
@Deprecated("Please change your import to `com.varabyte.kobweb.silk.style.selectors.focusWithin`")
val StyleScope.focusWithin get() = focusWithin

//endregion

//region Input

/**
 * Matches when an input element has been autofilled by the browser.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/:autofill">:autofill</a>
 */
@Deprecated("Please change your import to `com.varabyte.kobweb.silk.style.selectors.autofill`")
val StyleScope.autofill get() = autofill

/**
 * Represents a user interface element that is in an enabled state.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/:enabled">:enabled</a>
 */
@Deprecated("Please change your import to `com.varabyte.kobweb.silk.style.selectors.enabled`")
val StyleScope.enabled get() = enabled

/**
 * Represents a user interface element that is in a disabled state.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/:disabled">:disabled</a>
 */
@Deprecated("Please change your import to `com.varabyte.kobweb.silk.style.selectors.disabled`")
val StyleScope.disabled get() = disabled

/**
 * Represents any element that cannot be changed by the user.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/:read-only">:read-only</a>
 */
@Deprecated("Please change your import to `com.varabyte.kobweb.silk.style.selectors.readOnly`")
val StyleScope.readOnly get() = readOnly

/**
 * Represents any element that is user-editable.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/:read-write">:read-write</a>
 */
@Deprecated("Please change your import to `com.varabyte.kobweb.silk.style.selectors.readWrite`")
val StyleScope.readWrite get() = readWrite

/**
 * Matches an input element that is displaying placeholder text.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/:placeholder-shown">:placeholder-shown</a>
 */
@Deprecated("Please change your import to `com.varabyte.kobweb.silk.style.selectors.placeholderShown`")
val StyleScope.placeholderShown get() = placeholderShown

/**
 * Matches one or more UI elements that are the default among a set of elements.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/:default">:default</a>
 */
@Deprecated("Please change your import to `com.varabyte.kobweb.silk.style.selectors.default`")
val StyleScope.default get() = default

/**
 * Matches an element, such as checkboxes and radio buttons, that are checked or toggled to an `on` state.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/:checked">:checked</a>
 */
@Deprecated("Please change your import to `com.varabyte.kobweb.silk.style.selectors.checked`")
val StyleScope.checked get() = checked

/**
 * Matches when elements, such as checkboxes and radio buttons, are in an indeterminate state.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/:indeterminate">:indeterminate</a>
 */
@Deprecated("Please change your import to `com.varabyte.kobweb.silk.style.selectors.indeterminate`")
val StyleScope.indeterminate get() = indeterminate

/**
 * Matches an element with valid contents. For example, an input element with type 'email' which contains a validly formed email address.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/:valid">:valid</a>
 */
@Deprecated("Please change your import to `com.varabyte.kobweb.silk.style.selectors.valid`")
val StyleScope.valid get() = valid

/**
 * Matches an element with invalid contents. For example, an input element with type 'email' with a name entered.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/:invalid">:invalid</a>
 */
@Deprecated("Please change your import to `com.varabyte.kobweb.silk.style.selectors.invalid`")
val StyleScope.invalid get() = invalid

/**
 * Applies to elements with range limitations, for example a slider control, when the selected value is in the allowed range.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/:in-range">:in-range</a>
 */
@Deprecated("Please change your import to `com.varabyte.kobweb.silk.style.selectors.inRange`")
val StyleScope.inRange get() = inRange

/**
 * Applies to elements with range limitations, for example a slider control, when the selected value is outside the
 * allowed range.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/:out-of-range">:out-of-range</a>
 */
@Deprecated("Please change your import to `com.varabyte.kobweb.silk.style.selectors.outOfRange`")
val StyleScope.outOfRange get() = outOfRange

/**
 * Matches when a form element is required.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/:required">:required</a>
 */
@Deprecated("Please change your import to `com.varabyte.kobweb.silk.style.selectors.required`")
val StyleScope.required get() = required

/**
 * Matches when a form element is optional.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/:optional">:optional</a>
 */
@Deprecated("Please change your import to `com.varabyte.kobweb.silk.style.selectors.optional`")
val StyleScope.optional get() = optional

/**
 * Represents an element with correct input, but only when the user has interacted with it.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/:user-valid">:user-valid</a>
 */
@Deprecated("Please change your import to `com.varabyte.kobweb.silk.style.selectors.userValid`")
val StyleScope.userValid get() = userValid

/**
 * Represents an element with incorrect input, but only when the user has interacted with it.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/:user-invalid">:user-invalid</a>
 */
@Deprecated("Please change your import to `com.varabyte.kobweb.silk.style.selectors.userInvalid`")
val StyleScope.userInvalid get() = userInvalid

//endregion

//region Tree

/**
 * Represents an element that is the root of the document. In HTML this is usually the `<html>` element.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/:root">:root</a>
 */
@Deprecated("Please change your import to `com.varabyte.kobweb.silk.style.selectors.root`")
val StyleScope.root get() = root

/**
 * Represents an element with no children other than white-space characters.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/:empty">:empty</a>
 */
@Deprecated("Please change your import to `com.varabyte.kobweb.silk.style.selectors.empty`")
val StyleScope.empty get() = empty

/**
 * Matches an element that is the first of its siblings.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/:first-child">:first-child</a>
 */
@Deprecated("Please change your import to `com.varabyte.kobweb.silk.style.selectors.firstChild`")
val StyleScope.firstChild get() = firstChild

/**
 * Matches an element that is the last of its siblings.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/:last-child">:last-child</a>
 */
@Deprecated("Please change your import to `com.varabyte.kobweb.silk.style.selectors.lastChild`")
val StyleScope.lastChild get() = lastChild

/**
 * Matches an element that has no siblings. For example, a list item with no other list items in that list.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/:only-child">:only-child</a>
 */
@Deprecated("Please change your import to `com.varabyte.kobweb.silk.style.selectors.onlyChild`")
val StyleScope.onlyChild get() = onlyChild

/**
 * Matches an element that is the first of its siblings, and also matches a certain type selector.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/:first-of-type">:first-of-type</a>
 */
@Deprecated("Please change your import to `com.varabyte.kobweb.silk.style.selectors.firstOfType`")
val StyleScope.firstOfType get() = firstOfType

/**
 * Matches an element that is the last of its siblings, and also matches a certain type selector.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/:last-of-type">:last-of-type</a>
 */
@Deprecated("Please change your import to `com.varabyte.kobweb.silk.style.selectors.lastOfType`")
val StyleScope.lastOfType get() = lastOfType

/**
 * Matches an element that has no siblings of the chosen type selector.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/:only-of-type">:only-of-type</a>
 */
@Deprecated("Please change your import to `com.varabyte.kobweb.silk.style.selectors.onlyOfType`")
val StyleScope.onlyOfType get() = onlyOfType

//endregion

//endregion

//region Pseudo elements

/**
 * Styles to apply to a virtual element that is created before the first element in some container.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/::before">::before</a>
 */
@Deprecated("Please change your import to `com.varabyte.kobweb.silk.style.selectors.before`")
val StyleScope.before get() = before

/**
 * Styles to apply to a virtual element that is created after the last element in some container.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/::after">::after</a>
 */
@Deprecated("Please change your import to `com.varabyte.kobweb.silk.style.selectors.after`")
val StyleScope.after get() = after

/**
 * Styles to apply to the selected part of a document.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/::selection">::selection</a>
 */
@Deprecated("Please change your import to `com.varabyte.kobweb.silk.style.selectors.selection`")
val StyleScope.selection get() = selection

/**
 * Styles to apply to the first letter in a block of text.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/::first-letter">::first-letter</a>
 */
@Deprecated("Please change your import to `com.varabyte.kobweb.silk.style.selectors.firstLetter`")
val StyleScope.firstLetter get() = firstLetter

/**
 * Styles to apply to the first line in a block of text.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/::first-line">::first-line</a>
 */
@Deprecated("Please change your import to `com.varabyte.kobweb.silk.style.selectors.firstLine`")
val StyleScope.firstLine get() = firstLine

/**
 * Matches the placeholder text within an input element that is displaying placeholder text.
 *
 * Note that if you override the color of the placeholder, you should consider setting its `opacity` to `1`.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/::placeholder">::placeholder</a>
 */
@Deprecated("Please change your import to `com.varabyte.kobweb.silk.style.selectors.placeholder`")
val StyleScope.placeholder get() = placeholder

//endregion

// region Media queries

/**
 * Used to indicate styles which should only be applied when the page is being printed.
 */
@Deprecated("Please change your import to `com.varabyte.kobweb.silk.style.selectors.mediaPrint`")
val StyleScope.mediaPrint get() = mediaPrint

//endregion

// region Functional pseudo classes

@Deprecated("Please change your import to `com.varabyte.kobweb.silk.style.selectors.not`")
fun StyleScope.not(vararg params: CssRule.NonMediaCssRule) = not(*params)

//endregion

//region Aria attributes

/**
 * A way to select elements that have been tagged with an `aria-disabled` attribute.
 *
 * This is different from the `:disabled` pseudo-class selector! There are various reasons to use the ARIA version over
 * the HTML version; for example, some elements don't support `disabled` and also `disabled` elements don't fire
 * mouse events, which can be useful e.g. when implementing tooltips.
 */
@Deprecated("Please change your import to `com.varabyte.kobweb.silk.style.selectors.ariaDisabled`")
val StyleScope.ariaDisabled get() = ariaDisabled

/**
 * A way to select elements that have been tagged with an `aria-invalid` attribute.
 *
 * This is different from the `:invalid` pseudo-class selector! The `invalid` pseudo-class is useful when the standard
 * widget supports a general invalidation algorithm (like an email type input with an invalid email address), but the
 * `ariaInvalid` version can be used to support custom invalidation strategies.
 */
@Deprecated("Please change your import to `com.varabyte.kobweb.silk.style.selectors.ariaInvalid`")
val StyleScope.ariaInvalid get() = ariaInvalid

/**
 * A way to select elements that have been tagged with an `aria-required` attribute.
 *
 * This is different from the `:required` pseudo-class selector! It can be useful to use the `ariaRequired` tag when
 * using elements that don't support the `required` attribute, like elements created from divs, as a way to communicate
 * their required state to accessibility readers.
 */
@Deprecated("Please change your import to `com.varabyte.kobweb.silk.style.selectors.ariaRequired`")
val StyleScope.ariaRequired get() = ariaRequired

//endregion
