package helloworld.components.sections

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.varabyte.kobweb.compose.css.TextDecorationLine
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.foundation.layout.Spacer
import com.varabyte.kobweb.compose.ui.*
import com.varabyte.kobweb.compose.ui.unit.dp
import com.varabyte.kobweb.silk.components.forms.Button
import com.varabyte.kobweb.silk.components.icons.fa.FaMoon
import com.varabyte.kobweb.silk.components.icons.fa.FaSun
import com.varabyte.kobweb.silk.components.navigation.Link
import com.varabyte.kobweb.silk.theme.SilkPallete
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import com.varabyte.kobweb.silk.theme.colors.rememberColorMode
import com.varabyte.kobweb.silk.theme.shapes.Circle
import com.varabyte.kobweb.silk.theme.shapes.clip

private val NAV_ITEM_MODIFIER get() = Modifier.padding(0.dp, 15.dp)

@Composable
private fun NavLink(path: String, text: String) {
    Link(
        path,
        text,
        NAV_ITEM_MODIFIER,
        color = SilkPallete.current.primary,
        decorationLine = TextDecorationLine.None
    )
}

@Composable
fun NavHeader() {
    var colorMode by rememberColorMode()
    val palette = SilkPallete.current
    Box(
        Modifier
            .fillMaxWidth()
            .height(50.dp)
            // Intentionally invert the header colors
            .background(palette.onPrimary),
    ) {
        Row(
            Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavLink("/", "HOME")
            NavLink("/about", "ABOUT")
            Spacer()
            Button(
                onClick = { colorMode = colorMode.opposite() },
                NAV_ITEM_MODIFIER.clip(Circle())
            ) {
                Box(Modifier.padding(4.dp)) {
                    when (colorMode) {
                        ColorMode.LIGHT -> FaSun()
                        ColorMode.DARK -> FaMoon()
                    }
                }
            }
        }
    }
}