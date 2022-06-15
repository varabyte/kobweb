package com.varabyte.kobweb.compose.ui

import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.css.*
import org.w3c.dom.Element

// Just a marker interface to express intention
interface WebModifier : Modifier.Element

/**
 * A modifier element which works by setting CSS styles and/or attributes when it is applied.
 */
class AttrsModifier(internal val attrs: (AttrsScope<*>.() -> Unit)) : WebModifier

fun Modifier.attrsModifier(attrs: (AttrsScope<*>.() -> Unit)) = this then AttrsModifier(attrs)

/**
 * A modifier element that works by CSS styles when it is applied.
 */
class StyleModifier(internal val styles: (StyleScope.() -> Unit)) : WebModifier

fun Modifier.styleModifier(styles: (StyleScope.() -> Unit)) = this then StyleModifier(styles)

/**
 * Convert a [Modifier] into an [AttrsScope] which Compose for Web tags take as an argument, e.g. use it like so:
 *
 * ```
 * Div(attrs = modifier.asAttributesBuilder())
 * ```
 *
 * @param finalHandler A handler which, if supplied, gets called at the very end before returning the builder. This can
 *   be useful to occasionally avoid the creation of an unnecessary [AttrsModifier] to append at the tail.
 */
fun <T: Element, A: AttrsScope<T>> Modifier.asAttributesBuilder(finalHandler: (A.() -> Unit)? = null): A.() -> Unit {
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
fun Modifier.asStyleBuilder(finalHandler: (StyleScope.() -> Unit)? = null): StyleScope.() -> Unit {
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