package nekt.core

import androidx.compose.runtime.Composable

interface App {
    @Composable
    fun render(content: @Composable () -> Unit)
}

object DefaultApp : App {
    @Composable
    override fun render(content: @Composable () -> Unit) {
        content()
    }
}