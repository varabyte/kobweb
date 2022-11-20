package todo.api

import com.varabyte.kobweb.api.Api
import com.varabyte.kobweb.api.ApiContext
import com.varabyte.kobweb.api.data.getValue
import com.varabyte.kobweb.api.http.HttpMethod
import todo.model.TodoStore

@Api
fun removeTodo(ctx: ApiContext) {
    if (ctx.req.method != HttpMethod.POST) return

    val ownerId = ctx.req.params["owner"]
    val todoId = ctx.req.params["todo"]
    if (ownerId == null || todoId == null) {
        return
    }

    ctx.data.getValue<TodoStore>().remove(ownerId, todoId)
    ctx.res.status = 200
}