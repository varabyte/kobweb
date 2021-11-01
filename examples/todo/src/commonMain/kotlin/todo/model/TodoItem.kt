package todo.model

import kotlinx.serialization.Serializable

@Serializable
class TodoItem(
    val id: String,
    val text: String,
)