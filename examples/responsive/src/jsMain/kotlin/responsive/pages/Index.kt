package responsive.pages

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.silk.components.layout.SimpleGrid
import com.varabyte.kobweb.silk.components.layout.numColumns
import org.jetbrains.compose.web.css.px
import responsive.components.layouts.PageLayout

@Page
@Composable
fun MainPage() {
    PageLayout {
        val square = Modifier.size(250.px)
        SimpleGrid(numColumns(2, md = 3), Modifier.gap(10.px)) {
            Box(square.backgroundColor(Colors.Red))
            Box(square.backgroundColor(Colors.Green))
            Box(square.backgroundColor(Colors.Blue))
            Box(square.backgroundColor(Colors.Yellow))
            Box(square.backgroundColor(Colors.Orange))
            Box(square.backgroundColor(Colors.Magenta))
        }
    }
}