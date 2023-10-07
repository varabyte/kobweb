package com.varabyte.kobweb.silk.components.style

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.ui.Modifier
import org.jetbrains.compose.web.css.*

sealed class ComponentVariant {
    object Empty : ComponentVariant() {
        override fun addStylesInto(styleSheet: StyleSheet) = Unit

        @Composable
        override fun toModifier() = Modifier
    }

    infix fun then(next: ComponentVariant): ComponentVariant {
        return if (next === Empty) this
        else if (this === Empty) next
        else CompositeComponentVariant(this, next)
    }

    internal abstract fun addStylesInto(styleSheet: StyleSheet)

    @Composable
    internal abstract fun toModifier(): Modifier
}

/**
 * A default [ComponentVariant] implementation that represents a single variant style.
 */
class SimpleComponentVariant(
    internal val style: ComponentStyle,
    internal val baseStyle: ComponentStyle,
) : ComponentVariant() {
    /**
     * The raw variant name, unqualified by its parent base style.
     *
     * This name is not guaranteed to be unique across all variants. If you need that, check `style.name` instead.
     */
    val name: String
        get() = style.name.removePrefix("${baseStyle.name}-")

    override fun addStylesInto(styleSheet: StyleSheet) {
        // If you are using a variant, require it be associated with a tag already associated with the base style
        // e.g. if you have a link variant ("silk-link-undecorated") it should only be applied if the tag is also
        // a link (so this would be registered as ".silk-link.silk-link-undecorated").
        // To put it another way, if you use a link variant with a surface widget, it won't be applied.
        style.addStylesInto(styleSheet, ".${baseStyle.name}.${style.name}")
    }

    @Composable
    override fun toModifier() = style.toModifier()
    internal fun intoImmutableStyle() = style.intoImmutableStyle()
}

private class CompositeComponentVariant(private val head: ComponentVariant, private val tail: ComponentVariant) :
    ComponentVariant() {
    override fun addStylesInto(styleSheet: StyleSheet) {
        head.addStylesInto(styleSheet)
        tail.addStylesInto(styleSheet)
    }

    @Composable
    override fun toModifier() = head.toModifier().then(tail.toModifier())
}

fun ComponentVariant.thenIf(condition: Boolean, produce: () -> ComponentVariant): ComponentVariant {
    return this
        .then(if (condition) produce() else ComponentVariant.Empty)
}

fun ComponentVariant.thenUnless(condition: Boolean, produce: () -> ComponentVariant): ComponentVariant {
    return this.thenIf(!condition, produce)
}

fun ComponentVariant.thenIf(condition: Boolean, other: ComponentVariant): ComponentVariant {
    return this.thenIf(condition) { other }
}

fun ComponentVariant.thenUnless(condition: Boolean, other: ComponentVariant): ComponentVariant {
    return this.thenUnless(condition) { other }
}

/**
 * A convenience method for folding a list of component variants into one single one that represents all of them.
 */
@Composable
fun Iterable<ComponentVariant?>.combine(): ComponentVariant {
    var finalVariant: ComponentVariant = ComponentVariant.Empty
    for (variant in this) {
        finalVariant = finalVariant.then(variant ?: ComponentVariant.Empty)
    }
    return finalVariant
}
