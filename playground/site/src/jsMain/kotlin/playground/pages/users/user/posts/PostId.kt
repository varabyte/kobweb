package playground.pages.users.user.posts

import androidx.compose.runtime.*
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.PageContext
import org.jetbrains.compose.web.dom.Text
import playground.components.layouts.PageLayout

// Test for dynamic routes
// e.g. visit /users/bitspitle/posts/1
@Page("{}")
@Composable
fun PostPage(ctx: PageContext) {
    PageLayout("Post ID #${ctx.route.params["post-id"]}") {
        Text("User: ${ctx.route.params["user"]}")
    }
}
