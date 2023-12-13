package com.varabyte.kobweb.compose.css

import com.varabyte.kobweb.compose.util.titleCamelCaseToKebabCase
import org.jetbrains.compose.web.css.*
import org.w3c.dom.HTMLElement
import kotlin.reflect.KProperty

// A reimplementation of org.jetbrains.compose.web.css.CSSStyleVariable, since that version uses `out` variance which
// actually allowed invalid assignment to work.
// See https://github.com/JetBrains/compose-jb/issues/2763 for more detail.
// This version also provides first class support for Number and String values, internally delegating to
// StylePropertyNumber and StylePropertyString, respectively.

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
 *
 * @param T The underlying type of the variable.
 *
 * @param V The type used for querying and setting the variable. This is especially useful for exposing direct
 * `Number` and `String` values to the user, as these cannot be the underlying type.
 */
sealed class StyleVariable<T : StylePropertyValue, V>(
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

    /** Query the underlying variable's value. */
    protected fun variableValue(fallback: T?) =
        CSSVariableValue<T>("var(--$name${(fallback ?: defaultFallback)?.let { ", $it" } ?: ""})")

    /** Represents a [StyleVariable] of a custom type. */
    class PropertyValue<T : StylePropertyValue>(
        name: String,
        defaultFallback: T? = null,
        prefix: String? = null
    ) : StyleVariable<T, T>(name, defaultFallback, prefix) {
        override fun value(fallback: T?): T = variableValue(fallback)
    }

    /** Represents a [StyleVariable] of a number. */
    class NumberValue<T : Number>(
        name: String,
        defaultFallback: T? = null,
        prefix: String? = null
    ) : StyleVariable<StylePropertyNumber, T>(name, defaultFallback?.let { StylePropertyValue(it) }, prefix) {
        override fun value(fallback: T?): T = variableValue(fallback?.let { StylePropertyValue(it) }).unsafeCast<T>()
    }

    /** Represents a [StyleVariable] of a string. */
    class StringValue(
        name: String,
        defaultFallback: String? = null,
        prefix: String? = null
    ) : StyleVariable<StylePropertyString, String>(name, defaultFallback?.let { StylePropertyValue(it) }, prefix) {
        override fun value(fallback: String?): String =
            variableValue(fallback?.let { StylePropertyValue(it) }).unsafeCast<String>()
    }
}

/** Helper method for transforming a Kotlin property into a CSS variable name. */
private fun provideVariableName(groupObject: Any?, property: KProperty<*>) =
    buildString {
        if (groupObject != null) {
            append(
                groupObject::class.simpleName!!.titleCamelCaseToKebabCase().removeSuffix("-vars")
                    .removeSuffix("-variables")
            )
            append('-')
        }
        append(property.name.titleCamelCaseToKebabCase().removeSuffix("-var").removeSuffix("-variable"))
    }


/**
 * A delegate provider class which allows you to create a [StyleVariable.PropertyValue] instance via the `by` keyword.
 *
 * If wrapped inside a parent object, this will treat that as a source of a group name prefix that will get prepended
 * in front of your variable name.
 */
class StyleVariablePropertyProvider<T : StylePropertyValue> internal constructor(
    private val defaultFallback: T?,
    private val prefix: String?
) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>) =
        StyleVariable.PropertyValue(provideVariableName(thisRef, property), defaultFallback, prefix)
}

/**
 * A delegate provider class which allows you to create a [StyleVariable.NumberValue] instance via the `by` keyword.
 *
 * If wrapped inside a parent object, this will treat that as a source of a group name prefix that will get prepended
 * in front of your variable name.
 */
class StyleVariableNumberProvider<T : Number> internal constructor(
    private val defaultFallback: T?,
    private val prefix: String?
) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>) =
        StyleVariable.NumberValue(provideVariableName(thisRef, property), defaultFallback, prefix)
}

/**
 * A delegate provider class which allows you to create a [StyleVariable.StringValue] instance via the `by` keyword.
 *
 * If wrapped inside a parent object, this will treat that as a source of a group name prefix that will get prepended
 * in front of your variable name.
 */
class StyleVariableStringProvider internal constructor(
    private val defaultFallback: String?,
    private val prefix: String?
) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>) =
        StyleVariable.StringValue(provideVariableName(thisRef, property), defaultFallback, prefix)
}

/** Helper method for declaring a [StyleVariable.PropertyValue] instance via the `by` keyword. */
@Suppress("FunctionName")
fun <T : StylePropertyValue> StyleVariable(defaultFallback: T? = null, prefix: String? = null) =
    StyleVariablePropertyProvider(defaultFallback, prefix)

/** Helper method for declaring a [StyleVariable.NumberValue] instance via the `by` keyword. */
@Suppress("FunctionName")
fun <T : Number> StyleVariable(defaultFallback: T? = null, prefix: String? = null) =
    StyleVariableNumberProvider(defaultFallback, prefix)

