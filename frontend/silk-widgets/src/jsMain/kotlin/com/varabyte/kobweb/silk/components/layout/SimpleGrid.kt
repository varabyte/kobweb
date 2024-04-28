package com.varabyte.kobweb.silk.components.layout

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.dom.ElementRefScope
import com.varabyte.kobweb.compose.dom.registerRefScope
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.silk.style.breakpoint.Breakpoint
import com.varabyte.kobweb.silk.style.breakpoint.ResponsiveValues
import com.varabyte.kobweb.silk.style.component.ComponentKind
import com.varabyte.kobweb.silk.style.component.ComponentStyle
import com.varabyte.kobweb.silk.style.component.ComponentVariant
import com.varabyte.kobweb.silk.style.component.toModifier
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.w3c.dom.HTMLElement

private val columnVariables = Breakpoint.entries.associateWith { breakpoint ->
    StyleVariable.NumberValue<Int>("simple-grid-col-count-${breakpoint.name.lowercase()}", prefix = "silk")
}

interface SimpleGridKind : ComponentKind

val SimpleGridStyle = ComponentStyle<SimpleGridKind> {
    base {
        Modifier.display(DisplayStyle.Grid)
    }
    columnVariables.forEach { (breakpoint, variable) ->
        breakpoint {
            Modifier.gridTemplateColumns { repeat(variable.value()) { size(1.fr) } }
        }
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
 * Children of the Grid will be auto-slotted based on how many columns you specified:
 *
 * ```
 * SimpleGrid(numColumns(2)) {
 *   Box(...) // Row 0, Col 0
 *   Box(...) // Row 0, Col 1
 *   Box(...) // Row 1, Col 0
 * }
 * ```
 *
 * The [numColumns] parameter accepts responsive values, so that the behavior can change as the
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
    variant: ComponentVariant<SimpleGridKind>? = null,
    ref: ElementRefScope<HTMLElement>? = null,
    content: @Composable () -> Unit
) {
    Div(
        attrs = SimpleGridStyle.toModifier(variant)
            .setVariable(columnVariables.getValue(Breakpoint.ZERO), numColumns.base)
            .setVariable(columnVariables.getValue(Breakpoint.SM), numColumns.sm)
            .setVariable(columnVariables.getValue(Breakpoint.MD), numColumns.md)
            .setVariable(columnVariables.getValue(Breakpoint.LG), numColumns.lg)
            .setVariable(columnVariables.getValue(Breakpoint.XL), numColumns.xl)
            .then(modifier)
            .toAttrs()
    ) {
        registerRefScope(ref)
        content()
    }
}
