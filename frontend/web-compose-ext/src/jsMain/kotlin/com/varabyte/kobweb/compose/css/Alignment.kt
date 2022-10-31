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
enum class BaselineSet {
    FIRST,
    LAST
}

/**
 * Enumeration for strategies of how to handle overflowing items.
 */
enum class OverflowStrategy {
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

private fun OverflowStrategy.toValue(position: String) = "${name.lowercase()} $position"

// endregion

// region AlignContent

// See https://developer.mozilla.org/en-US/docs/Web/CSS/align-content
sealed class AlignContent(val value: String) {
    open class AlignContentKeyword(value: String) : AlignContent(value)
    class AlignContentPosition(value: String) : AlignContentKeyword(value)

    class BaselineAlignment(baselineSet: BaselineSet?) : AlignContent(baselineSet.toValue())
    class OverflowAlignment(strategy: OverflowStrategy, position: AlignContentPosition) : AlignContent(strategy.toValue(position.value))

    companion object {
        // Basic
        val Normal get() = AlignContentKeyword("normal")

        // Positional
        val Center get() = AlignContentPosition("center")
        val Start get() = AlignContentPosition("start")
        val End get() = AlignContentPosition("end")
        val FlexStart get() = AlignContentPosition("flex-start")
        val FlexEnd get() = AlignContentPosition("flex-end")

        // Distributed
        val SpaceBetween get() = AlignContentKeyword("space-between")
        val SpaceAround get() = AlignContentKeyword("space-around")
        val SpaceEvenly get() = AlignContentKeyword("space-evenly")
        val Stretch get() = AlignContentKeyword("stretch")

        // Baseline
        val Baseline get() = BaselineAlignment(null)
        val FirstBaseline get() = BaselineAlignment(BaselineSet.FIRST)
        val LastBaseline get() = BaselineAlignment(BaselineSet.LAST)

        // Overflow
        fun Safe(position: AlignContentPosition) = OverflowAlignment(OverflowStrategy.SAFE, position)
        fun Unsafe(position: AlignContentPosition) = OverflowAlignment(OverflowStrategy.UNSAFE, position)

        // Global
        val Inherit get() = AlignContentKeyword("inherit")
        val Initial get() = AlignContentKeyword("initial")
        val Revert get() = AlignContentKeyword("revert")
        val RevertLayer get() = AlignContentKeyword("revert-layer")
        val Unset get() = AlignContentKeyword("unset")
    }
}

fun StyleScope.alignContent(alignContent: AlignContent) {
    property("align-content", alignContent.value)
}

// endregion

// region AlignItems

// See https://developer.mozilla.org/en-US/docs/Web/CSS/align-items
sealed class AlignItems(val value: String) {
    open class AlignItemsKeyword(value: String) : AlignItems(value)
    class AlignItemsPosition(value: String) : AlignItemsKeyword(value)

    class BaselineAlignment(baselineSet: BaselineSet?) : AlignItems(baselineSet.toValue())
    class OverflowAlignment(strategy: OverflowStrategy, position: AlignItemsPosition) : AlignItems(strategy.toValue(position.value))

    companion object {
        // Basic
        val Normal get() = AlignItemsKeyword("normal")
        val Stretch get() = AlignItemsKeyword("stretch")

        // Positional
        val Center get() = AlignItemsPosition("center")
        val Start get() = AlignItemsPosition("start")
        val End get() = AlignItemsPosition("end")
        val SelfStart get() = AlignItemsPosition("self-start")
        val SelfEnd get() = AlignItemsPosition("self-end")
        val FlexStart get() = AlignItemsPosition("flex-start")
        val FlexEnd get() = AlignItemsPosition("flex-end")

        // Baseline
        val Baseline get() = BaselineAlignment(null)
        val FirstBaseline get() = BaselineAlignment(BaselineSet.FIRST)
        val LastBaseline get() = BaselineAlignment(BaselineSet.LAST)

        // Overflow
        fun Safe(position: AlignItemsPosition) = OverflowAlignment(OverflowStrategy.SAFE, position)
        fun Unsafe(position: AlignItemsPosition) = OverflowAlignment(OverflowStrategy.UNSAFE, position)

        // Global
        val Inherit get() = AlignItemsKeyword("inherit")
        val Initial get() = AlignItemsKeyword("initial")
        val Revert get() = AlignItemsKeyword("revert")
        val RevertLayer get() = AlignItemsKeyword("revert-layer")
        val Unset get() = AlignItemsKeyword("unset")
    }
}

fun StyleScope.alignItems(alignItems: AlignItems) {
    property("align-items", alignItems.value)
}

// endregion

// region AlignSelf

// See https://developer.mozilla.org/en-US/docs/Web/CSS/align-self
sealed class AlignSelf(val value: String) {
    open class AlignSelfKeyword(value: String) : AlignSelf(value)
    class AlignSelfPosition(value: String) : AlignSelfKeyword(value)

