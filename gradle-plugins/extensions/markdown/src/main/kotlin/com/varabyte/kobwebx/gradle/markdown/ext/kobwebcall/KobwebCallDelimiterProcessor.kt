package com.varabyte.kobwebx.gradle.markdown.ext.kobwebcall

import org.commonmark.node.Node
import org.commonmark.node.Nodes
import org.commonmark.node.Text
import org.commonmark.parser.delimiter.DelimiterProcessor
import org.commonmark.parser.delimiter.DelimiterRun

/**
 * A processor to parse inline Kobweb calls, which look something like `${.components.widgets.Example}`.
 *
 * Note that no spaces are allowed inside the curly braces here, or otherwise, the markdown parser skips over it,
 * unfortunately.
 *
 * @see [KobwebCall]
 */
class KobwebCallDelimiterProcessor(private val delimiters: Pair<Char, Char>) : DelimiterProcessor {
    override fun getOpeningCharacter() = delimiters.first
    override fun getClosingCharacter() = delimiters.second
    override fun getMinLength() = 1

    override fun process(openingRun: DelimiterRun, closingRun: DelimiterRun): Int {
        if (openingRun.length() == minLength && closingRun.length() == openingRun.length()) {
            val opener = openingRun.opener
            val closer = closingRun.closer

            // Author's note: I'm not sure if this is abusing the markdown engine or not, but what we want is the
            // format ${...}, while this processor only accepts {...}. So what we do is check if the previous literal
            // ahead of us ended with a $ and, if so, swallow it as our own.
            val previousText = opener.previous as? Text ?: return 0
            val previousLiteral = previousText.literal ?: return 0

            val thisBeginsWithDollar = (previousLiteral == "$" ||
                (previousLiteral.length >= 2 && previousLiteral.takeLast(2) == " $"))

            if (!thisBeginsWithDollar) return 0

            if (previousLiteral == "$") {
                previousText.unlink()
            } else {
                previousText.literal = previousLiteral.dropLast(1)
            }

            // Convert a text node to a KobwebCall node in place
            val text = (Nodes.between(opener, closer).single() as Text)
            val kobwebCall: Node = KobwebCall(text.literal)
            kobwebCall.sourceSpans = text.sourceSpans
            opener.insertAfter(kobwebCall)
            text.unlink()

            return openingRun.length()
        }

        return 0
    }
}
