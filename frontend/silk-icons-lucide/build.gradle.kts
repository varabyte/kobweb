import java.net.URI
import java.util.zip.ZipInputStream
import javax.xml.parsers.DocumentBuilderFactory

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    id("kobweb-compose")
    id("com.varabyte.kobweb.internal.publish")
}

group = "com.varabyte.kobwebx"
version = libs.versions.kobweb.get()

private val GENERATED_SRC_ROOT = "build/generated/icons/src/jsMain/kotlin"
val GENERATED_JSON_FILE = "lucide-icons.json"
val LUCIDE_REPO_BASE = "https://github.com/lucide-icons/lucide"
val LUCIDE_VERSION = "0.574.0"

abstract class GenerateIconsTask : DefaultTask() {
    @get:InputFile
    abstract val inputJsonFile: RegularFileProperty

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @get:Input
    abstract val generatedJsonFileName: Property<String>

    @TaskAction
    fun generate() {
        fun escapeKotlin(s: String): String = s
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\$", "\\\$")

        fun rawNameToMethodName(rawName: String) = "Lucide" + rawName.split("-").joinToString("") {
            it.replaceFirstChar { c ->
                if (c.isLowerCase()) c.titlecase() else c.toString()
            }
        }

        fun generateElementCode(tag: String, attributes: Map<String, String>): String {
            return when (tag) {
                "path" -> {
                    val d = attributes["d"] ?: ""
                    "        Path {\n            d(\"${escapeKotlin(d)}\")\n        }"
                }

                "circle" -> buildString {
                    append("        Circle {\n")
                    attributes["cx"]?.let { append("            cx(${it})\n") }
                    attributes["cy"]?.let { append("            cy(${it})\n") }
                    attributes["r"]?.let { append("            r(${it})\n") }
                    append("        }")
                }

                "rect" -> buildString {
                    append("        Rect {\n")
                    attributes["x"]?.let { append("            x(${it})\n") }
                    attributes["y"]?.let { append("            y(${it})\n") }
                    attributes["width"]?.let { append("            width(${it})\n") }
                    attributes["height"]?.let { append("            height(${it})\n") }
                    attributes["rx"]?.let { append("            rx(${it})\n") }
                    attributes["ry"]?.let { append("            ry(${it})\n") }
                    append("        }")
                }

                "line" -> buildString {
                    append("        Line {\n")
                    attributes["x1"]?.let { append("            x1(${it})\n") }
                    attributes["y1"]?.let { append("            y1(${it})\n") }
                    attributes["x2"]?.let { append("            x2(${it})\n") }
                    attributes["y2"]?.let { append("            y2(${it})\n") }
                    append("        }")
                }

                "polyline" -> buildString {
                    append("        Polyline {\n")
                    attributes["points"]?.let { pointsStr ->
                        val pairs = pointsStr.trim().split("\\s+".toRegex()).mapNotNull { point ->
                            val coords = point.split(",")
                            if (coords.size == 2) {
                                val x = coords[0].toDoubleOrNull()
                                val y = coords[1].toDoubleOrNull()
                                if (x != null && y != null) "$x to $y" else null
                            } else null
                        }
                        if (pairs.isNotEmpty()) {
                            append("            points(${pairs.joinToString(", ")})\n")
                        }
                    }
                    append("        }")
                }

                "polygon" -> buildString {
                    append("        Polygon {\n")
                    attributes["points"]?.let { pointsStr ->
                        val pairs = pointsStr.trim().split("\\s+".toRegex()).mapNotNull { point ->
                            val coords = point.split(",")
                            if (coords.size == 2) {
                                val x = coords[0].toDoubleOrNull()
                                val y = coords[1].toDoubleOrNull()
                                if (x != null && y != null) "$x to $y" else null
                            } else null
                        }
                        if (pairs.isNotEmpty()) {
                            append("            points(${pairs.joinToString(", ")})\n")
                        }
                    }
                    append("        }")
                }

                "ellipse" -> buildString {
                    append("        Ellipse {\n")
                    attributes["cx"]?.let { append("            cx(${it})\n") }
                    attributes["cy"]?.let { append("            cy(${it})\n") }
                    attributes["rx"]?.let { append("            rx(${it})\n") }
                    attributes["ry"]?.let { append("            ry(${it})\n") }
                    append("        }")
                }

                else -> "        // Unsupported SVG element: $tag"
            }
        }

        val generatedJsonFile = generatedJsonFileName.get()

        // Clean up previously generated files to avoid stale output
        val outDir = outputDir.get().asFile
        val packageDir = File(outDir, "com/varabyte/kobweb/silk/components/icons/lucide")
        if (packageDir.exists()) {
            packageDir.deleteRecursively()
        }

        val jsonText = inputJsonFile.get().asFile.readText()

        val iconsBlockMatch = "\"icons\"\\s*:\\s*\\{(.*?)\\}\\s*,\\s*\"deprecated\"".toRegex(RegexOption.DOT_MATCHES_ALL)
            .find(jsonText)
            ?: throw GradleException("Could not find 'icons' block in $generatedJsonFile")
        val iconsBlock = iconsBlockMatch.groupValues[1]

        val iconPattern = "\"([^\"]+)\"\\s*:\\s*\\[(.*)\\]".toRegex()
        // icon name -> list of (tag, attrs) pairs
        val activeIcons = mutableMapOf<String, List<Pair<String, Map<String, String>>>>()

        for (line in iconsBlock.split("\n")) {
            val trimmed = line.trim().removeSuffix(",")
            val match = iconPattern.matchEntire(trimmed) ?: continue
            val iconName = match.groupValues[1]
            val elementsStr = match.groupValues[2]

            val elements = mutableListOf<Pair<String, Map<String, String>>>()
            val elemPattern = "\\{\"tag\":\"([^\"]+)\"(?:,\"attrs\":\\{([^}]*)\\})?\\}".toRegex()
            for (elemMatch in elemPattern.findAll(elementsStr)) {
                val tag = elemMatch.groupValues[1]
                val attrsStr = elemMatch.groupValues[2]
                val attrs = mutableMapOf<String, String>()
                if (attrsStr.isNotEmpty()) {
                    val attrPattern = "\"([^\"]+)\":\"([^\"]*(?:\\\\\"[^\"]*)*)\"".toRegex()
                    for (attrMatch in attrPattern.findAll(attrsStr)) {
                        attrs[attrMatch.groupValues[1]] = attrMatch.groupValues[2]
                    }
                }
                elements.add(tag to attrs)
            }
            activeIcons[iconName] = elements
        }

        val deprecatedBlockMatch = "\"deprecated\"\\s*:\\s*\\{(.*)\\}".toRegex(RegexOption.DOT_MATCHES_ALL)
            .find(jsonText)
        val deprecatedIcons = mutableMapOf<String, String>()
        if (deprecatedBlockMatch != null) {
            val deprecatedStr = deprecatedBlockMatch.groupValues[1]
            val deprecatedPattern = "\"([^\"]+)\":\"([^\"]+)\"".toRegex()
            for (m in deprecatedPattern.findAll(deprecatedStr)) {
                deprecatedIcons[m.groupValues[1]] = m.groupValues[2]
            }
        }

        val activeMethodNames = activeIcons.keys.map { rawNameToMethodName(it) }.toSet()
        val activeMethodNamesLowerCase = activeMethodNames.map { it.lowercase() }.toSet()

        val iconParams = "modifier: Modifier = Modifier, size: CSSLengthValue = 1.em, strokeWidth: Number = 2, color: CSSColorValue? = null"

        val header = """
            |//@formatter:off
            |@file:Suppress("unused", "SpellCheckingInspection")
            |
            |// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            |// THIS FILE IS AUTOGENERATED.
            |//
            |// Do not edit this file by hand. Instead, run the `fetchLucideIcons` Gradle task to update
            |// `$generatedJsonFile` in the module root, then run the Gradle task "generateIcons".
            |// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            |
            |package com.varabyte.kobweb.silk.components.icons.lucide
        """.trimMargin()

        fun importsForTag(tag: String): String? = when (tag) {
            "circle" -> "import com.varabyte.kobweb.compose.dom.svg.Circle"
            "ellipse" -> "import com.varabyte.kobweb.compose.dom.svg.Ellipse"
            "line" -> "import com.varabyte.kobweb.compose.dom.svg.Line"
            "path" -> "import com.varabyte.kobweb.compose.dom.svg.Path"
            "polygon" -> "import com.varabyte.kobweb.compose.dom.svg.Polygon"
            "polyline" -> "import com.varabyte.kobweb.compose.dom.svg.Polyline"
            "rect" -> "import com.varabyte.kobweb.compose.dom.svg.Rect"
            else -> null
        }

        fun importsForIcon(elements: List<Pair<String, Map<String, String>>>): String {
            val tags = elements.map { it.first }.toSet()
            return tags.sorted().mapNotNull { importsForTag(it) }.joinToString("\n")
        }

        val commonImports = """
            |import androidx.compose.runtime.Composable
            |import com.varabyte.kobweb.compose.dom.svg.SVGStrokeLineCap
            |import com.varabyte.kobweb.compose.dom.svg.SVGStrokeLineJoin
            |import com.varabyte.kobweb.compose.dom.svg.ViewBox
            |import com.varabyte.kobweb.compose.ui.Modifier
            |import com.varabyte.kobweb.compose.ui.toAttrs
            |import com.varabyte.kobweb.silk.components.icons.IconRenderStyle
            |import com.varabyte.kobweb.silk.components.icons.createIcon
            |import org.jetbrains.compose.web.css.*
        """.trimMargin()

        // Generate one file per active icon
        for ((iconName, elements) in activeIcons.entries.sortedBy { it.key }) {
            val methodName = rawNameToMethodName(iconName)
            val svgImports = importsForIcon(elements)

            val elementsCode = elements.joinToString("\n") { (tag, attrs) ->
                generateElementCode(tag, attrs)
            }

            val fileContent = buildString {
                appendLine(header)
                appendLine()
                appendLine(commonImports)
                if (svgImports.isNotEmpty()) {
                    appendLine(svgImports)
                }
                appendLine()
                appendLine("@Composable")
                appendLine("fun $methodName(")
                appendLine("    $iconParams,")
                appendLine(") {")
                appendLine("    createIcon(")
                appendLine("        viewBox = ViewBox.sized(24),")
                appendLine("        width = size,")
                appendLine("        renderStyle = IconRenderStyle.Stroke(strokeWidth),")
                appendLine("        attrs = modifier.toAttrs {")
                appendLine("            strokeLineCap(SVGStrokeLineCap.Round)")
                appendLine("            strokeLineJoin(SVGStrokeLineJoin.Round)")
                appendLine("            if (color != null) {")
                appendLine("                stroke(color)")
                appendLine("            }")
                appendLine("        }")
                appendLine("    ) {")
                appendLine(elementsCode)
                appendLine("    }")
                appendLine("}")
            }

            val file = File(packageDir, "$methodName.kt")
            file.parentFile.mkdirs()
            file.writeText(fileContent)
        }

        // Generate deprecated alias files
        for ((deprecatedName, canonicalName) in deprecatedIcons.entries.sortedBy { it.key }) {
            val deprecatedMethodName = rawNameToMethodName(deprecatedName)
            if (deprecatedMethodName in activeMethodNames) continue
            if (deprecatedMethodName.lowercase() in activeMethodNamesLowerCase) continue
            val canonicalMethodName = rawNameToMethodName(canonicalName)

            if (canonicalName !in activeIcons) continue

            val fileContent = buildString {
                appendLine(header)
                appendLine()
                appendLine("import androidx.compose.runtime.Composable")
                appendLine("import com.varabyte.kobweb.compose.ui.Modifier")
                appendLine("import org.jetbrains.compose.web.css.*")
                appendLine()
                appendLine("@Deprecated(\"Use $canonicalMethodName instead.\", ReplaceWith(\"$canonicalMethodName(modifier = modifier, size = size, strokeWidth = strokeWidth, color = color)\"))")
                appendLine("@Composable")
                appendLine("fun $deprecatedMethodName(")
                appendLine("    $iconParams,")
                appendLine(") = $canonicalMethodName(modifier = modifier, size = size, strokeWidth = strokeWidth, color = color)")
            }

            val file = File(packageDir, "$deprecatedMethodName.kt")
            file.parentFile.mkdirs()
            file.writeText(fileContent)
        }

        println("Generated ${activeIcons.size} icon files + ${deprecatedIcons.size} deprecated aliases")
    }
}

