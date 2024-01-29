package com.varabyte.kobwebx.gradle.markdown

import java.io.File

class MarkdownData(
    val filePath: String,
    val frontMatter: Map<String, List<String>>,
) {
    override fun toString(): String {
        return buildString {
            append(
                """
                |MarkdownData(
                |    filePath = ${'"'}${'"'}${'"'}${filePath}${'"'}${'"'}${'"'},
                |    frontMatter = mapOf(${"\n"}${frontMatter.map { fm ->
                        "        \"${fm.key}\" to listOf(\"${fm.value.joinToString()}\")," 
                }.joinToString("\n")}
                |    ),
                |),
                """.trimMargin()
            )
        }
    }
}
