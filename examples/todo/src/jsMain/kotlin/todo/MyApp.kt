package todo

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.Cursor
import com.varabyte.kobweb.compose.css.FontStyle
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.css.cursor
import com.varabyte.kobweb.compose.css.fontStyle
import com.varabyte.kobweb.compose.css.fontWeight
import com.varabyte.kobweb.compose.css.textAlign
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.styleModifier
import com.varabyte.kobweb.core.App
import com.varabyte.kobweb.silk.SilkApp
import com.varabyte.kobweb.silk.components.layout.Surface
import org.jetbrains.compose.web.css.*

const val BORDER_COLOR = "#eaeaea"

// The standard Web Compose way of defining styles
// Note: It is hoped as Kobweb improves this section will be less and less necessary
object TodoStyleSheet : StyleSheet() {
    init {
        "body" style {
            fontFamily(
                "-apple-system", "BlinkMacSystemFont", "Segoe UI", "Roboto", "Oxygen", "Ubuntu",
                "Cantarell", "Fira Sans", "Droid Sans", "Helvetica Neue", "sans-serif"
            )
        }

        "footer" style {
            width(100.percent)
            height(100.px)
            property("border-top", "1px solid $BORDER_COLOR")
            fontSize(1.5.cssRem)

            display(DisplayStyle.Flex)
            justifyContent(JustifyContent.Center)
            alignItems(AlignItems.Center)
        }
    }
}

// The Kobweb way of defining styles, useable for composables provided by the Kobweb library (anything that takes a
// `Modifier` as its first argument)
object Styles {
    // "styleModifier" is an escape hatch for when Kobweb doesn't (yet) have the HTML style modifier that you
    // need. Allows you to define styles the traditional Web Compose way. You can use "attrModifier" too, which has
    // extra functionality, like specifying event listeners.
    val Title = Modifier
        .lineHeight(1.15)
        .fontSize(4.cssRem)
        .margin(top = 0.4.em, bottom = 0.6.em)
        .styleModifier {
            fontWeight(FontWeight.Bold)
        }
}

@App
@Composable
fun MyApp(content: @Composable () -> Unit) {
    Style(TodoStyleSheet)
    SilkApp {
        Surface(Modifier.width(100.vw).height(100.vh)) {
            content()
        }
    }
}