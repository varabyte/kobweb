package com.varabyte.kobweb.silk.components.layout

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.dom.ElementRefScope
import com.varabyte.kobweb.compose.dom.registerRefScope
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.compose.ui.modifiers.display
import com.varabyte.kobweb.compose.ui.styleModifier
import com.varabyte.kobweb.silk.components.style.ComponentStyle
import com.varabyte.kobweb.silk.components.style.ComponentVariant
import com.varabyte.kobweb.silk.components.style.base
import com.varabyte.kobweb.silk.components.style.breakpoint.Breakpoint
import com.varabyte.kobweb.silk.components.style.breakpoint.ResponsiveValues
import com.varabyte.kobweb.silk.components.style.toModifier
import com.varabyte.kobweb.silk.ui.thenIf
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.w3c.dom.HTMLElement

// Note: We restrict the number of columns supported by this widget because we have to statically predefine
// (num breakpoints) * (num columns) style variants (and num breakpoints is 5).
// I'm trying to be conservative for now, but if your project needs more than this, consider pinging me at
// https://github.com/varabyte/kobweb/issues/154
// In a pinch, you can fork this file into your own code and increase the column count in that version.
private const val MAX_COLUMN_COUNT = 5

val SimpleGridStyle by ComponentStyle.base(prefix = "silk") {
    Modifier.display(DisplayStyle.Grid)
}

private val SimpleGridColumnVariants: Map<Breakpoint, Map<Int, ComponentVariant>> = run {
    Breakpoint.values()
        .associateWith { breakpoint ->
            val isBaseVariant = breakpoint == Breakpoint.ZERO
            val name = if (isBaseVariant) "base" else breakpoint.toString().lowercase()

            val variants = (0 until MAX_COLUMN_COUNT)
                .associate { i ->
                    val numColumns = i + 1
                    val gridModifier = Modifier.styleModifier {
                        gridTemplateColumns("repeat($numColumns, 1fr)")
                    }
                    numColumns to SimpleGridStyle.addVariant("$name-$numColumns") {
                        if (isBaseVariant) {
                            base { gridModifier }
                        } else {
                            breakpoint { gridModifier }
                        }
                    }
                }

            variants
        }
}

/**
 * A convenience function for generating a [ResponsiveValues] instance to be consumed by [SimpleGrid].
 *
 * See the header docs for that method for more details.
 */
fun numColumns(base: Int, sm: Int = base, md: Int = sm, lg: Int = md, xl: Int = lg) =
    ResponsiveValues(base, sm, md, lg, xl)

/**
 * A widget making it easy to create a common case of responsive grids, specifically one where you simply specify the
 * number of columns and then its contents will flow to a new row automatically.
 *
 * Children of the Grid will be auto-slotted based on how many columns you specified
 *
 * ```
 * SimpleGrid(numColumns(2)) {
 *   Box(...) // Row 0, Col 0
 *   Box(...) // Row 0, Col 1
 *   Box(...) // Row 1, Col 0
 * }
 * ```
 *
 * The [numColumns] parameter actually takes accepts responsive values, so that the behavior can change as the
 * screen size changes:
 *
 * ```
 * SimpleGrid(numColumns(2, md = 3)) { ... }
 * ```
 *
 * Above, that will create a grid with two columns in smaller layouts (mobile, tablet) and 3 columns in larger ones
 * (desktop).
 */
@Composable
fun SimpleGrid(
    numColumns: ResponsiveValues<Int>,
    modifier: Modifier = Modifier,
    variant: ComponentVariant? = null,
    ref: ElementRefScope<HTMLElement>? = null,
    content: @Composable () -> Unit
) {
    require(numColumns.base <= MAX_COLUMN_COUNT && numColumns.sm <= MAX_COLUMN_COUNT && numColumns.md <= MAX_COLUMN_COUNT && numColumns.lg <= MAX_COLUMN_COUNT && numColumns.xl <= MAX_COLUMN_COUNT) {
        "SimpleGrid supports at most $MAX_COLUMN_COUNT columns. If you need more than this, consider pinging https://github.com/varabyte/kobweb/issues/154."
    }

    Div(
        attrs = SimpleGridStyle
            .toModifier(
                *buildList {
                    add(variant)
                    // Breakpoint.ZERO is special case used to mean "base" in this case
                    add(SimpleGridColumnVariants.getValue(Breakpoint.ZERO).getValue(numColumns.base))
                    if (numColumns.sm != numColumns.base) {
                        add(SimpleGridColumnVariants.getValue(Breakpoint.SM).getValue(numColumns.sm))
                    }
                    if (numColumns.md != numColumns.sm) {
                        add(SimpleGridColumnVariants.getValue(Breakpoint.MD).getValue(numColumns.md))
                    }
                    if (numColumns.lg != numColumns.md) {
                        add(SimpleGridColumnVariants.getValue(Breakpoint.LG).getValue(numColumns.lg))
                    }
                    if (numColumns.xl != numColumns.lg) {
                        add(SimpleGridColumnVariants.getValue(Breakpoint.XL).getValue(numColumns.xl))
                    }
                }.toTypedArray()
            )
            .then(modifier)
            .toAttrs()
    ) {
        registerRefScope(ref)
        content()
    }
}