package com.varabyte.kobweb.compose.ui.modifiers

import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.styleModifier
import org.jetbrains.compose.web.ExperimentalComposeWebApi
import org.jetbrains.compose.web.css.*

@ExperimentalComposeWebApi
fun Modifier.transform(transformContext: TransformBuilder.() -> Unit) = styleModifier {
    transform(transformContext)
}

