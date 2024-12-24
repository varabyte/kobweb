package com.varabyte.kobweb.compose.ui

import com.varabyte.kobweb.compose.attributes.ComparableAttrsScope
import com.varabyte.kobweb.compose.css.*
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.css.*
import org.w3c.dom.Element

// Just a marker interface to express intention
interface WebModifier : Modifier.Element

/**
 * A modifier element which works by setting CSS styles and/or attributes when it is applied.
 */
class AttrsModifier(internal val attrs: (AttrsScope<*>.() -> Unit)) : WebModifier {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AttrsModifier) return false

        val attrsResolved = ComparableAttrsScope<Element>()
        attrs.invoke(attrsResolved)

        val otherAttrsResolved = ComparableAttrsScope<Element>()
        other.attrs.invoke(otherAttrsResolved)

        return attrsResolved == otherAttrsResolved
    }

    override fun hashCode(): Int {
        val attrsResolved = ComparableAttrsScope<Element>()
        attrs.invoke(attrsResolved)
        return attrsResolved.hashCode()
    }
}

fun Modifier.attrsModifier(attrs: (AttrsScope<*>.() -> Unit)) = this then AttrsModifier(attrs)

/**
 * A modifier element that works by CSS styles when it is applied.
 */
class StyleModifier(internal val styles: (StyleScope.() -> Unit)) : WebModifier {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is StyleModifier) return false

        val stylesResolved = ComparableStyleScope()
        styles.invoke(stylesResolved)

        val otherStylesResolved = ComparableStyleScope()
        other.styles.invoke(otherStylesResolved)

        return stylesResolved == otherStylesResolved
    }

    override fun hashCode(): Int {
        val stylesResolved = ComparableStyleScope()
        styles.invoke(stylesResolved)
        return stylesResolved.hashCode()
    }
}

fun Modifier.styleModifier(styles: (StyleScope.() -> Unit)) = this then StyleModifier(styles)

/**
 * Convert a [Modifier] into an [AttrsScope] which Compose HTML tags take as an argument, e.g. use it like so:
 *
 * ```
 * Div(attrs = modifier.toAttrs())
 * ```
 *
 * @param finalHandler A handler which, if supplied, gets called at the very end before returning the builder. This can
 *   be useful to occasionally avoid the creation of an unnecessary [AttrsModifier] to append at the tail.
 */
fun <A : AttrsScope<Element>> Modifier.toAttrs(finalHandler: (A.() -> Unit)? = null): A.() -> Unit {
    val firstModifier = this
    return {
        firstModifier.fold(Unit) { _, modifierElement ->
            if (modifierElement is AttrsModifier) {
                modifierElement.attrs.invoke(this)
            } else if (modifierElement is StyleModifier) {
                style {
                    modifierElement.styles.invoke(this)
                }
            }
        }

        finalHandler?.invoke(this)
    }
}

/**
 * Convert a [Modifier] into a [StyleScope] which can be used to initialize a StyleSheet, for example.
 *
 * @param finalHandler A handler which, if supplied, gets called at the very end before returning the builder. This can
 *   be useful to occasionally avoid the creation of an unnecessary [StyleModifier] to append at the tail.
 */
fun Modifier.toStyles(finalHandler: (StyleScope.() -> Unit)? = null): StyleScope.() -> Unit {
    val firstModifier = this
    return {
        firstModifier.fold(Unit) { _, modifierElement ->
            if (modifierElement is StyleModifier) {
                modifierElement.styles.invoke(this)
            }
        }

        finalHandler?.invoke(this)
    }
}
