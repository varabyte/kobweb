package com.varabyte.kobwebx.gradle.markdown.ext.kobwebcall

import org.commonmark.node.Block
import org.commonmark.node.Text
import org.commonmark.parser.block.AbstractBlockParser
import org.commonmark.parser.block.AbstractBlockParserFactory
import org.commonmark.parser.block.BlockContinue
import org.commonmark.parser.block.BlockStart
import org.commonmark.parser.block.MatchedBlockParser
import org.commonmark.parser.block.ParserState

/**
 * A parser for the {{ Kobweb Call }} block pattern.
 *
 * Currently, the parser assumes that the block will only live on one line, and that it's content will get converted
 * into code, e.g. {{ .example.ComposableCall() }}.
 *
 * If no parentheses are added to the call, they will be appended automatically.
 */
class KobwebCallParser(val text: String) : AbstractBlockParser() {
    private val block = KobwebCallBlock().apply {
        appendChild(Text(text))
    }

    override fun getBlock(): Block {
        return block
    }

    // TODO(Bug #42): Support multi-line kobweb calls
    override fun tryContinue(state: ParserState): BlockContinue? {
        // A kobweb call is always (for now?) a single line, so this can never be true
        return BlockContinue.none()
    }

    class Factory(val delimiters: Pair<String, String>) : AbstractBlockParserFactory() {
        override fun tryStart(state: ParserState, matchedBlockParser: MatchedBlockParser): BlockStart? {
            val line = state.line.content.substring(state.nextNonSpaceIndex)

            if (!line.startsWith(delimiters.first)) return BlockStart.none()

            val closingIndex = line.indexOf(delimiters.second, startIndex = delimiters.first.length)
            if (closingIndex < 0) return BlockStart.none()

            val text = line.substring(delimiters.first.length, closingIndex).trim()
            return BlockStart.of(KobwebCallParser(text)).atIndex(state.nextNonSpaceIndex + delimiters.first.length)
        }
    }
}