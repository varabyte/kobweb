package helloworld

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Color
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.core.App
import com.varabyte.kobweb.silk.init.InitSilk
import com.varabyte.kobweb.silk.init.InitSilkContext
import com.varabyte.kobweb.silk.SilkApp
import com.varabyte.kobweb.silk.components.layout.AnimatedColorSurfaceVariant
import com.varabyte.kobweb.silk.components.layout.Surface
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import com.varabyte.kobweb.silk.theme.colors.getColorMode
import com.varabyte.kobweb.silk.init.registerBaseStyle
import kotlinx.browser.localStorage
import org.jetbrains.compose.web.css.LineStyle
import org.jetbrains.compose.web.css.em
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.css.vh

private const val COLOR_MODE_KEY = "helloworld:app:colorMode"

@InitSilk
fun updateTheme(ctx: InitSilkContext) = ctx.stylesheet.apply {
    initialColorMode = localStorage.getItem(COLOR_MODE_KEY)?.let { ColorMode.valueOf(it) } ?: ColorMode.DARK
}

@InitSilk
fun registerGlobalStyles(ctx: InitSilkContext) = ctx.stylesheet.apply {
    registerBaseStyle("body") {
        Modifier
            .fontFamily(
                "-apple-system", "BlinkMacSystemFont", "Segoe UI", "Roboto", "Oxygen", "Ubuntu",
                "Cantarell", "Fira Sans", "Droid Sans", "Helvetica Neue", "sans-serif"
            )
            .lineHeight(1.4)
    }

    registerBaseStyle("blockquote") {
        Modifier
            .borderLeft(width = 5.px, style = LineStyle.Solid, color = Color.rgb(0x0c0c0c))
            .margin(topBottom = 1.5.em, leftRight = 10.px)
            .padding(topBottom = 0.5.em, leftRight = 10.px)
            .textAlign(TextAlign.Left)
    }

    registerBaseStyle("#md-inline-demo") {
        Modifier.color(Colors.OrangeRed)
    }
}

@App
@Composable
fun MyApp(content: @Composable () -> Unit) {
    SilkApp {
        val colorMode = getColorMode()
        LaunchedEffect(colorMode) {
            localStorage.setItem(COLOR_MODE_KEY, colorMode.name)
        }

        Surface(Modifier.minHeight(100.vh), variant = AnimatedColorSurfaceVariant) {
            content()
        }
    }
}