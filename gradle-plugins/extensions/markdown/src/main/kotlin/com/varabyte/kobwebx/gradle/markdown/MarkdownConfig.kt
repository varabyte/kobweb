@file:Suppress("LeakingThis") // Following official Gradle guidance

package com.varabyte.kobwebx.gradle.markdown

import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.get

abstract class MarkdownConfig {
    /**
     * The path to all markdown resources to process.
     *
     * This path should live in the root of the project's `resources` folder, e.g. `src/jsMain/resources`
     */
    abstract val markdownPath: Property<String>

    /**
     * A list of imports that should be added to the top of every generated markdown file.
     *
     * If an import starts with a ".", it will be prepended with the current site's root package.
     *
     * Finally, you should NOT use the "import" keyword here.
     *
     * For example:
     *
     * ```kotlin
     * markdown {
     *    imports.add(".components.widgets.*")
     * }
     * ```
     * will add `import com.mysite.components.widgets.*` to the top of every generated markdown file.
     */
    abstract val imports: ListProperty<String>

    init {
        markdownPath.convention("markdown")
        imports.set(emptyList())
    }

    @Deprecated(
        "Use `handlers { ... }` instead. This property was renamed to avoid a naming conflict with Gradle project components.",
    )
    fun components(configure: MarkdownHandlers.() -> Unit) {
        val handlers = ((this as ExtensionAware).extensions["handlers"] as MarkdownHandlers)
        handlers.configure()
    }
}
