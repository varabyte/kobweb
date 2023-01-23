package clock

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.core.App
import com.varabyte.kobweb.silk.init.InitSilk
import com.varabyte.kobweb.silk.init.InitSilkContext
import com.varabyte.kobweb.silk.SilkApp
import com.varabyte.kobweb.silk.components.layout.AnimatedColorSurfaceVariant
import com.varabyte.kobweb.silk.components.layout.Surface
import com.varabyte.kobweb.silk.theme.SilkTheme
import com.varabyte.kobweb.silk.init.registerBaseStyle
import org.jetbrains.compose.web.css.vh
import org.jetbrains.compose.web.css.vw

@InitSilk
fun registerGlobalStyles(ctx: InitSilkContext) = ctx.stylesheet.apply {
    registerBaseStyle("body") {
        Modifier
            .fontFamily(
                "-apple-system", "BlinkMacSystemFont", "Segoe UI", "Roboto", "Oxygen", "Ubuntu",
                "Cantarell", "Fira Sans", "Droid Sans", "Helvetica Neue", "sans-serif"
            )
    }
}

@App
@Composable
fun MyApp(content: @Composable () -> Unit) {
    SilkApp {
        Surface(Modifier.minWidth(100.vw).minHeight(100.vh), variant = AnimatedColorSurfaceVariant) {
            content()
        }
    }
}
