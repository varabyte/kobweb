package helloworld

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.height
import com.varabyte.kobweb.compose.ui.width
import com.varabyte.kobweb.core.App
import com.varabyte.kobweb.core.DefaultApp
import com.varabyte.kobweb.silk.components.layout.Surface
import com.varabyte.kobweb.silk.SilkApp
import com.varabyte.kobweb.silk.theme.SilkTheme
import com.varabyte.kobweb.silk.theme.colors.rememberColorMode
import org.jetbrains.compose.web.css.height
import org.jetbrains.compose.web.css.vh
import org.jetbrains.compose.web.css.vw
import org.jetbrains.compose.web.css.width
import org.jetbrains.compose.web.dom.Div

@App
@Composable
fun MyApp(content: @Composable () -> Unit) {
    SilkApp {
        SilkTheme {
            Surface(Modifier.width(100.vw).height(100.vh)) {
                content()
            }
        }
    }
}