package playground.components.widgets

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.toAttrs
import kotlinx.browser.document
import kotlinx.dom.addClass
import org.jetbrains.compose.web.dom.Span
import org.w3c.dom.HTMLLIElement
import org.w3c.dom.HTMLOListElement
import org.w3c.dom.HTMLUListElement
import org.w3c.dom.Text
import playground.utilities.walk

val FoldersStyle = CssStyle {
    base {
        Modifier
            .fontFamily("monospace")
            .listStyle(ListStyleType.None)
            .textAlign(TextAlign.Start)
    }
}

@Composable
fun Folders(content: @Composable () -> Unit) {
    Span(attrs = FoldersStyle.toAttrs {
        ref { element ->
            element.children.walk { child ->
                when (child) {
                    is HTMLUListElement -> {
                        child.addClass("fa-ul")
                    }

                    is HTMLOListElement -> {
                        child.addClass("fa-ol")
                    }

                    is HTMLLIElement -> {
                        val span = document.createElement("span")
                        span.addClass("fa-li")
                        val i = document.createElement("i")
                        val childText = buildString {
                            child.childNodes.walk { liChild ->
                                if (liChild.parentNode == child && liChild is Text) {
                                    append(liChild.nodeValue.orEmpty())
                                }
                            }
                        }
                        i.addClass(
                            "fa-regular", when {
                                childText.contains('.') -> "fa-file"
                                child.children.length > 0 -> "fa-folder-open"
                                else -> "fa-folder"
                            }
                        )
                        span.appendChild(i)
                        child.insertBefore(span, child.firstChild)
                    }
                }
            }

            onDispose { }
        }
    }) {
        content()
    }
}
