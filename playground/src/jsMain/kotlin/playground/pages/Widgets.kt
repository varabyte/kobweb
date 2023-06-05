package playground.pages

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.BoxScope
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.silk.components.disclosure.Tabs
import com.varabyte.kobweb.silk.components.forms.Button
import com.varabyte.kobweb.silk.components.style.ComponentStyle
import com.varabyte.kobweb.silk.components.style.base
import com.varabyte.kobweb.silk.components.style.toModifier
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.theme.toSilkPalette
import org.jetbrains.compose.web.css.LineStyle
import org.jetbrains.compose.web.css.Position
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.px
import playground.components.layouts.PageLayout
import org.jetbrains.compose.web.dom.Text
import playground.components.widgets.GoHomeLink

val WidgetSectionStyle by ComponentStyle.base {
    Modifier
        .fillMaxWidth()
        .border(1.px, LineStyle.Solid, colorMode.toSilkPalette().border)
        .position(Position.Relative)
}

val WidgetPaddingStyle by ComponentStyle.base {
    Modifier
        .fillMaxSize()
        .padding(1.cssRem)
}

val WidgetLabelStyle by ComponentStyle.base {
    Modifier
        .position(Position.Relative)
        .fontSize(0.8.cssRem)
        .left(0.3.cssRem)
        .top((-.7).cssRem)
        .padding(0.2.cssRem)
        .backgroundColor(colorMode.toSilkPalette().background)
}

@Composable
fun WidgetSection(title: String, content: @Composable BoxScope.() -> Unit) {
    Box(WidgetSectionStyle.toModifier()) {
        Box(WidgetLabelStyle.toModifier()) {
            SpanText(title)
        }
        Box(WidgetPaddingStyle.toModifier()) {
            content()
        }
    }
}

@Page
@Composable
fun WidgetsPage() {
    PageLayout("WIDGETS") { Column(Modifier.gap(2.cssRem).fillMaxWidth().padding(2.cssRem).maxWidth(800.px), horizontalAlignment = Alignment.CenterHorizontally) {
        WidgetSection("Button") {
            Button(onClick = {}) { Text("Click me!") }
        }

        WidgetSection("Tabs") {
            Tabs {
                TabPanel {
                    Tab { Text("Tab 1") }; Panel { Text("Panel 1") }
                }
                TabPanel {
                    Tab { Text("Tab 2") }; Panel { Text("Panel 2") }
                }
                TabPanel {
                    Tab { Text("Tab 3") }; Panel { Text("Panel 3") }
                }
            }
        }

        GoHomeLink()
    } }
}