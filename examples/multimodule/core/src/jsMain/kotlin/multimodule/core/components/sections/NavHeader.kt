package multimodule.core.components.sections

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.BoxScope
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.foundation.layout.Spacer
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.navigation.Router
import com.varabyte.kobweb.silk.components.forms.Button
import com.varabyte.kobweb.silk.components.icons.fa.FaHouse
import com.varabyte.kobweb.silk.components.icons.fa.FaMoon
import com.varabyte.kobweb.silk.components.icons.fa.FaQuestion
import com.varabyte.kobweb.silk.components.icons.fa.FaSun
import com.varabyte.kobweb.silk.components.style.*
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import com.varabyte.kobweb.silk.theme.colors.rememberColorMode
import com.varabyte.kobweb.silk.theme.shapes.Circle
import com.varabyte.kobweb.silk.theme.shapes.clip
import com.varabyte.kobweb.silk.theme.toSilkPalette
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*

val NavHeaderStyle = ComponentStyle.base("nav-header") {
    Modifier
        .fillMaxWidth()
        .height(60.px)
        .padding(leftRight = 10.px, topBottom = 5.px)
        // Intentionally invert the header colors from the rest of the page
        .backgroundColor(colorMode.toSilkPalette().color)
}

val TitleStyle = ComponentStyle.base("nav-title") {
    Modifier
        .fontSize(26.px)
        .fontWeight(FontWeight.Bold)
        // Intentionally invert the header colors from the rest of the page
        .color(colorMode.toSilkPalette().background)
}

val NavButtonStyle = ComponentStyle.base("nav-button-outer") {
    Modifier
        .margin(leftRight = 5.px)
        .size(40.px)
        .clip(Circle())
}

@Composable
private fun NavButton(onClick: () -> Unit, content: @Composable BoxScope.() -> Unit) {
    Button(onClick, NavButtonStyle.toModifier(), content = content)
}

abstract class NavHeaderAction {
    @Composable
    fun renderActionInto(scope: BoxScope) {
        scope.renderAction()
    }

    @Composable
    protected abstract fun BoxScope.renderAction()

    abstract fun onActionClicked(router: Router)
}

object ExtraNavHeaderAction {
    private val mutableActionState by lazy { mutableStateOf<NavHeaderAction?>(null) }

    var current: NavHeaderAction?
        get() = mutableActionState.value
        set(value) { mutableActionState.value = value }
}

@Composable
fun NavHeader() {
    val ctx = rememberPageContext()
    var colorMode by rememberColorMode()
    Box(NavHeaderStyle.toModifier()) {
        Row(
            Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavButton(onClick = { ctx.router.navigateTo("/") }) { FaHouse() }
            NavButton(onClick = { ctx.router.navigateTo("/about") }) { FaQuestion() }
            Spacer()

            val router = rememberPageContext().router
            ExtraNavHeaderAction.current?.let { extraAction ->
                NavButton(onClick = { extraAction.onActionClicked(router) }) {
                    extraAction.renderActionInto(this)
                }
            }

            NavButton(onClick = { colorMode = colorMode.opposite() }) {
                when (colorMode) {
                    ColorMode.LIGHT -> FaMoon()
                    ColorMode.DARK -> FaSun()
                }
            }
        }

        Box(TitleStyle.toModifier().align(Alignment.Center)) {
            Text("Kobweb Chat \uD83D\uDCAC")
        }
    }
}
