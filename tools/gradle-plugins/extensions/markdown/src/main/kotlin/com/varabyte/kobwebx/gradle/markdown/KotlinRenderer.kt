package com.varabyte.kobwebx.gradle.markdown

import com.varabyte.kobweb.common.collect.TypedMap
import com.varabyte.kobweb.common.navigation.Route
import com.varabyte.kobweb.common.text.isSurrounded
import com.varabyte.kobweb.gradle.core.util.Reporter
import com.varabyte.kobweb.project.common.PackageUtils
import com.varabyte.kobwebx.gradle.markdown.ext.kobwebcall.KobwebCall
import com.varabyte.kobwebx.gradle.markdown.ext.kobwebcall.KobwebCallBlock
import com.varabyte.kobwebx.gradle.markdown.ext.kobwebcall.KobwebCallBlockVisitor
import com.varabyte.kobwebx.gradle.markdown.handlers.MarkdownHandlers
import com.varabyte.kobwebx.gradle.markdown.handlers.NodeScope
import com.varabyte.kobwebx.gradle.markdown.util.NodeCache
import com.varabyte.kobwebx.gradle.markdown.util.escapeQuotes
import com.varabyte.kobwebx.gradle.markdown.util.unescapeQuotes
import com.varabyte.kobwebx.gradle.markdown.util.unescapeTicks
import org.commonmark.ext.front.matter.YamlFrontMatterBlock
import org.commonmark.ext.front.matter.YamlFrontMatterVisitor
import org.commonmark.ext.gfm.tables.TableBlock
import org.commonmark.ext.gfm.tables.TableBody
import org.commonmark.ext.gfm.tables.TableCell
import org.commonmark.ext.gfm.tables.TableHead
import org.commonmark.ext.gfm.tables.TableRow
import org.commonmark.node.AbstractVisitor
import org.commonmark.node.BlockQuote
import org.commonmark.node.BulletList
import org.commonmark.node.Code
import org.commonmark.node.CustomBlock
import org.commonmark.node.CustomNode
import org.commonmark.node.Emphasis
import org.commonmark.node.FencedCodeBlock
import org.commonmark.node.HardLineBreak
import org.commonmark.node.Heading
import org.commonmark.node.HtmlBlock
import org.commonmark.node.HtmlInline
import org.commonmark.node.Image
import org.commonmark.node.IndentedCodeBlock
import org.commonmark.node.Link
import org.commonmark.node.ListBlock
import org.commonmark.node.ListItem
import org.commonmark.node.Node
import org.commonmark.node.OrderedList
import org.commonmark.node.Paragraph
import org.commonmark.node.SoftLineBreak
import org.commonmark.node.StrongEmphasis
import org.commonmark.node.Text
import org.commonmark.node.ThematicBreak
import org.commonmark.renderer.Renderer
import org.gradle.api.provider.Provider
import java.nio.file.Path
import java.util.*
import kotlin.io.path.invariantSeparatorsPathString

fun String.yamlStringToKotlinString(): String {
    return if (this.isSurrounded("\"")) {
        this.removeSurrounding("\"").unescapeQuotes()
    } else if (this.isSurrounded("'")) {
        this.removeSurrounding("'").unescapeTicks()
    } else {
        this
    }
}

/**
 * A markdown renderer that generates a Kobweb source file given an input markdown file.
 *
 * @property projectGroup The group of the project, which is used to resolve package shortcuts, e.g. "com.mysite"
 * @property nodeCache Cache which allows us to look up nodes by their relative path.
 * @property nodeMetadata Additional information about each node, including our own. This can be used to look up
 *   relevant file information about ourselves or other nodes fetched through the [nodeCache].
 * @property defaultRoot The default root layout to use if not specified in the markdown file. If null, and no root is
 *   specified by the markdown file, then no outer root node will be added to the page.
 * @property imports A list of additional imports to include at the top of the generated file.
 * @property handlers A set of handlers that can be used to customize how different markdown nodes are rendered.
 * @property pkg The package that the generated file should be placed in.
 * @property funName The name of the page function that will be generated.
 * @property reporter A reporter that can be used to log warnings and errors.
 */
