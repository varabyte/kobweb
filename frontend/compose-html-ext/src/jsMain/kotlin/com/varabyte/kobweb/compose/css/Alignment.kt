@file:Suppress("FunctionName") // Intentionally make some function names look like constructors

package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*

// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
// Important note: Some of these classes are redefinitions of those already supplied by Web for Compose. However, I
// think they didn't do it right, as their API both misses occasional valid choices and also doesn't restrict you from
// making invalid combinations of keywords.
// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

// region Common code

/**
 * Enumeration identifying which baseline set to apply additional alignment to.
 */
internal enum class BaselineSet {
    FIRST,
    LAST
}

/**
 * Enumeration for strategies of how to handle overflowing items.
 */
internal enum class OverflowStrategy {
    SAFE,
    UNSAFE;
}

private fun BaselineSet?.toValue(): String {
    val self = this
    return buildString {
        if (self != null) {
            append(self.name.lowercase())
            append(' ')
        }
        append("baseline")
    }
}

private fun OverflowStrategy.toValue(position: StylePropertyValue) = "${name.lowercase()} $position"

// endregion

// region AlignContent

/**
 * Kobweb's version of the `AlignContent` class, which is more fully featured than the Compose HTML one.
 *
 * In many common cases, you may not need to use it, but if you're doing anything with baseline or overflow, this
 * version provides it.
 */
// See https://developer.mozilla.org/en-US/docs/Web/CSS/align-content
sealed class AlignContent(private val value: String) : StylePropertyValue {
    override fun toString() = value

    private class AlignContentKeyword(value: String) : AlignContent(value)
    class AlignContentPosition internal constructor(value: String) : AlignContent(value)

    private class BaselineAlignment(baselineSet: BaselineSet?) : AlignContent(baselineSet.toValue())
    private class OverflowAlignment(strategy: OverflowStrategy, position: AlignContentPosition) :
        AlignContent(strategy.toValue(position))

    companion object {
        // Basic
        val Normal: AlignContent get() = AlignContentKeyword("normal")

        // Positional
        val Center get() = AlignContentPosition("center")
        val Start get() = AlignContentPosition("start")
        val End get() = AlignContentPosition("end")
        val FlexStart get() = AlignContentPosition("flex-start")
        val FlexEnd get() = AlignContentPosition("flex-end")

        // Distributed
        val SpaceBetween: AlignContent get() = AlignContentKeyword("space-between")
        val SpaceAround: AlignContent get() = AlignContentKeyword("space-around")
        val SpaceEvenly: AlignContent get() = AlignContentKeyword("space-evenly")
        val Stretch: AlignContent get() = AlignContentKeyword("stretch")

        // Baseline
        val Baseline: AlignContent get() = BaselineAlignment(null)
        val FirstBaseline: AlignContent get() = BaselineAlignment(BaselineSet.FIRST)
        val LastBaseline: AlignContent get() = BaselineAlignment(BaselineSet.LAST)

        // Overflow
        fun Safe(position: AlignContentPosition): AlignContent = OverflowAlignment(OverflowStrategy.SAFE, position)
        fun Unsafe(position: AlignContentPosition): AlignContent = OverflowAlignment(OverflowStrategy.UNSAFE, position)

        // Global
        val Inherit: AlignContent get() = AlignContentKeyword("inherit")
        val Initial: AlignContent get() = AlignContentKeyword("initial")
        val Revert: AlignContent get() = AlignContentKeyword("revert")
        val Unset: AlignContent get() = AlignContentKeyword("unset")
    }
}

fun StyleScope.alignContent(alignContent: AlignContent) {
    property("align-content", alignContent)
}

// endregion

// region AlignItems

/**
 * Kobweb's version of the `AlignItems` class, which is more fully featured than the Compose HTML one.
 *
 * In many common cases, you may not need to use it, but if you're doing anything with baseline or overflow, this
 * version provides it.
 */
