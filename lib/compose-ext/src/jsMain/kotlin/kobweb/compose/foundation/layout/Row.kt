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
fun Row(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalAlignment: Alignment.Vertical = Alignment.Top,
    content: @Composable () -> Unit
) {
    Div(attrs = modifier.castOrCreate().apply {
        add {
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
    }.asAttributeBuilderApplier()) {
        content()
    }
}