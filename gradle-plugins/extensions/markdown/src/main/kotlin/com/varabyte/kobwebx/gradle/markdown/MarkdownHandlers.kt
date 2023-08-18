@file:Suppress("LeakingThis") // Following official Gradle guidance

package com.varabyte.kobwebx.gradle.markdown

import com.varabyte.kobweb.common.collect.Key
import com.varabyte.kobweb.common.collect.TypedMap
import com.varabyte.kobweb.gradle.core.util.hasJsDependencyNamed
import org.commonmark.ext.gfm.tables.TableBlock
import org.commonmark.ext.gfm.tables.TableBody
import org.commonmark.ext.gfm.tables.TableCell
import org.commonmark.ext.gfm.tables.TableHead
import org.commonmark.ext.gfm.tables.TableRow
import org.commonmark.node.BlockQuote
import org.commonmark.node.BulletList
import org.commonmark.node.Code
import org.commonmark.node.Emphasis
import org.commonmark.node.FencedCodeBlock
import org.commonmark.node.HardLineBreak
import org.commonmark.node.Heading
import org.commonmark.node.HtmlBlock
import org.commonmark.node.HtmlInline
import org.commonmark.node.Image
import org.commonmark.node.Link
import org.commonmark.node.ListItem
import org.commonmark.node.Node
import org.commonmark.node.OrderedList
import org.commonmark.node.Paragraph
import org.commonmark.node.StrongEmphasis
import org.commonmark.node.Text
import org.commonmark.node.ThematicBreak
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.nodes.TextNode
import javax.inject.Inject

private const val JB_DOM = "org.jetbrains.compose.web.dom"
private const val KOBWEB_DOM = "com.varabyte.kobweb.compose.dom"
private const val SILK = "com.varabyte.kobweb.silk.components"

/**
 * Data available to [MarkdownHandlers] callbacks
 *
 * @param data A simple map that is created once per file and can be used by components however they want to.
 */
class NodeScope(val data: TypedMap, private val indentCountBase: Int = 0) {
    /** If set, will cause the Markdown visit to visit these nodes instead of the node's original children. */
    var childrenOverride: List<Node>? = null

    /**
     * Convenience method for adding indents in front of your lines of code.
     *
     * The indent applied here will be consistent with the indent used by the Markdown -> Kotlin renderer.
     */
    fun indent(indentCount: Int) = "    ".repeat(indentCountBase + indentCount)
}

/**
 * Register custom handlers for various Markdown elements.
 *
 * For example, if your project declares a fancy, custom horizontal rule, you can register it like so:
 *
 * ```
 * kobweb {
 *   markdown {
 *     handlers {
 *       hr.set { "com.myproject.components.widgets.FancyRule()" }
 *     }
 *   }
 * }
 * ```
 */
abstract class MarkdownHandlers @Inject constructor(project: Project) {
    companion object {
        /** Key used by [Heading] nodes to store IDs they generated for themselves. */
        val HeadingIdsKey = Key.create<MutableMap<Heading, String>>("md.heading.ids")
    }

    /**
     * The root composable to use as a fallback if no other root is provided.
     *
     * All markdown files, when converted to code, should have some root composable called first, wrapping all content.
     * This root is often done set a YAML block, but we should handle the case where one is not specified (either
     * because the YAML block doesn't include it or when there's no YAML block at all). This is because Kobweb's default
     * layouts use a Box as the root, which just stacks elements on top of each other.
     *
     * Note that a user might have overridden the Kobweb default root layout via the `@App` annotation, in which case,
     * they may not need to specify a root at all here (since that could just add an unnecessary extra layer to the DOM
     * tree). To indicate this (expectedly rare) case, this value may be set to the empty string to disable it.
     */
    abstract val defaultRoot: Property<String>

    /**
     * Use Silk components instead of Compose HTML components when relevant.
     *
     * If the user's project doesn't have a dependency on the Silk library, this should be set to false.
     */
    abstract val useSilk: Property<Boolean>

    /**
     * If true, attach an auto-generated header ID to each header element.
     *
     * For example,
     *
     * ```markdown
     * # This Is A Section
     * ```
     *
     * will generate a header tag with `id="this-is-a-section"`
     *
     * See also [idGenerator] if you need to override the default algorithm used for generating these IDs.
     */
    abstract val generateHeaderIds: Property<Boolean>

    /**
     * Handler for converting some incoming text (fairly unconstrained) into a final string value that should be used as
     * an ID for a URL fragment.
     *
     * By default, this simply only accepts letters and digits and converts everything else to hyphens (while removing
     * any duplicate neighboring hyphens), producing a lowercase value.
     *
     * However, if your project needs more fine-grained control over the generated names, you can set this callback
     * however you see fit.
     *
     * If you override this callback, you may want to check with https://www.rfc-editor.org/rfc/rfc3986 to ensure the ID
     * generated is valid.
     *
     * @see [generateHeaderIds]
     */
    abstract val idGenerator: Property<(String) -> String>

