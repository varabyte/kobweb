package com.varabyte.kobweb.compose.attributes

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.*
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.attributes.SyntheticEventListener
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.internal.runtime.ComposeWebInternalApi
import org.w3c.dom.Element
import org.w3c.dom.HTMLElement

private class DummyAttrsScope<E: Element> : AttrsScope<E> {
    override fun attr(attr: String, value: String): AttrsScope<E> = this
    override fun classes(classes: Collection<String>) = Unit
    override fun <E : HTMLElement, V> prop(update: (E, V) -> Unit, value: V) = Unit
    @ComposeWebInternalApi
    override fun registerEventListener(listener: SyntheticEventListener<*>) = Unit
    override fun style(builder: StyleScope.() -> Unit) = Unit
    override fun ref(effect: DisposableEffectScope.(E) -> DisposableEffectResult) = Unit

}

/**
 * A wrapper around an internal [AttrsScope] where equality / hashcode has meaning.
 */
class ComparableAttrsScope<E: Element>(private val wrapped: AttrsScope<E>) : AttrsScope<E> {
    constructor(): this(DummyAttrsScope())

    val attributes = mutableMapOf<String, String>()
    val classes = mutableSetOf<String>()
    val listeners = mutableSetOf<SyntheticEventListener<*>>()
    var style: ComparableStyleScope? = null

    override fun attr(attr: String, value: String): AttrsScope<E> {
        attributes[attr] = value
        wrapped.attr(attr, value)
        return this
    }

    override fun classes(classes: Collection<String>) {
        this.classes.addAll(classes)
    }

    override fun <E : HTMLElement, V> prop(update: (E, V) -> Unit, value: V) {
        wrapped.prop(update, value)
    }

    @ComposeWebInternalApi
    override fun registerEventListener(listener: SyntheticEventListener<*>) {
        listeners.add(listener)
        wrapped.registerEventListener(listener)
    }

    override fun style(builder: StyleScope.() -> Unit) {
        val style = this.style ?: ComparableStyleScope()
        style.builder()
        this.style = style
        wrapped.style(builder)
    }

    override fun ref(effect: DisposableEffectScope.(E) -> DisposableEffectResult) {
        wrapped.ref(effect)
    }

    override fun equals(other: Any?): Boolean {
        return (other is ComparableAttrsScope<*>
            && other.attributes == attributes
            && other.classes == classes
            && other.listeners == listeners
            && other.style == style
            )
    }

    override fun hashCode(): Int {
        var result = attributes.hashCode()
        result = 31 * result + classes.hashCode()
        result = 31 * result + listeners.hashCode()
        result = 31 * result + (style?.hashCode() ?: 0)
        return result
    }
}
