package com.varabyte.kobweb.compose.style

import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
import com.varabyte.kobweb.compose.foundation.layout.SpacedAligned
import org.jetbrains.compose.web.css.*

internal const val KOBWEB_ARRANGE_BOTTOM = "kobweb-arrange-bottom"
internal const val KOBWEB_ARRANGE_CENTER = "kobweb-arrange-center"
internal const val KOBWEB_ARRANGE_END = "kobweb-arrange-end"
internal const val KOBWEB_ARRANGE_FROM_STYLE = "kobweb-arrange-from-style"
internal const val KOBWEB_ARRANGE_SPACED_BY = "kobweb-arrange-spaced-by"
internal const val KOBWEB_ARRANGE_SPACE_AROUND = "kobweb-arrange-space-around"
internal const val KOBWEB_ARRANGE_SPACE_BETWEEN = "kobweb-arrange-space-between"
internal const val KOBWEB_ARRANGE_SPACE_EVENLY = "kobweb-arrange-space-evenly"
internal const val KOBWEB_ARRANGE_START = "kobweb-arrange-start"
internal const val KOBWEB_ARRANGE_TOP = "kobweb-arrange-top"

internal val ArrangeSpacedByVar by StyleVariable<CSSLengthOrPercentageNumericValue>(prefix = "kobweb")

internal fun KobwebComposeStyleSheet.initArrangeSpacedByStyle() {
    ".kobweb-row.${KOBWEB_ARRANGE_SPACED_BY}" { gap(ArrangeSpacedByVar.value()) }
    ".kobweb-col.${KOBWEB_ARRANGE_SPACED_BY}" { gap(ArrangeSpacedByVar.value()) }
}

fun Arrangement.Horizontal.toClassNames() = when (this) {
    Arrangement.End -> arrayOf(KOBWEB_ARRANGE_END)
    Arrangement.Start -> arrayOf(KOBWEB_ARRANGE_START)
    is Arrangement.HorizontalOrVertical -> this.toClassNames()
}

fun Arrangement.Vertical.toClassNames() = when (this) {
    Arrangement.Top -> arrayOf(KOBWEB_ARRANGE_TOP)
    Arrangement.Bottom -> arrayOf(KOBWEB_ARRANGE_BOTTOM)
    is Arrangement.HorizontalOrVertical -> toClassNames()
}

fun Arrangement.HorizontalOrVertical.toClassNames() = when (this) {
    Arrangement.Center -> arrayOf(KOBWEB_ARRANGE_CENTER)
    Arrangement.SpaceAround -> arrayOf(KOBWEB_ARRANGE_SPACE_AROUND)
    Arrangement.SpaceBetween -> arrayOf(KOBWEB_ARRANGE_SPACE_BETWEEN)
    Arrangement.SpaceEvenly -> arrayOf(KOBWEB_ARRANGE_SPACE_EVENLY)
    Arrangement.FromStyle -> arrayOf(KOBWEB_ARRANGE_FROM_STYLE)
    is SpacedAligned -> classNames
}
