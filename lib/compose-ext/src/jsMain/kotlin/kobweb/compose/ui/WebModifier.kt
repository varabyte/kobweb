package kobweb.compose.ui

import org.jetbrains.compose.web.attributes.AttrsBuilder
import org.jetbrains.compose.web.css.StyleBuilder

/**
 * A modifier element which works by setting CSS styles and/or attributes when it is applied.
 */
class WebModifier : Modifier.Element {
    internal val styleBuilders = mutableListOf<StyleBuilder.() -> Unit>()
    internal val attrBuilders = mutableListOf<AttrsBuilder<*>.() -> Unit>()

    fun style(builder: StyleBuilder.() -> Unit) {
        styleBuilders.add(builder)
    }

    fun attr(builder: AttrsBuilder<*>.() -> Unit) {
        attrBuilders.add(builder)
    }
}

fun webModifier(init: WebModifier.() -> Unit) = WebModifier().apply(init)
fun Modifier.webModifier(init: WebModifier.() -> Unit) = this then WebModifier().apply(init)

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
                element.attrBuilders.forEach { it.invoke(this) }
                style {
                    element.styleBuilders.forEach { it.invoke(this) }
                }
            }
        }

        finalHandler?.invoke(this)
    }
}
