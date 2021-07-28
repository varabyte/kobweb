package nekt.core

import androidx.compose.runtime.Composable
import nekt.compose.css.Cursor
import nekt.compose.css.cursor
import org.jetbrains.compose.web.css.*

interface App {
    val globalStyles: StyleSheet

    @Composable
    fun render(content: @Composable () -> Unit)
}

object DefaultApp : App {
    override val globalStyles = object : StyleSheet() {
        init {
            "html, body" style {
                padding(0.px)
                margin(0.px)
            }

            "a" style {
                color(Color.blue)
                textDecoration("underline")
            }

            "a:hover" style {
                cursor(Cursor.POINTER)
            }

            "*" style {
                boxSizing("border-box")
            }
        }
    }

    @Composable
    override fun render(content: @Composable () -> Unit) {
        content()
    }
}