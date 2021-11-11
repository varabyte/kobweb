package todo.model

import com.varabyte.kobweb.api.InitApi
import com.varabyte.kobweb.api.InitApiContext
import com.varabyte.kobweb.api.data.add
import java.util.*
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

// NOTE: This is a simple demo, so we create an in-memory store (which will get reset everytime server code is live
// reloaded). However, in a production app, you should use a real database instead, maybe a redis backend, or possibly
// even something that lives on a different server which you access via network calls.

@InitApi
fun initTodoStore(ctx: InitApiContext) {
    ctx.data.add(TodoStore())
}

class TodoStore {
    private val lock = ReentrantLock()
    private val todos = mutableMapOf<String, MutableList<TodoItem>>()

    fun add(ownerId: String, todo: String) {
        lock.withLock {
            todos.computeIfAbsent(ownerId) { mutableListOf() }.add(TodoItem(UUID.randomUUID().toString(), todo))
        }
    }

    fun remove(ownerId: String, id: String) {
        lock.withLock { todos[ownerId]?.removeIf { it.id == id } }
    }

    operator fun get(ownerId: String): List<TodoItem> = lock.withLock { todos[ownerId] } ?: emptyList()
}