package com.varabyte.kobwebx.gradle.markdown

import org.commonmark.node.*
import org.commonmark.renderer.Renderer
import org.gradle.api.provider.Provider

class KotlinRenderer(
    private val components: MarkdownComponents,
    private val fqPackage: String,
    private val funName: String,
) : Renderer {

    private var indentCount = 0
    private val indent get() = "    ".repeat(indentCount)

    override fun render(node: Node, output: Appendable) {
        output.appendLine(
            """
            package $fqPackage

            import androidx.compose.runtime.*
            import com.varabyte.kobweb.core.*

            @Page
            @Composable
            fun $funName() {
        """.trimIndent()
        )

        indentCount++
        node.accept(RenderVisitor(output))
        indentCount--

        assert(indentCount == 0)
        output.appendLine(
            """
            }
        """.trimIndent()
        )
    }

    override fun render(node: Node): String {
        return buildString {
            render(node, this)
        }
    }

    private inner class RenderVisitor(private val output: Appendable) : AbstractVisitor() {
        private fun doVisit(node: Node, composable: Provider<String>, vararg args: String) {
            output.append("$indent${composable.get()}")
            if (args.isNotEmpty() || node.firstChild == null) {
                output.append("(${args.joinToString(",")})")
            }

            if (node.firstChild != null) {
                output.appendLine(" {")
                ++indentCount
                visitChildren(node)
                --indentCount
                output.appendLine("$indent}")
            }
            else {
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
            doVisit(code, components.code)
        }

        override fun visit(emphasis: Emphasis) {
            doVisit(emphasis, components.em)
        }

        override fun visit(fencedCodeBlock: FencedCodeBlock) {
            doVisit(fencedCodeBlock, components.code)
        }

        override fun visit(hardLineBreak: HardLineBreak) {
            doVisit(hardLineBreak, components.p)
            println("Visiting hardLineBreak")
            visitChildren(hardLineBreak)
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
            doVisit(indentedCodeBlock, components.code)
        }

        override fun visit(link: Link) {
            doVisit(link, components.a)
        }

        override fun visit(listItem: ListItem) {
            doVisit(listItem, components.li)
        }

        override fun visit(orderedList: OrderedList) {
            doVisit(orderedList, components.ol)
        }

        override fun visit(paragraph: Paragraph) {
            doVisit(paragraph, components.p)
        }

        override fun visit(softLineBreak: SoftLineBreak) {
            doVisit(softLineBreak, components.br)
        }

        override fun visit(strongEmphasis: StrongEmphasis) {
            doVisit(strongEmphasis, components.b)
        }

        override fun visit(text: Text) {
            doVisit(text, components.text, "\"${text.literal}\"")
        }

        // TODO: Support custom nodes, like front matter and tables
//        override fun visit(customBlock: CustomBlock) {
//            if (customBlock is YamlFrontMatterBlock) {
//                val yamlVisitor = YamlFrontMatterVisitor()
//                customBlock.accept(yamlVisitor)
//                println("Visited YAML")
//                yamlVisitor.data.forEach { (k, v) ->
//                    println("$k -> ${v.joinToString { "\"$it\"" }}")
//                }
//            } else {
//                println("Visiting customBlock (${customBlock::class.java})")
//                visitChildren(customBlock)
//            }
//        }
    }
}