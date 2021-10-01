package com.varabyte.kobweb.compose.foundation.layout

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.asAttributeBuilder
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div

@Composable
fun Box(
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.TopStart,
    content: @Composable () -> Unit
) {
    Div(attrs = modifier.asAttributeBuilder {
        style {
            display(DisplayStyle.Flex)
            flexDirection(FlexDirection.Column)

            when {
                contentAlignment === Alignment.TopStart -> {
                    justifyContent(JustifyContent.FlexStart)
                    alignItems(AlignItems.FlexStart)
                }
                contentAlignment === Alignment.TopCenter -> {
                    justifyContent(JustifyContent.FlexStart)
                    alignItems(AlignItems.Center)
                }
                contentAlignment === Alignment.TopEnd -> {
                    justifyContent(JustifyContent.FlexStart)
                    alignItems(AlignItems.FlexEnd)
                }
                contentAlignment === Alignment.CenterStart -> {
                    justifyContent(JustifyContent.Center)
                    alignItems(AlignItems.FlexStart)
                }
                contentAlignment === Alignment.Center -> {
                    justifyContent(JustifyContent.Center)
                    alignItems(AlignItems.Center)
                }
                contentAlignment === Alignment.CenterEnd -> {
                    justifyContent(JustifyContent.Center)
                    alignItems(AlignItems.FlexEnd)
                }
                contentAlignment === Alignment.BoottomStart -> {
                    justifyContent(JustifyContent.FlexEnd)
                    alignItems(AlignItems.FlexStart)
                }
                contentAlignment === Alignment.BoottomCenter -> {
                    justifyContent(JustifyContent.FlexEnd)
                    alignItems(AlignItems.Center)
                }
                contentAlignment === Alignment.BoottomEnd -> {
                    justifyContent(JustifyContent.FlexEnd)
                    alignItems(AlignItems.FlexEnd)
                }
            }
        }
    }) {
        content()
    }
}