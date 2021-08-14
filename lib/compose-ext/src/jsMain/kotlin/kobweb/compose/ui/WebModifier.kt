package kobweb.compose.ui

import org.jetbrains.compose.web.attributes.AttrsBuilder

/**
 * A modifier element which works by setting CSS styles and/or attributes when it is applied.
 */
class WebModifier(internal val attrs: (AttrsBuilder<*>.() -> Unit)? = null) : Modifier.Element
fun Modifier.webModifier(attrs: (AttrsBuilder<*>.() -> Unit)? = null) = this then WebModifier(attrs)

/**
 * Convert a [Modifier] into something consumable by Web Compose's normal css API, for example:
 *
 * ```
 * Div(attrs = modifier.asAttributeBuilder())
 * ```
 *
 * @param finalHandler A handler which, if supplied, gets called at the very end before returning the
 *   builder. This can be useful to allow avoiding the creation of an unnecessary [WebModifier] to append at the
 *   tail, just for applying some final local CSS styles within a composable.
 */
fun Modifier.asAttributeBuilder(finalHandler: (AttrsBuilder<*>.() -> Unit)? = null): AttrsBuilder<*>.() -> Unit {
    val firstModifier = this
    return {
        firstModifier.fold(Unit) { _, element ->
            if (element is WebModifier) {
                element.attrs?.invoke(this)
            }
        }

        finalHandler?.invoke(this)
    }
}
