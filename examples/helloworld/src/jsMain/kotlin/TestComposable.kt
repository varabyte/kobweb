import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.css.Color
import org.jetbrains.compose.web.css.backgroundColor
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text

@Composable
fun TestComposable(name: String = "World") {
    Div({
        style {
            backgroundColor(Color.red)
        }
    }
    ) {
        Text("Hello ")
        Text(name)
    }
}