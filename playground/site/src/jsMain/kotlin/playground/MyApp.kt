package playground

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Color
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.core.App
import com.varabyte.kobweb.silk.SilkApp
import com.varabyte.kobweb.silk.components.layout.Surface
import com.varabyte.kobweb.silk.components.style.common.SmoothColorStyle
import com.varabyte.kobweb.silk.components.style.toModifier
import com.varabyte.kobweb.silk.init.InitSilk
import com.varabyte.kobweb.silk.init.InitSilkContext
import com.varabyte.kobweb.silk.init.registerBaseStyle
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import com.varabyte.kobweb.silk.theme.colors.getColorMode
import kotlinx.browser.localStorage
import org.jetbrains.compose.web.css.*

private const val COLOR_MODE_KEY = "playground:app:colorMode"

@InitSilk
fun updateTheme(ctx: InitSilkContext) = ctx.config.apply {
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

    registerBaseStyle("table, th, td") {
        Modifier.border(1.px, LineStyle.Solid, Colors.LightGray)
    }

    registerBaseStyle("table") {
        Modifier.borderCollapse(BorderCollapse.Collapse)
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

        Surface(SmoothColorStyle.toModifier().minHeight(100.vh)) {
            content()
        }
    }
}
