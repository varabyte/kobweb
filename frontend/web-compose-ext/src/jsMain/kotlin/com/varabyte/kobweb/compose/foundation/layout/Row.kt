package com.varabyte.kobweb.compose.foundation.layout

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.asAttributeBuilder
import com.varabyte.kobweb.compose.ui.webModifier
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div

class RowScope {
    fun Modifier.align(alignment: Alignment.Vertical) = webModifier {
        style {
            when (alignment) {
                Alignment.Top -> alignSelf(AlignSelf.FlexStart)
                Alignment.CenterVertically -> alignSelf(AlignSelf.Center)
                Alignment.Bottom -> alignSelf(AlignSelf.FlexEnd)
            }
        }
    }
}

@Composable
fun Row(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalAlignment: Alignment.Vertical = Alignment.Top,
    content: @Composable RowScope.() -> Unit
) {
    Div(attrs = modifier.asAttributeBuilder {
        style {
            display(DisplayStyle.Flex)
            flexDirection(FlexDirection.Row)

            when {
                horizontalArrangement === Arrangement.Start -> justifyContent(JustifyContent.FlexStart)
                horizontalArrangement === Arrangement.End -> justifyContent(JustifyContent.FlexEnd)
            }

            when {
                verticalAlignment === Alignment.Top -> alignItems(AlignItems.FlexStart)
                verticalAlignment === Alignment.CenterVertically -> alignItems(AlignItems.Center)
                verticalAlignment === Alignment.Bottom -> alignItems(AlignItems.FlexEnd)
            }
        }
    }) {
        RowScope().content()
    }
}