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
import org.jetbrains.compose.web.css.FlexWrap
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.css.alignItems
import org.jetbrains.compose.web.css.alignSelf
import org.jetbrains.compose.web.css.display
import org.jetbrains.compose.web.css.flexDirection
import org.jetbrains.compose.web.css.flexWrap
import org.jetbrains.compose.web.css.justifyContent
import org.jetbrains.compose.web.dom.Div

class RowScope {
    fun Modifier.align(alignment: Alignment.Vertical) = styleModifier {
        when (alignment) {
            Alignment.Top -> alignSelf(AlignSelf.FlexStart)
            Alignment.CenterVertically -> alignSelf(AlignSelf.Center)
            Alignment.Bottom -> alignSelf(AlignSelf.FlexEnd)
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
            flexWrap(FlexWrap.Wrap)

            when {
                horizontalArrangement === Arrangement.Start -> justifyContent(JustifyContent.FlexStart)
                horizontalArrangement === Arrangement.Center -> justifyContent(JustifyContent.Center)
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