package com.varabyte.kobweb.example.todo.api

import com.varabyte.kobweb.api.Api
import com.varabyte.kobweb.api.ApiContext
import com.varabyte.kobweb.api.data.getValue
import com.varabyte.kobweb.example.todo.model.TodoStore

@Api
fun addTodo(ctx: ApiContext) {
    val ownerId = ctx.req.query["owner"]
    val todo = ctx.req.query["todo"]
    if (ownerId == null || todo == null) {
        ctx.res.status = 400
        return
    }

    ctx.data.getValue<TodoStore>().add(ownerId, todo)
}