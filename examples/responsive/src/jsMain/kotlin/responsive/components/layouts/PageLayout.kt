package responsive.components.layouts

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.components.style.breakpoint.Breakpoint
import com.varabyte.kobweb.silk.theme.breakpoint.rememberBreakpoint
import org.jetbrains.compose.web.css.*
import responsive.components.sections.NavMenuButton
import responsive.components.sections.NavSideMenu

@Composable
private fun ContentColumn(modifier: Modifier, content: @Composable () -> Unit) {
    Column(
        Modifier.padding(top = 20.px).then(modifier),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        content()
    }
}

@Composable
fun PageLayout(content: @Composable () -> Unit) {
    val bp by rememberBreakpoint()
    if (bp < Breakpoint.MD) {
        NavMenuButton()
        ContentColumn(Modifier.fillMaxSize(), content)
    } else {
        Row(Modifier.fillMaxSize()) {
            NavSideMenu()
            ContentColumn(Modifier.fillMaxHeight().flexGrow(1), content)
        }
    }
}