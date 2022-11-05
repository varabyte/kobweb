package multimodule.auth.api.account

import com.varabyte.kobweb.api.Api
import com.varabyte.kobweb.api.ApiContext
import com.varabyte.kobweb.api.data.getValue
import com.varabyte.kobweb.api.http.HttpMethod
import com.varabyte.kobweb.api.http.readBodyText
import com.varabyte.kobweb.api.http.setBodyText
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import multimodule.auth.model.Accounts
import multimodule.auth.model.auth.CreateAccountResponse
import multimodule.auth.model.auth.Account

@Api
fun create(ctx: ApiContext) {
    if (ctx.req.method != HttpMethod.POST) return
    val account = Json.decodeFromString(Account.serializer(), ctx.req.readBodyText()!!)
    val accounts = ctx.data.getValue<Accounts>()
    val result = CreateAccountResponse(accounts.set.none { it.username == account.username })
    if (result.succeeded) {
        accounts.set.add(account)
    }

    ctx.res.setBodyText(Json.encodeToString(result))
}