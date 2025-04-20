package playground.pages

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.data.add
import com.varabyte.kobweb.core.init.InitRoute
import com.varabyte.kobweb.core.init.InitRouteContext
import com.varabyte.kobweb.silk.components.forms.Button
import com.varabyte.kobweb.silk.components.icons.fa.FaArrowUp
import com.varabyte.kobweb.silk.components.navigation.Link
import kotlinx.browser.window
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.H1
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.SMOOTH
import org.w3c.dom.ScrollBehavior
import org.w3c.dom.ScrollToOptions
import playground.components.layouts.PageLayoutData

@InitRoute
fun initLong1Page(ctx: InitRouteContext) {
    ctx.data.add(PageLayoutData("Long 1!"))
}

// Useful for testing scrolling to fragments. See also Long2Page.
@Page
@Composable
fun Long1Page() {
    Link("#h50", "Go halfway down")

    for (i in 0..100) {
        H1(Modifier.id("h$i").toAttrs()) {
            Text("Header 1.$i")
        }
        Link("/long2#h$i") {
            Text("Jump to long2#$i")
        }
    }

    Button(onClick = {
        window.scroll(ScrollToOptions(top = 0.0, behavior = ScrollBehavior.SMOOTH))
    }, Modifier.position(Position.Fixed).bottom(10.px).right(10.px)) {
        FaArrowUp()
    }
}
