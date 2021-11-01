package todo.api

import com.varabyte.kobweb.api.Api
import com.varabyte.kobweb.api.ApiContext
import com.varabyte.kobweb.api.data.getValue
import com.varabyte.kobweb.api.http.setBodyText
import todo.model.TodoStore
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Api
fun listTodos(ctx: ApiContext) {
    val ownerId = ctx.req.query["owner"]
    if (ownerId == null) {
        ctx.res.status = 400
        return
    }

    val todos = ctx.data.getValue<TodoStore>()
    ctx.res.setBodyText(Json.encodeToString(todos[ownerId]))
}