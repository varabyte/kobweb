package com.varabyte.kobweb.icons.codegen

import java.io.File

/**
 * Google's Material Icons (https://developers.google.com/fonts/docs/material_icons), rendered with its icon fonts.
 *
 * Its version is a commit in https://github.com/google/material-design-icons, which has no release tags.
 *
 * The icons file lists icon names per style, one style per line:
 * ```
 * mdi=10k,10mp,...
 * mdio=10k,10mp,...
 * mdir=...
 * mdis=...
 * mdit=...
 * ```
 *
 * for the filled, outlined, rounded, sharp, and two-toned styles. Each icon becomes a composable that renders it, e.g.
 * `MdiArrowBack`.
 */
class MdiIconSet : IconSet {
    override val id = "mdi"

    /** Icon styles, in the order they're listed in the icons file. */
    private enum class IconStyle(val key: String, val fontName: String) {
        FILLED("mdi", "MaterialIcons-Regular"),
        OUTLINED("mdio", "MaterialIconsOutlined-Regular"),
        ROUNDED("mdir", "MaterialIconsRound-Regular"),
        SHARP("mdis", "MaterialIconsSharp-Regular"),
        TWO_TONED("mdit", "MaterialIconsTwoTone-Regular"),
    }

    override fun fetch(version: String, iconsFile: File) {
        val repo = MaterialDesignIconsRepo(commit = version)
        val lines = IconStyle.entries.map { style ->
            val names = repo.fetchCodepointNames("font/${style.fontName}.codepoints")
            "${style.key}=${names.joinToString(",")}"
        }

        iconsFile.writeText(
            (listOf("# See the README.md for more information about this file. It is parsed by this project's generateIcons gradle task.") + lines)
                .joinToString("\n")
        )
        println("Wrote ${iconsFile.name}")
    }

    override fun generateBindings(iconsFile: File, options: IconCodegenOptions, outputDir: File) {
        val iconRawNames: Map<IconStyle, List<String>> = iconsFile
            .readLines()
            .filter { line -> !line.startsWith("#") }
            .associate { line ->
                val (key, names) = line.split("=", limit = 2)
                val style = IconStyle.entries.find { it.key == key } ?: error("Unexpected style grouping: $key")

                style to names
                    .split(",")
                    .filter { it.isNotEmpty() }
                    // Some icons have aliases that differ only by underscores (e.g. "print_shop" and "printshop"),
                    // which would generate near-identical method names (e.g. MdiPrintShop and MdiPrintshop). Keep only
                    // the longest version of each name, since its underscores give us more readable method names.
                    .groupBy { it.replace("_", "") }
                    .map { (_, values) -> values.maxBy { it.length } }
            }

        // For each icon name, figure out what styles it's available in. This affects the function signature we generate.
        val iconStyleGroups = mutableMapOf<String, MutableSet<IconStyle>>()
        iconRawNames.forEach { (style, rawNames) ->
            rawNames.forEach { rawName -> iconStyleGroups.getOrPut(rawName) { mutableSetOf() }.add(style) }
        }

        iconStyleGroups.keys.requireUniqueMethodNames { it.toMethodName() }

        val visibility = options.visibility.modifierPrefix
        val iconsHeader =
            """
            |${iconsFile.generatedFileNotice}
            |//@formatter:off
            |@file:Suppress("unused", "SpellCheckingInspection")
            |
            |package ${options.packageName}
            |
            |import androidx.compose.runtime.*
            |import com.varabyte.kobweb.compose.ui.Modifier
            |import ${options.packageName}.IconStyle.*
            |
            """.trimMargin()

        val packageDir = outputDir.preparePackageDir(options.packageName)

        packageDir.resolve("_MdIcon.kt").writeText(
            iconsHeader + $$"""
            |import com.varabyte.kobweb.compose.ui.toAttrs
            |import org.jetbrains.compose.web.dom.Span
            |import org.jetbrains.compose.web.dom.Text
            |
            |$${visibility}enum class IconStyle {
            |    FILLED,
            |    OUTLINED,
            |    ROUNDED,
            |    SHARP,
            |    TWO_TONED;
            |}
            |
            |private fun IconStyle.toClassNameSuffix(): String {
            |    return when (this) {
            |        FILLED -> ""
            |        OUTLINED -> "-outlined"
            |        ROUNDED -> "-round"
            |        SHARP -> "-sharp"
            |        TWO_TONED -> "-two-tone"
            |    }
            |}
            |
            |internal fun IconStyle.assertValidStyle(methodName: String, vararg validStyles: IconStyle) {
            |    if (this !in validStyles) {
            |        error("Attempted to render \"$methodName\" with style $this. Must be one of: ${validStyles.joinToString()}")
            |    }
            |}
            |
            |@Composable
            |$${visibility}fun MdIcon(
            |    name: String,
            |    modifier: Modifier,
            |    style: IconStyle = FILLED,
            |) {
            |    Span(
            |        attrs = modifier.toAttrs { classes("material-icons${style.toClassNameSuffix()}") }
            |    ) {
            |        Text(name)
            |    }
            |}
            |
            """.trimMargin()
        )

        for ((rawName, styles) in iconStyleGroups) {
            // TODO(194): Figure out how ligature fallbacks to work, if people report us breaking on legacy browsers.
            //  See also: https://developers.google.com/fonts/docs/material_icons#using_the_icons_in_html
            val methodName = rawName.toMethodName()
            val iconCode = when (styles.size) {
                // A rare solo-style icon? No need to allow a user to pass in a style parameter in that case
                1 -> "@Composable ${visibility}fun $methodName(modifier: Modifier = Modifier) = MdIcon(\"$rawName\", modifier, ${styles.first().name})"

                // This icon supports all styles. No need to assert input parameters.
                IconStyle.entries.size -> "@Composable ${visibility}fun $methodName(modifier: Modifier = Modifier, style: IconStyle = FILLED) = MdIcon(\"$rawName\", modifier, style)"

                // Not all styles are valid. For now, we throw a runtime exception (via assertValidStyle) to inform
                // the user about this, but we can always decide to relax this later.
                else -> "@Composable ${visibility}fun $methodName(modifier: Modifier = Modifier, style: IconStyle = ${styles.first().name}) = MdIcon(\"$rawName\", modifier, style.also { it.assertValidStyle(\"$methodName\", ${styles.joinToString { it.name }}) })"
            }

            // Name files after the raw icon name, not the method name. Method names can differ only by case, which
            // collides on case-insensitive file systems and is rejected by the Kotlin/JS compiler.
            packageDir.resolve("$rawName.kt").writeText("$iconsHeader\n$iconCode")
        }
    }

    /** Converts a raw icon name to its method name, e.g. "arrow_back" -> "MdiArrowBack". */
    private fun String.toMethodName() = "Mdi" + split("_").joinToString("") { part -> part.replaceFirstChar { it.uppercase() } }
}
