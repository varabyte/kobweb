package playground.pages

import androidx.compose.runtime.*
import com.varabyte.kobweb.browser.http.get
import com.varabyte.kobweb.browser.http.http
import com.varabyte.kobweb.browser.http.patch
import com.varabyte.kobweb.browser.http.post
import com.varabyte.kobweb.browser.http.put
import com.varabyte.kobweb.core.Page
import kotlinx.browser.window
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Text
import playground.components.layouts.PageLayout

@Serializable
data class Post(
    @SerialName("id")
    val id: Int,
    @SerialName("title")
    val title: String,
    @SerialName("body")
    val body: String,
    @SerialName("userId")
    val userId: Int
)

@Page
@Composable
fun HttpPage() {
    PageLayout("Http Serialization test") {
        var post by remember { mutableStateOf<Post?>(null) }
        var posts by remember { mutableStateOf<List<Post>?>(null) }

        LaunchedEffect(Unit) {
            val response = window.http.get<List<Post>>("https://jsonplaceholder.typicode.com/posts")
            posts = response
        }

        LaunchedEffect(Unit) {
            val response = window.http.post<Post>(
                "https://jsonplaceholder.typicode.com/posts",
                body = """{"title": "foo", "body": "bar", "userId": 1}""".encodeToByteArray(),
                headers = mapOf("Content-type" to "application/json")
            )
            post = response
        }

        LaunchedEffect(Unit) {
            val response = window.http.put<Post>(
                "https://jsonplaceholder.typicode.com/posts/1",
                body = """{"id": 1, "title": "updated foo", "body": "updated bar", "userId": 1}""".encodeToByteArray(),
                headers = mapOf("Content-type" to "application/json")
            )
            post = response
        }

        LaunchedEffect(Unit) {
            val response = window.http.patch<Post>(
                "https://jsonplaceholder.typicode.com/posts/1",
                body = """{"title": "foo"}""".encodeToByteArray(),
                headers = mapOf("Content-type" to "application/json")
            )
            post = response
        }

        Text("Post:")
        P()
        if (post != null) {
            Text("Title: ${post!!.title}")
            P()
            Text("Body: ${post!!.body}")
        } else {
            Text("Loading...")
        }

        Text("Posts:")
        P()
        if (posts != null) {
            posts!!.forEach { p ->
                Text("Title: ${p.title}")
                P()
                Text("Body: ${p.body}")
                P()
            }
        } else {
            Text("Loading...")
        }
    }
}
