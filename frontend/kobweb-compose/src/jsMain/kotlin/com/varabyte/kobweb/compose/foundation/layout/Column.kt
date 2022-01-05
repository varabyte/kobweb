package com.varabyte.kobweb.compose.foundation.layout

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.style.toClassName
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.asAttributeBuilder
import com.varabyte.kobweb.compose.ui.attrModifier
import org.jetbrains.compose.web.dom.Div

class ColumnScope {
    fun Modifier.align(alignment: Alignment.Horizontal) = attrModifier {
        classes("${alignment.toClassName()}-self")
    }
}

@Composable
fun Column(
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: @Composable ColumnScope.() -> Unit
) {
    Div(modifier.asAttributeBuilder {
        classes("kobweb-col", verticalArrangement.toClassName(), horizontalAlignment.toClassName())
    }) {
        ColumnScope().content()
    }
}