package playground.pages.layoutTest

import androidx.compose.runtime.*
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.layout.NoLayout
import com.varabyte.kobweb.silk.components.forms.Button
import com.varabyte.kobweb.silk.components.text.SpanText
import org.jetbrains.compose.web.dom.Text
import playground.components.layouts.LegacyLayout

@Page
@Composable
@NoLayout
fun LegacyLayoutTestPage() {
    var lineCount by remember { mutableStateOf(0) }
    LegacyLayout("Legacy Layout Test", onCountIncreased = { lineCount = it }) {
        for (i in 0 until lineCount) {
            SpanText("Line $i")
        }
        Button(onClick = { logMessage() }) { Text("Log count to console") }
    }
}
