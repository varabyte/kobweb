package com.varabyte.kobweb.silk.components.style

import org.jetbrains.compose.web.css.*

//region Pseudo classes

//region Location

/**
 * Styles to apply to components that would be matched by both "link" or "visited".
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:any-link
 */
val StyleModifiers.anyLink get() = CssRule.OfPseudoClass(this, "any-link")

/**
 * Styles to apply to components that represent navigation links which have not yet been visited.
 *
 * Be aware that you should use the LVHA order if using link, visited, hover, and/or active pseudo classes.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:link
 */
val StyleModifiers.link get() = CssRule.OfPseudoClass(this, "link")

/**
 * Styles to apply to elements that are targets of links in the same document.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:target
 */
val StyleModifiers.target get() = CssRule.OfPseudoClass(this, "target")

/**
 * Styles to apply to components that represent navigation links which have previously been visited.
 *
 * Be aware that you should use the LVHA order if using link, visited, hover, and/or active pseudo classes.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:visited
 */
val StyleModifiers.visited get() = CssRule.OfPseudoClass(this, "visited")

//endregion

//region User action

/**
 * Styles to apply to components when a cursor is pointing at them.
 *
 * Be aware that you should use the LVHA order if using link, visited, hover, and/or active pseudo classes.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:hover
 */
val StyleModifiers.hover get() = CssRule.OfPseudoClass(this, "hover")

/**
 * Styles to apply to components when a cursor is interacting with them.
 *
 * Be aware that you should use the LVHA order if using link, visited, hover, and/or active pseudo classes.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:active
 */
val StyleModifiers.active get() = CssRule.OfPseudoClass(this, "active")


/**
 * Styles to apply to components when they have focus.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:focus
 */
val StyleModifiers.focus get() = CssRule.OfPseudoClass(this, "focus")

/**
 * Styles to apply to components when they have keybaord / a11y-assisted focus.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:focus-visible
 */
val StyleModifiers.focusVisible get() = CssRule.OfPseudoClass(this, "focus-visible")

//endregion

//region Input

/**
 * Matches when an input element has been autofilled by the browser.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:autofill
 */
val StyleModifiers.autofill get() = CssRule.OfPseudoClass(this, "autofill")

/**
 * Represents a user interface element that is in an enabled state.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:enabled
 */
val StyleModifiers.enabled get() = CssRule.OfPseudoClass(this, "enabled")

/**
 * Represents a user interface element that is in a disabled state.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:disabled
 */
val StyleModifiers.disabled get() = CssRule.OfPseudoClass(this, "disabled")

/**
 * Represents any element that cannot be changed by the user.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:read-only
 */
val StyleModifiers.readOnly get() = CssRule.OfPseudoClass(this, "read-only")

/**
 * Represents any element that is user-editable.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:read-write
 */
val StyleModifiers.readWrite get() = CssRule.OfPseudoClass(this, "read-write")

/**
 * Matches an input element that is displaying placeholder text.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:placeholder-shown
 */
val StyleModifiers.placeholderShown get() = CssRule.OfPseudoClass(this, "placeholder-shown")

/**
 * Matches one or more UI elements that are the default among a set of elements.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:default
 */
val StyleModifiers.default get() = CssRule.OfPseudoClass(this, "default")

/**
 * Matches an element, such as checkboxes and radio buttons, that are checked or toggled to an `on` state.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:checked
 */
val StyleModifiers.checked get() = CssRule.OfPseudoClass(this, "checked")

/**
 * Matches when elements, such as checkboxes and radio buttons, are toggled on.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:indeterminate
 */
val StyleModifiers.indeterminate get() = CssRule.OfPseudoClass(this, "indeterminate")

/**
 * Matches an element with valid contents. For example, an input element with type 'email' which contains a validly formed email address.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:valid
 */
val StyleModifiers.valid get() = CssRule.OfPseudoClass(this, "valid")

/**
 * Matches an element with invalid contents. For example, an input element with type 'email' with a name entered.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:invalid
 */
val StyleModifiers.invalid get() = CssRule.OfPseudoClass(this, "invalid")

