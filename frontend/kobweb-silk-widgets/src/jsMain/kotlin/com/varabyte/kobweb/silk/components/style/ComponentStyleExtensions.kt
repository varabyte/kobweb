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
var ComponentModifiers.anyLink: Modifier?
    get() = pseudoClasses["any-link"]
    set(value) = setPseudoClassModifier("any-link", value)

/**
 * Styles to apply to components that represent navigation links which have not yet been visited.
 *
 * Be aware that you should use the LVHA order if using link, visited, hover, and/or active pseudo classes.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:link
 */
var ComponentModifiers.link: Modifier?
    get() = pseudoClasses["link"]
    set(value) = setPseudoClassModifier("link", value)

/**
 * Styles to apply to elements that are targets of links in the same document
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:target
 */
var ComponentModifiers.target: Modifier?
    get() = pseudoClasses["target"]
    set(value) = setPseudoClassModifier("target", value)

/**
 * Styles to apply to components that represent navigation links which have previously been visited.
 *
 * Be aware that you should use the LVHA order if using link, visited, hover, and/or active pseudo classes.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:visited
 */
var ComponentModifiers.visited: Modifier?
    get() = pseudoClasses["visited"]
    set(value) = setPseudoClassModifier("visited", value)


//endregion

//region User action

/**
 * Styles to apply to components when a cursor is pointing at them.
 *
 * Be aware that you should use the LVHA order if using link, visited, hover, and/or active pseudo classes.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:hover
 */
var ComponentModifiers.hover: Modifier?
    get() = pseudoClasses["hover"]
    set(value) = setPseudoClassModifier("hover", value)

/**
 * Styles to apply to components when a cursor is interacting with them.
 *
 * Be aware that you should use the LVHA order if using link, visited, hover, and/or active pseudo classes.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:active
 */
var ComponentModifiers.active: Modifier?
    get() = pseudoClasses["active"]
    set(value) = setPseudoClassModifier("active", value)

/**
 * Styles to apply to components when they have focus.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:focus
 */
var ComponentModifiers.focus: Modifier?
    get() = pseudoClasses["focus"]
    set(value) = setPseudoClassModifier("focus", value)

//endregion

//region Input

/**
 * Matches when an <input> has been autofilled by the browser.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:autofill
 */
var ComponentModifiers.autofill: Modifier?
    get() = pseudoClasses["autofill"]
    set(value) = setPseudoClassModifier("autofill", value)

/**
 * Represents a user interface element that is in an enabled state.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:enabled
 */
var ComponentModifiers.enabled: Modifier?
    get() = pseudoClasses["enabled"]
    set(value) = setPseudoClassModifier("enabled", value)

/**
 * Represents a user interface element that is in a disabled state.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:disabled
 */
var ComponentModifiers.disabled: Modifier?
    get() = pseudoClasses["disabled"]
    set(value) = setPseudoClassModifier("disabled", value)

/**
 * Represents any element that cannot be changed by the user.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:read-only
 */
var ComponentModifiers.readOnly: Modifier?
    get() = pseudoClasses["read-only"]
    set(value) = setPseudoClassModifier("read-only", value)

/**
 * Represents any element that is user-editable.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:read-write
 */
var ComponentModifiers.readWrite: Modifier?
    get() = pseudoClasses["read-write"]
    set(value) = setPseudoClassModifier("read-write", value)

/**
 * Matches an input element that is displaying placeholder text, for example from the HTML5 placeholder attribute.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:placeholder-shown
 */
var ComponentModifiers.placeholderShown: Modifier?
    get() = pseudoClasses["placeholder-shown"]
    set(value) = setPseudoClassModifier("placeholder-shown", value)

/**
 * Matches one or more UI elements that are the default among a set of elements.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:default
 */
var ComponentModifiers.default: Modifier?
    get() = pseudoClasses["default"]
    set(value) = setPseudoClassModifier("default", value)

/**
 *
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:checked
 */
var ComponentModifiers.checked: Modifier?
    get() = pseudoClasses["checked"]
    set(value) = setPseudoClassModifier("checked", value)

/**
 * Matches when elements such as checkboxes and radiobuttons are toggled on.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:indeterminate
 */
var ComponentModifiers.indeterminate: Modifier?
    get() = pseudoClasses["indeterminate"]
    set(value) = setPseudoClassModifier("indeterminate", value)

/**
 * Matches an element with valid contents. For example an input element with type 'email' which contains a validly formed email address.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:valid
 */
var ComponentModifiers.valid: Modifier?
    get() = pseudoClasses["valid"]
    set(value) = setPseudoClassModifier("valid", value)

/**
 * Matches an element with invalid contents. For example an input element with type 'email' with a name entered.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:invalid
 */
var ComponentModifiers.invalid: Modifier?
    get() = pseudoClasses["invalid"]
    set(value) = setPseudoClassModifier("invalid", value)

/**
 * Applies to elements with range limitations, for example a slider control, when the selected value is in the allowed range.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:in-range
 */
var ComponentModifiers.inRange: Modifier?
    get() = pseudoClasses["in-range"]
    set(value) = setPseudoClassModifier("in-range", value)

/**
 * Applies to elements with range limitations, for example a slider control, when the selected value is outside the
 * allowed range.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:out-of-range
 */
