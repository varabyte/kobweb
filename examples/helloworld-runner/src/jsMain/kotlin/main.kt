import helloworld.MyApp
import helloworld.pages.HomePage
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.renderComposable

object GlobalStylesheet : StyleSheet() {
    init {
        "html, body" style {
            padding(0.px)
            margin(0.px)
        }

        "*" style {
           boxSizing("border-box")
        }
    }
}

fun main() {
    val app = MyApp()
    val homePage = HomePage()

    renderComposable(rootElementId = "root") {
        Style(GlobalStylesheet)
        app.render {
            homePage.render()
        }
    }
}