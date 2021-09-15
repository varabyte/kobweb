import com.varabyte.kobweb.core.Router
import helloworld.MyApp
import helloworld.pages.AboutPage
import helloworld.pages.HomePage
import org.jetbrains.compose.web.renderComposable

fun main() {
    val app = MyApp()
    Router.register("/") { HomePage() }
    Router.register("/about") { AboutPage() }
    Router.navigateTo("/")

    renderComposable(rootElementId = "root") {
        app.render {
            Router.renderActivePage()
        }
    }
}