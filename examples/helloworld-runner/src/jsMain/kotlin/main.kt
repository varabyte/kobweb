import helloworld.MyApp
import helloworld.pages.AboutPage
import helloworld.pages.HomePage
import nekt.compose.css.Cursor
import nekt.compose.css.cursor
import nekt.core.Router
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.renderComposable

object GlobalStylesheet : StyleSheet() {
    init {
        "html, body" style {
            padding(0.px)
            margin(0.px)
        }

        "a" style {
            color(Color.blue)
            textDecoration("underline")
        }

        "a:hover" style {
            cursor(Cursor.POINTER)
        }

        "*" style {
           boxSizing("border-box")
        }
    }
}

fun main() {
    val app = MyApp()
    Router.register("/", HomePage())
    Router.register("/about", AboutPage())
    Router.navigateTo("/")

    renderComposable(rootElementId = "root") {
        Style(GlobalStylesheet)
        app.render {
            Router.getActivePage().render()
        }
    }
}