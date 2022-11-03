package com.varabyte.kobweb.compose.foundation.layout

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.dom.ElementRefListener
import com.varabyte.kobweb.compose.dom.registerRefListener
import com.varabyte.kobweb.compose.style.toClassName
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.asAttributesBuilder
import com.varabyte.kobweb.compose.ui.attrsModifier
import org.jetbrains.compose.web.dom.Div
import org.w3c.dom.HTMLElement

class BoxScope {
    fun Modifier.align(alignment: Alignment) = attrsModifier {
        classes("${alignment.toClassName()}-self")
    }
}

@Composable
fun Box(
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.TopStart,
    refListener: ElementRefListener<HTMLElement>? = null,
    content: @Composable BoxScope.() -> Unit = {}
) {
    Div(attrs = modifier.asAttributesBuilder {
        classes("kobweb-box", contentAlignment.toClassName())
        registerRefListener(refListener)
    }) {
        BoxScope().content()
    }
}