package helloworld.components.sections

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import kobweb.compose.css.Cursor
import kobweb.compose.css.TextDecorationLine
import kobweb.compose.foundation.layout.Row
import kobweb.compose.foundation.layout.Spacer
import kobweb.compose.ui.background
import kobweb.compose.ui.height
import kobweb.compose.ui.padding
import kobweb.silk.components.forms.Button
import kobweb.silk.components.navigation.Link
import kobweb.silk.components.text.Text
import kobweb.silk.theme.SilkPallete
import kobweb.silk.theme.colors.rememberColorMode
import org.jetbrains.compose.common.foundation.layout.Box
import org.jetbrains.compose.common.foundation.layout.fillMaxWidth
import org.jetbrains.compose.common.ui.Alignment
import org.jetbrains.compose.common.ui.Modifier
import org.jetbrains.compose.common.ui.padding
import org.jetbrains.compose.common.ui.unit.dp

@Composable
private fun NavLink(path: String, text: String) {
    val linkModifiers = Modifier
        .padding(5.dp, 15.dp)

    Link(
        path,
        text,
        linkModifiers,
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
            .background(palette.onPrimary)
    ) {
        Row(
            Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavLink("/", "HOME")
            NavLink("/about", "ABOUT")
            Spacer()
            Button(
                onClick = { colorMode = colorMode.opposite() }
            ) {
                Text("Toggle Color Mode")
            }
        }
    }
}