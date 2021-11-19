package todo.pages

import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.*
import com.varabyte.kobweb.browser.api
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.foundation.layout.Spacer
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.silk.components.navigation.Link
import kotlinx.browser.window
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Footer
import org.jetbrains.compose.web.dom.Text
import todo.Styles
import todo.components.widgets.LoadingSpinner
import todo.components.widgets.TodoCard
import todo.components.widgets.TodoForm
import todo.model.TodoItem
import com.varabyte.kobweb.silk.components.text.Text as SilkText

private suspend fun loadAndReplaceTodos(id: String, todos: SnapshotStateList<TodoItem>) {
    return window.api.get("list?owner=$id")!!.let { listBytes ->
        Snapshot.withMutableSnapshot {
            todos.clear()
            todos.addAll(Json.decodeFromString(listBytes.decodeToString()))
        }
    }
}

@Page
@Composable
fun HomePage() {
    var id by remember { mutableStateOf("") }
    var ready by remember { mutableStateOf(false) }
    var loadingCount by remember { mutableStateOf(1) } // How many API requests are occurring at the same time
    val todos = remember { mutableStateListOf<TodoItem>() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        check(!ready && loadingCount == 1)
        id = window.localStorage.getItem("id") ?: run {
            window.api.get("id")!!.decodeToString().also {
                window.localStorage.setItem("id", it)
            }
        }

        loadAndReplaceTodos(id, todos)
        loadingCount--
        ready = true
    }

    Column(
        modifier = Modifier.fillMaxSize().minWidth(600.px),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
    ) {
        if (!ready) {
            Box(Modifier.fillMaxWidth().padding(top = 2.cssRem), contentAlignment = Alignment.TopCenter) {
                LoadingSpinner()
            }
            return@Column
        }

        Row(Styles.Title, horizontalArrangement = Arrangement.Center) {
            SilkText("TODO App with ")
            Link("https://github.com/varabyte/kobweb", "Kobweb!")
        }

        Column(Modifier.fillMaxSize()) {
            Column(
                Modifier.fillMaxWidth().maxWidth(800.px).align(Alignment.CenterHorizontally),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TodoForm("Type a TODO and press ENTER", loadingCount > 0) { todo ->
                    coroutineScope.launch {
                        loadingCount++
                        window.api.post("add?owner=$id&todo=$todo")
                        loadAndReplaceTodos(id, todos)
                        loadingCount--
                    }
                }

                todos.forEachIndexed { i, todo ->
                    TodoCard(onClick = {
                        coroutineScope.launch {
                            loadingCount++
                            todos.removeAt(i)
                            window.api.post("remove?owner=$id&todo=${todo.id}")
                            loadAndReplaceTodos(id, todos)
                            loadingCount--
                        }
                    }) {
                        // Avoid collision with Silk's Text method.
                        Text(todo.text)
                    }
                }
            }

            Spacer()
            Footer {
                Row {
                    SilkText("Project inspired by ")
                    Link(
                        "https://blog.upstash.com/nextjs-todo",
                        "Upstash's Next.js TODO App",
                    )
                }
            }
        }
    }
}