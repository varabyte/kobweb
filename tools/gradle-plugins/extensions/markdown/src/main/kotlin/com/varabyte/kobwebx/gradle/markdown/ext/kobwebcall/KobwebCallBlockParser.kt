package com.varabyte.kobwebx.gradle.markdown.ext.kobwebcall

import org.commonmark.node.Block
import org.commonmark.parser.Parser
import org.commonmark.parser.SourceLine
import org.commonmark.parser.block.AbstractBlockParser
import org.commonmark.parser.block.AbstractBlockParserFactory
import org.commonmark.parser.block.BlockContinue
import org.commonmark.parser.block.BlockStart
import org.commonmark.parser.block.MatchedBlockParser
import org.commonmark.parser.block.ParserState

/**
 * A parser for the {{{ MethodCall }}} block pattern.
 *
 * @see KobwebCall
 */
class KobwebCallBlockParser(private val closingDelimiters: String) : AbstractBlockParser() {
    private val block = KobwebCallBlock()

    // If true, looks like {{{ MethodCall }}}, otherwise {{{ MethodCall\n...\n}}}
    private var isSingleLine = false
    private var method: String? = null
    private val lines = mutableListOf<SourceLine>()

    override fun getBlock(): Block = block

    override fun tryContinue(state: ParserState): BlockContinue? {
        val content = state.line.content
        return if (content.startsWith(closingDelimiters) || isSingleLine) {
            return BlockContinue.finished()
        } else {
            BlockContinue.atIndex(state.nextNonSpaceIndex)
        }
    }

    override fun addLine(line: SourceLine) {
        if (method == null) {
            // If the block was a single line, e.g. {{{ call }}},
            // then this will get called BEFORE tryContinue, with trailing delimiters
            // at the end (e.g. " call }}}")
            var trimmedMethod = line.content.toString().trim()
            if (trimmedMethod.endsWith(closingDelimiters)) {
                isSingleLine = true
                trimmedMethod = trimmedMethod.removeSuffix(closingDelimiters).trim()
            }

            this.method = trimmedMethod

        } else {
            lines.add(line)
        }
    }

    override fun closeBlock() {
        method?.takeUnless { it.isBlank() }?.let { method ->
            val lines = lines
                .dropWhile { it.content.isBlank() }
                .dropLastWhile { it.content.isBlank() }

            block.appendChild(KobwebCall(method, appendBrace = lines.isNotEmpty()))

            if (lines.isNotEmpty()) {
                val baseIndent = lines.minOf { it.sourceSpan.columnIndex }
                val content = lines
                    .joinToString("\n") { " ".repeat(it.sourceSpan.columnIndex - baseIndent) + it.content }
                val innerDocument = Parser.builder().build().parse(content)
                block.appendChild(innerDocument)
            }
        }
    }

    class Factory(delimiters: Pair<Char, Char>) : AbstractBlockParserFactory() {
        private val BLOCK_DELIMITER_LEN = 3
        private val OPENING_DELIMITER = delimiters.first.toString().repeat(BLOCK_DELIMITER_LEN)
        private val CLOSING_DELIMITER = delimiters.second.toString().repeat(BLOCK_DELIMITER_LEN)

        override fun tryStart(state: ParserState, mathedBlockParser: MatchedBlockParser): BlockStart? {
            val line = state.line.content.substring(state.nextNonSpaceIndex)
            return if (line.startsWith(OPENING_DELIMITER)) {
                BlockStart.of(KobwebCallBlockParser(CLOSING_DELIMITER))
                    .atIndex(BLOCK_DELIMITER_LEN)
            } else {
                BlockStart.none()
            }
        }
    }
}
