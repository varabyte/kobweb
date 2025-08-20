@file:Suppress("LeakingThis") // Following official Gradle guidance

package com.varabyte.kobwebx.gradle.markdown

import com.varabyte.kobwebx.gradle.markdown.ext.kobwebcall.KobwebCallExtension
import com.varabyte.kobwebx.gradle.markdown.frontmatter.FrontMatterExtension
import org.commonmark.Extension
import org.commonmark.ext.autolink.AutolinkExtension
import org.commonmark.ext.footnotes.FootnotesExtension
import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension
import org.commonmark.ext.gfm.tables.TablesExtension
import org.commonmark.ext.task.list.items.TaskListItemsExtension
import org.commonmark.parser.IncludeSourceSpans
import org.commonmark.parser.Parser
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input

/**
 * List feature extensions to markdown that this plugin supports.
 *
 * This block will be nested under [MarkdownBlock], e.g.
 *
 * ```
 * kobwebx {
 *   markdown {
 *     features { ... }
 *   }
 * }
 * ```
 */
abstract class MarkdownFeatures {
    /**
     * If true, convert URLs and email addresses into links automatically.
     *
     * Defaults to `true`.
     *
     * @see <a href="https://github.com/commonmark/commonmark-java#autolink">Autolink</a>
     */
    @get:Input
    abstract val autolink: Property<Boolean>

    /**
     * If true, support front matter (a header YAML block at the top of your markdown file with key/value pairs).
     *
     * Defaults to `true`.
     *
     * @see <a href="https://github.com/commonmark/commonmark-java#yaml-front-matter">YAML front matter</a>
     */
    @get:Input
    abstract val frontMatter: Property<Boolean>

    /**
     * If true, support a syntax for inserting a composable call into the final generated Kotlin source:
     *
     * ```
     * {{{ .components.widgets.VisitorCounter }}}
     * ```
     *
     * becomes:
     *
     * ```
     * org.example.myproject.components.widgets.VisitorCounter()
     * ```
     *
     * Defaults to `true`.
     */
    @get:Input
    abstract val kobwebCall: Property<Boolean>

    /**
     * The delimiters used to delineate code for the [kobwebCall] feature.
     *
     * By default, it is curly braces `{}`, but you can change the character if this
     * causes a problem in your project for some unforeseeable reason.
     */
    @get:Input
    abstract val kobwebCallDelimiters: Property<Pair<Char, Char>>

    /**
     * If true, support creating tables via pipe syntax.
     *
     * Defaults to `true`.
     *
     * @see <a href="https://github.com/commonmark/commonmark-java#tables">Tables</a>
     * @see <a href="https://docs.github.com/en/github/writing-on-github/working-with-advanced-formatting/organizing-information-with-tables">Organizing information with tables</a>
     */
    @get:Input
    abstract val tables: Property<Boolean>

    /**
     * If true, support creating task list items via a convenient syntax:
     *
     * ```
     * - [ ] task #1
     * - [x] task #2
     * ```
     *
     * Defaults to `true`.
     *
     * @see <a href="https://github.com/commonmark/commonmark-java#task-list-items">Task List Items</a>
     */
    @get:Input
    abstract val taskList: Property<Boolean>

    /**
     * If true, support GFM strikethrough syntax using double tildes, e.g. `~~text~~`.
     *
     * Defaults to `true`.
     *
     * @see <a href="https://github.com/commonmark/commonmark-java#strikethrough">Strikethrough</a>
     */
    @get:Input
    abstract val strikethrough: Property<Boolean>

    /**
     * If true, support footnotes like "Main text[^1]" with definitions "[^1]: Footnote text".
     *
     * Defaults to `true`.
     *
     * Note: Inline footnotes via `^[inline]` are not supported at this time.
     */
    @get:Input
    abstract val footnotes: Property<Boolean>

    init {
        autolink.convention(true)
        footnotes.convention(true)
        frontMatter.convention(true)
        kobwebCall.convention(true)
        kobwebCallDelimiters.convention('{' to '}')
        strikethrough.convention(true)
        tables.convention(true)
        taskList.convention(true)
    }

    /**
     * Create a markdown parser configured based on the currently activated features.
     */
    fun createParser(): Parser {
        val extensions = mutableListOf<Extension>()
        if (autolink.get()) {
            extensions.add(AutolinkExtension.create())
        }
        if (frontMatter.get()) {
            extensions.add(FrontMatterExtension.create())
        }
        if (kobwebCall.get()) {
            extensions.add(KobwebCallExtension.create(kobwebCallDelimiters.get()) { createParser() })
        }
        if (tables.get()) {
            extensions.add(TablesExtension.create())
        }
        if (taskList.get()) {
            extensions.add(TaskListItemsExtension.create())
        }
        if (strikethrough.get()) {
            extensions.add(StrikethroughExtension.create())
        }
        if (footnotes.get()) {
            extensions.add(FootnotesExtension.create())
        }

        return Parser.builder()
            .extensions(extensions)
            .includeSourceSpans(IncludeSourceSpans.BLOCKS_AND_INLINES)
            .build()
    }
}
