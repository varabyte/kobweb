// Sealed class private constructors are useful, actually!
// Intentionally make some function names look like constructors
@file:Suppress("RedundantVisibilityModifier", "FunctionName")

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
sealed interface AlignContent : StylePropertyValue {
    sealed interface AlignContentPosition : AlignContent

    companion object : CssGlobalValues<AlignContent> {
        // Basic
        val Normal get() = "normal".unsafeCast<AlignContent>()

        // Positional
        val Center get() = "center".unsafeCast<AlignContentPosition>()
        val Start get() = "start".unsafeCast<AlignContentPosition>()
        val End get() = "end".unsafeCast<AlignContentPosition>()
        val FlexStart get() = "flex-start".unsafeCast<AlignContentPosition>()
        val FlexEnd get() = "flex-end".unsafeCast<AlignContentPosition>()

        // Distributed
        val SpaceBetween get() = "space-between".unsafeCast<AlignContent>()
        val SpaceAround get() = "space-around".unsafeCast<AlignContent>()
        val SpaceEvenly get() = "space-evenly".unsafeCast<AlignContent>()
        val Stretch get() = "stretch".unsafeCast<AlignContent>()

        // Baseline
        val Baseline get() = (null as? BaselineSet?).toValue().unsafeCast<AlignContent>()
        val FirstBaseline get() = BaselineSet.FIRST.toValue().unsafeCast<AlignContent>()
        val LastBaseline get() = BaselineSet.LAST.toValue().unsafeCast<AlignContent>()

        // Overflow
        fun Safe(position: AlignContentPosition) = OverflowStrategy.SAFE.toValue(position).unsafeCast<AlignContent>()
        fun Unsafe(position: AlignContentPosition) = OverflowStrategy.UNSAFE.toValue(position).unsafeCast<AlignContent>()
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
sealed interface AlignItems : StylePropertyValue {
    sealed interface AlignItemsPosition : AlignItems

    companion object : CssGlobalValues<AlignItems> {
        // Basic
        val Normal get() = "normal".unsafeCast<AlignItems>()
        val Stretch get() = "stretch".unsafeCast<AlignItems>()

        // Positional
        val Center get() = "center".unsafeCast<AlignItemsPosition>()
        val Start get() = "start".unsafeCast<AlignItemsPosition>()
        val End get() = "end".unsafeCast<AlignItemsPosition>()
        val SelfStart get() = "self-start".unsafeCast<AlignItemsPosition>()
        val SelfEnd get() = "self-end".unsafeCast<AlignItemsPosition>()
        val FlexStart get() = "flex-start".unsafeCast<AlignItemsPosition>()
        val FlexEnd get() = "flex-end".unsafeCast<AlignItemsPosition>()

        // Baseline
        val Baseline get() = (null as? BaselineSet?).toValue().unsafeCast<AlignItems>()
        val FirstBaseline get() = BaselineSet.FIRST.toValue().unsafeCast<AlignItems>()
        val LastBaseline get() = BaselineSet.LAST.toValue().unsafeCast<AlignItems>()

        // Overflow
        fun Safe(position: AlignItemsPosition): AlignItems = OverflowStrategy.SAFE.toValue(position).unsafeCast<AlignItems>()
        fun Unsafe(position: AlignItemsPosition): AlignItems = OverflowStrategy.UNSAFE.toValue(position).unsafeCast<AlignItems>()
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
sealed interface AlignSelf : StylePropertyValue {
    sealed interface AlignSelfPosition : AlignSelf

    companion object : CssGlobalValues<AlignSelf> {
        // Basic
        val Auto get() = "auto".unsafeCast<AlignSelf>()
        val Normal get() = "normal".unsafeCast<AlignSelf>()
        val Stretch get() = "stretch".unsafeCast<AlignSelf>()

        // Positional
        val Center get() = "center".unsafeCast<AlignSelfPosition>()
        val Start get() = "start".unsafeCast<AlignSelfPosition>()
        val End get() = "end".unsafeCast<AlignSelfPosition>()
        val SelfStart get() = "self-start".unsafeCast<AlignSelfPosition>()
        val SelfEnd get() = "self-end".unsafeCast<AlignSelfPosition>()
        val FlexStart get() = "flex-start".unsafeCast<AlignSelfPosition>()
        val FlexEnd get() = "flex-end".unsafeCast<AlignSelfPosition>()

        // Baseline
        val Baseline get() = (null as? BaselineSet?).toValue().unsafeCast<AlignSelf>()
        val FirstBaseline get() = BaselineSet.FIRST.toValue().unsafeCast<AlignSelf>()
        val LastBaseline get() = BaselineSet.LAST.toValue().unsafeCast<AlignSelf>()

        // Overflow
        fun Safe(position: AlignSelfPosition) = OverflowStrategy.SAFE.toValue(position).unsafeCast<AlignSelf>()
        fun Unsafe(position: AlignSelfPosition) = OverflowStrategy.UNSAFE.toValue(position).unsafeCast<AlignSelf>()
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
sealed interface JustifyContent : StylePropertyValue {
    sealed interface JustifyContentPosition : JustifyContent

    companion object : CssGlobalValues<JustifyContent> {
        // Basic
        val Normal get() = "normal".unsafeCast<JustifyContent>()

        // Positional
        val Center get() = "center".unsafeCast<JustifyContentPosition>()
        val Start get() = "start".unsafeCast<JustifyContentPosition>()
        val End get() = "end".unsafeCast<JustifyContentPosition>()
        val FlexStart get() = "flex-start".unsafeCast<JustifyContentPosition>()
        val FlexEnd get() = "flex-end".unsafeCast<JustifyContentPosition>()
        val Left get() = "left".unsafeCast<JustifyContentPosition>()
        val Right get() = "right".unsafeCast<JustifyContentPosition>()

        // Distributed
        val SpaceBetween get() = "space-between".unsafeCast<JustifyContent>()
        val SpaceAround get() = "space-around".unsafeCast<JustifyContent>()
        val SpaceEvenly get() = "space-evenly".unsafeCast<JustifyContent>()
        val Stretch get() = "stretch".unsafeCast<JustifyContent>()

        // Overflow
        fun Safe(position: JustifyContentPosition) = OverflowStrategy.SAFE.toValue(position).unsafeCast<JustifyContent>()
        fun Unsafe(position: JustifyContentPosition) =
            OverflowStrategy.UNSAFE.toValue(position).unsafeCast<JustifyContent>()
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
sealed interface JustifyItems : StylePropertyValue {
    sealed interface JustifyItemsPosition : JustifyItems

    companion object : CssGlobalValues<JustifyItems> {
        // Basic
        val Normal get() = "normal".unsafeCast<JustifyItems>()
        val Stretch get() = "stretch".unsafeCast<JustifyItems>()

        // Positional
        val Center get() = "center".unsafeCast<JustifyItemsPosition>()
        val Start get() = "start".unsafeCast<JustifyItemsPosition>()
        val End get() = "end".unsafeCast<JustifyItemsPosition>()
        val FlexStart get() = "flex-start".unsafeCast<JustifyItemsPosition>()
        val FlexEnd get() = "flex-end".unsafeCast<JustifyItemsPosition>()
        val SelfStart get() = "self-start".unsafeCast<JustifyItemsPosition>()
        val SelfEnd get() = "self-end".unsafeCast<JustifyItemsPosition>()
        val Left get() = "left".unsafeCast<JustifyItemsPosition>()
        val Right get() = "right".unsafeCast<JustifyItemsPosition>()

        // Baseline
        val Baseline get() = (null as? BaselineSet?).toValue().unsafeCast<JustifyItems>()
        val FirstBaseline get() = BaselineSet.FIRST.toValue().unsafeCast<JustifyItems>()
        val LastBaseline get() = BaselineSet.LAST.toValue().unsafeCast<JustifyItems>()

        // Overflow
        fun Safe(position: JustifyItemsPosition) = OverflowStrategy.SAFE.toValue(position)
        fun Unsafe(position: JustifyItemsPosition) = OverflowStrategy.UNSAFE.toValue(position)
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
sealed interface JustifySelf : StylePropertyValue {
    sealed interface JustifySelfPosition : JustifySelf

    companion object : CssGlobalValues<JustifySelf> {
        // Basic
        val Auto get() = "auto".unsafeCast<JustifySelf>()
        val Normal get() = "normal".unsafeCast<JustifySelf>()
        val Stretch get() = "stretch".unsafeCast<JustifySelf>()

        // Positional  
        val Center get() = "center".unsafeCast<JustifySelfPosition>()
        val Start get() = "start".unsafeCast<JustifySelfPosition>()
        val End get() = "end".unsafeCast<JustifySelfPosition>()
        val SelfStart get() = "self-start".unsafeCast<JustifySelfPosition>()
        val SelfEnd get() = "self-end".unsafeCast<JustifySelfPosition>()
        val FlexStart get() = "flex-start".unsafeCast<JustifySelfPosition>()
        val FlexEnd get() = "flex-end".unsafeCast<JustifySelfPosition>()
        val Left get() = "left".unsafeCast<JustifySelfPosition>()
        val Right get() = "right".unsafeCast<JustifySelfPosition>()

        // Baseline
        val Baseline get() = (null as? BaselineSet?).toValue().unsafeCast<JustifySelf>()
        val FirstBaseline get() = BaselineSet.FIRST.toValue().unsafeCast<JustifySelf>()
        val LastBaseline get() = BaselineSet.LAST.toValue().unsafeCast<JustifySelf>()

        // Overflow
        fun Safe(position: JustifySelfPosition) = OverflowStrategy.SAFE.toValue(position).unsafeCast<JustifySelf>()
        fun Unsafe(position: JustifySelfPosition) = OverflowStrategy.UNSAFE.toValue(position).unsafeCast<JustifySelf>()
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
