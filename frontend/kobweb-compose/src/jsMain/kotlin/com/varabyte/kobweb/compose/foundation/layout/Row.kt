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

class RowScope {
    fun Modifier.align(alignment: Alignment.Vertical) = attrsModifier {
        classes("${alignment.toClassName()}-self")
    }

    // Convenient remapping to "flexGrow" for users coming from the world of Android
    fun Modifier.weight(value: Number) = this.flexGrow(value)
}

@Composable
fun Row(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalAlignment: Alignment.Vertical = Alignment.Top,
    ref: ElementRefScope<HTMLElement>? = null,
    content: @Composable RowScope.() -> Unit
) {
    Div(modifier.toAttrs {
        classes("kobweb-row", horizontalArrangement.toClassName(), verticalAlignment.toClassName())
    }) {
        registerRefScope(ref)
        RowScope().content()
    }
}
