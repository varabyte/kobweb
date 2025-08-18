package com.varabyte.kobwebx.gradle.markdown

import com.varabyte.kobweb.common.collect.TypedMap
import com.varabyte.kobweb.common.navigation.Route
import com.varabyte.kobweb.gradle.core.util.Reporter
import com.varabyte.kobweb.project.common.PackageUtils
import com.varabyte.kobwebx.frontmatter.FrontMatterElement
import com.varabyte.kobwebx.gradle.markdown.ext.kobwebcall.KobwebCall
import com.varabyte.kobwebx.gradle.markdown.ext.kobwebcall.KobwebCallBlock
import com.varabyte.kobwebx.gradle.markdown.ext.kobwebcall.KobwebCallBlockVisitor
import com.varabyte.kobwebx.gradle.markdown.frontmatter.FrontMatterBlock
import com.varabyte.kobwebx.gradle.markdown.handlers.KOBWEB_DOM
import com.varabyte.kobwebx.gradle.markdown.handlers.MarkdownHandlers
import com.varabyte.kobwebx.gradle.markdown.handlers.NodeScope
import com.varabyte.kobwebx.gradle.markdown.util.NodeCache
import com.varabyte.kobwebx.gradle.markdown.util.escapeQuotes
import com.varabyte.kobwebx.gradle.markdown.util.unescapeQuotes
import org.commonmark.ext.footnotes.FootnoteDefinition
import org.commonmark.ext.footnotes.FootnoteReference
import org.commonmark.ext.gfm.strikethrough.Strikethrough
import org.commonmark.ext.gfm.tables.TableBlock
import org.commonmark.ext.gfm.tables.TableBody
import org.commonmark.ext.gfm.tables.TableCell
import org.commonmark.ext.gfm.tables.TableHead
import org.commonmark.ext.gfm.tables.TableRow
import org.commonmark.ext.task.list.items.TaskListItemMarker
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
import java.util.*
import kotlin.io.path.invariantSeparatorsPathString

/**
 * A markdown renderer that generates a Kobweb source file given an input markdown file.
 *
 * @property projectGroup The group of the project, which is used to resolve package shortcuts, e.g. "com.mysite"
 * @property nodeCache Cache which allows us to look up nodes by their relative path.
 * @property defaultRoot The default root layout to use if not specified in the markdown file. If null, and no root is
 *   specified by the markdown file, then no outer root node will be added to the page.
 * @property imports A list of additional imports to include at the top of the generated file.
 * @property handlers A set of handlers that can be used to customize how different markdown nodes are rendered.
 * @property funName The name of the page function that will be generated.
 * @property reporter A reporter that can be used to log warnings and errors.
 */