    class BaselineAlignment(baselineSet: BaselineSet?) : AlignSelf(baselineSet.toValue())
    class OverflowAlignment(strategy: OverflowStrategy, position: AlignSelfPosition) : AlignSelf(strategy.toValue(position.value))

    companion object {
        // Basic
        val Auto get() = AlignSelfKeyword("auto")
        val Normal get() = AlignSelfKeyword("normal")
        val Stretch get() = AlignSelfKeyword("stretch")

        // Positional
        val Center get() = AlignSelfPosition("center")
        val Start get() = AlignSelfPosition("start")
        val End get() = AlignSelfPosition("end")
        val SelfStart get() = AlignSelfPosition("self-start")
        val SelfEnd get() = AlignSelfPosition("self-end")
        val FlexStart get() = AlignSelfPosition("flex-start")
        val FlexEnd get() = AlignSelfPosition("flex-end")

        // Baseline
        val Baseline get() = BaselineAlignment(null)
        val FirstBaseline get() = BaselineAlignment(BaselineSet.FIRST)
        val LastBaseline get() = BaselineAlignment(BaselineSet.LAST)

        // Overflow
        fun Safe(position: AlignSelfPosition) = OverflowAlignment(OverflowStrategy.SAFE, position)
        fun Unsafe(position: AlignSelfPosition) = OverflowAlignment(OverflowStrategy.UNSAFE, position)

        // Global
        val Inherit get() = AlignSelfKeyword("inherit")
        val Initial get() = AlignSelfKeyword("initial")
        val Revert get() = AlignSelfKeyword("revert")
        val RevertLayer get() = AlignSelfKeyword("revert-layer")
        val Unset get() = AlignSelfKeyword("unset")
    }
}

fun StyleScope.alignSelf(alignSelf: AlignSelf) {
    property("align-self", alignSelf.value)
}

// endregion


// region JustifyContent

// See https://developer.mozilla.org/en-US/docs/Web/CSS/justify-content
sealed class JustifyContent(val value: String) {
    open class JustifyContentKeyword(value: String) : JustifyContent(value)
    class JustifyContentPosition(value: String) : JustifyContentKeyword(value)

    class OverflowAlignment(strategy: OverflowStrategy, position: JustifyContentPosition) : JustifyContent(strategy.toValue(position.value))

    companion object {
        // Basic
        val Normal get() = JustifyContentKeyword("normal")

        // Positional
        val Center get() = JustifyContentPosition("center")
        val Start get() = JustifyContentPosition("start")
        val End get() = JustifyContentPosition("end")
        val FlexStart get() = JustifyContentPosition("flex-start")
        val FlexEnd get() = JustifyContentPosition("flex-end")
        val Left get() = JustifyContentPosition("left")
        val Right get() = JustifyContentPosition("right")

        // Distributed
        val SpaceBetween get() = JustifyContentKeyword("space-between")
        val SpaceAround get() = JustifyContentKeyword("space-around")
        val SpaceEvenly get() = JustifyContentKeyword("space-evenly")
        val Stretch get() = JustifyContentKeyword("stretch")

        // Global
        val Inherit get() = JustifyContentKeyword("inherit")
        val Initial get() = JustifyContentKeyword("initial")
        val Revert get() = JustifyContentKeyword("revert")
        val RevertLayer get() = JustifyContentKeyword("revert-layer")
        val Unset get() = JustifyContentKeyword("unset")
    }
}

fun StyleScope.justifyContent(justifyContent: JustifyContent) {
    property("justify-content", justifyContent.value)
}

// endregion

// region JustifyItems

// See https://developer.mozilla.org/en-US/docs/Web/CSS/justify-items
sealed class JustifyItems(val value: String) {
    open class JustifyItemsKeyword(value: String) : JustifyItems(value)
    class JustifyItemsPosition(value: String) : JustifyItemsKeyword(value)

