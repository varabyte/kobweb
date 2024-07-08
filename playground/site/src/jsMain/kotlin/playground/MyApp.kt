package playground

import androidx.compose.runtime.*
import com.varabyte.kobweb.browser.storage.getItem
import com.varabyte.kobweb.browser.storage.setItem
import com.varabyte.kobweb.browser.storage.createStorageKey
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Color
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.core.App
import com.varabyte.kobweb.silk.SilkApp
import com.varabyte.kobweb.silk.components.layout.Surface
import com.varabyte.kobweb.silk.init.InitSilk
import com.varabyte.kobweb.silk.init.InitSilkContext
import com.varabyte.kobweb.silk.init.registerStyleBase
import com.varabyte.kobweb.silk.style.common.SmoothColorStyle
import com.varabyte.kobweb.silk.style.toModifier
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import kotlinx.browser.localStorage
import org.jetbrains.compose.web.css.*

private val COLOR_MODE_KEY =
    ColorMode.entries.createStorageKey("playground:app:colorMode", defaultValue = ColorMode.DARK)

@InitSilk
fun updateTheme(ctx: InitSilkContext) = ctx.config.apply {
    initialColorMode = localStorage.getItem(COLOR_MODE_KEY)!!
}

@InitSilk
fun registerGlobalStyles(ctx: InitSilkContext) = ctx.stylesheet.apply {
    registerStyleBase("body") {
        Modifier
            .fontFamily(
                "-apple-system", "BlinkMacSystemFont", "Segoe UI", "Roboto", "Oxygen", "Ubuntu",
                "Cantarell", "Fira Sans", "Droid Sans", "Helvetica Neue", "sans-serif"
            )
            .lineHeight(1.4)
    }

    registerStyleBase("blockquote") {
        Modifier
            .borderLeft(width = 5.px, style = LineStyle.Solid, color = Color.rgb(0x0c0c0c))
            .margin(topBottom = 1.5.em, leftRight = 10.px)
            .padding(topBottom = 0.5.em, leftRight = 10.px)
            .textAlign(TextAlign.Left)
    }

    registerStyleBase("table, th, td") {
        Modifier.border(1.px, LineStyle.Solid, Colors.LightGray)
    }

    registerStyleBase("table") {
        Modifier.borderCollapse(BorderCollapse.Collapse)
    }

    registerStyleBase("#md-inline-demo") {
        Modifier.color(Colors.OrangeRed)
    }
}

@App
@Composable
fun AppEntry(content: @Composable () -> Unit) {
    SilkApp {
        val colorMode = ColorMode.current
        LaunchedEffect(colorMode) { localStorage.setItem(COLOR_MODE_KEY, colorMode) }

        Surface(SmoothColorStyle.toModifier().minHeight(100.vh)) {
            content()
        }
    }
}
