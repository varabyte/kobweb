import helloworld.pages.HomePage
import org.jetbrains.compose.web.renderComposable

fun main() {
    val homePage = HomePage()
    renderComposable(rootElementId = "root") {
        homePage.render()
    }
}