package com.varabyte.kobwebx.gradle.markdown.frontmatter

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlList
import com.charleskorn.kaml.YamlMap
import com.charleskorn.kaml.YamlNode
import com.charleskorn.kaml.YamlNull
import com.charleskorn.kaml.YamlScalar
import com.charleskorn.kaml.YamlTaggedNode
import com.varabyte.kobwebx.frontmatter.FrontMatterElement
import org.commonmark.node.Document
import org.commonmark.parser.SourceLine
import org.commonmark.parser.block.AbstractBlockParser
import org.commonmark.parser.block.AbstractBlockParserFactory
import org.commonmark.parser.block.BlockContinue
import org.commonmark.parser.block.BlockStart
import org.commonmark.parser.block.MatchedBlockParser
import org.commonmark.parser.block.ParserState

internal class FrontMatterBlockParser : AbstractBlockParser() {
    companion object {
        private val DELIMITER_REGEX = Regex("""^---\s*$""")
    }

    class Factory : AbstractBlockParserFactory() {
        override fun tryStart(state: ParserState, matchedBlockParser: MatchedBlockParser): BlockStart? {
            val line = state.line
            val parentParser = matchedBlockParser.matchedBlockParser
            if (parentParser.block is Document && parentParser.block.firstChild == null &&
                DELIMITER_REGEX.matches(line.content)
            ) {
                return BlockStart.of(FrontMatterBlockParser())
                    .atIndex(state.nextNonSpaceIndex)
            }

            return BlockStart.none()
        }
    }

    private val block = FrontMatterBlock()
    private val lines = mutableListOf<SourceLine>()

    override fun tryContinue(state: ParserState): BlockContinue? {
        val line = state.line
        if (DELIMITER_REGEX.matches(line.content)) {
            val fmRootNode = Yaml.default.parseToYamlNode(lines.joinToString("\n") { it.content })

            fun YamlNode.toFrontMatterElement(): FrontMatterElement {
                return when (this) {
                    is YamlList -> FrontMatterElement.ValueList(items.mapNotNull { it.toFrontMatterElement() as? FrontMatterElement.Scalar })
                    is YamlMap -> {
                        FrontMatterElement.ValueMap(
                            entries.map { (key, value) ->
                                key.content to value.toFrontMatterElement()
                            }.toMap()
                        )
                    }

                    is YamlScalar -> FrontMatterElement.Scalar(content)
                    is YamlNull -> FrontMatterElement.Scalar("")
                    is YamlTaggedNode -> error("Tagged YAML not supported. If you need this, consider reporting an issue at https://github.com/varabyte/kobweb/issues/new?labels=bug&template=bug_report.md&title=I%20want%20to%20use%20YAML%20tags%20in%20frontmatter")
                }
            }

            // Top-level frontmatter will always be a map of key/value pairs.
            block.appendChild(FrontMatterNode(fmRootNode.toFrontMatterElement() as FrontMatterElement.ValueMap))

            return BlockContinue.finished()
        }

        lines.add(line)
        return BlockContinue.atIndex(state.index)
    }

    override fun getBlock() = block
}