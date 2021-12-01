package com.varabyte.kobweb.silk.components.style

import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.silk.components.style.breakpoint.Breakpoint

//region Pseudo classes

//region Location

/**
 * Styles to apply to components that would be matched by both "link" or "visited"
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:any-link
 */
fun ComponentModifiers.anyLink(createModifier: () -> Modifier) {
    pseudoClass("any-link", createModifier)
}

/**
 * Styles to apply to components that represent navigation links which have not yet been visited.
 *
 * Be aware that you should use the LVHA order if using link, visited, hover, and/or active pseudo classes.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:link
 */
fun ComponentModifiers.link(createModifier: () -> Modifier) {
    pseudoClass("link", createModifier)
}

/**
 * Styles to apply to elements that are targets of links in the same document
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:target
 */
fun ComponentModifiers.target(createModifier: () -> Modifier) {
    pseudoClass("target", createModifier)
}

/**
 * Styles to apply to components that represent navigation links which have previously been visited.
 *
 * Be aware that you should use the LVHA order if using link, visited, hover, and/or active pseudo classes.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:visited
 */
fun ComponentModifiers.visited(createModifier: () -> Modifier) {
    pseudoClass("visited", createModifier)
}

//endregion

//region User action

/**
 * Styles to apply to components when a cursor is pointing at them.
 *
 * Be aware that you should use the LVHA order if using link, visited, hover, and/or active pseudo classes.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:hover
 */
fun ComponentModifiers.hover(createModifier: () -> Modifier) {
    pseudoClass("hover", createModifier)
}

/**
 * Styles to apply to components when a cursor is interacting with them.
 *
 * Be aware that you should use the LVHA order if using link, visited, hover, and/or active pseudo classes.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:active
 */
fun ComponentModifiers.active(createModifier: () -> Modifier) {
    pseudoClass("active", createModifier)
}


/**
 * Styles to apply to components when they have focus.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:focus
 */
fun ComponentModifiers.focus(createModifier: () -> Modifier) {
    pseudoClass("focus", createModifier)
}

//endregion

//region Input

/**
 * Matches when an <input> has been autofilled by the browser.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:autofill
 */
fun ComponentModifiers.autofill(createModifier: () -> Modifier) {
    pseudoClass("autofill", createModifier)
}

/**
 * Represents a user interface element that is in an enabled state.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:enabled
 */
fun ComponentModifiers.enabled(createModifier: () -> Modifier) {
    pseudoClass("enabled", createModifier)
}

/**
 * Represents a user interface element that is in a disabled state.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:disabled
 */
fun ComponentModifiers.disabled(createModifier: () -> Modifier) {
    pseudoClass("disabled", createModifier)
}

/**
 * Represents any element that cannot be changed by the user.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:read-only
 */
fun ComponentModifiers.readOnly(createModifier: () -> Modifier) {
    pseudoClass("read-only", createModifier)
}

/**
 * Represents any element that is user-editable.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:read-write
 */
fun ComponentModifiers.readWrite(createModifier: () -> Modifier) {
    pseudoClass("read-write", createModifier)
}

/**
 * Matches an input element that is displaying placeholder text, for example from the HTML5 placeholder attribute.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:placeholder-shown
 */
fun ComponentModifiers.placeholderShown(createModifier: () -> Modifier) {
    pseudoClass("placeholder-shown", createModifier)
}

/**
 * Matches one or more UI elements that are the default among a set of elements.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:default
 */
fun ComponentModifiers.default(createModifier: () -> Modifier) {
    pseudoClass("default", createModifier)
}

/**
 *
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:checked
 */
fun ComponentModifiers.checked(createModifier: () -> Modifier) {
    pseudoClass("checked", createModifier)
}

/**
 * Matches when elements such as checkboxes and radiobuttons are toggled on.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:indeterminate
 */
fun ComponentModifiers.indeterminate(createModifier: () -> Modifier) {
    pseudoClass("indeterminate", createModifier)
}

/**
 * Matches an element with valid contents. For example an input element with type 'email' which contains a validly formed email address.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:valid
 */
fun ComponentModifiers.valid(createModifier: () -> Modifier) {
    pseudoClass("valid", createModifier)
}

