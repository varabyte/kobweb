package com.varabyte.kobwebx.gradle.markdown

import com.varabyte.kobweb.gradle.application.extensions.hasDependencyNamed
import com.varabyte.kobweb.gradle.application.extensions.prefixQualifiedPackage
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
    private val components: MarkdownComponents,
    private val relativePackage: String,
    private val funName: String,
) : Renderer {

    private var indentCount = 0
    private val indent get() = "    ".repeat(indentCount)
    // If true, we have access to the `MarkdownContext` class and CompositionLocal
    private val dependsOnMarkdownArtifact = project.hasDependencyNamed("kobwebx-markdown")

    /**
     * @param name The name of this method. See also [toFqn]
     */
    private class MethodCall(val name: String) : CustomNode() {
        /**
         * Convert this class's [name] into a fully qualified name, additionally prefixing the project's package it
         * begins with a period.
         *
         * Examples:
         * * `test` -> `test()`
         * * `.test` -> `org.example.myproject.test()`
         * * `test()` -> `test()`
         */
        fun toFqn(project: Project): String {
            return buildString {
                append(project.prefixQualifiedPackage(name))
                if (name.last().isLetterOrDigit()) {
                    append("()")
                }
            }
        }
    }

    override fun render(node: Node, output: Appendable) {
        output.append(
            buildString {
                appendLine("package ${project.prefixQualifiedPackage(relativePackage)}")
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

    private inner class RenderVisitor(private val output: Appendable) : AbstractVisitor() {
        private val onFinish = Stack<() -> Unit>()
        fun finish() {
            onFinish.forEach { action -> action() }
        }

        private fun <N : Node> doVisit(node: N, composableCall: Provider<NodeScope.(N) -> String>) {
            val scope = NodeScope()
            val code = composableCall.get().invoke(scope, node)
            doVisit(node, code, scope.childrenOverride)
        }

        private fun doVisit(node: Node, code: String, childrenOverride: List<Node>? = null) {
            val children = childrenOverride ?: sequence<Node> {
                var curr: Node? = node.firstChild
                while (curr != null) {
                    yield(curr)
                    curr = curr.next
                }
            }.toList()

            output.append("$indent$code")
            // If this is a single line call (no children), we need to explicitly add the "()" parens if not already
            // done by the caller. Otherwise, we'll do "composableCall { ... }", which is really calling a function with
            // a lambda.
            if (code.last().isLetterOrDigit() && children.isEmpty()) {
                output.append("()")
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
            unsupported("Block quoting")
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

        override fun visit(softLineBreak: SoftLineBreak) {
            // Treat softline breaks like a space. The newlines here are probably coming from the fact that the user
            // pressed ENTER to keep the width of their md file from going over 80 or 100 characters.
            visit(Text(" "))
        }

        override fun visit(hardLineBreak: HardLineBreak) {
            doVisit(hardLineBreak, components.br)
        }

        override fun visit(heading: Heading) {
            when (heading.level) {
                1 -> doVisit(heading, components.h1)
                2 -> doVisit(heading, components.h2)
                else -> doVisit(heading, components.h3)
            }
        }

        override fun visit(thematicBreak: ThematicBreak) {
            doVisit(thematicBreak, components.hr)
        }

        override fun visit(htmlInline: HtmlInline) {
            unsupported("Inline HTML")
        }

        override fun visit(htmlBlock: HtmlBlock) {
            unsupported("Inline HTML")
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
            doVisit(paragraph, components.p)
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

        private fun List<String>.serialize(): String {
            return buildString {
                append("listOf(")
                append(joinToString { "\"${it.replace("\"", "\\\"")}\""})
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

                if (yamlVisitor.data.isNotEmpty()) {
                    if (dependsOnMarkdownArtifact) {
                        output.appendLine("${indent}CompositionLocalProvider(LocalMarkdownContext provides MarkdownContext(${yamlVisitor.data.serialize()})) {")
                        ++indentCount
                    }

                    // If "root" is set in the YAML block, that represents a top level composable which should wrap
                    // everything else.
                    val maybeRoot = yamlVisitor.data["root"]?.single()
                    maybeRoot?.let { root ->
                        output.appendLine("$indent${MethodCall(root).toFqn(project)} {")
                        ++indentCount
                    }

                    onFinish += {
                        if (maybeRoot != null) {
                            --indentCount
                            output.appendLine("$indent}")
                        }

                        if (dependsOnMarkdownArtifact) {
                            --indentCount
                            output.appendLine("$indent}")
                        }
                    }
                }
            } else if (customBlock is KobwebCallBlock) {
                val visitor = KobwebCallVisitor()
                customBlock.accept(visitor)
                output.appendLine("$indent${MethodCall(visitor.text).toFqn(project)}")
            }
        }
    }
}