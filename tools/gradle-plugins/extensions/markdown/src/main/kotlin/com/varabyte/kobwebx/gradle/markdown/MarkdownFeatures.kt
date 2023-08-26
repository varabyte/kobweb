@file:Suppress("LeakingThis") // Following official Gradle guidance

package com.varabyte.kobwebx.gradle.markdown

import com.varabyte.kobwebx.gradle.markdown.ext.kobwebcall.KobwebCallExtension
import org.commonmark.Extension
import org.commonmark.ext.autolink.AutolinkExtension
import org.commonmark.ext.front.matter.YamlFrontMatterExtension
import org.commonmark.ext.gfm.tables.TablesExtension
import org.commonmark.ext.task.list.items.TaskListItemsExtension
import org.commonmark.parser.Parser
import org.gradle.api.provider.Property

/**
 * List feature extensions to markdown that this plugin supports.
 *
 * This block will be nested under [MarkdownConfig], e.g.
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
     * See also: https://github.com/commonmark/commonmark-java#autolink
     */
    abstract val autolink: Property<Boolean>

    /**
     * If true, support front matter (a header YAML block at the top of your markdown file with key/value pairs)
     *
     * See also: https://github.com/commonmark/commonmark-java#yaml-front-matter
     */
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
     */
    abstract val kobwebCall: Property<Boolean>

    /**
     * The delimiters used to delineate code for the [kobwebCall] feature.
     *
     * By default, it is curly braces `{}` but you can change the character if this
     * causes a problem in your project for some unforeseeable reason.
     */
    abstract val kobwebCallDelimiters: Property<Pair<Char, Char>>

    /**
     * If true, support creating tables via pipe syntax.
     *
     * See also: https://github.com/commonmark/commonmark-java#tables
     * See also: https://docs.github.com/en/github/writing-on-github/working-with-advanced-formatting/organizing-information-with-tables
     */
    abstract val tables: Property<Boolean>

    /**
     * If true, support creating task list items via a convenient syntax:
     *
     * ```
     * - [ ] task #1
     * - [x] task #2
     * ```
     *
     * See also: https://github.com/commonmark/commonmark-java#task-list-items
     */
    abstract val taskList: Property<Boolean>

    init {
        autolink.convention(true)
        frontMatter.convention(true)
        kobwebCall.convention(true)
        kobwebCallDelimiters.convention('{' to '}')
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
            extensions.add(YamlFrontMatterExtension.create())
        }
        if (kobwebCall.get()) {
            extensions.add(KobwebCallExtension.create(kobwebCallDelimiters.get()))
        }
        if (tables.get()) {
            extensions.add(TablesExtension.create())
        }
        if (taskList.get()) {
            extensions.add(TaskListItemsExtension.create())
        }

        return Parser.builder()
            .extensions(extensions)
            .build()
    }
}
