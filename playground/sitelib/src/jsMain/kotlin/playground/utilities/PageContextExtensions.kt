package playground.utilities

import com.varabyte.kobweb.core.PageContext

fun PageContext.setTitle(title: String) {
    data["title"] = title
}

fun PageContext.getTitle(): String? = data["title"] as? String