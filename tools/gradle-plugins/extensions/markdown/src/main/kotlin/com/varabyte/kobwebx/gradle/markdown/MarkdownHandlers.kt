@file:Suppress("LeakingThis") // Following official Gradle guidance

package com.varabyte.kobwebx.gradle.markdown

import com.varabyte.kobweb.common.collect.Key
import com.varabyte.kobweb.common.collect.TypedMap
import com.varabyte.kobweb.gradle.core.util.getJsDependencyResults
import com.varabyte.kobweb.gradle.core.util.hasDependencyNamed
import com.varabyte.kobweb.project.common.PackageUtils
import com.varabyte.kobwebx.gradle.markdown.util.escapeDollars
import com.varabyte.kobwebx.gradle.markdown.util.escapeQuotes
import com.varabyte.kobwebx.gradle.markdown.util.escapeTripleQuotes
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
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Nested
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.nodes.TextNode
import javax.inject.Inject

private const val JB_DOM = "org.jetbrains.compose.web.dom"
private const val KOBWEB_DOM = "com.varabyte.kobweb.compose.dom"
private const val SILK = "com.varabyte.kobweb.silk.components"

val SilkCalloutTypes = mapOf(
    "CAUTION" to "$SILK.display.CalloutType.CAUTION",
    "IMPORTANT" to "$SILK.display.CalloutType.IMPORTANT",
    "NOTE" to "$SILK.display.CalloutType.NOTE",
    "QUESTION" to "$SILK.display.CalloutType.QUESTION",
    "QUOTE" to "$SILK.display.CalloutType.QUOTE",
    "TIP" to "$SILK.display.CalloutType.TIP",
    "WARNING" to "$SILK.display.CalloutType.WARNING",
)

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
 * Creates a handler for the blockquote type which delegates to the Silk `Callout` widget is using silk.
 *
 * IMPORTANT: It is an error to use this in a project that does not depend on Silk.
 *
 * When set, Silk can parse blockquotes with a special syntax to generate callouts. For example:
 *
 * ```
 * > [!NOTE]
 * > This is a note.
 * ```
 *
 * will generate a note callout.
 *
 * The default list of callout keywords are, by default, provided by [SilkCalloutTypes] map, but users can extend
 * this set themselves or provide their own by setting the [types] parameter:
 *
 * ```
 * markdown {
 *    handlers.blockquote = CalloutBlockquoteHandler(
 *      types = SilkCalloutTypes + mapOf("CUSTOM" to ".components.widgets.callouts.CustomCalloutType")
 *    )
 * }
 * ```
 *
 * By default, the label for a callout is the type itself (e.g. "Note", "Tip", etc.). However, the label can be
 * overridden by users on a case-by-case basis by specifying the label inside the callout syntax. For
 * example: `[!NOTE "My Custom Label"]`.
 *
 * But you can change the default label globally, specifying it in the [labels] parameter. For example, if you want
 * to set the "QUOTE" type to have an empty label by default (which looks clean), you can set the [labels]
 * parameter to `mapOf("QUOTE" to "")`. NOTE: If you specify a key in [labels] that isn't also registered in
 * [types], it will essentially be ignored.
 *
 * Finally, you can specify an alternate callout variant to use (perhaps `OutlinedCalloutVariant` or something from
 * your own project) by setting the [variant] parameter.
 */
