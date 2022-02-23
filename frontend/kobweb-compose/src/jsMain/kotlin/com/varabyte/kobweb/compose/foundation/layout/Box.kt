package com.varabyte.kobweb.compose.foundation.layout

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.style.toClassName
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.asAttributesBuilder
import com.varabyte.kobweb.compose.ui.attrsModifier
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.ElementScope
import org.w3c.dom.HTMLDivElement

class BoxScope {
    fun Modifier.align(alignment: Alignment) = attrsModifier {
        classes("${alignment.toClassName()}-self")
    }
}

@Composable
fun Box(
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.TopStart,
    elementScope: (@Composable ElementScope<HTMLDivElement>.() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit = {}
) {
    Div(attrs = modifier.asAttributesBuilder {
        classes("kobweb-box", contentAlignment.toClassName())
    }) {
        elementScope?.invoke(this)
        BoxScope().content()
    }
}