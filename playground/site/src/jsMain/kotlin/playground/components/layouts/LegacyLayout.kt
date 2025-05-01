package playground.components.layouts

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.core.layout.Layout
import com.varabyte.kobweb.silk.components.forms.Button
import org.jetbrains.compose.web.dom.H1
import org.jetbrains.compose.web.dom.Text

class LegacyScope(val count: State<Int>) {
    fun logMessage() {
        console.log("Current count is ${count.value}")
    }
}

@Composable
@Layout
fun LegacyLayout(title: String, onCountIncreased: (Int) -> Unit, content: @Composable LegacyScope.() -> Unit) {
    val countState = remember { mutableStateOf(0) }
    var count by countState
    val legacyScope = remember(countState) { LegacyScope(countState) }

    Column(Modifier.fillMaxWidth()) {
        H1 { Text(title) }
        Button(onClick = {
            count++
            onCountIncreased(count)
        }) { Text("Click me!") }
        content.invoke(legacyScope)
    }
}