    class BaselineAlignment(baselineSet: BaselineSet?) : JustifyItems(baselineSet.toValue())
    class OverflowAlignment(strategy: OverflowStrategy, position: JustifyItemsPosition) : JustifyItems(strategy.toValue(position.value))

    companion object {
        // Basic
        val Normal get() = JustifyItemsKeyword("normal")
        val Stretch get() = JustifyItemsKeyword("stretch")

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
        val Baseline get() = BaselineAlignment(null)
        val FirstBaseline get() = BaselineAlignment(BaselineSet.FIRST)
        val LastBaseline get() = BaselineAlignment(BaselineSet.LAST)

        // Overflow
        fun Safe(position: JustifyItemsPosition) = OverflowAlignment(OverflowStrategy.SAFE, position)
        fun Unsafe(position: JustifyItemsPosition) = OverflowAlignment(OverflowStrategy.UNSAFE, position)

        // Global
        val Inherit get() = JustifyItemsKeyword("inherit")
        val Initial get() = JustifyItemsKeyword("initial")
        val Revert get() = JustifyItemsKeyword("revert")
        val RevertLayer get() = JustifyItemsKeyword("revert-layer")
        val Unset get() = JustifyItemsKeyword("unset")
    }
}

fun StyleScope.justifyItems(justifyItems: JustifyItems) {
    property("justify-items", justifyItems.value)
}

// endregion

// region JustifySelf

// See https://developer.mozilla.org/en-US/docs/Web/CSS/justify-self
sealed class JustifySelf(val value: String) {
    open class JustifySelfKeyword(value: String) : JustifySelf(value)
    class JustifySelfPosition(value: String) : JustifySelfKeyword(value)

    class BaselineAlignment(baselineSet: BaselineSet?) : JustifySelf(baselineSet.toValue())
    class OverflowAlignment(strategy: OverflowStrategy, position: JustifySelfPosition) : JustifySelf(strategy.toValue(position.value))

    companion object {
        // Basic
        val Auto get() = JustifySelfKeyword("auto")
        val Normal get() = JustifySelfKeyword("normal")
        val Stretch get() = JustifySelfKeyword("stretch")

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
        val Baseline get() = BaselineAlignment(null)
        val FirstBaseline get() = BaselineAlignment(BaselineSet.FIRST)
        val LastBaseline get() = BaselineAlignment(BaselineSet.LAST)

        // Overflow
        fun Safe(position: JustifySelfPosition) = OverflowAlignment(OverflowStrategy.SAFE, position)
        fun Unsafe(position: JustifySelfPosition) = OverflowAlignment(OverflowStrategy.UNSAFE, position)

        // Global
        val Inherit get() = JustifySelfKeyword("inherit")
        val Initial get() = JustifySelfKeyword("initial")
        val Revert get() = JustifySelfKeyword("revert")
        val RevertLayer get() = JustifySelfKeyword("revert-layer")
        val Unset get() = JustifySelfKeyword("unset")
    }
}

fun StyleScope.justifySelf(justifySelf: JustifySelf) {
    property("justify-self", justifySelf.value)
}

// endregion

// region Place methods

fun StyleScope.placeContent(alignContent: AlignContent, justifyContent: JustifyContent) {
    property("place-content", "${alignContent.value} ${justifyContent.value}")
}

fun StyleScope.placeItems(alignItems: AlignItems, justifyItems: JustifyItems) {
    property("place-items","${alignItems.value} ${justifyItems.value}")
}

fun StyleScope.placeSelf(alignSelf: AlignSelf, justifySelf: JustifySelf) {
    placeSelf("${alignSelf.value} ${justifySelf.value}")
}

// endregion
