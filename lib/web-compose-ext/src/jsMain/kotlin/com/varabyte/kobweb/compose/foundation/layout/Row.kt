package com.varabyte.kobweb.compose.foundation.layout

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.asAttributeBuilder
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div

@Composable
fun Row(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalAlignment: Alignment.Vertical = Alignment.Top,
    content: @Composable () -> Unit
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
        content()
    }
}