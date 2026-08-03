package playground.pages

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.UserSelect
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.userSelect
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.data.add
import com.varabyte.kobweb.core.init.InitRoute
import com.varabyte.kobweb.core.init.InitRouteContext
import com.varabyte.kobweb.silk.components.forms.Button
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.style.extendedByBase
import com.varabyte.kobweb.silk.style.toModifier
import com.varabyte.kobweb.silk.theme.colors.palette.button
import com.varabyte.kobweb.silk.theme.colors.palette.link
import com.varabyte.kobweb.silk.theme.colors.palette.tab
import com.varabyte.kobweb.silk.theme.colors.palette.toPalette
import com.varabyte.kobweb.worker.rememberWorker
import org.jetbrains.compose.web.dom.Br
import org.jetbrains.compose.web.dom.Text
import playground.components.layouts.PageLayoutData
import playground.worker.CountdownInput
import playground.worker.CountdownOutput
import playground.worker.CountdownWorker
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds

@InitRoute
fun initWorker2Page(ctx: InitRouteContext) {
    ctx.data.add(PageLayoutData("COUNTDOWN WORKER"))
}

val CountdownTextStyle = CssStyle.base {
    Modifier.userSelect(UserSelect.None)
}

val InactiveCountdownTextStyle = CountdownTextStyle.extendedByBase {
    Modifier.color(if (colorMode.isDark) Colors.Gray else Colors.DarkGray)
}

@Page
@Composable
fun Worker2Page() {
    val timers = remember { mutableStateListOf<CountdownOutput?>() }
    val worker = rememberWorker { CountdownWorker { output ->
        repeat(output.id - timers.size + 1) { timers.add(null) }
        timers[output.id] = output.takeUnless { it.isFinished() }
    } }

    // Optional: Some day make this configurable?
    var minSeconds by remember { mutableStateOf(5) }
    var maxSeconds by remember { mutableStateOf(30) }

    Button(onClick = {
        var nextKey = timers.size
        for ((i, element) in timers.withIndex()) {
            println("\t$i: ${element != null}")
            if (element == null) {
                nextKey = i
                break
            }
        }

        worker.postInput(
            CountdownInput(
                nextKey,
                Random.nextInt(minSeconds, maxSeconds).seconds
            )
        )
    }) {
        Text("Add timer (${minSeconds}s to ${maxSeconds}s)")
    }

    Br()
    timers.forEachIndexed { index, output ->
        SpanText(
            "Remaining time: ${output?.remaining ?: 0.seconds}",
            modifier = (if (output != null) CountdownTextStyle else InactiveCountdownTextStyle).toModifier()
        )
        Br()
    }
}
