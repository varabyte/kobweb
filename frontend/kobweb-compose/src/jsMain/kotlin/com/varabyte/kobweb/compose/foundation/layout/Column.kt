package com.varabyte.kobweb.compose.foundation.layout

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.dom.ElementRefScope
import com.varabyte.kobweb.compose.dom.registerRefScope
import com.varabyte.kobweb.compose.style.toClassName
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.attrsModifier
import com.varabyte.kobweb.compose.ui.modifiers.*
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
 * NOTE: This modifier sets attribute properties and can therefore not be used within ComponentStyles.
 */
fun Modifier.asColumn(
    verticalArrangement: Arrangement.Vertical = ColumnDefaults.VerticalArrangement,
    horizontalAlignment: Alignment.Horizontal = ColumnDefaults.HorizontalAlignment,
) = this.classNames("kobweb-col", verticalArrangement.toClassName(), horizontalAlignment.toClassName())

@Composable
fun Column(
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = ColumnDefaults.VerticalArrangement,
    horizontalAlignment: Alignment.Horizontal = ColumnDefaults.HorizontalAlignment,
    ref: ElementRefScope<HTMLElement>? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Div(modifier.asColumn(verticalArrangement, horizontalAlignment).toAttrs()) {
        registerRefScope(ref)
        ColumnScopeInstance.content()
    }
}
