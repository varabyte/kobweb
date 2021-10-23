package com.varabyte.kobweb.compose.ui

import org.jetbrains.compose.web.attributes.AttrsBuilder
import org.jetbrains.compose.web.css.StyleBuilder

// Just a marker interface to express intention
interface WebModifier : Modifier.Element

/**
 * A modifier element which works by setting CSS styles and/or attributes when it is applied.
 */
class AttrModifier(internal val attrs: (AttrsBuilder<*>.() -> Unit)) : WebModifier
fun Modifier.attrModifier(attrs: (AttrsBuilder<*>.() -> Unit)) = this then AttrModifier(attrs)

/**
 * A modifier element that works by CSS styles when it is applied.
 */
class StyleModifier(internal val styles: (StyleBuilder.() -> Unit)) : WebModifier
fun Modifier.styleModifier(styles: (StyleBuilder.() -> Unit)) = this then StyleModifier(styles)

/**
 * Convert a [Modifier] into something consumable by Web Compose's normal css API, for example:
 *
 * ```
 * Div(attrs = modifier.asAttributeBuilder())
 * ```
 *
 * @param finalHandler A handler which, if supplied, gets called at the very end before returning the
 *   builder. This can be useful to allow avoiding the creation of an unnecessary [AttrModifier] to append at the
 *   tail, just for applying some final local CSS styles within a composable.
 */
fun Modifier.asAttributeBuilder(finalHandler: (AttrsBuilder<*>.() -> Unit)? = null): AttrsBuilder<*>.() -> Unit {
    val firstModifier = this
    return {
        firstModifier.fold(Unit) { _, element ->
            if (element is AttrModifier) {
                element.attrs.invoke(this)
            }
            else if (element is StyleModifier) {
                style {
                    element.styles.invoke(this)
                }
            }
        }

        finalHandler?.invoke(this)
    }
}
