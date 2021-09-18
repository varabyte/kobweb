package com.varabyte.kobweb.plugins.kobweb.templates

fun createHtmlFile(title: String, links: List<String>, src: String): String = """
    <!DOCTYPE html>
    <html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>$title</title>
        ${links.joinToString("\n        ")}
    </head>
    <body>
        <div id="root"></div>
        <script src="$src"></script>
    </body>
    </html>
""".trimIndent()