    abstract val text: Property<NodeScope.(Text) -> String>
    abstract val img: Property<NodeScope.(Image) -> String>
    abstract val heading: Property<NodeScope.(Heading) -> String>
    abstract val p: Property<NodeScope.(Paragraph) -> String>
    abstract val br: Property<NodeScope.(HardLineBreak) -> String>
    abstract val a: Property<NodeScope.(Link) -> String>
    abstract val em: Property<NodeScope.(Emphasis) -> String>
    abstract val strong: Property<NodeScope.(StrongEmphasis) -> String>
    abstract val hr: Property<NodeScope.(ThematicBreak) -> String>
    abstract val ul: Property<NodeScope.(BulletList) -> String>
    abstract val ol: Property<NodeScope.(OrderedList) -> String>
    abstract val li: Property<NodeScope.(ListItem) -> String>
    abstract val code: Property<NodeScope.(FencedCodeBlock) -> String>
    abstract val inlineCode: Property<NodeScope.(Code) -> String>
    abstract val blockquote: Property<NodeScope.(BlockQuote) -> String>
    abstract val table: Property<NodeScope.(TableBlock) -> String>
    abstract val thead: Property<NodeScope.(TableHead) -> String>
    abstract val tbody: Property<NodeScope.(TableBody) -> String>
    abstract val tr: Property<NodeScope.(TableRow) -> String>
    abstract val td: Property<NodeScope.(TableCell) -> String>
    abstract val th: Property<NodeScope.(TableCell) -> String>

    /** Handler which is fed the raw text (name and attributes) within an opening tag, e.g. `span id="demo"` */
    abstract val rawTag: Property<NodeScope.(String) -> String>
    abstract val inlineTag: Property<NodeScope.(HtmlInline) -> String>

    abstract val html: Property<NodeScope.(HtmlBlock) -> String>

    fun String.escapeSingleQuotedText() = escapeQuotes().escapeDollars()
    fun String.escapeTripleQuotedText() = escapeDollars()

