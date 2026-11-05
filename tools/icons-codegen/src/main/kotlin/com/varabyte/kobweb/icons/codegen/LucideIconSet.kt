package com.varabyte.kobweb.icons.codegen

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.w3c.dom.Element
import java.io.File
import java.net.URI
import java.util.zip.ZipInputStream
import javax.xml.parsers.DocumentBuilderFactory

/**
 * Lucide icons (https://lucide.dev), rendered as inline SVG.
 *
 * The data file is JSON that looks like:
 * ```
 * {
 *   "version": "1.6.0",
 *   "icons": {
 *     "arrow-right": [{"tag":"path","attrs":{"d":"M5 12h14"}},{"tag":"path","attrs":{"d":"m12 5 7 7-7 7"}}],
 *     ...
 *   },
 *   "deprecated": {"old-name":"new-name", ...}
 * }
 * ```
 *
 * Each icon becomes a composable that draws its SVG elements, e.g. `LucideArrowRight`, and each deprecated name
 * becomes a deprecated alias for the icon that replaced it.
 */
class LucideIconSet : IconSet {
    override val id = "lucide"

    override fun fetch(version: String, dataFile: File) {
        val svgContents = mutableMapOf<String, String>()
        val jsonContents = mutableMapOf<String, String>()
        val zipUri = URI("https://github.com/lucide-icons/lucide/archive/refs/tags/$version.zip")
        ZipInputStream(zipUri.openDownloadStream()).use { zis ->
            while (true) {
                val entry = zis.nextEntry ?: break
                if (entry.isDirectory || !entry.name.contains("/icons/")) continue

                val fileName = entry.name.substringAfterLast("/")
                when {
                    fileName.endsWith(".svg") -> svgContents[fileName.removeSuffix(".svg")] = zis.readBytes().decodeToString()
                    fileName.endsWith(".json") -> jsonContents[fileName.removeSuffix(".json")] = zis.readBytes().decodeToString()
                }
            }
        }

        val iconSvgElements = svgContents.mapValues { (_, svgContent) -> parseSvgElements(svgContent) }

        // Each icon's metadata lists its old names, e.g. {"aliases": [{"name": "old-name", "deprecated": true}]}
        val deprecatedIcons = mutableMapOf<String, String>()
        for ((iconName, content) in jsonContents) {
            val aliases = Json.parseToJsonElement(content).jsonObject["aliases"] as? JsonArray ?: continue
            for (alias in aliases.filterIsInstance<JsonObject>()) {
                val name = alias["name"]?.jsonPrimitive?.content ?: continue
                if (alias["deprecated"]?.jsonPrimitive?.boolean == true) {
                    deprecatedIcons[name] = iconName
                }
            }
        }

        dataFile.bufferedWriter().use { writer ->
            writer.write("{\n")
            writer.write("  \"version\": \"$version\",\n")

            writer.write("  \"icons\": {\n")
            val sortedIcons = iconSvgElements.keys.sorted()
            sortedIcons.forEachIndexed { index, iconName ->
                val elements = iconSvgElements.getValue(iconName)
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
                writer.write("\"${jsonEscape(deprecatedName)}\":\"${jsonEscape(deprecatedIcons.getValue(deprecatedName))}\"")
                if (index < sortedDeprecated.size - 1) writer.write(",")
            }
            writer.write("}\n")

            writer.write("}\n")
        }
        println("Written ${iconSvgElements.size} icons to ${dataFile.name}")
    }

