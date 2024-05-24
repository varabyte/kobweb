package com.varabyte.kobwebx.gradle.markdown.ext.kobwebcall

import org.commonmark.Extension
import org.commonmark.parser.Parser
import org.commonmark.parser.Parser.ParserExtension

/**
 * An extension point for adding ${inlinecall} and {{{ blockcall }}} support for typing in raw code snippets into
 * Markdown.
 *
 * When these items are encountered, they will instruct Markdown to insert the code directly into the final Kotlin file.
 *
 * For example, `Hello ${Fancy("World")}, welcome to Kobweb!`
 *
 * will render
 *
 * ```
 * P {
 *   Text("Hello ")
 *   Fancy("World")
 *   Text(", welcome to Kobweb!")
 * }
 * ```
 *
 * The block syntax can be used to wrap markdown with code, as in
 *
 * ```
 * {{{ .components.widgets.Warning
 * Using this method before state is initialized can lead to an `InvalidStateException`.
 * }}}
 * ```
 */
class KobwebCallExtension private constructor(private val delimiters: Pair<Char, Char>) : ParserExtension {
    override fun extend(parserBuilder: Parser.Builder) {
        parserBuilder.customBlockParserFactory(KobwebCallBlockParser.Factory(delimiters))
        parserBuilder.customDelimiterProcessor(KobwebCallDelimiterProcessor(delimiters))
    }

    companion object {
        fun create(delimiters: Pair<Char, Char>): Extension {
            return KobwebCallExtension(delimiters)
        }
    }
}
