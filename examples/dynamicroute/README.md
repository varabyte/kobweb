This is a [Kobweb](https://github.com/varabyte/kobweb) project instantiated from the `examples/dynamicroute` template.

The purpose of this project is to demonstrate how to define routes which can absorb values as parameters. Here, the
route "users/{user}/posts/{post}" will capture a user visiting "users/103920/posts/5", passing those values into a map
that can be read when that page is visited:

```kotlin
// pages/users/user/posts/Post.kt

@Page("{post}") // Or, @Page("{}") as a shortcut
@Composable
fun PostPage() {
    val ctx = rememberPageContext()
    val userId = ctx.params.getValue("user")
    val postId = ctx.params.getValue("post")

    /* ... */
}
```

---

To run the sample, simply enter the following command in the terminal:

```bash
kobweb run
```

and open [http://localhost:8080](http://localhost:8080) with your browser to see the result.