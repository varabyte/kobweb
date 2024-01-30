package com.varabyte.kobwebx.gradle.markdown

/**
 * An entry representing a markdown resource and some relevant data about it.
 *
 * @property filePath The path to the markdown file, relative from the `resources/markdown` root.
 * @property frontMatter FrontMatter key/value pairs parsed from the markdown file. It can be useful to
 *   allow specifying metadata in your markdown files which you can query and use during the process step.
 */
class MarkdownEntry(
    val filePath: String,
    val frontMatter: Map<String, List<String>>,
)
