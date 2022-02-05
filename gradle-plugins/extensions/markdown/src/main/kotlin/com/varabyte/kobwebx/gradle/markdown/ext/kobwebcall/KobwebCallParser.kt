package com.varabyte.kobwebx.gradle.markdown.ext.kobwebcall

import com.varabyte.kobwebx.gradle.markdown.children
import org.commonmark.node.Block
import org.commonmark.parser.InlineParser
import org.commonmark.parser.SourceLine
import org.commonmark.parser.SourceLines
import org.commonmark.parser.block.AbstractBlockParser
import org.commonmark.parser.block.AbstractBlockParserFactory
import org.commonmark.parser.block.BlockContinue
import org.commonmark.parser.block.BlockStart
import org.commonmark.parser.block.MatchedBlockParser
import org.commonmark.parser.block.ParserState

/**
 * A parser for the {{{ MethodCall }}} block pattern.
 *
 * See also: [KobwebCall]
 */
class KobwebCallParser(private val closingDelimiters: String) : AbstractBlockParser() {
    private val block = KobwebCallBlock()

    private var method: String? = null
    private val lines = mutableListOf<SourceLine>()

    override fun getBlock(): Block {
        return block
    }

    override fun tryContinue(state: ParserState): BlockContinue? {
        val content = state.line.content
        return if (content.startsWith(closingDelimiters)) {
            return BlockContinue.finished()
        } else {
            BlockContinue.atIndex(state.nextNonSpaceIndex)
        }
    }

    override fun addLine(line: SourceLine) {
        val content = line.content.toString().removeSuffix(closingDelimiters).trim()
        if (method == null) {
            method = content
        }
        else {
            lines.add(line)
        }
    }

    override fun closeBlock() {
        while (lines.firstOrNull()?.content?.isBlank() == true) {
            lines.removeFirst()
        }

        while (lines.lastOrNull()?.content?.isBlank() == true) {
            lines.removeLast()
        }

        method?.let { method ->
            block.appendChild(KobwebCall(method, appendBrace = lines.isNotEmpty()))
        }
    }

    override fun parseInlines(inlineParser: InlineParser) {
        inlineParser.parse(SourceLines.of(lines), block)
    }

    class Factory(delimiters: Pair<Char, Char>) : AbstractBlockParserFactory() {
        private val BLOCK_DELIMITER_LEN = 3
        private val OPENING_DELIMITER = delimiters.first.toString().repeat(BLOCK_DELIMITER_LEN)
        private val CLOSING_DELIMITER = delimiters.second.toString().repeat(BLOCK_DELIMITER_LEN)

        override fun tryStart(state: ParserState, mathedBlockParser: MatchedBlockParser): BlockStart? {
            val line = state.line.content.substring(state.nextNonSpaceIndex)
            return if (line.startsWith(OPENING_DELIMITER)) {
                BlockStart.of(KobwebCallParser(CLOSING_DELIMITER))
                    .atIndex(BLOCK_DELIMITER_LEN)
            } else {
                BlockStart.none()
            }
        }
    }
}