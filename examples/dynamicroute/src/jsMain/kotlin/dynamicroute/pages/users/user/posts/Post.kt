package dynamicroute.pages.users.user.posts

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.silk.components.text.Text

@Page("{}")
@Composable
fun PostPage() {
    val ctx = rememberPageContext()
    val userId = ctx.params.getValue("user").toIntOrNull()
    val postId = ctx.params.getValue("post").toIntOrNull()

    if (userId != null && postId != null) {
        Text("Imagine that this page was rendering content for user $userId and post $postId...")
    }
    else {
        Text("Error! Invalid URL")
    }
}
