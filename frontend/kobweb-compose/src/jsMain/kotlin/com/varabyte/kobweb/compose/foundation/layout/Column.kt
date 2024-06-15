package com.varabyte.kobweb.compose.foundation.layout

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.dom.ElementRefScope
import com.varabyte.kobweb.compose.dom.registerRefScope
import com.varabyte.kobweb.compose.style.ArrangeSpacedByVar
import com.varabyte.kobweb.compose.style.toClassName
import com.varabyte.kobweb.compose.style.toClassNames
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.attrsModifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.thenIf
import com.varabyte.kobweb.compose.ui.toAttrs
import org.jetbrains.compose.web.dom.Div
import org.w3c.dom.HTMLElement

@LayoutScopeMarker
interface ColumnScope : FlexScope {
    fun Modifier.align(alignment: Alignment.Horizontal) = attrsModifier {
        classes("${alignment.toClassName()}-self")
    }
}

internal object ColumnScopeInstance : ColumnScope

object ColumnDefaults {
    val VerticalArrangement: Arrangement.Vertical = Arrangement.Top
    val HorizontalAlignment: Alignment.Horizontal = Alignment.Start
}

/**
 * Add classes that tell the browser to display this element as a column.
 *
 * This method is public as there may occasionally be cases where users could benefit from using this, but in general
 * you shouldn't reach for this unless you know what you're doing.
 *
 * NOTE: This modifier sets attribute properties and can therefore not be used within CssStyles.
 */
fun Modifier.columnClasses(
    verticalArrangement: Arrangement.Vertical = ColumnDefaults.VerticalArrangement,
    horizontalAlignment: Alignment.Horizontal = ColumnDefaults.HorizontalAlignment,
) = this.classNames("kobweb-col", *verticalArrangement.toClassNames(), horizontalAlignment.toClassName())

@Composable
fun Column(
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = ColumnDefaults.VerticalArrangement,
    horizontalAlignment: Alignment.Horizontal = ColumnDefaults.HorizontalAlignment,
    ref: ElementRefScope<HTMLElement>? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Div(
        attrs = modifier
            .columnClasses(verticalArrangement, horizontalAlignment)
            .thenIf(
                verticalArrangement is SpacedAligned,
            ) {
                Modifier.setVariable(ArrangeSpacedByVar, verticalArrangement.spacing)
            }
            .toAttrs(),
    ) {
        registerRefScope(ref)
        ColumnScopeInstance.content()
    }
}
