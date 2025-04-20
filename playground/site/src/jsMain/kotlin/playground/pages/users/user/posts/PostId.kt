package playground.pages.users.user.posts

import androidx.compose.runtime.*
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.PageContext
import com.varabyte.kobweb.core.data.add
import com.varabyte.kobweb.core.init.InitRoute
import com.varabyte.kobweb.core.init.InitRouteContext
import org.jetbrains.compose.web.dom.Text
import playground.components.layouts.PageLayoutData

@InitRoute
fun initPostPage(ctx: InitRouteContext) {
    ctx.data.add(PageLayoutData("Post ID #${ctx.route.params["post-id"]}"))
}

// Test for dynamic routes
// e.g. visit /users/bitspitle/posts/1
@Page("{}")
@Composable
fun PostPage(ctx: PageContext) {
    Text("User: ${ctx.route.params["user"]}")
}
