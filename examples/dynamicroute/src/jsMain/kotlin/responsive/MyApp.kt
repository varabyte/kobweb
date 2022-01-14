package dynamicroute

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.core.App
import com.varabyte.kobweb.silk.SilkApp
import com.varabyte.kobweb.silk.components.layout.Surface
import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.css.StyleSheet
import org.jetbrains.compose.web.css.fontFamily
import org.jetbrains.compose.web.css.vh
import org.jetbrains.compose.web.css.vw

object MyStyleSheet : StyleSheet() {
    init {
        "body" style {
            fontFamily(
                "-apple-system", "BlinkMacSystemFont", "Segoe UI", "Roboto", "Oxygen", "Ubuntu",
                "Cantarell", "Fira Sans", "Droid Sans", "Helvetica Neue", "sans-serif"
            )
        }
    }
}

@App
@Composable
fun MyApp(content: @Composable () -> Unit) {
    Style(MyStyleSheet)
    SilkApp {
        Surface(Modifier.width(100.vw).height(100.vh)) {
            content()
        }
    }
}