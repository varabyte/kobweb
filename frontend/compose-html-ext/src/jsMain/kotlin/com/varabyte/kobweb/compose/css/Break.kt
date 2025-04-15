package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*

// See https://developer.mozilla.org/en-US/docs/Web/CSS/break-after
sealed class BreakAfter private constructor(private val value: String) : StylePropertyValue {

    override fun toString() = value

    private class KeyWord(value: String) : BreakAfter(value)

    companion object : BreakStyleBeforeAfterPropertyValues<KeyWord> by breakStyleBeforeAfterPropertyValues(::KeyWord),
        BreakStyleGlobalValues<KeyWord> by globalStyleGlobalValues(::KeyWord),
        BreakInsidePropertyValues<KeyWord> by breakInsidePropertyValues(::KeyWord)
}

fun StyleScope.breakAfter(breakAfter: BreakAfter) {
    property("break-after", breakAfter)
}

// See https://developer.mozilla.org/en-US/docs/Web/CSS/break-before
sealed class BreakBefore private constructor(private val value: String) : StylePropertyValue {

    override fun toString() = value

    private class KeyWord(value: String) : BreakBefore(value)

    companion object : BreakStyleBeforeAfterPropertyValues<KeyWord> by breakStyleBeforeAfterPropertyValues(::KeyWord),
        BreakStyleGlobalValues<KeyWord> by globalStyleGlobalValues(::KeyWord),
        BreakInsidePropertyValues<KeyWord> by breakInsidePropertyValues(::KeyWord)
}

fun StyleScope.breakBefore(breakBefore: BreakBefore) {
    property("break-before", breakBefore)
}

//See https://developer.mozilla.org/en-US/docs/Web/CSS/break-inside
sealed class BreakInside private constructor(private val value: String) : StylePropertyValue {

    override fun toString() = value

    private class KeyWord(value: String) : BreakInside(value)

    companion object : BreakInsidePropertyValues<KeyWord> by breakInsidePropertyValues(::KeyWord),
        BreakStyleGlobalValues<KeyWord> by globalStyleGlobalValues(::KeyWord)
}

fun StyleScope.breakInside(breakInside: BreakInside) {
    property("break-inside", breakInside)
}

/**
 * This interface defines the values for properties: [BreakInside]
 */
@Suppress("PropertyName")
interface BreakInsidePropertyValues<T> {
    /* Generic break values */
    val Auto: T
    val Avoid: T

    /* Page break values */
    val AvoidPage: T

    /* Column break values */
    val AvoidColumn: T

    /* Region break values */
    val AvoidRegion: T

}

fun <T> breakInsidePropertyValues(factory: (String) -> T): BreakInsidePropertyValues<T> =
    object : BreakInsidePropertyValues<T> {
        override val Auto = factory("auto")
        override val Avoid = factory("avoid")
        override val AvoidPage = factory("avoid-page")
        override val AvoidColumn = factory("avoid-column")
        override val AvoidRegion = factory("avoid-region")

    }

/**
 * This interface defines the global values shared across the three properties: [BreakAfter], [BreakBefore] and [BreakInside]
 */
@Suppress("PropertyName")
interface BreakStyleGlobalValues<T> {
    /* Global values */
    val Inherit: T
    val Initial: T
    val Revert: T
    val RevertLayer: T
    val Unset: T
}

fun <T> globalStyleGlobalValues(factory: (String) -> T): BreakStyleGlobalValues<T> =
    object : BreakStyleGlobalValues<T> {
        /* Global values */
        override val Inherit = factory("inherit")
        override val Initial = factory("initial")
        override val Revert = factory("revert")
        override val RevertLayer = factory("revert-layer")
        override val Unset = factory("unset")
    }

/**
 * This interface defines the global values shared across the two properties: [BreakAfter], [BreakBefore]
 */
@Suppress("PropertyName")
interface BreakStyleBeforeAfterPropertyValues<T> {
    /* Generic break values */
    val Always: T
    val All: T

    /* Page break values */
    val Page: T
    val Left: T
    val Right: T
    val Recto: T
    val Verso: T

    /* Column break values */
    val Column: T

    /* Region break values */
    val Region: T
}

fun <T> breakStyleBeforeAfterPropertyValues(factory: (String) -> T): BreakStyleBeforeAfterPropertyValues<T> =
    object : BreakStyleBeforeAfterPropertyValues<T> {

        /* Generic break values */
        override val Always = factory("always")
        override val All = factory("all")

        /* Page break values */
        override val Page = factory("page")
        override val Left = factory("left")
        override val Right = factory("right")
        override val Recto = factory("recto")
        override val Verso = factory("verso")

        /* Column break values */
        override val Column = factory("column")

        /* Region break values */
        override val Region = factory("region")
    }