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
class KobwebCallBlockParser(
    private val openingDelimiters: String,
    private val closingDelimiters: String,
    private val createParser: () -> Parser
) : AbstractBlockParser() {
    private val block = KobwebCallBlock()

    // If true, looks like {{{ MethodCall }}}, otherwise {{{ MethodCall\n...\n}}}
    private var isSingleLine = false
    private var method: String? = null
    private val lines = mutableListOf<SourceLine>()
    private var nestingDepth = 0 // Don't consume }}} of nested call blocks

    override fun getBlock(): Block = block

    override fun tryContinue(state: ParserState): BlockContinue? {
        val content = state.line.content
        if (content.trimStart().startsWith(openingDelimiters) && !content.trimEnd().endsWith(closingDelimiters)) {
            ++nestingDepth
        }
        val blockContinue: BlockContinue? = when {
            isSingleLine -> BlockContinue.finished()
            content.trimStart().startsWith(closingDelimiters) -> {
                --nestingDepth
                check(nestingDepth >= 0)
                if (nestingDepth == 0) BlockContinue.finished() else null
            }

            else -> null
        }

        return blockContinue ?: BlockContinue.atIndex(state.nextNonSpaceIndex)
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
            } else {
                // If here, it means we started with a "{{{" but markdown already consumed it for us
                ++nestingDepth
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
                val baseIndent = lines.mapNotNull { it.sourceSpan }.minOf { it.columnIndex }
                val content = lines
                    .joinToString("\n") { " ".repeat((it.sourceSpan?.columnIndex ?: baseIndent) - baseIndent) + it.content }
                val innerDocument = createParser.invoke().parse(content)
                block.appendChild(innerDocument)
            }
        }
    }

    class Factory(delimiters: Pair<Char, Char>, private val createParser: () -> Parser) : AbstractBlockParserFactory() {
        private val BLOCK_DELIMITER_LEN = 3
        private val OPENING_DELIMITER = delimiters.first.toString().repeat(BLOCK_DELIMITER_LEN)
        private val CLOSING_DELIMITER = delimiters.second.toString().repeat(BLOCK_DELIMITER_LEN)

        override fun tryStart(state: ParserState, mathedBlockParser: MatchedBlockParser): BlockStart? {
            val line = state.line.content.substring(state.nextNonSpaceIndex)
            return if (line.startsWith(OPENING_DELIMITER)) {
                BlockStart.of(KobwebCallBlockParser(OPENING_DELIMITER, CLOSING_DELIMITER, createParser))
                    .atIndex(BLOCK_DELIMITER_LEN)
            } else {
                BlockStart.none()
            }
        }
    }
}
