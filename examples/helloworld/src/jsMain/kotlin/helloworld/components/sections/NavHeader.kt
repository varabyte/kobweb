package helloworld.components.sections

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import kobweb.compose.css.TextDecorationLine
import kobweb.compose.foundation.layout.Box
import kobweb.compose.foundation.layout.Row
import kobweb.compose.foundation.layout.Spacer
import kobweb.compose.ui.background
import kobweb.compose.ui.fillMaxSize
import kobweb.compose.ui.height
import kobweb.compose.ui.padding
import kobweb.silk.components.forms.Button
import kobweb.silk.components.icons.fa.FaMoon
import kobweb.silk.components.icons.fa.FaSun
import kobweb.silk.components.navigation.Link
import kobweb.silk.components.text.Text
import kobweb.silk.theme.SilkPallete
import kobweb.silk.theme.colors.ColorMode
import kobweb.silk.theme.colors.rememberColorMode
import kobweb.silk.theme.shapes.Circle
import kobweb.silk.theme.shapes.clip
import org.jetbrains.compose.common.foundation.layout.fillMaxWidth
import org.jetbrains.compose.common.ui.Alignment
import org.jetbrains.compose.common.ui.Modifier
import org.jetbrains.compose.common.ui.padding
import org.jetbrains.compose.common.ui.unit.dp

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