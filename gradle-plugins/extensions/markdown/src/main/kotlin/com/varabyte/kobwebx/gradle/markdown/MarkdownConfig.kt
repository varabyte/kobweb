@file:Suppress("LeakingThis") // Following official Gradle guidance

package com.varabyte.kobwebx.gradle.markdown

import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.get

abstract class MarkdownConfig {
    /**
     * The path to all markdown resources to process.
     *
     * This path should live in the root of the project's `resources` folder, e.g. `src/jsMain/resources`
     */
    abstract val markdownPath: Property<String>

    init {
        markdownPath.convention("markdown")
    }

    @Deprecated(
        "Use `handlers { ... }` instead. This property was renamed to avoid a naming conflict with Gradle project components.",
    )
    fun components(configure: MarkdownHandlers.() -> Unit) {
        val handlers = ((this as ExtensionAware).extensions["handlers"] as MarkdownHandlers)
        handlers.configure()
    }
}
