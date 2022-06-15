package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*

/**
 * Enumeration identifying which baseline set to apply additional alignment to.
 */
enum class BaselineSet {
    FIRST,
    LAST
}

// region JustifySelf

// See https://developer.mozilla.org/en-US/docs/Web/CSS/justify-self
class JustifySelf(val value: String) {

    class BaselineAlignment(val baselineSet: BaselineSet? = null) {

        override fun toString() = buildString {
            if (baselineSet != null) {
                append(baselineSet.name.lowercase())
                append(' ')
            }
            append("baseline")
        }
    }

    class OverflowAlignment(val position: JustifySelf, val strategy: Strategy) {
        enum class Strategy {
            SAFE,
            UNSAFE,
        }

        override fun toString() = "${strategy.name.lowercase()} ${position.value}"
    }

    companion object {
        // Basic
        inline val Auto get() = JustifySelf("auto")
        inline val Normal get() = JustifySelf("normal")
        inline val Stretch get() = JustifySelf("stretch")

        // Positional
        inline val Center get() = JustifySelf("center")
        inline val Start get() = JustifySelf("start")
        inline val End get() = JustifySelf("end")
        inline val FlexStart get() = JustifySelf("flex-start")
        inline val FlexEnd get() = JustifySelf("flex-end")
        inline val SelfStart get() = JustifySelf("self-start")
        inline val SelfEnd get() = JustifySelf("self-end")
        inline val Left get() = JustifySelf("left")
        inline val Right get() = JustifySelf("right")

        // Baseline
        inline val Baseline get() = JustifySelf("baseline")

        // Global
        inline val Inherit get() = JustifySelf("inherit")
        inline val Initial get() = JustifySelf("initial")
        inline val Revert get() = JustifySelf("revert")
        inline val Unset get() = JustifySelf("unset")
    }
}

fun StyleBuilder.justifySelf(justifySelf: JustifySelf) {
    property("justify-self", justifySelf.value)
}

fun StyleBuilder.justifySelf(baseline: JustifySelf.BaselineAlignment) {
    property("justify-self", baseline.toString())
}

fun StyleBuilder.justifySelf(overflow: JustifySelf.OverflowAlignment) {
    property("justify-self", overflow.toString())
}

// endregion

// region JustifyItems

// See https://developer.mozilla.org/en-US/docs/Web/CSS/justify-items
class JustifyItems(val value: String) {
    class BaselineAlignment(val baselineSet: BaselineSet? = null) {
        override fun toString() = buildString {
            if (baselineSet != null) {
                append(baselineSet.name.lowercase())
                append(' ')
            }
            append("baseline")
        }
    }

    class OverflowAlignment(val position: JustifySelf, val strategy: Strategy) {
        enum class Strategy {
            SAFE,
            UNSAFE,
        }

        override fun toString() = "${strategy.name.lowercase()} ${position.value}"
    }

    companion object {
        // Basic
        inline val Normal get() = JustifyItems("normal")
        inline val Stretch get() = JustifyItems("stretch")

        // Positional
        inline val Center get() = JustifyItems("center")
        inline val Start get() = JustifyItems("start")
        inline val End get() = JustifyItems("end")
        inline val FlexStart get() = JustifyItems("flex-start")
        inline val FlexEnd get() = JustifyItems("flex-end")
        inline val SelfStart get() = JustifyItems("self-start")
        inline val SelfEnd get() = JustifyItems("self-end")
        inline val Left get() = JustifyItems("left")
        inline val Right get() = JustifyItems("right")

        // Baseline
        inline val Baseline get() = JustifyItems("baseline")

        // Global
        inline val Inherit get() = JustifyItems("inherit")
        inline val Initial get() = JustifyItems("initial")
        inline val Revert get() = JustifyItems("revert")
        inline val Unset get() = JustifyItems("unset")
    }
}

fun StyleBuilder.justifyItems(justifyItems: JustifyItems) {
    property("justify-items", justifyItems.value)
}

fun StyleBuilder.justifyItems(baseline: JustifyItems.BaselineAlignment) {
    property("justify-items", baseline.toString())
}

fun StyleBuilder.justifyItems(overflow: JustifyItems.OverflowAlignment) {
    property("justify-items", overflow.toString())
}

// endregion

fun StyleBuilder.placeContent(alignContent: AlignContent, justifyContent: JustifyContent) {
    property("place-content", "${alignContent.value} ${justifyContent.value}")
}

fun StyleBuilder.placeSelf(alignSelf: AlignSelf, justifySelf: JustifySelf) {
    placeSelf( "${alignSelf.value} ${justifySelf.value}")
}