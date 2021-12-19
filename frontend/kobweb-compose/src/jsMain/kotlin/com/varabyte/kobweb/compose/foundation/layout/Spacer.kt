package com.varabyte.kobweb.compose.foundation.layout

import androidx.compose.runtime.*
import org.jetbrains.compose.web.css.flexGrow
import org.jetbrains.compose.web.dom.Div

/**
 * An element which grows to consume all remaining space in a [Row] or [Column].
 */
@Composable
fun Spacer() {
    Div(attrs = { classes("kobweb-spacer") })
}