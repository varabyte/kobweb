package playground.components.layouts

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.core.PageContext
import com.varabyte.kobweb.core.data.getValue
import com.varabyte.kobweb.core.layout.Layout
import com.varabyte.kobweb.silk.components.forms.Button
import org.jetbrains.compose.web.dom.H1
import org.jetbrains.compose.web.dom.Text

class ModernScope(val count: State<Int>) {
    companion object {
        @get:Composable
        val instance get() = LocalModernScope.current
    }

    fun logMessage() {
        console.log("Current count is ${count.value}")
    }
}

class ModernLayoutData(val title: String, val onCountIncreased: (Int) -> Unit)

val LocalModernScope = compositionLocalOf<ModernScope> { error("No ModernScope provided!") }

@Composable
@Layout
fun ModernLayout(ctx: PageContext, content: @Composable () -> Unit) {
    val countState = remember { mutableStateOf(0) }
    var count by countState

    Column(Modifier.fillMaxWidth()) {
        val data = ctx.data.getValue<ModernLayoutData>()
        H1 { Text(data.title) }
        Button(onClick = {
            count++
            data.onCountIncreased(count)
        }) { Text("Click me!") }

        CompositionLocalProvider(LocalModernScope provides ModernScope(countState)) {
            content()
        }
    }
}
