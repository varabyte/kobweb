package com.varabyte.kobwebx.gradle.markdown.ext.kobwebcall

import org.commonmark.node.Block
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
class KobwebCallParser(val text: String) : AbstractBlockParser() {
    private val block = KobwebCallBlock().apply {
        appendChild(KobwebCall(text))
    }

    override fun getBlock(): Block {
        return block
    }

    override fun tryContinue(state: ParserState): BlockContinue? {
        // A kobweb call is (for now) always a single line, so this can never be true
        // TODO(Bug #42): Support multi-line kobweb calls
        return BlockContinue.none()
    }

    class Factory(delimiters: Pair<Char, Char>) : AbstractBlockParserFactory() {
        private val BLOCK_DELIMITER_LEN = 3
        private val OPENING_DELIMITER = delimiters.first.toString().repeat(BLOCK_DELIMITER_LEN)
        private val CLOSING_DELIMITER = delimiters.second.toString().repeat(BLOCK_DELIMITER_LEN)

        override fun tryStart(state: ParserState, matchedBlockParser: MatchedBlockParser): BlockStart? {
            val line = state.line.content.substring(state.nextNonSpaceIndex)

            if (!line.startsWith(OPENING_DELIMITER)) return BlockStart.none()

            val closingIndex = line.indexOf(CLOSING_DELIMITER, startIndex = BLOCK_DELIMITER_LEN)
            if (closingIndex < 0) return BlockStart.none()

            val text = line.substring(BLOCK_DELIMITER_LEN, closingIndex).trim()
            return BlockStart.of(KobwebCallParser(text))
                .atIndex(state.nextNonSpaceIndex + BLOCK_DELIMITER_LEN)
        }
    }
}