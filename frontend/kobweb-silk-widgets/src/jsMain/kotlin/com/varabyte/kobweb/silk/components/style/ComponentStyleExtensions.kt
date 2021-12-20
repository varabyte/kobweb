package com.varabyte.kobweb.silk.components.style

import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.silk.components.style.breakpoint.Breakpoint
import org.jetbrains.compose.web.css.CSSMediaQuery

//region Pseudo classes

//region Location

/**
 * Styles to apply to components that would be matched by both "link" or "visited"
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:any-link
 */
val ComponentModifiers.anyLink get() = CssRule.OfPseudoClass(this, "any-link")

/**
 * Styles to apply to components that represent navigation links which have not yet been visited.
 *
 * Be aware that you should use the LVHA order if using link, visited, hover, and/or active pseudo classes.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:link
 */
val ComponentModifiers.link get() = CssRule.OfPseudoClass(this, "link")

/**
 * Styles to apply to elements that are targets of links in the same document
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:target
 */
val ComponentModifiers.target get() = CssRule.OfPseudoClass(this, "target")

/**
 * Styles to apply to components that represent navigation links which have previously been visited.
 *
 * Be aware that you should use the LVHA order if using link, visited, hover, and/or active pseudo classes.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:visited
 */
val ComponentModifiers.visited get() = CssRule.OfPseudoClass(this, "visited")

//endregion

//region User action

/**
 * Styles to apply to components when a cursor is pointing at them.
 *
 * Be aware that you should use the LVHA order if using link, visited, hover, and/or active pseudo classes.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:hover
 */
val ComponentModifiers.hover get() = CssRule.OfPseudoClass(this, "hover")

/**
 * Styles to apply to components when a cursor is interacting with them.
 *
 * Be aware that you should use the LVHA order if using link, visited, hover, and/or active pseudo classes.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:active
 */
val ComponentModifiers.active get() = CssRule.OfPseudoClass(this, "active")


/**
 * Styles to apply to components when they have focus.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:focus
 */
val ComponentModifiers.focus get() = CssRule.OfPseudoClass(this, "focus")

//endregion

//region Input

/**
 * Matches when an <input> has been autofilled by the browser.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:autofill
 */
val ComponentModifiers.autofill get() = CssRule.OfPseudoClass(this, "autofill")

/**
 * Represents a user interface element that is in an enabled state.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:enabled
 */
val ComponentModifiers.enabled get() = CssRule.OfPseudoClass(this, "enabled")

/**
 * Represents a user interface element that is in a disabled state.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:disabled
 */
val ComponentModifiers.disabled get() = CssRule.OfPseudoClass(this, "disabled")

/**
 * Represents any element that cannot be changed by the user.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:read-only
 */
val ComponentModifiers.readOnly get() = CssRule.OfPseudoClass(this, "read-only")

/**
 * Represents any element that is user-editable.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:read-write
 */
val ComponentModifiers.readWrite get() = CssRule.OfPseudoClass(this, "read-write")

/**
 * Matches an input element that is displaying placeholder text, for example from the HTML5 placeholder attribute.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:placeholder-shown
 */
val ComponentModifiers.placeholderShown get() = CssRule.OfPseudoClass(this, "placeholder-shown")

/**
 * Matches one or more UI elements that are the default among a set of elements.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:default
 */
val ComponentModifiers.default get() = CssRule.OfPseudoClass(this, "default")

/**
 *
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:checked
 */
val ComponentModifiers.checked get() = CssRule.OfPseudoClass(this, "checked")

/**
 * Matches when elements such as checkboxes and radiobuttons are toggled on.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:indeterminate
 */
val ComponentModifiers.indeterminate get() = CssRule.OfPseudoClass(this, "indeterminate")

/**
 * Matches an element with valid contents. For example an input element with type 'email' which contains a validly formed email address.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:valid
 */
val ComponentModifiers.valid get() = CssRule.OfPseudoClass(this, "valid")

