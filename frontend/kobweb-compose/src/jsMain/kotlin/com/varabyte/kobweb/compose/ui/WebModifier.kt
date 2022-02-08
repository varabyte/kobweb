package com.varabyte.kobweb.compose.ui

import org.jetbrains.compose.web.attributes.AttrsBuilder
import org.jetbrains.compose.web.css.StyleBuilder
import org.w3c.dom.Element

// Just a marker interface to express intention
interface WebModifier : Modifier.Element

/**
 * A modifier element which works by setting CSS styles and/or attributes when it is applied.
 */
class AttrsModifier(internal val attrs: (AttrsBuilder<*>.() -> Unit)) : WebModifier

fun Modifier.attrsModifier(attrs: (AttrsBuilder<*>.() -> Unit)) = this then AttrsModifier(attrs)

/**
 * A modifier element that works by CSS styles when it is applied.
 */
class StyleModifier(internal val styles: (StyleBuilder.() -> Unit)) : WebModifier

fun Modifier.styleModifier(styles: (StyleBuilder.() -> Unit)) = this then StyleModifier(styles)

/**
 * Convert a [Modifier] into an [AttrsBuilder] which Compose for Web tags take as an argument, e.g. use it like so:
 *
 * ```
 * Div(attrs = modifier.asAttributesBuilder())
 * ```
 *
 * @param finalHandler A handler which, if supplied, gets called at the very end before returning the builder. This can
 *   be useful to occasionally avoid the creation of an unnecessary [AttrsModifier] to append at the tail.
 */
fun <T: Element, A: AttrsBuilder<T>> Modifier.asAttributesBuilder(finalHandler: (A.() -> Unit)? = null): A.() -> Unit {
    val firstModifier = this
    return {
        firstModifier.fold(Unit) { _, element ->
            if (element is AttrsModifier) {
                element.attrs.invoke(this)
            } else if (element is StyleModifier) {
                style {
                    element.styles.invoke(this)
                }
            }
        }

        finalHandler?.invoke(this)
    }
}

/**
 * Convert a [Modifier] into a [StyleBuilder] which can be used to initialize a StyleSheet, for example.
 *
 * @param finalHandler A handler which, if supplied, gets called at the very end before returning the builder. This can
 *   be useful to occasionally avoid the creation of an unnecessary [StyleModifier] to append at the tail.
 */
fun Modifier.asStyleBuilder(finalHandler: (StyleBuilder.() -> Unit)? = null): StyleBuilder.() -> Unit {
    val firstModifier = this
    return {
        firstModifier.fold(Unit) { _, element ->
            if (element is StyleModifier) {
                element.styles.invoke(this)
            }
        }

        finalHandler?.invoke(this)
    }
}