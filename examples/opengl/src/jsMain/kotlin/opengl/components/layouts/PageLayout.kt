package opengl.components.layouts

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.foundation.layout.*
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.toCssColor
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.components.navigation.Link
import com.varabyte.kobweb.silk.components.text.Text
import com.varabyte.kobweb.silk.theme.SilkTheme
import org.jetbrains.compose.web.css.*

@Composable
fun PageLayout(content: @Composable BoxScope.() -> Unit) {
    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(Modifier.fillMaxSize()) {
            Box(Modifier.fillMaxSize()) {
                content()
            }
        }

        Spacer()
        Box(
            Modifier
                .fillMaxWidth()
                .borderTop(1.px, LineStyle.Solid, SilkTheme.palette.border.toCssColor())
                .fontSize(1.5.cssRem),
            Alignment.Center
        ) {
            Row(Modifier.margin(topBottom = 1.cssRem, leftRight = 0.cssRem)) {
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