    init {
        project.afterEvaluate {
            useSilk.convention(project.hasJsDependencyNamed("kobweb-silk"))
        }

        defaultRoot.convention("com.varabyte.kobweb.compose.foundation.layout.Column")

        generateHeaderIds.convention(true)
        idGenerator.convention { text ->
            val mergedText = text
                .map { c ->
                    when {
                        c.isLetterOrDigit() -> c.lowercase()
                        else -> '-'
                    }
                }
                .joinToString("")

            // Regexes are hard to read, so what's happening here is sometimes multiple special characters / spaces
            // could end up next to each other, causing double (or more) repeated dashes. We compress those so the
            // string doesn't look weird.
            mergedText
                .replace(Regex("""--+"""), "-")
                .removePrefix("-")
                .removeSuffix("-")
        }

        // region Markdown Node handlers

        text.convention { text -> "$JB_DOM.Text(\"${text.literal.escapeSingleQuotedText()}\")" }
        img.convention { img ->
            val altText = img.children()
                .filterIsInstance<Text>()
                .map { it.literal.escapeSingleQuotedText() }
                .joinToString("")
            this.childrenOverride = emptyList()

            if (useSilk.get()) {
                """$SILK.graphics.Image("${img.destination}", "$altText")"""
            } else {
                """$JB_DOM.Img("${img.destination}", "$altText")"""
            }
        }
        heading.convention { heading ->
            buildString {
                append("$JB_DOM.H${heading.level}")
                if (generateHeaderIds.get()) {
                    val text = heading.children()
                        .mapNotNull { node ->
                            when (node) {
                                is Text -> node.literal
                                is Code -> node.literal
                                else -> null
                            }
                        }
                        .joinToString("")
                    val headingIds = data.computeIfAbsent(HeadingIdsKey) { mutableMapOf() }
                    val id = run {
                        val baseId = idGenerator.get().invoke(text)
                        var currId = baseId
                        var count = 2
                        while (headingIds.containsValue(currId)) {
                            currId = "$baseId-$count"
                            ++count
                        }
                        currId
                    }
                    headingIds[heading] = id
                    append("(attrs = { id(\"$id\") })")
                }
            }
        }
        p.convention { "$JB_DOM.P" }
        br.convention { "$JB_DOM.Br" }
        a.convention { link ->
            if (useSilk.get()) {
                val linkText = link.children()
                    .filterIsInstance<Text>()
                    .firstOrNull()
                    ?.literal
                    ?.escapeSingleQuotedText()
                    .orEmpty()

                childrenOverride = listOf() // We "consumed" the children, no more need to visit them
                "$SILK.navigation.Link(\"${link.destination}\", \"$linkText\")"
            } else {
                "$JB_DOM.A(\"${link.destination}\")"
            }
        }
        em.convention { "$JB_DOM.Em" }
        strong.convention { "$JB_DOM.B" }
        hr.convention {
            if (useSilk.get()) {
                "$SILK.layout.Divider"
            } else {
                "$JB_DOM.Hr"
            }
        }
        ul.convention { "$JB_DOM.Ul" }
        ol.convention { "$JB_DOM.Ol" }
        li.convention { "$JB_DOM.Li" }
        code.convention { codeBlock ->
            val text = "\"\"\"${codeBlock.literal.escapeTripleQuotedText()}\"\"\""
            // Code blocks should generate <pre><code>...</code></pre>
            // https://daringfireball.net/projects/markdown/syntax#precode
            "$JB_DOM.Pre { $JB_DOM.Code { $JB_DOM.Text($text) } }"
        }
        inlineCode.convention { code ->
            childrenOverride = listOf(Text(code.literal))
            "$JB_DOM.Code"
        }
        blockquote.convention {
            "$KOBWEB_DOM.GenericTag(\"blockquote\")"
        }
        table.convention { "$JB_DOM.Table" }
        thead.convention { "$JB_DOM.Thead" }
        tbody.convention { "$JB_DOM.Tbody" }
        tr.convention { "$JB_DOM.Tr" }

        // Convert a map of CSS style properties to an `style { ... }` block
        fun Map<String, String>.toStylesBlock(): String {
            val styleMap = this.takeIf { it.isNotEmpty() } ?: return ""
            return buildString {
                append("style {")
                append(styleMap.map { (key, value) -> "property(\"$key\", \"$value\")" }.joinToString(";"))
                append("}")
            }
        }
        // Create relevant `(attrs = { ... })` call parameters for a table cell
        fun TableCell.toCallParams(): String {
            val alignment = alignment ?: return ""

            val properties = mutableMapOf<String, String>()
            properties["text-align"] = alignment.name.lowercase()
            return "(attrs = { ${properties.toStylesBlock()} })"
        }

        td.convention { cell -> "$JB_DOM.Td${cell.toCallParams()}" }
        th.convention { cell -> "$JB_DOM.Th${cell.toCallParams()}" }

        fun String.stripTagBrackets() =
            this.removePrefix("</").removePrefix("<").removeSuffix("/>").removeSuffix(">")

        rawTag.convention { tag ->
            val parts = tag.stripTagBrackets().split(' ', limit = 2)
            val name = "\"${parts[0]}\""
            val attrs = parts.getOrNull(1)?.escapeQuotes()?.let { "\"$it\"" } ?: "null"

            "$KOBWEB_DOM.GenericTag($name, $attrs)"
        }

        inlineTag.set { htmlInline ->
            val voidElements = setOf("br", "hr", "img")
            val tag = htmlInline.literal

            val scope = this
            buildString {
                if (!tag.startsWith("</")) {
                    append(rawTag.get().invoke(scope, tag))
                    if (!tag.endsWith("/>") && tag.stripTagBrackets() !in voidElements) {
                        append(" {")
                    }
                } else {
                    // Closing tag
                    append("}")
                }
            }
        }

        html.set { htmlBlock ->
            fun renderNode(el: Element, indentCount: Int, sb: StringBuilder) {
                sb.append("${indent(indentCount)}$KOBWEB_DOM.GenericTag(\"${el.tagName()}\"")

                if (el.attributesSize() > 0) {
                    sb.append(", ")
                    sb.append('"')
                    sb.append(
                        el.attributes().joinToString(" ") { attr ->
                            """${attr.key}=\"${attr.value.escapeSingleQuotedText()}\""""
                        }
                    )
                    sb.append('"')
                }

                if (el.childNodeSize() > 0) {
                    sb.appendLine(") {")
                    el.childNodes().forEach { child ->
                        if (child is TextNode) {
                            if (child.text().isNotBlank()) {
                                sb.appendLine(indent(indentCount + 1) + text.get().invoke(this, Text(child.text().trim())))
                            }
                        } else if (child is Element) {
                            renderNode(child, indentCount + 1, sb)
                            if (!sb.endsWith("\n")) {
                                sb.appendLine()
                            }
                        }
                    }
                    sb.appendLine(indent(indentCount) + "}")
                } else {
                    sb.append(')')
                }
            }

            val sb = StringBuilder()
            val doc = Jsoup.parseBodyFragment(htmlBlock.literal)
            val body = doc.body()
            // Children size can be 0 (if input text was a <!-- comment -->) or 1, if we have a single root element
            check(body.childrenSize() <= 1) { "Unexpected html block in Markdown." }
            doc.body().children().first()?.let { root -> renderNode(root, indentCount = 0, sb) }

            sb.toString()
        }

        // endregion
    }
}
