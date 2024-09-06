package com.varabyte.kobweb.compose.dom

import androidx.compose.runtime.*
import org.jetbrains.compose.web.dom.ElementScope
import org.w3c.dom.Element

/**
 * A callback scope for listeners that inform the user about events associated with an underlying raw DOM element.
 *
 * You do not construct these directly. Instead, see [ref], [disposableRef], and [refScope].
 *
 * @param TElement The element type of this scope is `in`, so that even if you register a refscope against a very
 *   specific element type (e.g. `HTMLDivElement`), you can still register a more generic handler against it
 *   (e.g. an `HTMLElement`)
 */
@ConsistentCopyVisibility
data class ElementRefScope<in TElement : Element> internal constructor(
    internal val keyedCallbacks: List<KeysToEffect<TElement>>
) {
    internal sealed class RefCallback<in TElement : Element> {
        abstract operator fun invoke(scope: DisposableEffectScope, element: TElement): DisposableEffectResult
        data class Simple<TElement : Element>(val handle: (TElement) -> Unit) : RefCallback<TElement>() {
            override fun invoke(scope: DisposableEffectScope, element: TElement): DisposableEffectResult {
                handle(element)
                return scope.onDispose {}
            }
        }

        data class Disposable<TElement : Element>(val effect: DisposableEffectScope.(TElement) -> DisposableEffectResult) :
            RefCallback<TElement>() {
            override fun invoke(scope: DisposableEffectScope, element: TElement): DisposableEffectResult {
                return scope.effect(element)
            }
        }
    }

    internal data class KeysToEffect<in TElement : Element>(
        val keys: List<Any?>,
        val refCallback: RefCallback<TElement>,
    )

    class Builder<TElement : Element> {
        private val keyedCallbacks = mutableListOf<KeysToEffect<TElement>>()

        /**
         * Register a listener that will be triggered with the raw html element whenever it gets added to the DOM.
         */
        fun ref(vararg keys: Any?, handle: (TElement) -> Unit) {
            keyedCallbacks.add(KeysToEffect(keys.toList(), RefCallback.Simple(handle)))
        }

        /**
         * Like [ref], but will get triggered both when the element is added to *and* removed from the DOM.
         *
         * The callback passed in here *must* end with an `onDispose` block, as in:
         *
         * ```
         * {
         *    ...
         *    onDispose { ... }
         *  }
         * ```
         */
        fun disposableRef(vararg keys: Any?, effect: DisposableEffectScope.(TElement) -> DisposableEffectResult) {
            keyedCallbacks.add(KeysToEffect(keys.toList(), RefCallback.Disposable(effect)))
        }

        /**
         * Add any ref callbacks contained in another ref scope.
         *
         * This is useful to have if a widget internally creates its own ref scope for some reason but also wants to
         * accommodate any callbacks passed in by the caller.
         *
         * @param other Another ref scope to include as part of this one. Accepts null for convenience because this is
         *  often a nullable parameter that accepts a value passed in from a user.
         */
        fun add(other: ElementRefScope<TElement>?) {
            if (other != null) keyedCallbacks.addAll(other.keyedCallbacks)
        }

        internal fun build() = ElementRefScope(keyedCallbacks)
    }
}

/**
 * Convenience method for installing an [ElementRefScope] into an [ElementScope].
 *
 * This helps avoid a bunch of annoying boilerplate that the Compose HTML API otherwise requires you to do fairly
 * regularly.
 */
@NonRestartableComposable
@Composable
fun <TElement : Element> ElementScope<TElement>.registerRefScope(scope: ElementRefScope<TElement>?) {
    registerRefScope(scope) { it }
}

/**
 * A more flexible version of [registerRefScope] which allows you to transform the element type of the scope.
 *
 * This can be useful in some cases where you can't get access to the raw element you want directly (e.g. because the
 * JB widget doesn't expose a content block, like TextArea), but you can create a different element and register an
 * [ElementRefScope] against that with a transformation step to get around it.
 */
@NonRestartableComposable
@Composable
fun <TElementSrc : Element, TElementDst : Element> ElementScope<TElementSrc>.registerRefScope(
    scope: ElementRefScope<TElementDst>?,
    transform: (TElementSrc) -> TElementDst
) {
    if (scope == null) return
    scope.keyedCallbacks.forEach { keyedCallback ->
        DisposableEffect(*keyedCallback.keys.toTypedArray()) {
            keyedCallback.refCallback.invoke(this, transform(scopeElement))
        }
    }
}

/**
 * Create a trivial ref listener where you're informed about when a raw element is added to the DOM tree.
 *
 * For example:
 *
 * ```
 * ref { element ->
 *   println("${element.localName} was added to the dom.")
 * }
 * ```
 *
 * @param keys Any number of keys which, if any change, will cause the effect to be disposed and restarted.
 *
 * @see disposableRef
 * @see refScope
 */
fun <TElement : Element> ref(vararg keys: Any?, handle: (TElement) -> Unit) = refScope {
    ref(*keys, handle = handle)
}

/**
 * Create a ref listener which informs you both when a raw element is added to the DOM tree *and* when it is removed.
 *
 * The last call in the block *must* be to `onDispose`.
 *
 * For example:
 *
 * ```
 * disposableRef { element ->
 *   println("${element.localName} was added to the dom.")
 *   onDispose { println("${element.localName} was removed from the dom.") }
 * }
 * ```
 *
 * @param keys Any number of keys which, if any change, will cause the effect to be disposed and restarted.
 *
 * @see ref
 * @see refScope
 */
fun <TElement : Element> disposableRef(
    vararg keys: Any?,
    effect: DisposableEffectScope.(TElement) -> DisposableEffectResult
) = refScope {
    disposableRef(*keys, effect = effect)
}

/**
 * Create a scope which lets you specify any number of [ref] and [disposableRef] handlers.
 *
 * This can be useful if you might have one element that must be rebuilt in response to two different keys
 * independently.
 *
 * For example:
 *
 * ```
 * refScope {
 *   ref(isFeature1Enabled) {
 *     println("Element was initialized, possibly due to feature1's setting changing.")
 *   }
 *   ref(isFeature2Enabled) {
 *     println("Element was initialized, possibly due to feature2's setting changing.")
 *   }
 * }
 * ```
 *
 * @see ref
 * @see disposableRef
 */
fun <TElement : Element> refScope(init: ElementRefScope.Builder<TElement>.() -> Unit) = run {
    ElementRefScope.Builder<TElement>().apply(init).build()
}