/** Helper method for declaring a [StyleVariable.StringValue] instance via the `by` keyword. */
// ensure type always has to be explicitly `StyleVariable<String>()` or inferred `StyleVariable(..)`
@Suppress("FunctionName", "FINAL_UPPER_BOUND")
fun <T : String> StyleVariable(defaultFallback: T? = null, prefix: T? = null) =
    StyleVariableStringProvider(defaultFallback, prefix)

// Below we define some additional overloads for numeric unit types, so that they are automatically inferred to be
// slightly broader types when applicable. For example, `StyleVariable(8.px)` will be inferred to be
// `StyleVariable.PropertyValue<CSSLengthNumericValue>()` as opposed to `StyleVariable.PropertyValue<CSSPxValue>()`.
// This is in general desirable, as style variables are intended to be used for css properties that do not care about
// the specific unit type. The user can always explicitly specify the type if they need to.
// These functions also enforce that `CSSNumericValue` is used as the underlying type, as opposed to `CSSSizeValue`,
// which is a safer and more accurate choice (see CSSUnits.kt for more detail).

// this overload is necessary to avoid ambiguity when no default fallback is provided
/** Helper method for declaring a [StyleVariable.PropertyValue] instance via the `by` keyword. */
@Suppress("FunctionName")
fun <T : StylePropertyValue> StyleVariable(prefix: String? = null) =
    StyleVariablePropertyProvider<T>(null, prefix)

/**
 * Helper method for declaring a [StyleVariable.PropertyValue] instance representing an angle value via the `by`
 * keyword.
 */
@Suppress("FunctionName")
fun <T : CSSAngleValue> StyleVariable(defaultFallback: T? = null, prefix: String? = null) =
    StyleVariablePropertyProvider<CSSAngleNumericValue>(defaultFallback, prefix)

/**
 * Helper method for declaring a [StyleVariable.PropertyValue] instance representing a length value via the `by`
 * keyword.
 */
@Suppress("FunctionName")
fun <T : CSSLengthValue> StyleVariable(defaultFallback: T? = null, prefix: String? = null) =
    StyleVariablePropertyProvider<CSSLengthNumericValue>(defaultFallback, prefix)

/**
 * Helper method for declaring a [StyleVariable.PropertyValue] instance representing a percentage value via the `by`
 * keyword.
 */
@Suppress("FunctionName")
fun <T : CSSPercentageValue> StyleVariable(defaultFallback: T? = null, prefix: String? = null) =
    StyleVariablePropertyProvider<CSSPercentageNumericValue>(defaultFallback, prefix)

/**
 * Helper method for declaring a [StyleVariable.PropertyValue] instance representing a length or percentage value via
 * the `by` keyword.
 */
@Suppress("FunctionName")
fun <T : CSSLengthOrPercentageValue> StyleVariable(defaultFallback: T? = null, prefix: String? = null) =
    StyleVariablePropertyProvider<CSSLengthOrPercentageNumericValue>(defaultFallback, prefix)

/**
 * Helper method for declaring a [StyleVariable.PropertyValue] instance representing a time value via the `by` keyword.
 */
@Suppress("FunctionName")
fun <T : CSSTimeNumericValue> StyleVariable(defaultFallback: T? = null, prefix: String? = null) =
    StyleVariablePropertyProvider<CSSTimeNumericValue>(defaultFallback, prefix)

/**
 * Helper method for declaring a [StyleVariable.PropertyValue] instance representing a flex value via the `by` keyword.
 */
@Suppress("FunctionName")
fun <T : CSSFlexNumericValue> StyleVariable(defaultFallback: T? = null, prefix: String? = null) =
    StyleVariablePropertyProvider<CSSFlexNumericValue>(defaultFallback, prefix)

/**
 * Helper method for setting a [StyleVariable] onto a raw HTML element.
 *
 * Most users will use `Modifier.setVariable` instead, but there are cases where this approach can be useful, like
 * grabbing the root element from the DOM and adding the variables onto it.
 */
fun <T> HTMLElement.setVariable(variable: StyleVariable<*, T>, value: T) {
    this.style.setProperty("--${variable.name}", value.toString())
}

// NOTE: These should just be `variable.invoke(value)`, but it seems broken for inline styles.
// See also: https://github.com/JetBrains/compose-jb/issues/2702

fun <T : StylePropertyValue> StyleScope.setVariable(variable: StyleVariable.PropertyValue<T>, value: T) {
    property("--${variable.name}", value)
}

fun <T : Number> StyleScope.setVariable(variable: StyleVariable.NumberValue<T>, value: T) {
    property("--${variable.name}", value)
}

fun StyleScope.setVariable(variable: StyleVariable.StringValue, value: String) {
    property("--${variable.name}", value)
}