var ComponentModifiers.outOfRange: Modifier?
    get() = pseudoClasses["out-of-range"]
    set(value) = setPseudoClassModifier("out-of-range", value)

/**
 * Matches when a form element is required.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:required
 */
var ComponentModifiers.required: Modifier?
    get() = pseudoClasses["required"]
    set(value) = setPseudoClassModifier("required", value)

/**
 * Matches when a form element is optional.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:optional
 */
var ComponentModifiers.optional: Modifier?
    get() = pseudoClasses["optional"]
    set(value) = setPseudoClassModifier("optional", value)

/**
 * Represents an element with incorrect input, but only when the user has interacted with it.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:user-invalid
 */
var ComponentModifiers.userInvalid: Modifier?
    get() = pseudoClasses["user-invalid"]
    set(value) = setPseudoClassModifier("user-invalid", value)

//endregion

//region Tree

/**
 * Represents an element that is the root of the document. In HTML this is usually the <html> element.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:root
 */
var ComponentModifiers.root: Modifier?
    get() = pseudoClasses["root"]
    set(value) = setPseudoClassModifier("root", value)

/**
 * Represents an element with no children other than white-space characters.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:empty
 */
var ComponentModifiers.empty: Modifier?
    get() = pseudoClasses["empty"]
    set(value) = setPseudoClassModifier("empty", value)

/**
 * Matches an element that is the first of its siblings.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:first-child
 */
var ComponentModifiers.firstChild: Modifier?
    get() = pseudoClasses["first-child"]
    set(value) = setPseudoClassModifier("first-child", value)

/**
 * Matches an element that is the last of its siblings.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:last-child
 */
var ComponentModifiers.lastChild: Modifier?
    get() = pseudoClasses["last-child"]
    set(value) = setPseudoClassModifier("last-child", value)

/**
 * Matches an element that has no siblings. For example a list item with no other list items in that list.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:only-child
 */
var ComponentModifiers.onlyChild: Modifier?
    get() = pseudoClasses["only-child"]
    set(value) = setPseudoClassModifier("only-child", value)

/**
 * Matches an element that is the first of its siblings, and also matches a certain type selector.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:first-of-type
 */
var ComponentModifiers.firstOfType: Modifier?
    get() = pseudoClasses["first-of-type"]
    set(value) = setPseudoClassModifier("first-of-type", value)

/**
 * Matches an element that is the last of its siblings, and also matches a certain type selector.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:last-of-type
 */
var ComponentModifiers.lastOfType: Modifier?
    get() = pseudoClasses["last-of-type"]
    set(value) = setPseudoClassModifier("last-of-type", value)

/**
 * Matches an element that has no siblings of the chosen type selector.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:only-of-type
 */
var ComponentModifiers.onlyOfType: Modifier?
    get() = pseudoClasses["only-of-type"]
    set(value) = setPseudoClassModifier("only-of-type", value)

//endregion

//endregion

//region Pseudo elements

/**
 * Styles to apply to a virtual element that is created before the first element in some container.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/::before
 */
var ComponentModifiers.before: Modifier?
    get() = pseudoElements["before"]
    set(value) = setPseudoElementModifier("before", value)

/**
 * Styles to apply to a virtual element that is created after the last element in some container.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/::after
 */
var ComponentModifiers.after: Modifier?
    get() = pseudoElements["after"]
    set(value) = setPseudoElementModifier("after", value)

/**
 * Styles to apply to the selected part of a document
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/::selection
 */
var ComponentModifiers.selection: Modifier?
    get() = pseudoElements["selection"]
    set(value) = setPseudoElementModifier("selection", value)

/**
 * Styles to apply to the first letter in a block of text
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/::first-letter
 */
var ComponentModifiers.firstLetter: Modifier?
    get() = pseudoElements["first-letter"]
    set(value) = setPseudoElementModifier("first-letter", value)

/**
 * Styles to apply to the first line in a block of text
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/::first-line
 */
var ComponentModifiers.firstLine: Modifier?
    get() = pseudoElements["first-line"]
    set(value) = setPseudoElementModifier("first-line", value)

//endregion

//region Breakpoints

/** Convenience property for adding a small [Breakpoint] */
var ComponentModifiers.sm: Modifier?
    get() = breakpoints[Breakpoint.SM]
    set(value) = setBreakpointModifier(Breakpoint.SM, value)

/** Convenience property for adding a medium [Breakpoint] */
var ComponentModifiers.md: Modifier?
    get() = breakpoints[Breakpoint.MD]
    set(value) = setBreakpointModifier(Breakpoint.MD, value)

/** Convenience property for adding a large [Breakpoint] */
var ComponentModifiers.lg: Modifier?
    get() = breakpoints[Breakpoint.LG]
    set(value) = setBreakpointModifier(Breakpoint.LG, value)

/** Convenience property for adding an extra-large [Breakpoint] */
var ComponentModifiers.xl: Modifier?
    get() = breakpoints[Breakpoint.XL]
    set(value) = setBreakpointModifier(Breakpoint.XL, value)

/** Convenience property for adding an extra-extra-large [Breakpoint] */
var ComponentModifiers.xxl: Modifier?
    get() = breakpoints[Breakpoint.XXL]
    set(value) = setBreakpointModifier(Breakpoint.XXL, value)

//endregion