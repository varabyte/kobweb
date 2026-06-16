package playground.pages

import androidx.compose.runtime.*
import com.varabyte.kobweb.browser.api
import com.varabyte.kobweb.browser.http.bodyOf
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.data.add
import com.varabyte.kobweb.core.init.InitRoute
import com.varabyte.kobweb.core.init.InitRouteContext
import com.varabyte.kobweb.silk.components.forms.Button
import com.varabyte.kobweb.silk.components.forms.Input
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.dom.Br
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.HTMLInputElement
import org.w3c.files.get
import org.w3c.xhr.FormData
import playground.components.layouts.PageLayoutData

@InitRoute
fun initMultiPage(ctx: InitRouteContext) {
    ctx.data.add(PageLayoutData("Multipart Request Test"))
}

@Page
@Composable
fun MultipartPage() {
    val scope = rememberCoroutineScope()
    var filePath by remember { mutableStateOf("") }
    Input(InputType.File, filePath, onValueChange = { filePath = it }, Modifier.id("file-input"))
    Br()
    Button(onClick = {
        val fileInput = document.getElementById("file-input") as HTMLInputElement
        val file = fileInput.files?.get(0)
        if (file != null) {
            scope.launch {
                val response = window.api.post("multipart", body = bodyOf(FormData().apply {
                    append("file", file, file.name)
                    append("description", "Kobweb multipart test")
                }))

                if (response.ok) {
                    response.text().await().let { window.alert(it) }
                } else {
                    window.alert("Upload failed: ${response.status}")
                }
            }
        }
    }, enabled = filePath.isNotBlank()) {
        Text("Upload")
    }
}
