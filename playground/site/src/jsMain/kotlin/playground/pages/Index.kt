package playground.pages

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.thenIf
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.silk.components.layout.Surface
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.style.breakpoint.Breakpoint
import com.varabyte.kobweb.silk.style.extendedByBase
import com.varabyte.kobweb.silk.style.toAttrs
import com.varabyte.kobweb.silk.style.toModifier
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text
import playground.components.layouts.PageLayout

val TestStyle = CssStyle.base {
    Modifier
        .display(DisplayStyle.Block)
        .padding(4.px)
        .background(if (colorMode.isLight) Colors.Red else Colors.Green)
        .thenIf(colorMode.isLight, Modifier.margin { left(1.cssRem) })
}

val MyStyle = CssStyle {
    base {
        Modifier
            .height(100.px)
            .backgroundColor(Colors.Red)
            .width(if (colorMode.isLight) 50.px else 60.px)
    }
    Breakpoint.MD {
        Modifier.width(300.px)
    }
}

val MyStyle2 = CssStyle {
    base {
        Modifier
            .height(100.px)
            .backgroundColor(Colors.Red)
            .width(300.px)
    }
    Breakpoint.MD {
        Modifier
            .width(if (colorMode.isLight) 50.px else 60.px)
    }
}

val Style1 = CssStyle.base {
    Modifier.backgroundColor(Colors.Red).width(if (colorMode.isLight) 50.px else 60.px)
}

val Style2 = Style1.extendedByBase {
    Modifier.width(500.px)
}

@Page
@Composable
fun HomePage() {
    PageLayout("asf") {
        Div(MyStyle.toAttrs()) { Text("I should be thick") }
        Surface(MyStyle.toModifier(), colorModeOverride = ColorMode.current.opposite) { Text("I should be thick") }
        Div(MyStyle2.toAttrs()) { Text("I should be thin") }
        Surface(MyStyle2.toModifier(), colorModeOverride = ColorMode.current.opposite) { Text("I should be thin") }
        Div(Style2.toAttrs()) { Text("I should be thick") }
        Surface(Style2.toModifier(), colorModeOverride = ColorMode.current.opposite) { Text("I should be thick") }

        Surface(TestStyle.toModifier().textAlign(TextAlign.Start), colorModeOverride = ColorMode.current.opposite) {
            Text("Local theme: ${ColorMode.current}")
            Div(TestStyle.toAttrs()) { Text("Div(TestStyle.toAttrs())") }
            Surface(TestStyle.toModifier(), colorModeOverride = ColorMode.current.opposite) {
                Text("Local theme: ${ColorMode.current}")
                Div(TestStyle.toAttrs()) { Text("Div(TestStyle.toAttrs())") }
                Surface(TestStyle.toModifier(), colorModeOverride = ColorMode.current.opposite) {
                    Text("Local theme: ${ColorMode.current}")
                    Div(TestStyle.toAttrs()) { Text("Div(TestStyle.toAttrs())") }
                    Surface(TestStyle.toModifier(), colorModeOverride = ColorMode.current.opposite) {
                        Text("Local theme: ${ColorMode.current}")
                        Div(TestStyle.toAttrs()) { Text("Div(TestStyle.toAttrs())") }
                        Surface(TestStyle.toModifier(), colorModeOverride = ColorMode.current.opposite) {
                            Text("Local theme: ${ColorMode.current}")
                            Div(TestStyle.toAttrs()) { Text("Div(TestStyle.toAttrs())") }
                            Surface(TestStyle.toModifier(), colorModeOverride = ColorMode.current.opposite) {
                                Text("Local theme: ${ColorMode.current}")
                                Div(TestStyle.toAttrs()) { Text("Div(TestStyle.toAttrs())") }
                            }
                        }
                    }
                }
            }
        }
    }
}