package responsive.pages

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.silk.components.layout.SimpleGrid
import com.varabyte.kobweb.silk.components.layout.numColumns
import com.varabyte.kobweb.silk.components.style.breakpoint.Breakpoint
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.theme.breakpoint.rememberBreakpoint
import org.jetbrains.compose.web.css.*
import responsive.components.layouts.PageLayout

@Page
@Composable
fun MainPage() {
    PageLayout {
        val bp by rememberBreakpoint()
        val msg = when {
            bp >= Breakpoint.MD -> "This page is currently in desktop mode. Reduce width for mobile mode."
            else -> "This page is currently in mobile mode. Increase width for desktop mode."
        }
        SpanText(msg, Modifier.margin(20.px))

        val square = Modifier.size(200.px)
        SimpleGrid(numColumns(2, md = 3), Modifier.gap(10.px)) {
            Box(square.backgroundColor(Colors.Red))
            Box(square.backgroundColor(Colors.Orange))
            Box(square.backgroundColor(Colors.Yellow))
            Box(square.backgroundColor(Colors.Green))
            Box(square.backgroundColor(Colors.Blue))
            Box(square.backgroundColor(Colors.Purple))
        }
    }
}