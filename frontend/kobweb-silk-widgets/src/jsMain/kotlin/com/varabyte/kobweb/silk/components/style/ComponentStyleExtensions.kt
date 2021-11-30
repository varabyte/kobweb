package com.varabyte.kobweb.silk.components.style

import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.silk.components.style.breakpoint.Breakpoint

//region Pseudo classes

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
 * Styles to apply to components that represent navigation links which have previously been visited.
 *
 * Be aware that you should use the LVHA order if using link, visited, hover, and/or active pseudo classes.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/:visited
 */
var ComponentModifiers.visited: Modifier?
    get() = pseudoClasses["visited"]
    set(value) = setPseudoClassModifier("visited", value)


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