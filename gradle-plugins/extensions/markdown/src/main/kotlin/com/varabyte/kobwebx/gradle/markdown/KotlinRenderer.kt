package com.varabyte.kobwebx.gradle.markdown

import com.varabyte.kobweb.common.collect.TypedMap
import com.varabyte.kobweb.gradle.application.extensions.hasDependencyNamed
import com.varabyte.kobwebx.gradle.markdown.ext.kobwebcall.KobwebCall
import com.varabyte.kobwebx.gradle.markdown.ext.kobwebcall.KobwebCallBlock
import com.varabyte.kobwebx.gradle.markdown.ext.kobwebcall.KobwebCallVisitor
import org.commonmark.ext.front.matter.YamlFrontMatterBlock
import org.commonmark.ext.front.matter.YamlFrontMatterVisitor
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
import org.commonmark.node.LinkReferenceDefinition
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
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import java.util.*

class KotlinRenderer(
    private val project: Project,
    private val filePath: String,
    private val components: MarkdownComponents,
    private val pkg: String,
    private val funName: String,
) : Renderer {

    private var indentCount = 0
    private val indent get() = "    ".repeat(indentCount)
    // If true, we have access to the `MarkdownContext` class and CompositionLocal
    private val dependsOnMarkdownArtifact = project.hasDependencyNamed("kobwebx-markdown")

    // Flexible data which can be used by Node handlers however they need
    private val data = TypedMap()

    override fun render(node: Node, output: Appendable) {
        node.accept(SoftLineBreakConversionVisitor())
        node.accept(TextMergingVisitor())

        output.append(
            buildString {
                appendLine("package $pkg")
                appendLine()
                appendLine("import androidx.compose.runtime.*")
                appendLine("import com.varabyte.kobweb.core.*")
                if (dependsOnMarkdownArtifact) {
                    appendLine("import com.varabyte.kobwebx.markdown.*")
                }

                appendLine()
                appendLine("@Page")
                appendLine("@Composable")
                appendLine("fun $funName() {")
            }
        )

        indentCount++
        RenderVisitor(output).visitAndFinish(node)
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

    /** Avoid a bunch of unecessary calls to Text functions by merging all sibling text strings together. */
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

    private inner class RenderVisitor(private val output: Appendable) : AbstractVisitor() {
        private val onFinish = Stack<() -> Unit>()
        fun finish() {
            onFinish.forEach { action -> action() }
        }

        private fun <N : Node> doVisit(node: N, composableCall: Provider<NodeScope.(N) -> String>) {
            val scope = NodeScope(data)
            composableCall.get().invoke(scope, node).takeIf { it.isNotBlank() }?.let { code ->
                doVisit(node, code, scope)
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

        private fun unsupported(feature: String) {
            error("$feature is currently unsupported")
        }

        override fun visit(blockQuote: BlockQuote) {
            doVisit(blockQuote, components.blockquote)
        }

        override fun visit(code: Code) {
            doVisit(code, components.inlineCode)
        }

        override fun visit(emphasis: Emphasis) {
            doVisit(emphasis, components.em)
        }

        override fun visit(fencedCodeBlock: FencedCodeBlock) {
            doVisit(fencedCodeBlock, components.code)
        }

        override fun visit(hardLineBreak: HardLineBreak) {
            doVisit(hardLineBreak, components.br)
        }

        override fun visit(heading: Heading) {
            doVisit(heading, components.heading)
        }

        override fun visit(thematicBreak: ThematicBreak) {
            doVisit(thematicBreak, components.hr)
        }

        override fun visit(htmlInline: HtmlInline) {
            doVisit(htmlInline, components.inlineTag)
        }

        override fun visit(htmlBlock: HtmlBlock) {
            doVisit(htmlBlock, components.html)
        }

        override fun visit(image: Image) {
            doVisit(image, components.img)
        }

        override fun visit(indentedCodeBlock: IndentedCodeBlock) {
            // Delegate to fenced code blocks, which (as far as I can tell) are a superset of indented code blocks
            val fenced = FencedCodeBlock().apply { literal = indentedCodeBlock.literal }
            visit(fenced)
        }

        override fun visit(link: Link) {
            doVisit(link, components.a)
        }

        override fun visit(listItem: ListItem) {
            doVisit(listItem, components.li)
        }

        override fun visit(bulletList: BulletList) {
            doVisit(bulletList, components.ul)
        }

        override fun visit(orderedList: OrderedList) {
            doVisit(orderedList, components.ol)
        }

        override fun visit(paragraph: Paragraph) {
            // Detect:
            // <ul>
            //   <li><p>Markdown wraps list item text in paragraphs -- yes, it's surprising</p></li>
            fun Paragraph.isInTightList() = (parent?.parent as? ListBlock)?.isTight ?: false
            if (paragraph.isInTightList()) {
                visitChildren(paragraph)
            }
            else {
                doVisit(paragraph, components.p)
            }
        }

        override fun visit(strongEmphasis: StrongEmphasis) {
            doVisit(strongEmphasis, components.strong)
        }

        override fun visit(text: Text) {
            doVisit(text, components.text)
        }

        override fun visit(linkReferenceDefinition: LinkReferenceDefinition) {
            unsupported("Link referencing")
        }

        override fun visit(customNode: CustomNode) {
            if (customNode is KobwebCall) {
                output.appendLine("$indent${customNode.toFqn(project)}")
            }
        }

        private fun List<String>.serialize(): String {
            return buildString {
                append("listOf(")
                append(joinToString { "\"${it.escapeQuotes()}\""})
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
            if (customBlock is YamlFrontMatterBlock) {
                val yamlVisitor = YamlFrontMatterVisitor()
                customBlock.accept(yamlVisitor)

                var contextCreated = false
                if (yamlVisitor.data.isNotEmpty()) {
                    if (dependsOnMarkdownArtifact) {
                        // "root" if present is a special value and not something that should be exposed to users. This
                        // lets us avoid worrying about nasty escape / slash issues, since it's possible you'll
                        // pass String values into the root (e.g. `PageLayout(title = "\"Hello\" they said")`) that
                        // already have escaped characters in it. This makes the `serialize` call below less complex.
                        val dataWithoutRoot = yamlVisitor.data.minus("root")
                        if (dataWithoutRoot.isNotEmpty()) {
                            val mdCtx = buildString {
                                append("MarkdownContext(")
                                append(listOf("\"$filePath\"", dataWithoutRoot.serialize()).joinToString())
                                append(")")
                            }
                            output.appendLine("${indent}CompositionLocalProvider(LocalMarkdownContext provides $mdCtx) {")
                            ++indentCount
                            contextCreated = true
                        }
                    }

                    // If "root" is set in the YAML block, that represents a top level composable which should wrap
                    // everything else.
                    val maybeRoot = yamlVisitor.data["root"]?.single()
                    maybeRoot?.let { root ->
                        visit(KobwebCall(root, appendBrace = true))
                        ++indentCount
                    }

                    onFinish += {
                        if (maybeRoot != null) {
                            --indentCount
                            output.appendLine("$indent}")
                        }

                        if (contextCreated) {
                            --indentCount
                            output.appendLine("$indent}")
                        }
                    }
                }
            } else if (customBlock is KobwebCallBlock) {
                val visitor = KobwebCallVisitor()
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
        }
    }
}