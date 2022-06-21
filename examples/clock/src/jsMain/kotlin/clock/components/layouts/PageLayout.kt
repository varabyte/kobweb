package clock.components.layouts

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.WhiteSpace
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.BoxScope
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.foundation.layout.Spacer
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.graphics.toCssColor
import com.varabyte.kobweb.silk.components.forms.Button
import com.varabyte.kobweb.silk.components.icons.fa.FaMoon
import com.varabyte.kobweb.silk.components.icons.fa.FaSun
import com.varabyte.kobweb.silk.components.navigation.Link
import com.varabyte.kobweb.silk.theme.SilkTheme
import com.varabyte.kobweb.silk.theme.colors.rememberColorMode
import com.varabyte.kobweb.silk.theme.shapes.Circle
import com.varabyte.kobweb.silk.theme.shapes.clip
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Text

@Composable
fun PageLayout(content: @Composable BoxScope.() -> Unit) {
    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(Modifier.fillMaxSize()) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                var colorMode by rememberColorMode()
                Button(
                    onClick = { colorMode = colorMode.opposite() },
                    Modifier.margin(10.px).clip(Circle()).fontSize(24.px)
                ) {
                    Box(Modifier.margin(4.px)) {
                        if (colorMode.isLight()) FaSun() else FaMoon()
                    }
                }
            }
            Box(Modifier.fillMaxSize()) {
                content()
            }
        }

        val borderColor = SilkTheme.palette.color.toCssColor()
        Spacer()
        Box(
            Modifier
                .fillMaxWidth()
                .borderTop(1.px, LineStyle.Solid, borderColor)
                .fontSize(1.5.cssRem),
            Alignment.Center
        ) {
            // Use PreWrap to preserve trailing space in text
            Row(Modifier.margin(topBottom = 1.cssRem, leftRight = 0.cssRem).whiteSpace(WhiteSpace.PreWrap)) {
                Text("This project is built using ")
                Link(
                    "https://github.com/varabyte/kobweb",
                    "Kobweb",
                )
                Text(", a full-stack Kotlin framework.")
            }
        }
    }
}