// See https://developer.mozilla.org/en-US/docs/Web/CSS/align-items
sealed class AlignItems private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    private class AlignItemsKeyword(value: String) : AlignItems(value)
    class AlignItemsPosition internal constructor(value: String) : AlignItems(value)

    private class BaselineAlignment(baselineSet: BaselineSet?) : AlignItems(baselineSet.toValue())
    private class OverflowAlignment(strategy: OverflowStrategy, position: AlignItemsPosition) :
        AlignItems(strategy.toValue(position))

    companion object {
        // Basic
        val Normal: AlignItems get() = AlignItemsKeyword("normal")
        val Stretch: AlignItems get() = AlignItemsKeyword("stretch")

        // Positional
        val Center get() = AlignItemsPosition("center")
        val Start get() = AlignItemsPosition("start")
        val End get() = AlignItemsPosition("end")
        val SelfStart get() = AlignItemsPosition("self-start")
        val SelfEnd get() = AlignItemsPosition("self-end")
        val FlexStart get() = AlignItemsPosition("flex-start")
        val FlexEnd get() = AlignItemsPosition("flex-end")

        // Baseline
        val Baseline: AlignItems get() = BaselineAlignment(null)
        val FirstBaseline: AlignItems get() = BaselineAlignment(BaselineSet.FIRST)
        val LastBaseline: AlignItems get() = BaselineAlignment(BaselineSet.LAST)

        // Overflow
        fun Safe(position: AlignItemsPosition): AlignItems = OverflowAlignment(OverflowStrategy.SAFE, position)
        fun Unsafe(position: AlignItemsPosition): AlignItems = OverflowAlignment(OverflowStrategy.UNSAFE, position)

        // Global
        val Inherit: AlignItems get() = AlignItemsKeyword("inherit")
        val Initial: AlignItems get() = AlignItemsKeyword("initial")
        val Revert: AlignItems get() = AlignItemsKeyword("revert")
        val Unset: AlignItems get() = AlignItemsKeyword("unset")
    }
}

fun StyleScope.alignItems(alignItems: AlignItems) {
    property("align-items", alignItems)
}

// endregion

// region AlignSelf

/**
 * Kobweb's version of the `AlignSelf` class, which is more fully featured than the Compose HTML one.
 *
 * In many common cases, you may not need to use it, but if you're doing anything with baseline or overflow, this
 * version provides it.
 */
// See https://developer.mozilla.org/en-US/docs/Web/CSS/align-self
sealed class AlignSelf private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    private class AlignSelfKeyword(value: String) : AlignSelf(value)
    class AlignSelfPosition internal constructor(value: String) : AlignSelf(value)

    private class BaselineAlignment(baselineSet: BaselineSet?) : AlignSelf(baselineSet.toValue())
    private class OverflowAlignment(strategy: OverflowStrategy, position: AlignSelfPosition) :
        AlignSelf(strategy.toValue(position))

    companion object {
        // Basic
        val Auto: AlignSelf get() = AlignSelfKeyword("auto")
        val Normal: AlignSelf get() = AlignSelfKeyword("normal")
        val Stretch: AlignSelf get() = AlignSelfKeyword("stretch")

        // Positional
        val Center get() = AlignSelfPosition("center")
        val Start get() = AlignSelfPosition("start")
        val End get() = AlignSelfPosition("end")
        val SelfStart get() = AlignSelfPosition("self-start")
        val SelfEnd get() = AlignSelfPosition("self-end")
        val FlexStart get() = AlignSelfPosition("flex-start")
        val FlexEnd get() = AlignSelfPosition("flex-end")

        // Baseline
        val Baseline: AlignSelf get() = BaselineAlignment(null)
        val FirstBaseline: AlignSelf get() = BaselineAlignment(BaselineSet.FIRST)
        val LastBaseline: AlignSelf get() = BaselineAlignment(BaselineSet.LAST)

        // Overflow
        fun Safe(position: AlignSelfPosition): AlignSelf = OverflowAlignment(OverflowStrategy.SAFE, position)
        fun Unsafe(position: AlignSelfPosition): AlignSelf = OverflowAlignment(OverflowStrategy.UNSAFE, position)

        // Global
        val Inherit: AlignSelf get() = AlignSelfKeyword("inherit")
        val Initial: AlignSelf get() = AlignSelfKeyword("initial")
        val Revert: AlignSelf get() = AlignSelfKeyword("revert")
        val Unset: AlignSelf get() = AlignSelfKeyword("unset")
    }
}

