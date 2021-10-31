package com.varabyte.kobweb.silk.components

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.classNames
import com.varabyte.kobweb.silk.theme.SilkTheme

/**
 * Marker interface for a key used to fetch styling for a component.
 *
 * For example,
 *
 * ```
 * val ButtonKey = object : ComponentKey { name = "button" }
 *
 * // Later, to fetch
 * SilkTheme.componentModifiers[ButtonKey].toModifier()
 * ```
 *
 * The name will be used to ensure the component is tagged with a classname prefixed with silk, so above "silk-button".
 * While we don't currently use it ourselves, your own app can take advantage of it and add css styles directly if
 * preferred.
 *
 * Base modifiers can be updated by passing replacements into the `SilkTheme` composable:
 *
 * ```
 * object MyCustomButtonModifier : ComponentModifier { ... }
 *
 * /* ... */
 *
 * SilkTheme(componentModifiers = listOf(ButtonKey to MyCustomButtonModifier)) {
 *    /* ... */
 * }
 * // Your theme will not be applied anymore after the closing brace
 * ```
 *
 * or if you want to _extend_ a base modifier, you can use the [then] function:
 *
 * ```
 * object MyCustomButtonModifier : ComponentModifier { ... }
 *
 * /* ... */
 *
 * val baseModifier = SilkTheme.componentModifiers[ButtonKey]
 * SilkTheme(componentModifiers = listOf(ButtonKey to baseModifier.then(SilkThemeMyCustomButtonModifier))) {
 *    /* ... */
 * }
 * ```
 */
class ComponentKey(val name: String)

/**
 * An interface that provides styling for some target component.
 *
 * ComponentModifiers can be passed optional data that may help determine its final styles, but this value is dependent
 * on each component to decide.
 *
 * TODO(#50): Revisit this API if the upstream bug around generics is resolved
 */
interface ComponentModifier {
    @Composable
    fun toModifier(data: Any?): Modifier
}

/**
 * A useful modifier in case you want to define a component that doesn't have any modifier associated with it, or to
 * completely remove styling for an existing system modifier with it.
 */
object NoOpComponentModifier : ComponentModifier {
    @Composable
    override fun toModifier(data: Any?) = Modifier
}

/**
 * A modifier which will tag the current element with `"silk-$name"` as a classname.
 *
 * This is useful as a minimum styling that ALL components are guaranteed to be applied with (even if you replace the
 * current modifier for some key with a [NoOpComponentModifier].
 */
class ClassNameComponentModifier(private val name: String) : ComponentModifier {
    @Composable
    override fun toModifier(data: Any?): Modifier {
        return Modifier.classNames("silk-$name")
    }
}

/**
 * A convenience function for composing two [ComponentModifier]s
 */
fun ComponentModifier.then(other: ComponentModifier?): ComponentModifier {
    if (other == null) return this

    val self = this
    return object : ComponentModifier {
        @Composable
        override fun toModifier(data: Any?): Modifier {
            return self.toModifier(data).then(other.toModifier(data))
        }
    }
}

interface ComponentModifiers {
    /**
     * Fetch a component modifier by its [ComponentKey].
     *
     * Modifiers can be overridden and additional modifiers can be registered using the [SilkTheme] composable.
     *
     * This method will throw an exception if no style was previously registered with the target key.
     */
    operator fun get(key: ComponentKey): ComponentModifier
}

internal class MutableComponentModifiers(private val parent: ComponentModifiers? = null) : ComponentModifiers {
    private val baseModifiers = mutableMapOf<ComponentKey, ComponentModifier>()

    operator fun set(key: ComponentKey, baseModifier: ComponentModifier) {
        baseModifiers[key] = ClassNameComponentModifier(key.name).then(baseModifier)
    }

    override fun get(key: ComponentKey): ComponentModifier {
        return baseModifiers[key] ?: if (parent != null) parent[key] else error {
            "No style registered with the key $key. Use the SilkTheme method to do this."
        }
    }
}