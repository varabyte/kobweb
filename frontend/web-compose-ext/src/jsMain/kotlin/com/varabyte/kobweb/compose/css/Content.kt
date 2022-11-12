package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*

fun StyleScope.content(value: String) {
    // CSS content should always be surrounded by quotes, but this is a pretty subtle requirement that's easy to miss
    // and causes silent failures. The person is passing in a String so their intention is clear. Let's just quote it
    // for them if they don't have it!
    val content = if (value.length >= 2 && value.first() == '"' && value.last() == '"') {
        value
    } else {
        '"' + value.replace("\"", "\\\"") + '"'
    }
    property("content", content)
}
