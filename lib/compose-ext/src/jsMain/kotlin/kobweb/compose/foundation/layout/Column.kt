package kobweb.compose.foundation.layout

import androidx.compose.runtime.Composable
import kobweb.compose.ui.Alignment
import kobweb.compose.ui.Modifier
import kobweb.compose.ui.asAttributeBuilder
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div

@Composable
fun Column(
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: @Composable () -> Unit
) {
    Div(attrs = modifier.asAttributeBuilder {
        style {
            display(DisplayStyle.Flex)
            flexDirection(FlexDirection.Column)

            when {
                verticalArrangement === Arrangement.Top -> justifyContent(JustifyContent.FlexStart)
                verticalArrangement === Arrangement.Bottom -> justifyContent(JustifyContent.FlexEnd)
            }

            when {
                horizontalAlignment === Alignment.Start -> alignItems(AlignItems.FlexStart)
                horizontalAlignment === Alignment.CenterHorizontally -> alignItems(AlignItems.Center)
                horizontalAlignment === Alignment.End -> alignItems(AlignItems.FlexEnd)
            }
        }
    }) {
        content()
    }
}