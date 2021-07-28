import helloworld.MyApp
import helloworld.pages.AboutPage
import helloworld.pages.HomePage
import nekt.compose.css.Cursor
import nekt.compose.css.cursor
import nekt.core.Router
import nekt.ui.config.Theme
import nekt.ui.css.withTransitionDefaults
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.renderComposable

fun main() {
    val app = MyApp()
    Router.register("/", HomePage())
    Router.register("/about", AboutPage())
    Router.navigateTo("/")

    renderComposable(rootElementId = "root") {
        Style(app.globalStyles)
        app.render {
            Router.getActivePage().render()
        }
    }
}