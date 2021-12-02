package clock.components.layouts

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.foundation.layout.Spacer
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.graphics.toCssColor
import com.varabyte.kobweb.compose.ui.styleModifier
import com.varabyte.kobweb.silk.components.navigation.Link
import com.varabyte.kobweb.silk.components.text.Text
import com.varabyte.kobweb.silk.theme.SilkTheme
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.fontSize

@Composable
fun PageLayout(content: @Composable () -> Unit) {
    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        content()

        val borderColor = SilkTheme.palette.color.toCssColor()
        Spacer()
        Box(
            Modifier.fillMaxWidth().styleModifier {
                property("border-top", "1px solid $borderColor")
                fontSize(1.5.cssRem)
            },
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