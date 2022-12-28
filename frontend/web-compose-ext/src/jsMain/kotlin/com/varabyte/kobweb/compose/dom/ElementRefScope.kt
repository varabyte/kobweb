package com.varabyte.kobweb.compose.dom

import androidx.compose.runtime.*
import org.jetbrains.compose.web.dom.ElementScope
import org.w3c.dom.Element

/**
 * A callback scope for listeners that inform the user about events associated with an underlying raw DOM element.
 *
 * You do not construct these directly. Instead, see [ref], [disposableRef], and [refScope].
 */
data class ElementRefScope<TElement : Element> internal constructor(
    internal val keyedCallbacks: List<KeysToEffect<TElement>>
) {
    internal sealed class RefCallback<TElement : Element> {
        abstract operator fun invoke(scope: DisposableEffectScope, element: TElement): DisposableEffectResult
        class Simple<TElement : Element>(val handle: (TElement) -> Unit) : RefCallback<TElement>() {
            override fun invoke(scope: DisposableEffectScope, element: TElement): DisposableEffectResult {
                handle(element)
                return scope.onDispose {}
            }
        }
        class Disposable<TElement : Element>(val effect: DisposableEffectScope.(TElement) -> DisposableEffectResult) : RefCallback<TElement>() {
            override fun invoke(scope: DisposableEffectScope, element: TElement): DisposableEffectResult {
                return scope.effect(element)
            }
        }
    }

    internal data class KeysToEffect<TElement : Element>(
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

        internal fun build() = ElementRefScope(keyedCallbacks)
    }
}

operator fun <TElement : Element> ElementRefScope<TElement>.plus(other: ElementRefScope<TElement>?): ElementRefScope<TElement> {
    return if (other != null) ElementRefScope(keyedCallbacks + other.keyedCallbacks) else this
}

/**
 * Convenience method for installing an [ElementRefScope] into an [ElementScope].
 *
 * This helps avoid a bunch of annoying boilerplate that the Compose for Web API otherwise requires you to do fairly
 * regularly.
 */
@NonRestartableComposable
@Composable
fun <TElement : Element> ElementScope<TElement>.registerRefScope(scope: ElementRefScope<TElement>?) {
    if (scope == null) return
    scope.keyedCallbacks.forEach { keyedCallback ->
        DisposableEffect(*keyedCallback.keys.toTypedArray()) {
            keyedCallback.refCallback.invoke(this, scopeElement)
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
 * See also: [disposableRef], [refScope]
 *
 * @param keys Any number of keys which, if any change, will cause the effect to be disposed and restarted.
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
 *   onDispose { println("${element.localName} was added to the dom.") }
 * }
 * ```
 *
 * See also: [ref], [refScope]
 *
 * @param keys Any number of keys which, if any change, will cause the effect to be disposed and restarted.
 */
fun <TElement : Element> disposableRef(vararg keys: Any?, effect: DisposableEffectScope.(TElement) -> DisposableEffectResult) = refScope {
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
 * See also: [ref], [disposableRef]
 */
fun <TElement : Element> refScope(init: ElementRefScope.Builder<TElement>.() -> Unit) = run {
    ElementRefScope.Builder<TElement>().apply(init).build()
}
