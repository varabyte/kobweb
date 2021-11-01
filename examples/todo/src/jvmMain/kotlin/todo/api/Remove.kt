package todo.api

import com.varabyte.kobweb.api.Api
import com.varabyte.kobweb.api.ApiContext
import com.varabyte.kobweb.api.data.getValue
import todo.model.TodoStore

@Api
fun removeTodo(ctx: ApiContext) {
    val ownerId = ctx.req.query["owner"]
    val todoId = ctx.req.query["todo"]
    if (ownerId == null || todoId == null) {
        ctx.res.status = 400
        return
    }

    ctx.data.getValue<TodoStore>().remove(ownerId, todoId)
}