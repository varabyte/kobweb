package com.varabyte.kobwebx.gradle.markdown.frontmatter

import org.commonmark.Extension
import org.commonmark.parser.Parser

internal class FrontMatterExtension : Parser.ParserExtension {
    override fun extend(builder: Parser.Builder) {
        builder.customBlockParserFactory(FrontMatterBlockParser.Factory())
    }

    companion object {
        fun create(): Extension = FrontMatterExtension()
    }
}