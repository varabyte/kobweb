package com.varabyte.kobwebx.gradle.markdown.ext.kobwebcall

import org.commonmark.node.Node
import org.commonmark.node.Nodes
import org.commonmark.node.Text
import org.commonmark.parser.delimiter.DelimiterProcessor
import org.commonmark.parser.delimiter.DelimiterRun

/**
 * A processor to parse inline Kobweb calls, which look something like `{.components.widgets.Example}`.
 *
 * Note that no spaces are allowed inside the curly braces here, or otherwise, the markdown parser skips over it,
 * unfortunately.
 *
 * See also: [KobwebCall]
 */
class KobwebCallDelimiterProcessor(private val delimiters: Pair<Char, Char>) : DelimiterProcessor {
    override fun getOpeningCharacter() = delimiters.first
    override fun getClosingCharacter() = delimiters.second
    override fun getMinLength() = 1

    override fun process(openingRun: DelimiterRun, closingRun: DelimiterRun): Int {
        return if (openingRun.length() >= minLength && closingRun.length() == openingRun.length()) {
            // Use exactly two delimiters even if we have more, and don't care about internal openers/closers.
            val opener = openingRun.opener
            val closer = closingRun.closer

            // Convert a text node to a KobwebCall node in place
            val text = (Nodes.between(opener, closer).single() as Text)
            val kobwebCall: Node = KobwebCall(text.literal)
            kobwebCall.sourceSpans = text.sourceSpans
            opener.insertAfter(kobwebCall)
            text.unlink()

            openingRun.length()
        } else {
            0
        }
    }
}