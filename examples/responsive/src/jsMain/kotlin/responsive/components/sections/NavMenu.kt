package responsive.components.sections

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.CSSFloat
import com.varabyte.kobweb.compose.css.Cursor
import com.varabyte.kobweb.compose.css.UserSelect
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.silk.components.forms.Button
import com.varabyte.kobweb.silk.components.icons.fa.FaBars
import com.varabyte.kobweb.silk.components.icons.fa.FaX
import com.varabyte.kobweb.silk.components.navigation.Link
import com.varabyte.kobweb.silk.components.navigation.UncoloredLinkVariant
import com.varabyte.kobweb.silk.components.navigation.UndecoratedLinkVariant
import com.varabyte.kobweb.silk.components.style.*
import com.varabyte.kobweb.silk.components.style.breakpoint.Breakpoint
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*

val NavMenuButtonStyle by ComponentStyle.base {
    Modifier
        .margin(5.px)
        .padding(5.px)
}

val NavMenuStyle by ComponentStyle {
    base {
        Modifier
            .userSelect(UserSelect.None)
            .backgroundColor(Colors.LightGray)
    }

    Breakpoint.MD {
        Modifier
            .fillMaxHeight()
            .float(CSSFloat.Left)
    }
}

val NavMenuItemStyle by ComponentStyle {
    base {
        Modifier
            .fillMaxWidth()
            .padding(leftRight = 10.px, topBottom = 5.px)
            // This would be a real link in an actual app, but here we just create a fake menu
            .cursor(Cursor.Pointer)
    }

    hover {
        Modifier.backgroundColor(Colors.DarkGray)
    }
}

@Composable
private fun NavMenuItems(modifier: Modifier = Modifier) {
    Column(NavMenuStyle.toModifier().then(modifier)) {
        val linkVariant = UndecoratedLinkVariant.then(UncoloredLinkVariant)
        Link("/", "Home", NavMenuItemStyle.toModifier(), linkVariant)
        Link("/blog", "Blog", NavMenuItemStyle.toModifier(), linkVariant)
        Link("/profile", "Profile", NavMenuItemStyle.toModifier(), linkVariant)
        Link("/about", "About", NavMenuItemStyle.toModifier(), linkVariant)
    }
}

@Composable
fun NavMenuButton() {
    var isMenuShown by remember { mutableStateOf(false) }
    Column {
        Button(onClick = { isMenuShown = true }, NavMenuButtonStyle.toModifier()) {
            FaBars()
        }

        if (isMenuShown) {
            Div(Modifier
                .position(Position.Absolute)
                .fillMaxSize()
                .onClick { isMenuShown = false }
                .toAttrs()
            ) {
                NavMenuItems(Modifier.fillMaxWidth().position(Position.Relative).top(35.px))
            }
        }
    }
}

@Composable
fun NavSideMenu() {
    NavMenuItems(Modifier.minWidth(100.px))
}