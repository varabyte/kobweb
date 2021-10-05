package com.varabyte.kobweb.compose.foundation.layout

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.asAttributeBuilder
import com.varabyte.kobweb.compose.ui.webModifier
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div

class BoxScope() {
    fun Modifier.align(alignment: Alignment) = webModifier {
        style {
            when (alignment) {
                // justify in grid means "row" while align means "col"
                Alignment.TopStart -> {
                    alignSelf(AlignSelf.Start)
                    justifySelf(AlignSelf.Start.value)
                }
                Alignment.TopCenter -> {
                    alignSelf(AlignSelf.Start)
                    justifySelf(AlignSelf.Center.value)
                }
                Alignment.TopEnd -> {
                    alignSelf(AlignSelf.Start)
                    justifySelf(AlignSelf.End.value)
                }
                Alignment.CenterStart -> {
                    alignSelf(AlignSelf.Center)
                    justifySelf(AlignSelf.Start.value)
                }
                Alignment.Center -> {
                    alignSelf(AlignSelf.Center)
                    justifySelf(AlignSelf.Center.value)
                }
                Alignment.CenterEnd -> {
                    justifySelf(AlignSelf.End.value)
                    alignSelf(AlignSelf.Center)
                }
                Alignment.BoottomStart -> {
                    justifySelf(AlignSelf.Start.value)
                    alignSelf(AlignSelf.End)
                }
                Alignment.BoottomCenter -> {
                    justifySelf(AlignSelf.Center.value)
                    alignSelf(AlignSelf.End)
                }
                Alignment.BoottomEnd -> {
                    justifySelf(AlignSelf.End.value)
                    alignSelf(AlignSelf.End)
                }
            }
        }
    }
}

@Composable
fun Box(
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.TopStart,
    content: @Composable BoxScope.() -> Unit = {}
) {
    Div(attrs = modifier.asAttributeBuilder {
        style {
            // The Compose "Box" concept means: all children should be stacked one of top of the other. We do this by
            // setting the current element to grid but then jam all of its children into its top-left (and only) cell.
            display(DisplayStyle.Grid)
            gridTemplateColumns("1fr")
            gridTemplateRows("1fr")

            when (contentAlignment) {
                // justify in grid means "row" while align means "col"
                Alignment.TopStart -> {
                    alignItems(AlignItems.Start)
                    justifyItems(AlignItems.Start.value)
                }
                Alignment.TopCenter -> {
                    alignItems(AlignItems.Start)
                    justifyItems(AlignItems.Center.value)
                }
                Alignment.TopEnd -> {
                    alignItems(AlignItems.Start)
                    justifyItems(AlignItems.End.value)
                }
                Alignment.CenterStart -> {
                    alignItems(AlignItems.Center)
                    justifyItems(AlignItems.Start.value)
                }
                Alignment.Center -> {
                    alignItems(AlignItems.Center)
                    justifyItems(AlignItems.Center.value)
                }
                Alignment.CenterEnd -> {
                    justifyItems(AlignItems.End.value)
                    alignItems(AlignItems.Center)
                }
                Alignment.BoottomStart -> {
                    justifyItems(AlignItems.Start.value)
                    alignItems(AlignItems.End)
                }
                Alignment.BoottomCenter -> {
                    justifyItems(AlignItems.Center.value)
                    alignItems(AlignItems.End)
                }
                Alignment.BoottomEnd -> {
                    justifyItems(AlignItems.End.value)
                    alignItems(AlignItems.End)
                }
            }
        }
    }) {
        BoxScope().content()
    }
}