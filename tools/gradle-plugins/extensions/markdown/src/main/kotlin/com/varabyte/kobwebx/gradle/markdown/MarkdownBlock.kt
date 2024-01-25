@file:Suppress("LeakingThis") // Following official Gradle guidance

package com.varabyte.kobwebx.gradle.markdown

import com.varabyte.kobweb.common.text.splitCamelCase
import com.varabyte.kobweb.gradle.core.extensions.KobwebBlock
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties

abstract class MarkdownBlock(baseGenDir: Provider<String>) : KobwebBlock.FileGeneratingBlock {
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

    abstract val process: Property<(List<MarkdownData>) -> String>

    // TODO should probably rename these
    internal abstract val packageName: Property<String>
    internal abstract val ktFileName: Property<String>
    internal abstract val generateMarkdownListingFile: Property<Boolean>

    fun generateMarkdownListingFile(
        packageName: String = "dev.stralman.markdown.listing",
        ktFileName: String = "MarkdownDataListing.kt",
    ) {
        this.packageName.set(packageName)
        this.ktFileName.set(ktFileName)
        generateMarkdownListingFile.set(true)
    }

    fun KClass<*>.toFormattedString(): String {
        val propertiesString = memberProperties
            .joinToString("\n|") { property ->
                val propertyName = property.name
                val propertyType = property.returnType.toString()
                "|   val $propertyName: $propertyType,"
            }

        return """
        |class ${this.simpleName}(
        $propertiesString
        |)
        |""".trimMargin()
    }

    init {
        markdownPath.convention("markdown")
        imports.set(emptyList())
        genDir.convention(baseGenDir.map { "$it/markdown" })
        process.convention { markdownData ->
            buildString {
                appendLine(
                    """
                    |${MarkdownData::class.toFormattedString()}
                    |
                    |// Map of path of markdown file to frontmatter data
                    |val MARKDOWN_DATA_LIST: List<${MarkdownData::class.simpleName}> = listOf( 
                    """.trimMargin()
                )
                appendLine(markdownData.map { it }.joinToString("\n"))
                appendLine(")")
            }
        }
        generateMarkdownListingFile.set(false)
    }
}
