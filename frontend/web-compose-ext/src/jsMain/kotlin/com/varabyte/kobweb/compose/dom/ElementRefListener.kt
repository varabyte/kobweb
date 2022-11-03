package com.varabyte.kobweb.compose.dom

import androidx.compose.runtime.*
import org.jetbrains.compose.web.attributes.AttrsScope
import org.w3c.dom.Element

/**
 * Simple class for registering logic that will be triggered with a raw element when it is added to the DOM.
 *
 * Users do not instantiate this directly. Instead, use the [refListener] and [disposableRefListener] methods.
 */
class ElementRefListener<TElement : Element> internal constructor(
    val effect: DisposableEffectScope.(TElement) -> DisposableEffectResult,
)

/**
 * Create a simple ref listener where you don't care about the dispose case.
 *
 * For example:
 *
 * ```
 * refListener { element ->
 *   println("${element.localName} was added to the dom.")
 * }
 * ```
 *
 * See also: [disposableRefListener], [AttrsScope.registerRefListener]
 */
fun <TElement : Element> refListener(withRef: (TElement) -> Unit) = ElementRefListener<TElement> {
    withRef(it)
    onDispose {}
}

/**
 * Create a ref listener where you also handle disposal.
 *
 * To do this, your registered block *must* end with an inner `onDispose { ... }` block. For example:
 *
 * ```
 * disposableRefListener { element ->
 *   println("${element.localName} was added to the dom.")
 *   onDispose {
 *     println("${element.localName} was removed from the dom.")
 *   }
 * }
 * ```
 *
 * See also: [refListener], [AttrsScope.registerRefListener]
 */
fun <TElement : Element> disposableRefListener(effect: DisposableEffectScope.(TElement) -> DisposableEffectResult) = ElementRefListener(effect)

/**
 * An alternate replacement for using the [AttrsScope.ref] method directly.
 *
 * In other words, instead of this:
 *
 * ```
 * attrs = {
 *   ref { ... } // This version requires adding an onDispose block.
 * }
 * ```
 *
 * you can do this:
 *
 * ```
 * attrs = {
 *   registerRefListener(refListener { ... }) // This version does not need an onDispose block.
 * ```
 *
 * By working with [ElementRefListener] instead of a callback type directly, it may give your API a bit more flexibility
 * for users who may or may not care about handling dispose:
 *
 * ```
 * @Composable
 * fun MyWidget(
 *    /* ... */
 *    refListener: ElementRefListener<Element>? = null,
 * ) {
 *   Div(attrs = {
 *     registerRefListener(refListener)
 *   })
 * }
 *
 * // Called either way:
 * MyWidget(refListener = refListener { ... }) // Option #1
 * MyWidget(refListener = disposableRefListener { ...; onDispose { ... } }) // Option #2
 * ```
 */
fun <TElement : Element> AttrsScope<TElement>.registerRefListener(listener: ElementRefListener<TElement>?) {
    if (listener != null) {
        ref(listener.effect)
    }
}