fun CalloutBlockquoteHandler(
    types: Map<String, String> = SilkCalloutTypes,
    labels: Map<String, String> = emptyMap(),
    variant: String? = null,
): NodeScope.(BlockQuote) -> String {
    return { blockQuote ->
        val silkCallout = blockQuote.firstChild.firstChild?.let { firstChild ->
            firstChild as Text
            val regex = """\[!([^ ]+)( "(.*)")?]""".toRegex()
            val typeMatch = regex.find(firstChild.literal) ?: return@let null
            firstChild.literal = firstChild.literal.substringAfter(typeMatch.value)

            val typeId = typeMatch.groupValues[1]
            val isLabelSet = typeMatch.groupValues[2].isNotBlank()
            val label = typeMatch.groupValues[3].takeIf { isLabelSet } ?: labels[typeId]

            val calloutTypeFqn = types[typeId]?.let {
                PackageUtils.resolvePackageShortcut(data.getValue(MarkdownHandlers.DataKeys.ProjectGroup), it)
            }

            @Suppress("NAME_SHADOWING") val variant = variant ?: "$SILK.display.CalloutDefaults.Variant"

            if (calloutTypeFqn != null) {
                """$SILK.display.Callout(type = $calloutTypeFqn, label = ${label?.let { "\"$it\"" }}, variant = $variant)"""
            } else {
                """$SILK.display.Callout(type = $SILK.display.CalloutType.UNKNOWN, label = "Invalid callout type [!$typeId]", variant = $variant)"""
            }
        }
        silkCallout ?: "$KOBWEB_DOM.GenericTag(\"blockquote\")"
    }
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
abstract class MarkdownHandlers @Inject constructor(project: Project, newDefaultRoot: Property<String>) {
    /**
     * Keys which can be used to read/write data values into/out of the [NodeScope.data] container.
     */
    object DataKeys {
        /** Key used by [Heading] nodes to store IDs they generated for themselves. */
        val HeadingIds = Key.create<MutableMap<Heading, String>>("md.heading.ids")

        /**
         * Key used to fetch the project group name.
         *
         * Using this instead of `project.group` avoids conflicting with the Gradle build cache.
         */
        val ProjectGroup = Key.create<String>("md.project.group")
    }

    companion object {
        @Deprecated("Use `MarkdownHandlers.DataKeys.HeadingIds` instead.")
        val HeadingIdsKey = DataKeys.HeadingIds
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
    @Deprecated("Use the `defaultRoot` property in the parent markdown block instead.")
    @get:Internal
    val defaultRoot: Property<String> = newDefaultRoot

    /**
     * Use Silk components instead of Compose HTML components when relevant.
     *
     * If the user's project doesn't have a dependency on the Silk library, this should be set to false.
     */
    @get:Input
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
    @get:Input
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
     * @see generateHeaderIds
     */
    @get:Nested
    abstract val idGenerator: Property<(String) -> String>

    @get:Nested
    abstract val text: Property<NodeScope.(Text) -> String>
    @get:Nested
    abstract val img: Property<NodeScope.(Image) -> String>
    @get:Nested
    abstract val heading: Property<NodeScope.(Heading) -> String>
    @get:Nested
    abstract val p: Property<NodeScope.(Paragraph) -> String>
    @get:Nested
    abstract val br: Property<NodeScope.(HardLineBreak) -> String>
    @get:Nested
    abstract val a: Property<NodeScope.(Link) -> String>
    @get:Nested
    abstract val em: Property<NodeScope.(Emphasis) -> String>
    @get:Nested
    abstract val strong: Property<NodeScope.(StrongEmphasis) -> String>
    @get:Nested
    abstract val hr: Property<NodeScope.(ThematicBreak) -> String>
    @get:Nested
    abstract val ul: Property<NodeScope.(BulletList) -> String>
    @get:Nested
    abstract val ol: Property<NodeScope.(OrderedList) -> String>
    @get:Nested
    abstract val li: Property<NodeScope.(ListItem) -> String>
    @get:Nested
    abstract val code: Property<NodeScope.(FencedCodeBlock) -> String>
    @get:Nested
    abstract val inlineCode: Property<NodeScope.(Code) -> String>
    @get:Nested
    abstract val blockquote: Property<NodeScope.(BlockQuote) -> String>
    @get:Nested
    abstract val table: Property<NodeScope.(TableBlock) -> String>
    @get:Nested
    abstract val thead: Property<NodeScope.(TableHead) -> String>
    @get:Nested
    abstract val tbody: Property<NodeScope.(TableBody) -> String>
    @get:Nested
    abstract val tr: Property<NodeScope.(TableRow) -> String>
    @get:Nested
    abstract val td: Property<NodeScope.(TableCell) -> String>
    @get:Nested
    abstract val th: Property<NodeScope.(TableCell) -> String>

    /** Handler which is fed the raw text (name and attributes) within an opening tag, e.g. `span id="demo"` */
    @get:Nested
    abstract val rawTag: Property<NodeScope.(String) -> String>
    @get:Nested
    abstract val inlineTag: Property<NodeScope.(HtmlInline) -> String>

    @get:Nested
    abstract val html: Property<NodeScope.(HtmlBlock) -> String>

    fun String.escapeSingleQuotedText() = escapeQuotes().escapeDollars()
    fun String.escapeTripleQuotedText() = escapeDollars().escapeTripleQuotes()

    /**
     * Data pulled out of an [Image] node into an easier-to-consume format.
     *
     * @see processImage
     */
    class ImageData(val destination: String, val altText: String, val title: String?)

    /**
     * Helper function to process an [Image] node, passing information to a callback to generate the final output.
     *
     * Users can override the `img` handler like so:
     *
     * ```
     * kobweb {
     *   markdown {
     *     handlers {
     *       img.set {
     *         processImage(it) { data ->
     *           "com.myproject.components.widgets.ImageWidget(\"${data.destination}\", \"${data.altText}\")"
     *         }
     *       }
     *     }
     *   }
     * }
     * ```
     *
     * Users can of course process the [Image] node directly, but they should know that it carries its alt text as
     * children nodes, which this logic abstracts away.
     */
    fun NodeScope.processImage(image: Image, output: (ImageData) -> String): String {
        val altText = image.children()
            .filterIsInstance<Text>()
            .map { it.literal.escapeSingleQuotedText() }
            .joinToString("")
        this.childrenOverride = emptyList()

        return output(ImageData(image.destination, altText, image.title))
    }

    init {
        useSilk.convention(project.getJsDependencyResults().hasDependencyNamed("com.varabyte.kobweb:kobweb-silk"))

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
        img.convention { image ->
            processImage(image) { data ->
                buildString {
                    append(if (useSilk.get()) "$SILK.graphics.Image" else "$JB_DOM.Img")
                    append("""("${data.destination}", "${data.altText}")""")
                }
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
                    val headingIds = data.computeIfAbsent(DataKeys.HeadingIds) { mutableMapOf() }
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
                "$SILK.navigation.Link(\"${link.destination}\")"
            } else {
                "$JB_DOM.A(\"${link.destination}\")"
            }
        }
        em.convention { "$JB_DOM.Em" }
        strong.convention { "$JB_DOM.B" }
        hr.convention {
            if (useSilk.get()) {
                "$SILK.layout.HorizontalDivider"
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
        blockquote.convention { blockquote ->
            if (useSilk.get()) {
                CalloutBlockquoteHandler().invoke(this, blockquote)
            } else {
                "$KOBWEB_DOM.GenericTag(\"blockquote\")"
            }
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
                                // child text of inline html seem to begin with a newline that we don't want to treat as
                                // part of the final text.
                                // e.g.
                                // <pre>
                                // test
                                // </pre>
                                // should be the string "test", not "\ntest"
                                sb.appendLine(
                                    "${indent(indentCount + 1)}$JB_DOM.Text(\"\"\"${
                                        child.wholeText.removePrefix("\n").escapeSingleQuotedText()
                                    }\"\"\")"
                                )
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
            doc.body().children().forEach { root -> renderNode(root, indentCount = 0, sb) }

            sb.toString()
        }

        // endregion
    }
}
