package com.varabyte.kobweb.compose.css

import com.varabyte.kobweb.compose.util.titleCamelCaseToKebabCase
import org.jetbrains.compose.web.css.*
import org.w3c.dom.HTMLElement
import kotlin.reflect.KProperty

// A reimplementation of org.jetbrains.compose.web.css.CSSStyleVariable, since that version uses `out` variance which
// actually allowed invalid assignment to work.
// See https://github.com/JetBrains/compose-jb/issues/2763 for more detail.
// TODO: name
sealed class KobwebCssVariable<T : StylePropertyValue, V>(
    name: String,
    private val defaultFallback: T?,
    prefix: String?
) : CSSVariable {
    final override val name = prefix?.let { "$it-$name" } ?: name

    /**
     * Query this variable's current value.
     *
     * It is preferred that `setVariable` was called in a parent scope prior to using this variable, but you can provide
     * an optional fallback value in case one was not. If this fallback and [defaultFallback] are both set, then the
     * fallback passed to this method will take precedence.
     */
    abstract fun value(fallback: V? = null): V

    protected fun variableValue(fallback: T?) =
        CSSVariableValue<T>("var(--$name${(fallback ?: defaultFallback)?.let { ", $it" } ?: ""})")
}

/**
 * A class for declaring a CSS style property (i.e. a variable value that can be used inside CSS styles).
 *
 * You can declare one, set it, and use it in your styles like this:
 *
 * ```
 * val bgColor by StyleVariable<Color>()
 * val MainAreaStyle by ComponentStyle.base {
 *   Modifier.setVariable(bgColor, Colors.Black)
 * }
 *
 * val InvertedAreaStyle by ComponentStyle.base {
 *   Modifier.setVariable(bgColor, Colors.White)
 * }
 *
 * // Box style is expected to be applied underneath a root element using either MainAreaStyle or InvertedAreaStyle
 * val BoxStyle by ComponentStyle.base {
 *   // Will be black if rendered in the main area or white in the inverted area.
 *   Modifier.backgroundColor(bgColor.value())
 * }
 * ```
 *
 * @param name The (globally unique) name for this variable.
 *
 * @param defaultFallback When you query a variable, you can specify a fallback at that time. However, if not specified,
 *   then you can provide this default fallback to be used instead. See also: [value].
 */
class StyleVariable<T : StylePropertyValue>(
    name: String,
    defaultFallback: T? = null,
    prefix: String? = null
) : KobwebCssVariable<T, T>(name, defaultFallback, prefix) {
    override fun value(fallback: T?): T = variableValue(fallback)
}

class StyleVariableNumber<T : Number>(
    name: String,
    defaultFallback: T? = null,
    prefix: String? = null
) : KobwebCssVariable<StylePropertyNumber, T>(name, defaultFallback?.let { StylePropertyValue(it) }, prefix) {
    override fun value(fallback: T?): T = variableValue(fallback?.let { StylePropertyValue(it) }).unsafeCast<T>()
}

class StyleVariableString(
    name: String,
    defaultFallback: String? = null,
    prefix: String? = null
) : KobwebCssVariable<StylePropertyString, String>(name, defaultFallback?.let { StylePropertyValue(it) }, prefix) {
    override fun value(fallback: String?): String =
        variableValue(fallback?.let { StylePropertyValue(it) }).unsafeCast<String>()
}

private fun provideVariableName(property: KProperty<*>) =
    property.name.titleCamelCaseToKebabCase().removeSuffix("-var").removeSuffix("-variable")

/**
 * A delegate provider class which allows you to create a [StyleVariable] instance via the `by` keyword.
 */
class StyleVariableProvider<T : StylePropertyValue> internal constructor(
    private val defaultFallback: T?,
    private val prefix: String?
) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>) =
        StyleVariable(provideVariableName(property), defaultFallback, prefix)
}

class StyleVariableNumberProvider<T : Number> internal constructor(
    private val defaultFallback: T?,
    private val prefix: String?
) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>) =
        StyleVariableNumber(provideVariableName(property), defaultFallback, prefix)
}

class StyleVariableStringProvider internal constructor(
    private val defaultFallback: String?,
    private val prefix: String?
) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>) =
        StyleVariableString(provideVariableName(property), defaultFallback, prefix)
}

/** Helper method for declaring a [StyleVariable] instance via the `by` keyword. */
@Suppress("FunctionName")
fun <T : StylePropertyValue> StyleVariable(defaultFallback: T? = null, prefix: String? = null) =
    StyleVariableProvider(defaultFallback, prefix)

/** Helper method for declaring a [StyleVariableNumber] instance via the `by` keyword. */
@Suppress("FunctionName")
fun <T : Number> StyleVariable(defaultFallback: T? = null, prefix: String? = null) =
    StyleVariableNumberProvider(defaultFallback, prefix)

/** Helper method for declaring a [StyleVariableString] instance via the `by` keyword. */
// ensure type always has to be explicitly `StyleVariable<String>()` or inferred `StyleVariable(..)`
@Suppress("FunctionName", "FINAL_UPPER_BOUND")
fun <T : String> StyleVariable(defaultFallback: T? = null, prefix: T? = null) =
    StyleVariableStringProvider(defaultFallback, prefix)

/**
 * Helper method for setting a [StyleVariable] onto a raw HTML element.
 *
 * Most users will use `Modifier.setVariable` instead, but there are cases where this approach can be useful, like
 * grabbing the root element from the DOM and adding the variables onto it.
 */
fun <T> HTMLElement.setVariable(variable: KobwebCssVariable<*, T>, value: T) {
    this.style.setProperty("--${variable.name}", value.toString())
}
