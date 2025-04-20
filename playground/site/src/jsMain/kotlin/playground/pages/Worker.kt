package playground.pages

import androidx.compose.runtime.*
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.data.add
import com.varabyte.kobweb.core.init.InitRoute
import com.varabyte.kobweb.core.init.InitRouteContext
import com.varabyte.kobweb.silk.components.forms.Input
import com.varabyte.kobweb.worker.rememberWorker
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Text
import playground.components.layouts.PageLayoutData
import playground.worker.SumInputs
import playground.worker.SumWorker

@InitRoute
fun initWorkerPage(ctx: InitRouteContext) {
    ctx.data.add(PageLayoutData("WORKER"))
}

@Page
@Composable
fun WorkerPage() {
    var sum by remember { mutableStateOf(0) }
    val worker = rememberWorker { SumWorker {
        sum = it.sum
    } }

    var a by remember { mutableStateOf<Int?>(0) }
    var b by remember { mutableStateOf<Int?>(0) }

    LaunchedEffect(a, b) {
        worker.postInput(SumInputs(a?:0, b?:0))
    }

    Input(InputType.Number, value = a, onValueChange = { a = it?.toInt() })
    Input(InputType.Number, value = b, onValueChange = { b = it?.toInt() })

    P()
    Text("Sum from worker: $sum")
}
