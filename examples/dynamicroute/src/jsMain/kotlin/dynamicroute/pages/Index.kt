package dynamicroute.pages

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.silk.components.forms.Button
import com.varabyte.kobweb.silk.components.text.Text
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Input
import org.jetbrains.compose.web.dom.P
import dynamicroute.components.layouts.PageLayout

@Page
@Composable
fun MainPage() {
    PageLayout {
        val ctx = rememberPageContext()
        var userId by remember { mutableStateOf<Int?>(null) }
        var postId by remember { mutableStateOf<Int?>(null) }

        Row {
            Text("User ID: ")
            Input(
                InputType.Number,
                attrs = {
                    onInput { e -> userId = e.value?.toInt() }
                }
            )
        }
        Row {
            Text("Post ID: ")
            Input(
                InputType.Number,
                attrs = {
                    onInput { e -> postId = e.value?.toInt() }
                }
            )
        }

        P {
            Button(onClick = {
                if (userId != null && postId != null) {
                    ctx.router.navigateTo("/users/$userId/posts/$postId")
                }
            }, Modifier.padding(5.px)) {
                Text("Jump to post")
            }
        }
    }
}