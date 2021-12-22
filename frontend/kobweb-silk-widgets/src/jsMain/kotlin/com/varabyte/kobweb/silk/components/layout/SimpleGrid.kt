package com.varabyte.kobweb.silk.components.layout

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.asAttributeBuilder
import com.varabyte.kobweb.compose.ui.modifiers.display
import com.varabyte.kobweb.compose.ui.styleModifier
import com.varabyte.kobweb.silk.components.style.ComponentStyle
import com.varabyte.kobweb.silk.components.style.ComponentVariant
import com.varabyte.kobweb.silk.components.style.base
import com.varabyte.kobweb.silk.components.style.breakpoint.Breakpoint
import com.varabyte.kobweb.silk.components.style.breakpoint.ResponsiveValues
import com.varabyte.kobweb.silk.components.style.toModifier
import org.jetbrains.compose.web.css.DisplayStyle
import org.jetbrains.compose.web.css.display
import org.jetbrains.compose.web.css.gridTemplateColumns
import org.jetbrains.compose.web.dom.Div

private const val MAX_COLUMN_COUNT = 4

val SimpleGridStyle = ComponentStyle.base("silk-simple-grid") {
    Modifier.display(DisplayStyle.Grid)
}

private val SimpleGridColumnVariants: Map<Breakpoint?, Map<Int, ComponentVariant>> = run {
    (listOf(null) + Breakpoint.values())
        .associateWith { breakpoint ->
            val name = breakpoint?.toString()?.lowercase() ?: "base"
            val variants = (0 until MAX_COLUMN_COUNT)
                .associate { i ->
                    val numColumns = i + 1
                    val gridModifier = Modifier.styleModifier {
                        gridTemplateColumns("repeat($numColumns, 1fr)")
                    }
                    numColumns to SimpleGridStyle.addVariant("$name-$numColumns") {
                        if (breakpoint == null) {
                            base { gridModifier }
                        } else {
                            breakpoint { gridModifier }
                        }
                    }
                }

            variants
        }
}

fun numColumns(base: Int, sm: Int = base, md: Int = sm, lg: Int = md, xl: Int = lg) =
    ResponsiveValues(base, sm, md, lg, xl)

/**
 * A widget making it easy to create a common case of responsive grids, specifically one where you simply specify the
 * number of columns and then its contents will flow to a new row automatically.
 *
 * To add a new child to this grid, use the [SimpleGridScope.Cell] composable:
 *
 * ```
 * SimpleGrid(numColumns(2)) {
 *   Cell { Box(...) } // Row 0, Col 0
 *   Cell { Box(...) } // Row 0, Col 1
 *   Cell { Box(...) } // Row 1, Col 0
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
    content: @Composable () -> Unit
) {
    Div(
        attrs = SimpleGridStyle.toModifier(variant).then(modifier).asAttributeBuilder {
            // null is special case to mean "base" in this case
            classes(SimpleGridColumnVariants.getValue(null).getValue(numColumns.base).style.name)
            if (numColumns.sm != numColumns.base) {
                classes(SimpleGridColumnVariants.getValue(Breakpoint.SM).getValue(numColumns.sm).style.name)
            }
            if (numColumns.md != numColumns.sm) {
                classes(SimpleGridColumnVariants.getValue(Breakpoint.MD).getValue(numColumns.md).style.name)
            }
            if (numColumns.lg != numColumns.md) {
                classes(SimpleGridColumnVariants.getValue(Breakpoint.LG).getValue(numColumns.lg).style.name)
            }
            if (numColumns.xl != numColumns.lg) {
                classes(SimpleGridColumnVariants.getValue(Breakpoint.XL).getValue(numColumns.xl).style.name)
            }
        }
    ) {
        content()
    }
}