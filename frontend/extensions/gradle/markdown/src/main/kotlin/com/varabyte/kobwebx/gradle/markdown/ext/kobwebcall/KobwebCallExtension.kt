package com.varabyte.kobwebx.gradle.markdown.ext.kobwebcall

import org.commonmark.Extension
import org.commonmark.parser.Parser
import org.commonmark.parser.Parser.ParserExtension

/**
 * An extension point for adding {{ Kobweb Call }} support into markdown.
 */
class KobwebCallExtension private constructor(private val delimiters: Pair<String, String>) : ParserExtension {
    override fun extend(parserBuilder: Parser.Builder) {
        parserBuilder.customBlockParserFactory(KobwebCallParser.Factory(delimiters))
    }

    companion object {
        fun create(delimiters: Pair<String, String>): Extension {
            return KobwebCallExtension(delimiters)
        }
    }
}