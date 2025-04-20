@file:Suppress("NOTHING_TO_INLINE")

package playground.pages

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.data.add
import com.varabyte.kobweb.core.init.InitRoute
import com.varabyte.kobweb.core.init.InitRouteContext
import com.varabyte.kobweb.silk.components.forms.TextInput
import com.varabyte.kobweb.silk.components.text.SpanText
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Text
import playground.components.layouts.PageLayoutData

@Composable
private inline fun RecomposeCount() {
    var recomposeCount by remember { mutableStateOf(0) }
    recomposeCount++
    Text(recomposeCount.toString())
}

@Composable
private inline fun CodeText(text: String) {
    SpanText(text, Modifier.fontFamily("monospace"))
}

@InitRoute
fun initRecomposePage(ctx: InitRouteContext) {
    ctx.data.add(PageLayoutData("Recompose"))
}

// This page is for testing that we don't get unexpected unecessary recompositions which can happen if we screw up
// composable parameters.
@Page
@Composable
fun RecomposePage() {
    var msg by remember { mutableStateOf("") }
    TextInput(
        msg,
        onTextChange = { msg = it },
        Modifier.width(350.px).margin(bottom = 2.cssRem),
        placeholder = "Type text here to test sibling recompositions"
    )

    SpanText(
        "The following blocks show code being run plus its recompose count when the above text input's text changes. If everything is healthy with our library code, the recompose count will stay at 1.",
        Modifier.width(400.px).fontStyle(FontStyle.Italic)
    )
    P()

    // All code below here should follow the template:
    // CodeText(...)
    // Code { RecomposeCount() }
    // P()
    //
    // If we notice a case where the recompose count increments unexpectedly, we should add an entry below and fix
    // it in the library code.

    // Fixed by marking BoxScope @Immutable
    CodeText("Box(Modifier.width(...))")
    Box(Modifier.width(100.px), contentAlignment = Alignment.Center) {
        RecomposeCount()
    }
    P()

    // Fixed by marking RowScope @Immutable
    CodeText("Row(Arrangement.spacedBy(...))")
    Row(horizontalArrangement = Arrangement.spacedBy(10.px)) {
        RecomposeCount()
    }
    P()

    // Fixed by marking ColumnScope @Immutable
    CodeText("Column(Arrangement.spacedBy(...))")
    Column(verticalArrangement = Arrangement.spacedBy(10.px)) {
        RecomposeCount()
    }
    P()
}
