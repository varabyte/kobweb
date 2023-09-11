@file:Suppress("LeakingThis") // Following official Gradle guidance

package com.varabyte.kobwebx.gradle.markdown

import com.varabyte.kobweb.common.text.splitCamelCase
import com.varabyte.kobweb.gradle.core.extensions.FileGeneratingBlock
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.get

abstract class MarkdownConfig(baseGenDir: Provider<String>) : FileGeneratingBlock {
    object RouteOverride {
        /**
         * An algorithm for converting a markdown filename into a URL name that preserves the original filename.
         *
         * For example, a markdown filename like "ExamplePost.md" will be converted into "ExamplePost".
         */
        val Preserve: (String) -> String = { it }

        /**
         * An algorithm for converting a markdown filename into a kebab-case URL name.
         *
         * For example, a markdown filename like "ExamplePost.md" will be converted into "example-post".
         */
        val KebabCase: (String) -> String = { it.splitCamelCase().joinToString("-") { word -> word.lowercase() } }

        /**
         * An algorithm for converting a markdown filename into a snake-case URL name.
         *
         * For example, a markdown filename like "ExamplePost.md" will be converted into "example_post".
         */
        val SnakeCase: (String) -> String = { it.splitCamelCase().joinToString("_") { word -> word.lowercase() } }
    }

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

    /**
     * Logic to configure how a markdown filename should be converted into a final URL name.
     *
     * By default, a markdown filename like "ExamplePost.md" will be converted into lowercase, i.e. "examplepost".
     * However, you can use this property to override this default behavior.
     *
     * If you set this, then for the filename "ExamplePost.md", your callback will be invoked with the string
     * "ExamplePost".
     *
     * You can set this to any logic you want, but a [RouteOverride] object is provided with some common
     * choices.
     *
     * For example:
     *
     * ```kotlin
     * markdown {
     *   routeOverride.set(RouteOverrideAlgorithms.KebabCase)
     * }
     * ```
     */
    abstract val routeOverride: Property<(String) -> String>

    init {
        markdownPath.convention("markdown")
        imports.set(emptyList())
        genDir.convention(baseGenDir.map { "$it/markdown" })
    }

    @Deprecated(
        "Use `handlers { ... }` instead. This property was renamed to avoid a naming conflict with Gradle project components.",
    )
    fun components(configure: MarkdownHandlers.() -> Unit) {
        val handlers = ((this as ExtensionAware).extensions["handlers"] as MarkdownHandlers)
        handlers.configure()
    }
}
