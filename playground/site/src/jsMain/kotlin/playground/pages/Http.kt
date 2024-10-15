package playground.pages

import androidx.compose.runtime.*
import com.varabyte.kobweb.browser.http.get
import com.varabyte.kobweb.browser.http.http
import com.varabyte.kobweb.browser.http.patch
import com.varabyte.kobweb.browser.http.post
import com.varabyte.kobweb.browser.http.put
import com.varabyte.kobweb.core.Page
import kotlinx.browser.window
import kotlinx.coroutines.delay
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.compose.web.dom.Br
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Text
import playground.components.layouts.PageLayout
import kotlin.time.Duration.Companion.seconds

@Serializable
data class Post(
    @SerialName("id")
    val id: Int = 0,
    @SerialName("title")
    val title: String = "",
    @SerialName("body")
    val body: String = "",
    @SerialName("userId")
    val userId: Int = 0,
)

@Page
@Composable
fun HttpPage() {
    // See https://jsonplaceholder.typicode.com/guide/

    PageLayout("Http Serialization test") {
        var post by remember { mutableStateOf<Post?>(null) }
        var posts by remember { mutableStateOf<List<Post>?>(null) }

        LaunchedEffect(Unit) {
            delay(1.seconds)

            post = window.http.post<Post, Post>(
                "https://jsonplaceholder.typicode.com/posts",
                body = Post(title = "foo", body = "bar", userId = 1),
            )
            println("After posting: $post")
            delay(1.seconds)

            post = window.http.put<Post, Post>(
                "https://jsonplaceholder.typicode.com/posts/1",
                body = Post(title = "updated title", body = "updated body", userId = 1),
            )
            println("After putting: $post")
            delay(1.seconds)

            post = window.http.patch<Post, Post>(
                "https://jsonplaceholder.typicode.com/posts/1",
                body = Post(title = "patched title"),
            )
            println("After patching: $post")
            delay(1.seconds)

            posts = window.http.get<List<Post>>("https://jsonplaceholder.typicode.com/posts")
        }

        Text("Post:")
        P()
        if (post != null) {
            Text("Title: ${post!!.title}")
            Br()
            Text("Body: ${post!!.body}")
        } else {
            Text("Loading...")
        }

        P()
        val postCount = 10
        Text("First $postCount Posts:")
        P()
        if (posts != null) {
            posts!!.take(postCount).forEachIndexed { i, p ->
                Text("Post #${i + 1}")
                P()
                Text("Title: ${p.title}")
                Br()
                Text("Body: ${p.body}")
                P()
            }
        } else {
            Text("Loading...")
        }
    }
}
