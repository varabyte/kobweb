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
    content: @Composable () -> Unit = {}
) {
    Div(attrs = modifier.asAttributeBuilder {
        style {
            // The Compose "Box" concept means: all children should be stacked one of top of the other. We do this by
            // setting the current element to grid but then jam all of its children into its top-left (and only) cell.
            display(DisplayStyle.Grid)
            gridTemplateColumns("1fr")
            gridTemplateRows("1fr")

            when {
                // justify in grid means "row" while align means "col"
                contentAlignment === Alignment.TopStart -> {
                    alignItems(AlignItems.Start)
                    justifyItems(AlignItems.Start.value)
                }
                contentAlignment === Alignment.TopCenter -> {
                    alignItems(AlignItems.Start)
                    justifyItems(AlignItems.Center.value)
                }
                contentAlignment === Alignment.TopEnd -> {
                    alignItems(AlignItems.Start)
                    justifyItems(AlignItems.End.value)
                }
                contentAlignment === Alignment.CenterStart -> {
                    alignItems(AlignItems.Center)
                    justifyItems(AlignItems.Start.value)
                }
                contentAlignment === Alignment.Center -> {
                    alignItems(AlignItems.Center)
                    justifyItems(AlignItems.Center.value)
                }
                contentAlignment === Alignment.CenterEnd -> {
                    justifyItems(AlignItems.End.value)
                    alignItems(AlignItems.Center)
                }
                contentAlignment === Alignment.BoottomStart -> {
                    justifyItems(AlignItems.Start.value)
                    alignItems(AlignItems.End)
                }
                contentAlignment === Alignment.BoottomCenter -> {
                    justifyItems(AlignItems.Center.value)
                    alignItems(AlignItems.End)
                }
                contentAlignment === Alignment.BoottomEnd -> {
                    justifyItems(AlignItems.End.value)
                    alignItems(AlignItems.End)
                }
            }
        }
    }) {
        content()
    }
}