class KotlinRenderer internal constructor(
    private val projectGroup: String,
    private val nodeCache: NodeCache,
    private val defaultRoot: String?,
    private val imports: List<String>,
    private val handlers: MarkdownHandlers,
    private val funName: String,
    // If true, we have access to the `MarkdownContext` class and CompositionLocal
    private val dependsOnMarkdownArtifact: Boolean,
    private val reporter: Reporter,
) : Renderer {
    private var indentCount = 0
    private val indent get() = NodeScope(reporter, TypedMap()).indent(indentCount)

    // Flexible data which can be used by Node handlers however they need
    private val data = TypedMap().apply {
        set(MarkdownHandlers.DataKeys.ProjectGroup, projectGroup)
    }

    override fun render(node: Node, output: Appendable) {
        node.accept(SoftLineBreakConversionVisitor())
        node.accept(TextMergingVisitor())

        val frontMatterData = with(FrontMatterVisitor()) {
            node.accept(this)
            this.data
        }

        val metadataEntry = nodeCache.metadata.getValue(node)

        output.append(
            buildString {
                appendLine("package ${metadataEntry.`package`}")
                appendLine()
                appendLine("import androidx.compose.runtime.*")
                appendLine("import com.varabyte.kobweb.core.*")
                if (dependsOnMarkdownArtifact) {
                    appendLine("import com.varabyte.kobwebx.markdown.*")
                }
                (imports + frontMatterData?.imports.orEmpty()).forEach { importPath ->
                    appendLine("import ${PackageUtils.resolvePackageShortcut(projectGroup, importPath)}")
                }

                appendLine()

                metadataEntry.routeWithSlug?.let { routeWithSlug ->
                    append("@Page(\"")
                    append(frontMatterData?.routeOverride?.let {
                        if (it.startsWith("/")) it else metadataEntry.routeWithoutSlug + it
                    } ?: routeWithSlug)
                    appendLine("\")")
                }

                appendLine("@Composable")
                appendLine("fun ${frontMatterData?.funName ?: funName}() {")
            }
        )

        indentCount++
        RenderVisitor(output, metadataEntry, frontMatterData).visitAndFinish(node)
        indentCount--

        assert(indentCount == 0)
        output.appendLine("}")
    }

    override fun render(node: Node): String {
        return buildString {
            render(node, this)
        }
    }

    private fun RenderVisitor.visitAndFinish(node: Node) {
        node.accept(this)
        finish()
    }

    /** Soft breaks between lines should just become spaces. */
    private inner class SoftLineBreakConversionVisitor : AbstractVisitor() {
        override fun visit(softLineBreak: SoftLineBreak) {
            val space = Text(" ")
            space.sourceSpans = softLineBreak.sourceSpans
            softLineBreak.insertAfter(space)
            softLineBreak.unlink()
        }
    }

    /** Avoid a bunch of unnecessary calls to Text functions by merging all sibling text strings together. */
    private inner class TextMergingVisitor : AbstractVisitor() {
        override fun visit(text: Text) {
            (text.previous as? Text)?.let { textPrev ->
                val merged = Text(textPrev.literal + text.literal)
                merged.sourceSpans = textPrev.sourceSpans + text.sourceSpans
                textPrev.insertBefore(merged)
                textPrev.unlink()
                text.unlink()
            }
        }
    }

    private class FrontMatterData(val raw: Map<String, List<String>>) {
        val root: String? get() = raw["root"]?.singleOrNull()
        val funName: String? get() = raw["funName"]?.singleOrNull()
        val imports: List<String>? get() = raw["imports"]
        val routeOverride: String? get() = raw["routeOverride"]?.singleOrNull()

        // Hide front matter data from the user that is meant to be consumed by the renderer
        fun filterUserData(): Map<String, List<String>> {
            return raw.filterKeys {
                it != "root" &&
                    it != "funName" &&
                    it != "imports" &&
                    it != "routeOverride"
            }
        }
    }

    /** Read data out of the front matter block (if present) */
    private inner class FrontMatterVisitor : AbstractVisitor() {
        var data: FrontMatterData? = null
            private set

        override fun visit(customBlock: CustomBlock) {
            if (customBlock is YamlFrontMatterBlock) {
                val yamlVisitor = YamlFrontMatterVisitor()
                customBlock.accept(yamlVisitor)

                data = FrontMatterData(yamlVisitor.data)
            }
        }
    }

    private inner class RenderVisitor(
        private val output: Appendable,
        private val metadataEntry: NodeCache.Metadata.Entry,
        frontMatterData: FrontMatterData?
    ) :
        AbstractVisitor() {
        private val onFinish = Stack<() -> Unit>()
        fun finish() {
            onFinish.forEach { action -> action() }
        }

        init {
            var contextCreated = false
            if (metadataEntry.routeWithSlug != null && dependsOnMarkdownArtifact) {
                val userData = frontMatterData
                    ?.filterUserData()
                    ?.mapValues { (_, values) -> values.map { it.yamlStringToKotlinString() } }
                    ?: emptyMap()

                val mdCtx = buildString {
                    append("MarkdownContext(")
                    append("\"${metadataEntry.sourceFilePath.invariantSeparatorsPathString}\"")
                    append(", ")
                    append(userData.serialize())
                    append(")")
                }
                output.appendLine("${indent}CompositionLocalProvider(LocalMarkdownContext provides $mdCtx) {")
                ++indentCount
                contextCreated = true
            }

            // If "root" is set in the YAML block, that represents a top level composable which should wrap
            // everything else.
            val root = frontMatterData?.root ?: defaultRoot
            if (root != null) {
                visit(KobwebCall(root, appendBrace = true))
                ++indentCount
            }

            onFinish += {
                if (root != null) {
                    --indentCount
                    output.appendLine("$indent}")
                }

                if (contextCreated) {
                    --indentCount
                    output.appendLine("$indent}")
                }
            }
        }

        private fun <N : Node> doVisit(node: N, composableCall: Provider<NodeScope.(N) -> String>) {
            val scope = NodeScope(reporter, data, indentCount)
            composableCall.get().invoke(scope, node).takeIf { it.isNotBlank() }?.let { code ->
                // Remove leading indentation (if any) because we add it ourselves
                doVisit(node, code.trimStart(), scope)
            }
        }

        private fun doVisit(node: Node, code: String, scope: NodeScope) {
            val children = scope.childrenOverride ?: sequence<Node> {
                var curr: Node? = node.firstChild
                while (curr != null) {
                    yield(curr)
                    curr = curr.next
                }
            }.toList()

            if (code.last() == '}' && !code.contains('{')) {
                --indentCount
            }
            output.append("$indent$code")
            // If this is a single line call (no children), we need to explicitly add the "()" parens if not already
            // done by the caller. Otherwise, we'll do "composableCall { ... }", which is really calling a function with
            // a lambda.
            if (code.last().isLetterOrDigit() && children.isEmpty()) {
                output.append("()")
            } else if (code.last() == '{') {
                ++indentCount
            }

            if (children.isNotEmpty()) {
                output.appendLine(" {")
                ++indentCount
                children.forEach { child -> child.accept(this) }
                --indentCount
                output.appendLine("$indent}")
            } else {
                output.appendLine()
            }
        }

        override fun visit(blockQuote: BlockQuote) {
            doVisit(blockQuote, handlers.blockquote)
        }

        override fun visit(code: Code) {
            doVisit(code, handlers.inlineCode)
        }

        override fun visit(emphasis: Emphasis) {
            doVisit(emphasis, handlers.em)
        }

        override fun visit(fencedCodeBlock: FencedCodeBlock) {
            doVisit(fencedCodeBlock, handlers.code)
        }

        override fun visit(hardLineBreak: HardLineBreak) {
            doVisit(hardLineBreak, handlers.br)
        }

        override fun visit(heading: Heading) {
            doVisit(heading, handlers.heading)
        }

        override fun visit(thematicBreak: ThematicBreak) {
            doVisit(thematicBreak, handlers.hr)
        }

        override fun visit(htmlInline: HtmlInline) {
            doVisit(htmlInline, handlers.inlineTag)
        }

        override fun visit(htmlBlock: HtmlBlock) {
            doVisit(htmlBlock, handlers.html)
        }

        override fun visit(image: Image) {
            doVisit(image, handlers.img)
        }

        override fun visit(indentedCodeBlock: IndentedCodeBlock) {
            // Delegate to fenced code blocks, which (as far as I can tell) are a superset of indented code blocks
            val fenced = FencedCodeBlock().apply { literal = indentedCodeBlock.literal }
            visit(fenced)
        }

        override fun visit(link: Link) {
            // Relative links to other Markdown files, if they are present, get converted to their corresponding
            // generated routes, e.g. "[link](a/b.md)" -> "[link](a/b)". This isn't as simple as dropping the ".md"
            // extension however -- some pages override their route (e.g. "b.md" actually routes to "c/d"), and we
            // handle that here as well. We intentionally avoid absolute links
            // (e.g. "[link](http://path/to/example.md)"), since in that case, the user is explicitly linking to an
            // external resource.
            if (link.destination.endsWith(".md") && !link.destination.contains("://")) {
                val destinationPath = metadataEntry.sourceFilePath.resolveSibling(link.destination).normalize().toString()
                val destinationNode = nodeCache.getRelative(destinationPath.removePrefix("/"))
                if (destinationNode != null) {
                    // Retrieve the destination's route override, if present
                    val frontMatterData = with(FrontMatterVisitor()) {
                        destinationNode.accept(this)
                        this.data
                    }

                    val route = Route(
                        frontMatterData?.routeOverride
                        ?: nodeCache.metadata.getValue(destinationNode).routeWithSlug!! // Guaranteed set for a page
                    )
                    if (route.isDynamic) {
                        error("Markdown file link '${link.destination}' links to file with dynamic route override. This is not supported!")
                    }

                    link.destination = route.resolve(destinationPath)
                }
            }

            doVisit(link, handlers.a)
        }

        override fun visit(listItem: ListItem) {
            doVisit(listItem, handlers.li)
        }

        override fun visit(bulletList: BulletList) {
            doVisit(bulletList, handlers.ul)
        }

        override fun visit(orderedList: OrderedList) {
            doVisit(orderedList, handlers.ol)
        }

        override fun visit(paragraph: Paragraph) {
            // Detect:
            // <ul>
            //   <li><p>Markdown wraps list item text in paragraphs -- yes, it's surprising</p></li>
            fun Paragraph.isInTightList() = (parent?.parent as? ListBlock)?.isTight ?: false
            if (paragraph.isInTightList()) {
                visitChildren(paragraph)
            } else {
                doVisit(paragraph, handlers.p)
            }
        }

        override fun visit(strongEmphasis: StrongEmphasis) {
            doVisit(strongEmphasis, handlers.strong)
        }

        override fun visit(text: Text) {
            doVisit(text, handlers.text)
        }

        override fun visit(customNode: CustomNode) {
            when (customNode) {
                is KobwebCall -> {
                    output.appendLine("$indent${customNode.toFqn(projectGroup)}")
                }

                is TableHead -> visit(customNode)
                is TableBody -> visit(customNode)
                is TableRow -> visit(customNode)
                is TableCell -> visit(customNode)

                else -> {
                    val unhandledNodeName = customNode::class.simpleName!!
                    reporter.warn("Unhandled Markdown custom node: $unhandledNodeName. Consider reporting this at: https://github.com/varabyte/kobweb/issues/new?labels=bug&template=bug_report.md&title=Unhandled%20Markdown%20node%20%22$unhandledNodeName%22")
                }
            }
        }

        private fun visit(table: TableBlock) {
            doVisit(table, handlers.table)
        }

        private fun visit(tableHead: TableHead) {
            doVisit(tableHead, handlers.thead)
        }

        private fun visit(tableBody: TableBody) {
            doVisit(tableBody, handlers.tbody)
        }

        private fun visit(tableRow: TableRow) {
            doVisit(tableRow, handlers.tr)
        }

        private fun visit(tableCell: TableCell) {
            if (tableCell.isHeader) {
                doVisit(tableCell, handlers.th)
            } else {
                doVisit(tableCell, handlers.td)
            }
        }

        private fun List<String>.serialize(): String {
            return buildString {
                append("listOf(")
                append(joinToString { "\"${it.escapeQuotes()}\"" })
                append(")")
            }
        }

        private fun Map.Entry<String, List<String>>.serialize(): String {
            return "\"$key\" to ${value.serialize()}"
        }

        private fun Map<String, List<String>>.serialize(): String {
            return buildString {
                append("mapOf(")
                append(entries.joinToString { it.serialize() })
                append(")")
            }
        }

        override fun visit(customBlock: CustomBlock) {
            when (customBlock) {
                is KobwebCallBlock -> {
                    val visitor = KobwebCallBlockVisitor()
                    customBlock.accept(visitor)
                    visitor.call?.let { call ->
                        visit(call)
                        visitor.childrenNodes?.let { children ->
                            ++indentCount
                            children.forEach { node -> node.accept(this) }
                            --indentCount
                            output.appendLine("$indent}")
                        }
                    }
                }

                is YamlFrontMatterBlock -> {
                    // No-op. We don't need to do anything here because we already handled parsing front matter earlier.
                }

                is TableBlock -> visit(customBlock)

                else -> {
                    val unhandledBlockName = customBlock::class.simpleName!!
                    reporter.warn("Unhandled Markdown custom block: $unhandledBlockName. Consider reporting this at: https://github.com/varabyte/kobweb/issues/new?labels=bug&template=bug_report.md&title=Unhandled%20Markdown%20block%20%22$unhandledBlockName%22")
                }
            }
        }
    }
}
