package nekt.core

import org.jetbrains.compose.web.css.Color

open class App {
    companion object {
        private var instance: App? = null
        fun getInstance() = instance!!
    }

    init {
        require(instance == null) { "Only one App should ever be created." }
        instance = this
    }

    private val plugins = mutableListOf<Plugin>()

    fun extendWith(plugin: Plugin) { plugins.add(plugin) }

    open fun getTheme(): Theme {
        return Theme(
            Colors(
                light = Palette(
                    primary = Color.white,
                    background = Color.black,
                ),
                dark = Palette(
                    primary = Color.black,
                    background = Color.white,
                ),
            )
        )
    }
}