package playground.components.sections

import androidx.compose.runtime.*
import com.varabyte.kobweb.browser.dom.ElementTarget
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.foundation.layout.Spacer
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.components.forms.Button
import com.varabyte.kobweb.silk.components.icons.fa.FaMoon
import com.varabyte.kobweb.silk.components.icons.fa.FaSun
import com.varabyte.kobweb.silk.components.navigation.Link
import com.varabyte.kobweb.silk.components.navigation.LinkStyle
import com.varabyte.kobweb.silk.components.navigation.UndecoratedLinkVariant
import com.varabyte.kobweb.silk.components.overlay.PopupPlacement
import com.varabyte.kobweb.silk.components.overlay.Tooltip
import com.varabyte.kobweb.silk.components.style.common.SmoothColorStyle
import com.varabyte.kobweb.silk.style.CssName
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.style.component.ComponentKind
import com.varabyte.kobweb.silk.style.component.ComponentStyle
import com.varabyte.kobweb.silk.style.component.toModifier
import com.varabyte.kobweb.silk.style.selector.link
import com.varabyte.kobweb.silk.style.selector.visited
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import com.varabyte.kobweb.silk.theme.colors.palette.background
import com.varabyte.kobweb.silk.theme.colors.palette.color
import com.varabyte.kobweb.silk.theme.colors.palette.toPalette
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Text

interface OldKind : ComponentKind

val OldStyle = ComponentStyle<OldKind> {
    base { Modifier.backgroundColor(Colors.Green) }
}

val YeOldVariant = OldStyle.addVariant {
    if (colorMode.isDark) {
        base { Modifier.backgroundColor(Colors.Yellow) }
    }
}

val BoldOldVariant = OldStyle.addVariant {
    base { Modifier.fontWeight(FontWeight.Bolder) }
}

val NavHeaderStyle = CssStyle.base(extraModifier = { SmoothColorStyle.toModifier() }) {
    Modifier
        .fillMaxWidth()
        .height(50.px)
        // Intentionally invert the header colors from the rest of the page
        .backgroundColor(colorMode.toPalette().color)
}

interface NavItemKind : ComponentKind

val NavItemStyle = ComponentStyle<NavItemKind> {
    base { Modifier.margin(leftRight = 15.px) }
}

val NavLinkVariant = LinkStyle.addVariant {
    // Intentionally invert the header colors from the rest of the page
    val linkColor = colorMode.toPalette().background

    link { Modifier.color(linkColor) }
    visited { Modifier.color(linkColor) }
}

@CssName("-button")
val NavButtonVariant = NavItemStyle.addVariant {
    base { Modifier.padding(0.px).borderRadius(50.percent) }
}

@Composable
private fun NavLink(path: String, text: String) {
    Link(path, text, NavItemStyle.toModifier(), UndecoratedLinkVariant.then(NavLinkVariant))
}

@Composable
fun NavHeader() {
    var colorMode by ColorMode.currentState
    Box(NavHeaderStyle.toModifier()) {
        Row(
            Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(Modifier.size(50.px).then(OldStyle.toModifier(YeOldVariant, BoldOldVariant))) {
                Text("HI")
            }
            NavLink("/", "HOME")
            NavLink("/widgets", "WIDGETS")
            NavLink("/echo", "ECHO")
            NavLink("/worker", "WORKER")
            NavLink("/markdown", "MARKDOWN")
            Spacer()

            Button(
                onClick = { colorMode = colorMode.opposite },
                NavItemStyle.toModifier(NavButtonVariant),
            ) {
                when (colorMode) {
                    ColorMode.LIGHT -> FaMoon()
                    ColorMode.DARK -> FaSun()
                }
            }
            Tooltip(ElementTarget.PreviousSibling, "Toggle color mode", placement = PopupPlacement.BottomRight)
        }
    }
}
