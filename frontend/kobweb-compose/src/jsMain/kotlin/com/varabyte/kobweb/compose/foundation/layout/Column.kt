package com.varabyte.kobweb.compose.foundation.layout

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.dom.ElementRefScope
import com.varabyte.kobweb.compose.dom.registerRefScope
import com.varabyte.kobweb.compose.style.toClassName
import com.varabyte.kobweb.compose.ui.*
import com.varabyte.kobweb.compose.ui.modifiers.flexGrow
import org.jetbrains.compose.web.css.flexGrow
import org.jetbrains.compose.web.dom.Div
import org.w3c.dom.HTMLElement

class ColumnScope {
    fun Modifier.align(alignment: Alignment.Horizontal) = attrsModifier {
        classes("${alignment.toClassName()}-self")
    }

    // Convenient remapping to "flexGrow" for users coming from the world of Android
    fun Modifier.weight(value: Number) = this.flexGrow(value)
}

@Composable
fun Column(
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    ref: ElementRefScope<HTMLElement>? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Div(modifier.toAttrs {
        classes("kobweb-col", verticalArrangement.toClassName(), horizontalAlignment.toClassName())
    }) {
        registerRefScope(ref)
        ColumnScope().content()
    }
}