    override fun generate(dataFile: File, options: IconCodegenOptions, outputDir: File) {
        val json = Json.parseToJsonElement(dataFile.readText()).jsonObject

        val activeIcons: Map<String, List<ElementInfo>> = json.getValue("icons").jsonObject
            .mapValues { (_, elements) ->
                elements.jsonArray.map { element ->
                    val elementObj = element.jsonObject
                    val tag = elementObj.getValue("tag").jsonPrimitive.content
                    val attrs = elementObj["attrs"]?.jsonObject
                        ?.mapValues { (_, value) -> value.jsonPrimitive.content }
                        .orEmpty()
                    ElementInfo.from(tag, attrs)
                }
            }
        val deprecatedIcons: Map<String, String> = json["deprecated"]?.jsonObject
            ?.mapValues { (_, canonicalName) -> canonicalName.jsonPrimitive.content }
            .orEmpty()

        val activeMethodNames = activeIcons.keys.map { it.toMethodName() }.toSet()

        // Skip deprecated aliases that share a method name with an active icon, or whose replacement doesn't exist
        val generatedDeprecatedIcons = deprecatedIcons.filter { (deprecatedName, canonicalName) ->
            deprecatedName.toMethodName() !in activeMethodNames && canonicalName in activeIcons
        }
        (activeIcons.keys + generatedDeprecatedIcons.keys).requireUniqueMethodNames { it.toMethodName() }

        val visibility = options.visibility.modifierPrefix
        val fileHeader = buildString {
            appendLine("//@formatter:off")
            appendLine("@file:Suppress(\"unused\", \"SpellCheckingInspection\", \"PackageDirectoryMismatch\")")
            appendLine()
            appendLine("// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
            appendLine("// THIS FILE IS AUTOGENERATED.")
            appendLine("//")
            appendLine("// Do not edit this file by hand. Instead, run the `fetchLucideIcons` Gradle task to update")
            appendLine("// `${dataFile.name}` in the module root, then run the Gradle task \"generateIcons\".")
            appendLine("// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
            appendLine()
            appendLine("package ${options.packageName}")
            appendLine()
        }

        val packageDir = outputDir.preparePackageDir(options.packageName)

        for ((iconName, elements) in activeIcons.entries.sortedBy { it.key }) {
            val svgImports = elements.map { it.tag }.toSet().sorted().mapNotNull { tag ->
                if (tag in SUPPORTED_TAGS) "import com.varabyte.kobweb.compose.dom.svg.${tag.capitalize()}" else null
            }

            val fileContent = buildString {
                appendLine("import androidx.compose.runtime.Composable")
                appendLine("import com.varabyte.kobweb.compose.dom.svg.SVGStrokeLineCap")
                appendLine("import com.varabyte.kobweb.compose.dom.svg.SVGStrokeLineJoin")
                appendLine("import com.varabyte.kobweb.compose.dom.svg.ViewBox")
                appendLine("import com.varabyte.kobweb.compose.ui.Modifier")
                appendLine("import com.varabyte.kobweb.compose.ui.toAttrs")
                appendLine("import com.varabyte.kobweb.silk.components.icons.IconRenderStyle")
                appendLine("import com.varabyte.kobweb.silk.components.icons.createIcon")
                appendLine("import org.jetbrains.compose.web.css.*")
                svgImports.forEach { appendLine(it) }
                appendLine()
                appendLine("@Composable")
                appendLine("${visibility}fun ${iconName.toMethodName()}(")
                appendLine("$INDENT$ICON_PARAMS,")
                appendLine(") {")
                appendLine("${INDENT}createIcon(")
                appendLine("$INDENT${INDENT}viewBox = ViewBox.sized(24),")
                appendLine("$INDENT${INDENT}renderStyle = IconRenderStyle.Stroke(strokeWidth),")
                appendLine("$INDENT${INDENT}attrs = modifier.toAttrs {")
                appendLine("$INDENT$INDENT${INDENT}strokeLineCap(SVGStrokeLineCap.Round)")
                appendLine("$INDENT$INDENT${INDENT}strokeLineJoin(SVGStrokeLineJoin.Round)")
                appendLine("$INDENT${INDENT}}")
                appendLine("$INDENT) {")
                elements.forEach { appendElement(it) }
                appendLine("$INDENT}")
                appendLine("}")
            }

            // Name files after the raw (kebab-case) icon name, not the method name. Method names can differ only by
            // case (e.g. "letter-s" -> LucideLetterS, "letters" -> LucideLetters), which collides on case-insensitive
            // file systems and is rejected by the Kotlin/JS compiler.
            packageDir.resolve("$iconName.kt").writeText(fileHeader + fileContent)
        }

        val deprecatedDir = packageDir.resolve("deprecated")
        for ((deprecatedName, canonicalName) in generatedDeprecatedIcons.entries.sortedBy { it.key }) {
            val canonicalMethodName = canonicalName.toMethodName()
            val fileContent = buildString {
                appendLine("import androidx.compose.runtime.Composable")
                appendLine("import com.varabyte.kobweb.compose.ui.Modifier")
                appendLine("import org.jetbrains.compose.web.css.*")
                appendLine()
                appendLine("@Deprecated(\"Use $canonicalMethodName instead.\", ReplaceWith(\"$canonicalMethodName(modifier = modifier, strokeWidth = strokeWidth)\"))")
                appendLine("@Composable")
                appendLine("${visibility}fun ${deprecatedName.toMethodName()}(")
                appendLine("$INDENT$ICON_PARAMS,")
                appendLine(") = $canonicalMethodName(modifier = modifier, strokeWidth = strokeWidth)")
            }

            deprecatedDir.mkdirs()
            deprecatedDir.resolve("$deprecatedName.kt").writeText(fileHeader + fileContent)
        }
    }

    /** Parsed SVG element data, separating parsing from code generation. */
    private sealed class ElementInfo(val tag: String) {
        class Path(val d: String) : ElementInfo("path")
        class Simple(tag: String, val attrs: List<Pair<String, String>>) : ElementInfo(tag)
        class Points(tag: String, val points: List<String>) : ElementInfo(tag)
        class Unsupported(tag: String) : ElementInfo(tag)

        companion object {
            /** SVG element tags whose attributes we turn into builder calls, mapped to those attribute names. */
            private val SIMPLE_ELEMENT_ATTRS = mapOf(
                "circle" to listOf("cx", "cy", "r"),
                "ellipse" to listOf("cx", "cy", "rx", "ry"),
                "line" to listOf("x1", "y1", "x2", "y2"),
                "rect" to listOf("x", "y", "width", "height", "rx", "ry"),
            )

            fun from(tag: String, attributes: Map<String, String>): ElementInfo {
                if (tag == "path") {
                    return Path(attributes["d"] ?: "")
                }

                SIMPLE_ELEMENT_ATTRS[tag]?.let { attrNames ->
                    return Simple(tag, attrNames.mapNotNull { attr -> attributes[attr]?.let { value -> attr to value } })
                }

                if (tag == "polyline" || tag == "polygon") {
                    val pairs = attributes["points"]?.trim()?.split("\\s+|,".toRegex())?.chunked(2)?.mapNotNull { coords ->
                        if (coords.size == 2) {
                            val x = coords[0].toDoubleOrNull()
                            val y = coords[1].toDoubleOrNull()
                            if (x != null && y != null) "$x to $y" else null
                        } else null
                    } ?: error("Failed to parse `points` attribute: tag=$tag, attrs=$attributes")
                    return Points(tag, pairs)
                }

                return Unsupported(tag)
            }
        }
    }

    private fun StringBuilder.appendElement(element: ElementInfo) {
        val tagCapitalized = element.tag.capitalize()
        when (element) {
            is ElementInfo.Path -> {
                appendLine("$INDENT${INDENT}Path {")
                appendLine("$INDENT$INDENT${INDENT}d(\"${kotlinEscape(element.d)}\")")
                appendLine("$INDENT${INDENT}}")
            }
            is ElementInfo.Simple -> {
                appendLine("$INDENT${INDENT}$tagCapitalized {")
                for ((attr, value) in element.attrs) {
                    appendLine("$INDENT$INDENT${INDENT}$attr($value)")
                }
                appendLine("$INDENT${INDENT}}")
            }
            is ElementInfo.Points -> {
                appendLine("$INDENT${INDENT}$tagCapitalized {")
                if (element.points.isNotEmpty()) {
                    appendLine("$INDENT$INDENT${INDENT}points(${element.points.joinToString(", ")})")
                }
                appendLine("$INDENT${INDENT}}")
            }
            is ElementInfo.Unsupported -> {
                appendLine("$INDENT${INDENT}// Unsupported SVG element: ${element.tag}")
            }
        }
    }

    /** Converts a raw icon name to its method name, e.g. "arrow-right" -> "LucideArrowRight". */
    private fun String.toMethodName() = "Lucide" + split("-").joinToString("") { it.capitalize() }

    private fun String.capitalize() = replaceFirstChar { it.uppercase() }

    private fun parseSvgElements(svgContent: String): List<Pair<String, Map<String, String>>> {
        val factory = DocumentBuilderFactory.newInstance()
        factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false)
        factory.setFeature("http://xml.org/sax/features/external-general-entities", false)
        factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false)
        val svgRoot = factory.newDocumentBuilder().parse(svgContent.byteInputStream()).documentElement

        val children = svgRoot.childNodes
        return (0 until children.length)
            .map { children.item(it) }
            .filterIsInstance<Element>()
            .map { node ->
                val attrNodes = node.attributes
                node.tagName to (0 until attrNodes.length)
                    .map { attrNodes.item(it) }
                    .associate { it.nodeName to it.nodeValue }
            }
    }

    private fun jsonEscape(s: String): String = s
        .replace("\\", "\\\\")
        .replace("\"", "\\\"")
        .replace("\n", "\\n")
        .replace("\r", "\\r")
        .replace("\t", "\\t")

    private fun kotlinEscape(s: String): String = s
        .replace("\\", "\\\\")
        .replace("\"", "\\\"")
        .replace("$", "\\$")

    companion object {
        private const val INDENT = "    "
        private const val ICON_PARAMS = "modifier: Modifier = Modifier, strokeWidth: Number = 2"
        private val SUPPORTED_TAGS = setOf("circle", "ellipse", "line", "path", "polygon", "polyline", "rect")
    }
}