fun StyleScope.alignSelf(alignSelf: AlignSelf) {
    property("align-self", alignSelf)
}

// endregion


// region JustifyContent

/**
 * Kobweb's version of the `JustifyContent` class, which is more fully featured than the Compose HTML one.
 *
 * In many common cases, you may not need to use it, but if you're doing anything with overflow, this version provides
 * it.
 */
// See https://developer.mozilla.org/en-US/docs/Web/CSS/justify-content
sealed class JustifyContent private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    private class JustifyContentKeyword(value: String) : JustifyContent(value)
    class JustifyContentPosition internal constructor(value: String) : JustifyContent(value)

    private class OverflowAlignment(strategy: OverflowStrategy, position: JustifyContentPosition) :
        JustifyContent(strategy.toValue(position))

    companion object {
        // Basic
        val Normal: JustifyContent get() = JustifyContentKeyword("normal")

        // Positional
        val Center get() = JustifyContentPosition("center")
        val Start get() = JustifyContentPosition("start")
        val End get() = JustifyContentPosition("end")
        val FlexStart get() = JustifyContentPosition("flex-start")
        val FlexEnd get() = JustifyContentPosition("flex-end")
        val Left get() = JustifyContentPosition("left")
        val Right get() = JustifyContentPosition("right")

        // Distributed
        val SpaceBetween: JustifyContent get() = JustifyContentKeyword("space-between")
        val SpaceAround: JustifyContent get() = JustifyContentKeyword("space-around")
        val SpaceEvenly: JustifyContent get() = JustifyContentKeyword("space-evenly")
        val Stretch: JustifyContent get() = JustifyContentKeyword("stretch")

        // Overflow
        fun Safe(position: JustifyContentPosition): JustifyContent = OverflowAlignment(OverflowStrategy.SAFE, position)
        fun Unsafe(position: JustifyContentPosition): JustifyContent =
            OverflowAlignment(OverflowStrategy.UNSAFE, position)

        // Global
        val Inherit: JustifyContent get() = JustifyContentKeyword("inherit")
        val Initial: JustifyContent get() = JustifyContentKeyword("initial")
        val Revert: JustifyContent get() = JustifyContentKeyword("revert")
        val Unset: JustifyContent get() = JustifyContentKeyword("unset")
    }
}

fun StyleScope.justifyContent(justifyContent: JustifyContent) {
    property("justify-content", justifyContent)
}

// endregion

// region JustifyItems

/**
 * Kobweb's version of the `JustifyItems` class, which is more fully featured than the Compose HTML one.
 *
 * In many common cases, you may not need to use it, but if you're doing anything with baseline or overflow, this
 * version provides it.
 */
// See https://developer.mozilla.org/en-US/docs/Web/CSS/justify-items
sealed class JustifyItems private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    private class JustifyItemsKeyword(value: String) : JustifyItems(value)
    class JustifyItemsPosition internal constructor(value: String) : JustifyItems(value)

    private class BaselineAlignment(baselineSet: BaselineSet?) : JustifyItems(baselineSet.toValue())
    private class OverflowAlignment(strategy: OverflowStrategy, position: JustifyItemsPosition) :
        JustifyItems(strategy.toValue(position))

    companion object {
        // Basic
        val Normal: JustifyItems get() = JustifyItemsKeyword("normal")
        val Stretch: JustifyItems get() = JustifyItemsKeyword("stretch")

        // Positional
        val Center get() = JustifyItemsPosition("center")
        val Start get() = JustifyItemsPosition("start")
        val End get() = JustifyItemsPosition("end")
        val FlexStart get() = JustifyItemsPosition("flex-start")
        val FlexEnd get() = JustifyItemsPosition("flex-end")
        val SelfStart get() = JustifyItemsPosition("self-start")
        val SelfEnd get() = JustifyItemsPosition("self-end")
        val Left get() = JustifyItemsPosition("left")
        val Right get() = JustifyItemsPosition("right")

        // Baseline
        val Baseline: JustifyItems get() = BaselineAlignment(null)
        val FirstBaseline: JustifyItems get() = BaselineAlignment(BaselineSet.FIRST)
        val LastBaseline: JustifyItems get() = BaselineAlignment(BaselineSet.LAST)

        // Overflow
        fun Safe(position: JustifyItemsPosition): JustifyItems = OverflowAlignment(OverflowStrategy.SAFE, position)
        fun Unsafe(position: JustifyItemsPosition): JustifyItems = OverflowAlignment(OverflowStrategy.UNSAFE, position)

        // Global
        val Inherit: JustifyItems get() = JustifyItemsKeyword("inherit")
        val Initial: JustifyItems get() = JustifyItemsKeyword("initial")
        val Revert: JustifyItems get() = JustifyItemsKeyword("revert")
        val Unset: JustifyItems get() = JustifyItemsKeyword("unset")
    }
}

