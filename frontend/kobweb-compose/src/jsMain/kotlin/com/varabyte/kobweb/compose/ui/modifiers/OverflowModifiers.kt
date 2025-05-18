package com.varabyte.kobweb.compose.ui.modifiers

import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.styleModifier
import org.jetbrains.compose.web.css.*

fun Modifier.overflow(overflow: Overflow) = styleModifier {
    overflow(overflow)
}

fun Modifier.overflow(overflowX: Overflow, overflowY: Overflow) = styleModifier {
    overflow(overflowX, overflowY)
}

class OverflowScope internal constructor(private val styleScope: StyleScope) {
    fun x(overflowX: Overflow) = styleScope.overflowX(overflowX)
    fun y(overflowY: Overflow) = styleScope.overflowY(overflowY)
}

fun Modifier.overflow(scope: OverflowScope.() -> Unit) = styleModifier {
    OverflowScope(this).scope()
}

fun Modifier.overflowBlock(overflowBlock: OverflowBlock) = styleModifier {
    overflowBlock(overflowBlock)
}

fun Modifier.overflowInline(overflowInline: OverflowInline) = styleModifier {
    overflowInline(overflowInline)
}

fun Modifier.overflowScrollBehavior(overflowScrollBehavior: OverflowScrollBehavior) = styleModifier {
    overflowScrollBehavior(overflowScrollBehavior)
}

fun Modifier.overflowScrollBehavior(vararg values: OverflowScrollBehavior.Listable) = styleModifier {
    overflowScrollBehavior(OverflowScrollBehavior.list(*values))
}

fun Modifier.overflowScrollBehavior(values: List<OverflowScrollBehavior.Listable>) = styleModifier {
    overflowScrollBehavior(OverflowScrollBehavior.list(*values.toTypedArray()))
}

fun Modifier.overflowWrap(overflowWrap: OverflowWrap) = styleModifier {
    overflowWrap(overflowWrap)
}