/**
 * Matches an element with invalid contents. For example an input element with type 'email' with a name entered.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:invalid
 */
fun ComponentModifiers.invalid(createModifier: () -> Modifier) {
    pseudoClass("invalid", createModifier)
}

/**
 * Applies to elements with range limitations, for example a slider control, when the selected value is in the allowed range.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:in-range
 */
fun ComponentModifiers.inRange(createModifier: () -> Modifier) {
    pseudoClass("in-range", createModifier)
}

/**
 * Applies to elements with range limitations, for example a slider control, when the selected value is outside the
 * allowed range.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:out-of-range
 */
fun ComponentModifiers.outOfRange(createModifier: () -> Modifier) {
    pseudoClass("out-of-range", createModifier)
}

/**
 * Matches when a form element is required.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:required
 */
fun ComponentModifiers.required(createModifier: () -> Modifier) {
    pseudoClass("required", createModifier)
}

/**
 * Matches when a form element is optional.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:optional
 */
fun ComponentModifiers.optional(createModifier: () -> Modifier) {
    pseudoClass("optional", createModifier)
}

/**
 * Represents an element with incorrect input, but only when the user has interacted with it.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:user-invalid
 */
fun ComponentModifiers.userInvalid(createModifier: () -> Modifier) {
    pseudoClass("user-invalid", createModifier)
}

//endregion

//region Tree

/**
 * Represents an element that is the root of the document. In HTML this is usually the <html> element.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:root
 */
fun ComponentModifiers.root(createModifier: () -> Modifier) {
    pseudoClass("root", createModifier)
}

/**
 * Represents an element with no children other than white-space characters.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:empty
 */
fun ComponentModifiers.empty(createModifier: () -> Modifier) {
    pseudoClass("empty", createModifier)
}

/**
 * Matches an element that is the first of its siblings.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:first-child
 */
fun ComponentModifiers.firstChild(createModifier: () -> Modifier) {
    pseudoClass("first-child", createModifier)
}

/**
 * Matches an element that is the last of its siblings.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:last-child
 */
fun ComponentModifiers.lastChild(createModifier: () -> Modifier) {
    pseudoClass("last-child", createModifier)
}

/**
 * Matches an element that has no siblings. For example a list item with no other list items in that list.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:only-child
 */
fun ComponentModifiers.onlyChild(createModifier: () -> Modifier) {
    pseudoClass("only-child", createModifier)
}

/**
 * Matches an element that is the first of its siblings, and also matches a certain type selector.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:first-of-type
 */
fun ComponentModifiers.firstOfType(createModifier: () -> Modifier) {
    pseudoClass("first-of-type", createModifier)
}

/**
 * Matches an element that is the last of its siblings, and also matches a certain type selector.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:last-of-type
 */
fun ComponentModifiers.lastOfType(createModifier: () -> Modifier) {
    pseudoClass("last-of-type", createModifier)
}

/**
 * Matches an element that has no siblings of the chosen type selector.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:only-of-type
 */
fun ComponentModifiers.onlyOfType(createModifier: () -> Modifier) {
    pseudoClass("only-of-type", createModifier)
}

//endregion

//endregion

//region Pseudo elements

/**
 * Styles to apply to a virtual element that is created before the first element in some container.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/::before
 */
fun ComponentModifiers.before(createModifier: () -> Modifier) {
    pseudoElement("before", createModifier)
}

/**
 * Styles to apply to a virtual element that is created after the last element in some container.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/::after
 */
fun ComponentModifiers.after(createModifier: () -> Modifier) {
    pseudoElement("after", createModifier)
}

/**
 * Styles to apply to the selected part of a document
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/::selection
 */
fun ComponentModifiers.selection(createModifier: () -> Modifier) {
    pseudoElement("selection", createModifier)
}

/**
 * Styles to apply to the first letter in a block of text
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/::first-letter
 */
fun ComponentModifiers.firstLetter(createModifier: () -> Modifier) {
    pseudoElement("first-letter", createModifier)
}

/**
 * Styles to apply to the first line in a block of text
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/::first-line
 */
fun ComponentModifiers.firstLine(createModifier: () -> Modifier) {
    pseudoElement("first-line", createModifier)
}

//endregion