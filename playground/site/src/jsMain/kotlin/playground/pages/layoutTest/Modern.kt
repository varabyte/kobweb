package playground.pages.layoutTest

import androidx.compose.runtime.*
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.PageContext
import com.varabyte.kobweb.core.data.add
import com.varabyte.kobweb.core.data.getValue
import com.varabyte.kobweb.core.init.InitRoute
import com.varabyte.kobweb.core.init.InitRouteContext
import com.varabyte.kobweb.core.layout.Layout
import com.varabyte.kobweb.silk.components.forms.Button
import com.varabyte.kobweb.silk.components.text.SpanText
import org.jetbrains.compose.web.dom.Text
import playground.components.layouts.ModernLayoutData
import playground.components.layouts.ModernScope

private class ModernLayoutTestPageData(
    var onCountIncreasedHandler: (Int) -> Unit = {}
)

@InitRoute
fun initModernLayoutTestPage(ctx: InitRouteContext) {
    val pageData = ModernLayoutTestPageData()
    ctx.data.add(pageData)
    ctx.data.add(ModernLayoutData(
        "Modern Layout Test",
        onCountIncreased = { pageData.onCountIncreasedHandler(it) }
    ))
}

@Page
@Composable
@Layout(".components.layouts.ModernLayout")
fun ModernLayoutTestPage(ctx: PageContext) {
    var lineCount by remember { mutableStateOf(0) }
    LaunchedEffect(Unit) {
        ctx.data.getValue<ModernLayoutTestPageData>().onCountIncreasedHandler = { lineCount = it }
    }

    for (i in 0 until lineCount) {
        SpanText("Line $i")
    }
    val scope = ModernScope.instance
    Button(onClick = { scope.logMessage() }) { Text("Log count to console") }
}