/**
 * Applies to elements with range limitations, for example a slider control, when the selected value is in the allowed range.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:in-range
 */
val StyleModifiers.inRange get() = CssRule.OfPseudoClass(this, "in-range")

/**
 * Applies to elements with range limitations, for example a slider control, when the selected value is outside the
 * allowed range.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:out-of-range
 */
val StyleModifiers.outOfRange get() = CssRule.OfPseudoClass(this, "out-of-range")

/**
 * Matches when a form element is required.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:required
 */
val StyleModifiers.required get() = CssRule.OfPseudoClass(this, "required")

/**
 * Matches when a form element is optional.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:optional
 */
val StyleModifiers.optional get() = CssRule.OfPseudoClass(this, "optional")

/**
 * Represents an element with incorrect input, but only when the user has interacted with it.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:user-invalid
 */
val StyleModifiers.userInvalid get() = CssRule.OfPseudoClass(this, "user-invalid")

//endregion

//region Tree

/**
 * Represents an element that is the root of the document. In HTML this is usually the `<html>` element.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:root
 */
val StyleModifiers.root get() = CssRule.OfPseudoClass(this, "root")

/**
 * Represents an element with no children other than white-space characters.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:empty
 */
val StyleModifiers.empty get() = CssRule.OfPseudoClass(this, "empty")

/**
 * Matches an element that is the first of its siblings.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:first-child
 */
val StyleModifiers.firstChild get() = CssRule.OfPseudoClass(this, "first-child")

/**
 * Matches an element that is the last of its siblings.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:last-child
 */
val StyleModifiers.lastChild get() = CssRule.OfPseudoClass(this, "last-child")

/**
 * Matches an element that has no siblings. For example, a list item with no other list items in that list.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:only-child
 */
val StyleModifiers.onlyChild get() = CssRule.OfPseudoClass(this, "only-child")

/**
 * Matches an element that is the first of its siblings, and also matches a certain type selector.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:first-of-type
 */
val StyleModifiers.firstOfType get() = CssRule.OfPseudoClass(this, "first-of-type")

/**
 * Matches an element that is the last of its siblings, and also matches a certain type selector.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:last-of-type
 */
val StyleModifiers.lastOfType get() = CssRule.OfPseudoClass(this, "last-of-type")

/**
 * Matches an element that has no siblings of the chosen type selector.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:only-of-type
 */
val StyleModifiers.onlyOfType get() = CssRule.OfPseudoClass(this, "only-of-type")

//endregion

//endregion

//region Pseudo elements

/**
 * Styles to apply to a virtual element that is created before the first element in some container.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/::before
 */
val StyleModifiers.before get() = CssRule.OfPseudoElement(this, "before")

/**
 * Styles to apply to a virtual element that is created after the last element in some container.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/::after
 */
val StyleModifiers.after get() = CssRule.OfPseudoElement(this, "after")

/**
 * Styles to apply to the selected part of a document.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/::selection
 */
val StyleModifiers.selection get() = CssRule.OfPseudoElement(this, "selection")

/**
 * Styles to apply to the first letter in a block of text.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/::first-letter
 */
val StyleModifiers.firstLetter get() = CssRule.OfPseudoElement(this, "first-letter")

/**
 * Styles to apply to the first line in a block of text.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/::first-line
 */
val StyleModifiers.firstLine get() = CssRule.OfPseudoElement(this, "first-line")

/**
 * Matches the placeholder text within an input element that is displaying placeholder text.
 *
 * Note that if you override the color of the placeholder, you should consider setting its `opacity` to `1`.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/::placeholder
 */
val StyleModifiers.placeholder get() = CssRule.OfPseudoElement(this, "placeholder")

//endregion

// region Media queries

/**
 * Used to indicate styles which should only be applied when the page is being printed.
 */
val StyleModifiers.mediaPrint get() = CssRule.OfMedia(this, CSSMediaQuery.MediaType(CSSMediaQuery.MediaType.Enum.Print))

//endregion

// region Functional pseudo classes

fun StyleModifiers.not(vararg params: CssRule.NonMediaCssRule) = CssRule.OfFunctionalPseudoClass(this, "not", *params)

//endregion
