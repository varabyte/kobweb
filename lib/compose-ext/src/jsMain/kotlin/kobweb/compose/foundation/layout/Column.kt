package kobweb.compose.foundation.layout

import androidx.compose.runtime.Composable
import org.jetbrains.compose.common.foundation.layout.Arrangement
import org.jetbrains.compose.common.internal.castOrCreate
import org.jetbrains.compose.common.ui.Alignment
import org.jetbrains.compose.common.ui.Modifier
import org.jetbrains.compose.common.ui.asAttributeBuilderApplier
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div

@Composable
fun Column(
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: @Composable () -> Unit
) {
    Div(attrs = modifier.castOrCreate().apply {
        add {
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
    }.asAttributeBuilderApplier()) {
        content()
    }
}