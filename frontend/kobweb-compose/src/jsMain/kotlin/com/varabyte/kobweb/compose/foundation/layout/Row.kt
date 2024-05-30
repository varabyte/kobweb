package com.varabyte.kobweb.compose.foundation.layout

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.dom.ElementRefScope
import com.varabyte.kobweb.compose.dom.registerRefScope
import com.varabyte.kobweb.compose.style.ArrangeSpacedByValue
import com.varabyte.kobweb.compose.style.toClassName
import com.varabyte.kobweb.compose.style.toClasses
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.attrsModifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.thenIf
import com.varabyte.kobweb.compose.ui.toAttrs
import org.jetbrains.compose.web.dom.Div
import org.w3c.dom.HTMLElement

@LayoutScopeMarker
interface RowScope : FlexScope {
    fun Modifier.align(alignment: Alignment.Vertical) = attrsModifier {
        classes("${alignment.toClassName()}-self")
    }
}

internal object RowScopeInstance : RowScope

object RowDefaults {
    val HorizontalArrangement: Arrangement.Horizontal = Arrangement.Start
    val VerticalAlignment: Alignment.Vertical = Alignment.Top
}

/**
 * Add classes that tell the browser to display this element as a row.
 *
 * This method is public as there may occasionally be cases where users could benefit from using this, but in general
 * you shouldn't reach for this unless you know what you're doing.
 *
 * NOTE: This modifier sets attribute properties and can therefore not be used within CssStyles.
 */
fun Modifier.rowClasses(
    horizontalArrangement: Arrangement.Horizontal = RowDefaults.HorizontalArrangement,
    verticalAlignment: Alignment.Vertical = RowDefaults.VerticalAlignment,
) = this
    .classNames("kobweb-row", *horizontalArrangement.toClasses(), verticalAlignment.toClassName())

@Composable
fun Row(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = RowDefaults.HorizontalArrangement,
    verticalAlignment: Alignment.Vertical = RowDefaults.VerticalAlignment,
    ref: ElementRefScope<HTMLElement>? = null,
    content: @Composable RowScope.() -> Unit
) {
    Div(
        attrs = modifier
            .rowClasses(horizontalArrangement, verticalAlignment)
            .thenIf(
                horizontalArrangement is SpacedAligned,
            ) {
                Modifier.setVariable(ArrangeSpacedByValue, horizontalArrangement.spacing)
            }
            .toAttrs(),
    ) {
        registerRefScope(ref)
        RowScopeInstance.content()
    }
}