/**
 * Matches an element with invalid contents. For example an input element with type 'email' with a name entered.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:invalid
 */
val ComponentModifiers.invalid get() = CssRule.OfPseudoClass(this, "invalid")

/**
 * Applies to elements with range limitations, for example a slider control, when the selected value is in the allowed range.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:in-range
 */
val ComponentModifiers.inRange get() = CssRule.OfPseudoClass(this, "in-range")

/**
 * Applies to elements with range limitations, for example a slider control, when the selected value is outside the
 * allowed range.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:out-of-range
 */
val ComponentModifiers.outOfRange get() = CssRule.OfPseudoClass(this, "out-of-range")

/**
 * Matches when a form element is required.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:required
 */
val ComponentModifiers.required get() = CssRule.OfPseudoClass(this, "required")

/**
 * Matches when a form element is optional.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:optional
 */
val ComponentModifiers.optional get() = CssRule.OfPseudoClass(this, "optional")

/**
 * Represents an element with incorrect input, but only when the user has interacted with it.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:user-invalid
 */
val ComponentModifiers.userInvalid get() = CssRule.OfPseudoClass(this, "user-invalid")

//endregion

//region Tree

/**
 * Represents an element that is the root of the document. In HTML this is usually the <html> element.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:root
 */
val ComponentModifiers.root get() = CssRule.OfPseudoClass(this, "root")

/**
 * Represents an element with no children other than white-space characters.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:empty
 */
val ComponentModifiers.empty get() = CssRule.OfPseudoClass(this, "empty")

/**
 * Matches an element that is the first of its siblings.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:first-child
 */
val ComponentModifiers.firstChild get() = CssRule.OfPseudoClass(this, "first-child")

/**
 * Matches an element that is the last of its siblings.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:last-child
 */
val ComponentModifiers.lastChild get() = CssRule.OfPseudoClass(this, "last-child")

/**
 * Matches an element that has no siblings. For example a list item with no other list items in that list.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:only-child
 */
val ComponentModifiers.onlyChild get() = CssRule.OfPseudoClass(this, "only-child")

/**
 * Matches an element that is the first of its siblings, and also matches a certain type selector.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:first-of-type
 */
val ComponentModifiers.firstOfType get() = CssRule.OfPseudoClass(this, "first-of-type")

/**
 * Matches an element that is the last of its siblings, and also matches a certain type selector.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:last-of-type
 */
val ComponentModifiers.lastOfType get() = CssRule.OfPseudoClass(this, "last-of-type")

/**
 * Matches an element that has no siblings of the chosen type selector.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:only-of-type
 */
val ComponentModifiers.onlyOfType get() = CssRule.OfPseudoClass(this, "only-of-type")

//endregion

//endregion

//region Pseudo elements

/**
 * Styles to apply to a virtual element that is created before the first element in some container.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/::before
 */
val ComponentModifiers.before get() = CssRule.OfPseudoElement(this, "before")

/**
 * Styles to apply to a virtual element that is created after the last element in some container.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/::after
 */
val ComponentModifiers.after get() = CssRule.OfPseudoElement(this, "after")

/**
 * Styles to apply to the selected part of a document
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/::selection
 */
val ComponentModifiers.selection get() = CssRule.OfPseudoElement(this, "selection")

/**
 * Styles to apply to the first letter in a block of text
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/::first-letter
 */
val ComponentModifiers.firstLetter get() = CssRule.OfPseudoElement(this, "first-letter")

/**
 * Styles to apply to the first line in a block of text
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/::first-line
 */
val ComponentModifiers.firstLine get() = CssRule.OfPseudoElement(this, "first-line")

//endregion

// region Media queries

/**
 * Used to indicate styles which should only be applied when the page is being printed.
 */
val ComponentModifiers.mediaPrint get() = CssRule.OfMedia(this, CSSMediaQuery.MediaType(CSSMediaQuery.MediaType.Enum.Print))

//endregion