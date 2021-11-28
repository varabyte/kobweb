package responsive.pages

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.background
import com.varabyte.kobweb.compose.ui.modifiers.size
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
        SimpleGrid(numColumns(2, md = 3)) {
            Cell {
                Box(square.background(Colors.Red))
            }
            Cell {
                Box(square.background(Colors.Green))
            }
            Cell {
                Box(square.background(Colors.Blue))
            }
            Cell {
                Box(square.background(Colors.Yellow))
            }
            Cell {
                Box(square.background(Colors.Orange))
            }
            Cell {
                Box(square.background(Colors.Magenta))
            }
        }
    }
}