package nekt.core

import androidx.compose.runtime.Composable

abstract class App {
    private val plugins = mutableListOf<Plugin>()
    fun extendWith(plugin: Plugin) { plugins.add(plugin) }

    @Composable
    open fun render(content: @Composable () -> Unit) {
        content()
    }
}