val fetchLucideIconsTask = tasks.register("fetchLucideIcons") {
    val outputFile = layout.projectDirectory.file(GENERATED_JSON_FILE)
    val lucideVersion = LUCIDE_VERSION
    val lucideRepoBase = LUCIDE_REPO_BASE

    doLast {
        fun parseSvgElements(svgContent: String): List<Pair<String, Map<String, String>>> {
            val factory = DocumentBuilderFactory.newInstance()
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false)
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false)
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false)
            val builder = factory.newDocumentBuilder()
            val doc = builder.parse(svgContent.byteInputStream())
            val svgRoot = doc.documentElement

            val elements = mutableListOf<Pair<String, Map<String, String>>>()
            val children = svgRoot.childNodes
            for (i in 0 until children.length) {
                val node = children.item(i)
                if (node is org.w3c.dom.Element) {
                    val tag = node.tagName
                    val attrs = mutableMapOf<String, String>()
                    val attrNodes = node.attributes
                    for (j in 0 until attrNodes.length) {
                        val attr = attrNodes.item(j)
                        attrs[attr.nodeName] = attr.nodeValue
                    }
                    elements.add(tag to attrs)
                }
            }
            return elements
        }

        fun jsonEscape(s: String): String = s
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t")

        println("Fetching icons for version $lucideVersion...")

        val iconSvgElements = mutableMapOf<String, List<Pair<String, Map<String, String>>>>()
        val deprecatedIcons = mutableMapOf<String, String>()

        val zipUrl = "${lucideRepoBase}/archive/refs/tags/$lucideVersion.zip"
        val connection = URI(zipUrl).toURL().openConnection()
        connection.setRequestProperty("User-Agent", "kobweb-silk-icons-lucide-generator")

        val svgContents = mutableMapOf<String, String>()
        val jsonContents = mutableMapOf<String, String>()

        ZipInputStream(connection.getInputStream()).use { zis ->
            while (true) {
                val entry = zis.nextEntry ?: break
                if (entry.isDirectory) continue

                if (entry.name.contains("/icons/") && entry.name.endsWith(".svg")) {
                    val iconName = entry.name.substringAfterLast("/").removeSuffix(".svg")
                    svgContents[iconName] = zis.readBytes().decodeToString()
                } else if (entry.name.contains("/icons/") && entry.name.endsWith(".json")) {
                    val iconName = entry.name.substringAfterLast("/").removeSuffix(".json")
                    jsonContents[iconName] = zis.readBytes().decodeToString()
                }
            }
        }

        for ((iconName, svgContent) in svgContents) {
            iconSvgElements[iconName] = parseSvgElements(svgContent)
        }

        for ((iconName, content) in jsonContents) {
            val aliasesBlock = "\"aliases\"\\s*:\\s*\\[(.*?)\\]".toRegex(RegexOption.DOT_MATCHES_ALL)
                .find(content)?.groupValues?.get(1)
            if (aliasesBlock != null) {
                val aliasObjects = "\\{(.*?)\\}".toRegex(RegexOption.DOT_MATCHES_ALL).findAll(aliasesBlock)
                for (aliasObj in aliasObjects) {
                    val objContent = aliasObj.groupValues[1]
                    val nameMatch = "\"name\"\\s*:\\s*\"([^\"]+)\"".toRegex().find(objContent)
                    val isDeprecated = "\"deprecated\"\\s*:\\s*true".toRegex().containsMatchIn(objContent)
                    if (nameMatch != null && isDeprecated) {
                        deprecatedIcons[nameMatch.groupValues[1]] = iconName
                    }
                }
            }
        }

        outputFile.asFile.bufferedWriter().use { writer ->
            writer.write("{\n")
            writer.write("  \"version\": \"$lucideVersion\",\n")

            writer.write("  \"icons\": {\n")
            val sortedIcons = iconSvgElements.keys.sorted()
            sortedIcons.forEachIndexed { index, iconName ->
                val elements = iconSvgElements[iconName]!!
                writer.write("    \"${jsonEscape(iconName)}\": [")
                elements.forEachIndexed { elemIndex, (tag, attributes) ->
                    writer.write("{\"tag\":\"${jsonEscape(tag)}\"")
                    if (attributes.isNotEmpty()) {
                        writer.write(",\"attrs\":{")
                        attributes.entries.forEachIndexed { attrIndex, (key, value) ->
                            writer.write("\"${jsonEscape(key)}\":\"${jsonEscape(value)}\"")
                            if (attrIndex < attributes.size - 1) writer.write(",")
                        }
                        writer.write("}")
                    }
                    writer.write("}")
                    if (elemIndex < elements.size - 1) writer.write(",")
                }
                writer.write("]")
                if (index < sortedIcons.size - 1) writer.write(",")
                writer.write("\n")
            }
            writer.write("  },\n")

            writer.write("  \"deprecated\": {")
            val sortedDeprecated = deprecatedIcons.keys.sorted()
            sortedDeprecated.forEachIndexed { index, deprecatedName ->
                writer.write("\"${jsonEscape(deprecatedName)}\":\"${jsonEscape(deprecatedIcons[deprecatedName]!!)}\"")
                if (index < sortedDeprecated.size - 1) writer.write(",")
            }
            writer.write("}\n")

            writer.write("}\n")
        }
        println("Written ${iconSvgElements.size} icons to ${outputFile.asFile.name}")
    }
}

val generateIconsTask = tasks.register<GenerateIconsTask>("generateIcons") {
    inputJsonFile.set(layout.projectDirectory.file(GENERATED_JSON_FILE))
    outputDir.set(layout.buildDirectory.dir("generated/icons/src/jsMain/kotlin"))
    generatedJsonFileName.set(GENERATED_JSON_FILE)
}

kotlin {
    js {
        browser()
    }

    sourceSets {
        jsMain {
            kotlin.srcDir(generateIconsTask.flatMap { it.outputDir })
            dependencies {
                implementation(libs.compose.runtime)
                implementation(libs.compose.html.core)

                api(projects.frontend.kobwebCompose)
                api(projects.frontend.silkWidgets)
            }
        }
    }
}

kobwebPublication {
    artifactName.set("Kobweb Silk Icons (Lucide)")
    artifactId.set("silk-icons-lucide")
    description.set("A collection of composables that directly wrap Lucide icons.")
}
