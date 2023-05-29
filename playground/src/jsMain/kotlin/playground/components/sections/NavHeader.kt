package playground.components.sections

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.dom.ElementTarget
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.foundation.layout.Spacer
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.components.forms.Button
import com.varabyte.kobweb.silk.components.icons.fa.FaMoon
import com.varabyte.kobweb.silk.components.icons.fa.FaSun
import com.varabyte.kobweb.silk.components.navigation.Link
import com.varabyte.kobweb.silk.components.navigation.LinkStyle
import com.varabyte.kobweb.silk.components.navigation.UndecoratedLinkVariant
import com.varabyte.kobweb.silk.components.overlay.PopupPlacement
import com.varabyte.kobweb.silk.components.overlay.Tooltip
import com.varabyte.kobweb.silk.components.style.*
import com.varabyte.kobweb.silk.components.style.common.SmoothColorStyle
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import com.varabyte.kobweb.silk.theme.colors.rememberColorMode
import com.varabyte.kobweb.silk.theme.toSilkPalette
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px

val NavHeaderStyle by ComponentStyle.base(extraModifiers = { SmoothColorStyle.toModifier() }) {
    Modifier
        .fillMaxWidth()
        .height(50.px)
        // Intentionally invert the header colors from the rest of the page
        .backgroundColor(colorMode.toSilkPalette().color)
}

val NavItemStyle by ComponentStyle {
    base { Modifier.margin(leftRight = 15.px) }
}

val NavLinkVariant by LinkStyle.addVariant {
    // Intentionally invert the header colors from the rest of the page
    val linkColor = colorMode.toSilkPalette().background

    link { Modifier.color(linkColor) }
    visited { Modifier.color(linkColor) }
}

val NavButtonVariant = NavItemStyle.addVariant("button") {
    base { Modifier.padding(0.px).borderRadius(50.percent) }
}

@Composable
private fun NavLink(path: String, text: String) {
    Link(path, text, NavItemStyle.toModifier(), UndecoratedLinkVariant.then(NavLinkVariant))
}

@Composable
fun NavHeader() {
    var colorMode by rememberColorMode()
    Box(NavHeaderStyle.toModifier()) {
        Row(
            Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavLink("/", "HOME")
            NavLink("/about", "ABOUT")
            NavLink("/markdown", "MARKDOWN")
            Spacer()

            Button(
                onClick = { colorMode = colorMode.opposite() },
                NavItemStyle.toModifier(NavButtonVariant)
            ) {
                Box(Modifier.margin(8.px)) {
                    when (colorMode) {
                        ColorMode.LIGHT -> FaMoon()
                        ColorMode.DARK -> FaSun()
                    }
                }
            }
            Tooltip(ElementTarget.PreviousSibling, "Toggle color mode", placement = PopupPlacement.BottomRight)
        }
    }
}
