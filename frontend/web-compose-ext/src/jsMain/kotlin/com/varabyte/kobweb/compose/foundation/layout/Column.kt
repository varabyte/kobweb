package com.varabyte.kobweb.compose.foundation.layout

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.asAttributeBuilder
import com.varabyte.kobweb.compose.ui.styleModifier
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.AlignSelf
import org.jetbrains.compose.web.css.DisplayStyle
import org.jetbrains.compose.web.css.FlexDirection
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.css.alignItems
import org.jetbrains.compose.web.css.alignSelf
import org.jetbrains.compose.web.css.display
import org.jetbrains.compose.web.css.flexDirection
import org.jetbrains.compose.web.css.justifyContent
import org.jetbrains.compose.web.dom.Div

class ColumnScope {
    fun Modifier.align(alignment: Alignment.Horizontal) = styleModifier {
        when (alignment) {
            Alignment.Start -> alignSelf(AlignSelf.FlexStart)
            Alignment.CenterHorizontally -> alignSelf(AlignSelf.Center)
            Alignment.End -> alignSelf(AlignSelf.FlexEnd)
        }
    }
}

@Composable
fun Column(
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: @Composable ColumnScope.() -> Unit
) {
    Div(attrs = modifier.asAttributeBuilder {
        style {
            display(DisplayStyle.Flex)
            flexDirection(FlexDirection.Column)

            when {
                verticalArrangement === Arrangement.Top -> justifyContent(JustifyContent.FlexStart)
                verticalArrangement === Arrangement.Center -> justifyContent(JustifyContent.Center)
                verticalArrangement === Arrangement.Bottom -> justifyContent(JustifyContent.FlexEnd)
            }

            when {
                horizontalAlignment === Alignment.Start -> alignItems(AlignItems.FlexStart)
                horizontalAlignment === Alignment.CenterHorizontally -> alignItems(AlignItems.Center)
                horizontalAlignment === Alignment.End -> alignItems(AlignItems.FlexEnd)
            }
        }
    }) {
        ColumnScope().content()
    }
}