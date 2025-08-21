plugins {
    alias(libs.plugins.kotlin.multiplatform)
    id("kobweb-compose")
    id("com.varabyte.kobweb.internal.publish")
}

group = "com.varabyte.kobwebx"
version = libs.versions.kobweb.get()

private val GENERATED_SRC_ROOT = "build/generated/icons/src/jsMain/kotlin"

val installLucideTask = tasks.register<Exec>("installLucide") {
    val packageJsonFile = layout.projectDirectory.file("package.json")
    val lucideVersion = libs.versions.lucide.get()
    
    outputs.dir("node_modules")
    
    doFirst {
        // Create package.json if it doesn't exist
        if (!packageJsonFile.asFile.exists()) {
            packageJsonFile.asFile.writeText(
                """
                {
                  "name": "silk-icons-lucide-build",
                  "version": "1.0.0",
                  "dependencies": {
                    "lucide": "$lucideVersion"
                  }
                }
                """.trimIndent()
            )
        }
    }
    
    commandLine("npm", "install")
    workingDir(layout.projectDirectory)
}

val generateIconsTask = tasks.register("generateIcons") {
    dependsOn(installLucideTask)
    
    val iconsDir = file("node_modules/lucide/dist/esm/icons")
    val outputDir = layout.projectDirectory.file("$GENERATED_SRC_ROOT/com/varabyte/kobweb/silk/components/icons/lucide")
    val outputFile = outputDir.asFile.resolve("LucideIcons.kt")
    
    inputs.dir(iconsDir)
    outputs.dir(GENERATED_SRC_ROOT)
    
    doLast {
        if (!iconsDir.exists()) {
            throw GradleException("Lucide icons directory not found: ${iconsDir.absolutePath}")
        }

        // Get all icon names from .js files
        val iconFiles = iconsDir.listFiles { file ->
            file.isFile && file.name.endsWith(".js") && file.name != "index.js"
        } ?: throw GradleException("No icon files found")

        // Parse SVG elements from each icon file
        val iconData = mutableMapOf<String, List<Pair<String, Map<String, String>>>>()

        iconFiles.forEach { file ->
            val iconName = file.nameWithoutExtension
            val content = file.readText()

            // Extract the array content between [ and ];
            val arrayRegex = """const\s+\w+\s*=\s*\[(.*?)\];""".toRegex(RegexOption.DOT_MATCHES_ALL)
            val arrayMatch = arrayRegex.find(content)

            if (arrayMatch != null) {
                val arrayContent = arrayMatch.groupValues[1]
                val elements = mutableListOf<Pair<String, Map<String, String>>>()

                // Parse individual elements like ["path", { d: "...", ... }]
                val elementRegex = """\[\s*"(\w+)"\s*,\s*\{([^}]*)\}\s*\]""".toRegex()
                val elementMatches = elementRegex.findAll(arrayContent)

                for (match in elementMatches) {
                    val elementType = match.groupValues[1]
                    val attributesString = match.groupValues[2]
                    val attributes = mutableMapOf<String, String>()

                    // Parse attributes like d: "...", cx: "12", etc.
                    val attrRegex = """(\w+):\s*"([^"]*)"(?:,|\s|$)""".toRegex()
                    val attrMatches = attrRegex.findAll(attributesString)

                    for (attrMatch in attrMatches) {
                        val attrName = attrMatch.groupValues[1]
                        val attrValue = attrMatch.groupValues[2]
                        attributes[attrName] = attrValue
                    }

                    elements.add(elementType to attributes)
                }

                if (elements.isNotEmpty()) {
                    iconData[iconName] = elements
                    if (iconName == "camera") {
                        println("Found camera icon with ${elements.size} elements: ${elements.map { "${it.first}(${it.second.keys.joinToString()})" }}")
                    }
                }
            }
        }

        // Generate case statements for the when block
        val iconCases = iconData.entries.sortedBy { it.key }.joinToString("\n        ") { (iconName, elements) ->
            """"$iconName" -> renderIcon(listOf(${
                elements.joinToString(", ") { element ->
                    val (elementType, attrs) = element
                    val attrsString = attrs.entries.joinToString(", ") { """"${it.key}" to "${it.value}"""" }
                    """"$elementType" to mapOf($attrsString)"""
                }
            }), modifier, size, strokeWidth, color)"""
        }

        // Generate individual composable functions
        val iconMethods = iconData.keys.sorted().joinToString("\n\n") { iconName ->
            val methodName = "Lucide" + iconName.split("-").joinToString("") { 
                it.replaceFirstChar { char -> if (char.isLowerCase()) char.titlecase() else char.toString() }
            }

            """@Composable
fun $methodName(
    modifier: Modifier = Modifier,
    size: CSSLengthValue = 1.em,
    strokeWidth: Number = 2,
    color: CSSColorValue? = null
) = LucideIcon("$iconName", modifier, size, strokeWidth, color)"""
        }

        val code = """//@formatter:off
@file:Suppress("unused", "SpellCheckingInspection")

// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
// THIS FILE IS AUTOGENERATED.
//
// Do not edit this file by hand. Instead, run the Gradle task "generateIcons"
// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

package com.varabyte.kobweb.silk.components.icons.lucide

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.compose.dom.GenericTag
import com.varabyte.kobweb.compose.dom.svg.Circle
import com.varabyte.kobweb.compose.dom.svg.Line
import com.varabyte.kobweb.compose.dom.svg.Path
import com.varabyte.kobweb.compose.dom.svg.Polygon
import com.varabyte.kobweb.compose.dom.svg.Polyline
import com.varabyte.kobweb.compose.dom.svg.Rect
import com.varabyte.kobweb.compose.dom.svg.ViewBox
import com.varabyte.kobweb.silk.components.icons.createIcon
import com.varabyte.kobweb.silk.components.icons.IconRenderStyle
import org.jetbrains.compose.web.css.*

@Composable
fun LucideIcon(
    name: String,
    modifier: Modifier = Modifier,
    size: CSSLengthValue = 1.em,
    strokeWidth: Number = 2,
    color: CSSColorValue? = null
) {
    when (name) {
        $iconCases
        else -> {
            // Fallback for unknown icons
            renderIcon(emptyList(), modifier, size, strokeWidth, color)
        }
    }
}

@Composable
private fun renderIcon(
    elements: List<Pair<String, Map<String, String>>>,
    modifier: Modifier = Modifier,
    size: CSSLengthValue = 1.em,
    strokeWidth: Number = 2,
    color: CSSColorValue? = null
) {
    createIcon(
        viewBox = ViewBox.sized(24),
        width = size,
        renderStyle = IconRenderStyle.Stroke(strokeWidth),
        attrs = modifier.toAttrs {
            if (color != null) {
                attr("stroke", color.toString())
            }
            attr("stroke-linecap", "round")
            attr("stroke-linejoin", "round")
        }
    ) {
        if (elements.isEmpty()) {
            org.jetbrains.compose.web.dom.Text("?")
        } else {
            elements.forEach { (elementType, attributes) ->
                when (elementType) {
                    "path" -> {
                        Path {
                            attributes["d"]?.let { d(it) }
                        }
                    }
                    "circle" -> {
                        Circle {
                            attributes["cx"]?.let { cx(it.toDoubleOrNull() ?: 0.0) }
                            attributes["cy"]?.let { cy(it.toDoubleOrNull() ?: 0.0) }
                            attributes["r"]?.let { r(it.toDoubleOrNull() ?: 0.0) }
                        }
                    }
                    "rect" -> {
                        Rect {
                            attributes["x"]?.let { x(it.toDoubleOrNull() ?: 0.0) }
                            attributes["y"]?.let { y(it.toDoubleOrNull() ?: 0.0) }
                            attributes["width"]?.let { width(it.toDoubleOrNull() ?: 0.0) }
                            attributes["height"]?.let { height(it.toDoubleOrNull() ?: 0.0) }
                            attributes["rx"]?.let { rx(it.toDoubleOrNull() ?: 0.0) }
                            attributes["ry"]?.let { ry(it.toDoubleOrNull() ?: 0.0) }
                        }
                    }
                    "line" -> {
                        Line {
                            attributes["x1"]?.let { x1(it.toDoubleOrNull() ?: 0.0) }
                            attributes["y1"]?.let { y1(it.toDoubleOrNull() ?: 0.0) }
                            attributes["x2"]?.let { x2(it.toDoubleOrNull() ?: 0.0) }
                            attributes["y2"]?.let { y2(it.toDoubleOrNull() ?: 0.0) }
                        }
                    }
                    "polyline" -> {
                        Polyline {
                            attributes["points"]?.let { points ->
                                // Parse "x1,y1 x2,y2 x3,y3" into list of pairs
                                val pairs = points.split(" ").mapNotNull { point ->
                                    val coords = point.split(",")
                                    if (coords.size == 2) {
                                        val x = coords[0].toDoubleOrNull()
                                        val y = coords[1].toDoubleOrNull()
                                        if (x != null && y != null) x to y else null
                                    } else null
                                }
                                if (pairs.isNotEmpty()) {
                                    points(*pairs.toTypedArray())
                                }
                            }
                        }
                    }
                    "polygon" -> {
                        Polygon {
                            attributes["points"]?.let { points ->
                                // Parse "x1,y1 x2,y2 x3,y3" into list of pairs
                                val pairs = points.split(" ").mapNotNull { point ->
                                    val coords = point.split(",")
                                    if (coords.size == 2) {
                                        val x = coords[0].toDoubleOrNull()
                                        val y = coords[1].toDoubleOrNull()
                                        if (x != null && y != null) x to y else null
                                    } else null
                                }
                                if (pairs.isNotEmpty()) {
                                    points(*pairs.toTypedArray())
                                }
                            }
                        }
                    }
                    else -> {
                        // For any other element types, create a generic element
                        val attrsString = attributes.entries.joinToString(" ") { (attrName, attrValue) ->
                            attrName + "=\"" + attrValue + "\""
                        }
                        GenericTag(
                            name = elementType,
                            attrsStr = if (attrsString.isNotEmpty()) attrsString else null
                        )
                    }
                }
            }
        }
    }
}

$iconMethods
        """.trimIndent()

        outputDir.asFile.apply {
            mkdirs()
        }
        outputFile.writeText(code)

        println("Generated ${iconData.size} Lucide icon composables with SVG elements")
    }
}

kotlin {
    js {
        browser()
    }

    sourceSets {
        jsMain {
            kotlin.srcDir(GENERATED_SRC_ROOT)
            dependencies {
                implementation(libs.compose.runtime)
                implementation(libs.compose.html.core)
                api(projects.frontend.kobwebCompose)
                implementation(projects.frontend.composeHtmlExt)
                implementation(projects.frontend.silkWidgets)
                implementation(libs.kotlinx.serialization.json)
            }
        }
    }
}

// Ensure icons are generated before compilation
tasks.named("compileKotlinJs") {
    dependsOn(generateIconsTask)
}

kobwebPublication {
    artifactName.set("Kobweb Silk Icons (Lucide)")
    artifactId.set("silk-icons-lucide")
    description.set("A collection of composables that directly wrap Lucide icons.")
}