fun StyleScope.justifyItems(justifyItems: JustifyItems) {
    property("justify-items", justifyItems)
}

// endregion

// region JustifySelf

/**
 * Kobweb's version of the `JustifySelf` class, which is more fully featured than the Compose HTML one.
 *
 * In many common cases, you may not need to use it, but if you're doing anything with baseline or overflow, this
 * version provides it.
 */
// See https://developer.mozilla.org/en-US/docs/Web/CSS/justify-self
sealed class JustifySelf private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    private class JustifySelfKeyword(value: String) : JustifySelf(value)
    class JustifySelfPosition internal constructor(value: String) : JustifySelf(value)

    private class BaselineAlignment(baselineSet: BaselineSet?) : JustifySelf(baselineSet.toValue())
    private class OverflowAlignment(strategy: OverflowStrategy, position: JustifySelfPosition) :
        JustifySelf(strategy.toValue(position))

    companion object {
        // Basic
        val Auto: JustifySelf get() = JustifySelfKeyword("auto")
        val Normal: JustifySelf get() = JustifySelfKeyword("normal")
        val Stretch: JustifySelf get() = JustifySelfKeyword("stretch")

        // Positional
        val Center get() = JustifySelfPosition("center")
        val Start get() = JustifySelfPosition("start")
        val End get() = JustifySelfPosition("end")
        val SelfStart get() = JustifySelfPosition("self-start")
        val SelfEnd get() = JustifySelfPosition("self-end")
        val FlexStart get() = JustifySelfPosition("flex-start")
        val FlexEnd get() = JustifySelfPosition("flex-end")
        val Left get() = JustifySelfPosition("left")
        val Right get() = JustifySelfPosition("right")

        // Baseline
        val Baseline: JustifySelf get() = BaselineAlignment(null)
        val FirstBaseline: JustifySelf get() = BaselineAlignment(BaselineSet.FIRST)
        val LastBaseline: JustifySelf get() = BaselineAlignment(BaselineSet.LAST)

        // Overflow
        fun Safe(position: JustifySelfPosition): JustifySelf = OverflowAlignment(OverflowStrategy.SAFE, position)
        fun Unsafe(position: JustifySelfPosition): JustifySelf = OverflowAlignment(OverflowStrategy.UNSAFE, position)

        // Global
        val Inherit: JustifySelf get() = JustifySelfKeyword("inherit")
        val Initial: JustifySelf get() = JustifySelfKeyword("initial")
        val Revert: JustifySelf get() = JustifySelfKeyword("revert")
        val Unset: JustifySelf get() = JustifySelfKeyword("unset")
    }
}

fun StyleScope.justifySelf(justifySelf: JustifySelf) {
    property("justify-self", justifySelf)
}

// endregion

// region Place methods

fun StyleScope.placeContent(alignContent: AlignContent, justifyContent: JustifyContent) {
    property("place-content", "$alignContent $justifyContent")
}

fun StyleScope.placeItems(alignItems: AlignItems, justifyItems: JustifyItems) {
    property("place-items", "$alignItems $justifyItems")
}

fun StyleScope.placeSelf(alignSelf: AlignSelf, justifySelf: JustifySelf) {
    placeSelf("$alignSelf $justifySelf")
}

// endregion