class KotlinRenderer internal constructor(
    private val projectGroup: String,
    private val nodeCache: NodeCache,
    private val defaultLayout: String?,
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
                appendLine("import com.varabyte.kobweb.core.Page")
                appendLine("import com.varabyte.kobweb.core.data.*")
                appendLine("import com.varabyte.kobweb.core.layout.Layout")
                appendLine("import com.varabyte.kobweb.core.layout.NoLayout")
                appendLine("import com.varabyte.kobweb.core.init.InitRoute")
                appendLine("import com.varabyte.kobweb.core.init.InitRouteContext")
                if (dependsOnMarkdownArtifact) {
                    appendLine("import com.varabyte.kobwebx.frontmatter.*")
                    appendLine("import com.varabyte.kobwebx.markdown.*")
                }
                (imports + frontMatterData?.imports.orEmpty()).forEach { importPath ->
                    appendLine("import ${PackageUtils.resolvePackageShortcut(projectGroup, importPath)}")
                }

                appendLine()

                if (metadataEntry.routeWithSlug != null && dependsOnMarkdownArtifact) {
                    appendLine("@InitRoute")
                    appendLine("fun init${frontMatterData?.funName ?: funName}(ctx: InitRouteContext) {")
                    ++indentCount

                    fun StringBuilder.appendElement(key: String, element: FrontMatterElement) {
                        when (element) {
                            is FrontMatterElement.Scalar -> {
                                append("addScalar(\"$key\", \"${element.scalar.unescapeQuotes()}\"); ")
                            }

                            is FrontMatterElement.ValueList -> {
                                append("addList(\"$key\") { ")
                                element.list.mapNotNull { it.scalarOrNull() }.forEach { scalar ->
                                    append("addScalar(\"${scalar.unescapeQuotes()}\"); ")
                                }
                                append("}; ")
                            }

                            is FrontMatterElement.ValueMap -> {
                                append("addMap(\"$key\") { ")
                                element.map.forEach { (key, value) ->
                                    appendElement(key, value)
                                }
                                append("}; ")
                            }
                        }
                    }

                    val fmElementStr = frontMatterData?.filterUserData()?.takeIf { it.map.isNotEmpty() }?.let { filteredUserData ->
                        buildString {
                            append("FrontMatterElement.Builder { ")
                            filteredUserData.map.forEach { (key, value) ->
                                appendElement(key, value)
                            }
                            append("}")
                        }
                    } ?: "FrontMatterElement.EmptyMap"

                    val mdCtx = buildString {
                        append("MarkdownContext(")
                        append("\"${metadataEntry.sourceFilePath.invariantSeparatorsPathString}\"")
                        append(", ")
                        append(fmElementStr)
                        append(")")
                    }
                    appendLine("${indent}ctx.data.add($mdCtx)")
                    --indentCount
                    appendLine("}")
                    appendLine()
                }

                metadataEntry.routeWithSlug?.let { routeWithSlug ->
                    append("@Page(\"")
                    append(frontMatterData?.routeOverride?.let {
                        if (it.startsWith("/")) it else metadataEntry.routeWithoutSlug + it
                    } ?: routeWithSlug)
                    appendLine("\")")

                    (frontMatterData?.layout ?: defaultLayout)?.let { layout ->
                        if (layout.isNotEmpty()) {
                            appendLine("@Layout(\"$layout\")")
                        } else {
                            appendLine("@NoLayout")
                        }
                    }
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

    private class FrontMatterData(val raw: FrontMatterElement.ValueMap) {
        val root: String? get() = raw.map["root"]?.scalarOrNull()
        val layout: String? get() = raw.map["layout"]?.scalarOrNull()
        val funName: String? get() = raw.map["funName"]?.scalarOrNull()
        val imports: List<String>? get() = raw.map["imports"]?.scalarList() ?: emptyList()
        val routeOverride: String? get() = raw.map["routeOverride"]?.scalarOrNull()

        // Hide front matter data from the user that is meant to be consumed by the renderer
        fun filterUserData(): FrontMatterElement.ValueMap {
            return FrontMatterElement.ValueMap(raw.map.filterKeys {
                it != "root" &&
                    it != "layout" &&
                    it != "funName" &&
                    it != "imports" &&
                    it != "routeOverride"
            })
        }
    }

    /** Read data out of the front matter block (if present) */
    private inner class FrontMatterVisitor : AbstractVisitor() {
        var data: FrontMatterData? = null
            private set

        override fun visit(customBlock: CustomBlock) {
            if (customBlock is FrontMatterBlock) {
                data = FrontMatterData(customBlock.frontMatterNode.element)
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
            // If "root" is set in the YAML block, that represents a top-level composable which should wrap
            // everything else.
            val root = (frontMatterData?.root ?: defaultRoot)?.takeUnless {
                // A user is expected either to specify a layout (the new way) or a root (the old way), but not both.
                // Even if you can conceive of a case where you might want to specify both, it would probably be cleaner
                // if you updated the layout instead.
                (frontMatterData?.layout ?: defaultLayout) != null
            }?.takeUnless { it.isBlank() }
            if (root != null) {
                visit(KobwebCall(root, appendBrace = true))
                ++indentCount
            }

            onFinish += {
                if (root != null) {
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
        override fun visit(customNode: CustomNode) {
            when (customNode) {
                is KobwebCall -> {
                    output.appendLine("$indent${customNode.toFqn(projectGroup)}")
                }

                is TableHead -> visit(customNode)
                is TableBody -> visit(customNode)
                is TableRow -> visit(customNode)
                is TableCell -> visit(customNode)
                is Strikethrough -> visit(customNode)
                is FootnoteReference -> {
                    // Render footnote references as superscript with the actual label/number
                    val scope = NodeScope(reporter, data, indentCount)
                    val label = customNode.label
                    // Create a link to the footnote definition
                    val code = "$KOBWEB_DOM.GenericTag(\"sup\", \"id=\\\"fnref-$label\\\"\")"
                    scope.childrenOverride = listOf(
                        // Create a link to the footnote definition
                        org.commonmark.node.Link("#fn-$label", null).apply {
                            appendChild(Text(label))
                        }
                    )
                    doVisit(customNode, code, scope)
                }
                is TaskListItemMarker -> {
                    // Render a task list item, which is a checkbox that is either checked or not.
                    // We prefer to use Silk's FontAwesome icons if available, otherwise we fall back to a
                    // standard HTML checkbox.
                    val code = if (handlers.useSilk.get()) {
                        val icon = if (customNode.isChecked) {
                            "com.varabyte.kobweb.silk.components.icons.fa.FaSquareCheck"
                        } else {
                            "com.varabyte.kobweb.silk.components.icons.fa.FaSquare"
                        }
                        // Use a span to apply margin to the icon, giving it some breathing room from the text.
                        "$KOBWEB_DOM.GenericTag(\"span\", \"style=\\\"margin-right: 0.5em;\\\"\") { $icon() }"
                    } else {
                        val checkedAttr = if (customNode.isChecked) "checked" else ""
                        // The `disabled` attribute is important here, as these are decorative checkboxes.
                        // A right margin is added to space the checkbox from the list item's text.
                        val attrs = "type=\\\"checkbox\\\" disabled $checkedAttr style=\\\"margin-right: 0.5em;\\\""
                        "$KOBWEB_DOM.GenericTag(\"input\", \"$attrs\")"
                    }
                    output.appendLine("$indent$code")
                }
                else -> {
                    val unhandledNodeName = customNode::class.simpleName!!
                    reporter.warn("Unhandled Markdown custom node: $unhandledNodeName. Consider reporting this at: https://github.com/varabyte/kobweb/issues/new?labels=bug&template=bug_report.md&title=Unhandled%20Markdown%20node%20%22$unhandledNodeName%22")
                }
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

                is FrontMatterBlock -> {
                    // No-op. We don't need to do anything here because we already handled parsing front matter earlier.
                }

                is TableBlock -> visit(customBlock)
                is FootnoteDefinition -> {
                    // Render individual footnote definitions as block elements inside the footnote container
                    val scope = NodeScope(reporter, data, indentCount)
                    val code =
                        "com.varabyte.kobweb.compose.dom.GenericTag(\"div\", \"class=\\\"footnote-item\\\" id=\\\"fn-${customBlock.label}\\\"\")"
                    doVisit(customBlock, code, scope)
                }

                else -> {
                    val simple = customBlock::class.simpleName
                        val unhandledBlockName = simple!!
                        reporter.warn("Unhandled Markdown custom block: $unhandledBlockName. Consider reporting this at: https://github.com/varabyte/kobweb/issues/new?labels=bug&template=bug_report.md&title=Unhandled%20Markdown%20block%20%22$unhandledBlockName%22")

                }
            }
        }

        private fun visit(strikethrough: Strikethrough) {
            doVisit(strikethrough, handlers.strikethrough)
        }
    }
}
