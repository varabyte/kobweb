plugins {
    id("kobweb-compilation-settings")
    alias(libs.plugins.kotlin.multiplatform)
    id("kobweb-compose")
    id("com.varabyte.kobweb.internal.publish")
}

group = "com.varabyte.kobwebx"
version = libs.versions.kobweb.get()

enum class IconStyle {
    FILLED,
    OUTLINED,
    ROUNDED,
    SHARP,
    TWO_TONED;
}

@CacheableTask
abstract class GenerateIconsTask : DefaultTask() {
    @get:InputFile
    @get:PathSensitive(PathSensitivity.NONE)
    abstract val srcFile: RegularFileProperty

    @get:OutputDirectory
    abstract val genSrcRoot: DirectoryProperty

    @TaskAction
    fun generate() {
        val iconRawNames = srcFile.get().asFile
            .readLines()
            .asSequence()
            .filter { line -> !line.startsWith("#") }
            .associate { line ->
                // Convert icon name to function name, e.g.
                // align-left -> MdiAlignLeft
                line.split("=", limit = 2).let { parts ->
                    val style = when (parts[0]) {
                        "mdi" -> IconStyle.FILLED
                        "mdio" -> IconStyle.OUTLINED
                        "mdir" -> IconStyle.ROUNDED
                        "mdis" -> IconStyle.SHARP
                        "mdit" -> IconStyle.TWO_TONED
                        else -> throw GradleException("Unexpected style grouping: ${parts[0]}")
                    }
                    val names = parts[1]

                    style to names
                        .split(",")
                        // Some icons have aliases that differ only by underscores (e.g. "print_shop" and
                        // "printshop"), which would generate near-identical method names (e.g. MdiPrintShop and
                        // MdiPrintshop). Keep only the longest version of each name, since its underscores give us
                        // more readable method names.
                        .groupBy { it.replace("_", "") }
                        .map { (_, values) -> values.maxBy { it.length } }
                        .toList()
                }
            }

        // For each icon name, figure out what categories they are in. This will affect the function signature we generate.
        val iconStyleGroups = mutableMapOf<String, MutableSet<IconStyle>>()
        iconRawNames.forEach { entry ->
            val category = entry.key
            entry.value.forEach { rawName ->
                iconStyleGroups.computeIfAbsent(rawName) { mutableSetOf() }.add(category)
            }
        }

        // Convert icon name to method name, e.g. arrow_back -> MdiArrowBack
        @Suppress("DEPRECATION") // capitalize is way more readable than a direct replacement
        fun rawNameToMethodName(rawName: String) = "Mdi" + rawName.split("_").joinToString("") { it.capitalize() }

        iconStyleGroups.keys
            .groupBy { rawNameToMethodName(it) }
            .filterValues { it.size > 1 }
            .let { clashes ->
                if (clashes.isNotEmpty()) {
                    throw GradleException("Multiple icons map to the same method name: $clashes")
                }
            }

        val iconMethodEntries = iconStyleGroups
            .map { entry ->
                // TODO(194): Figure out how ligature fallbacks to work, if people report us breaking on legacy browsers.
                //  See also: https://developers.google.com/fonts/docs/material_icons#using_the_icons_in_html
                val rawName = entry.key
                val methodName = rawNameToMethodName(rawName)
                val styles = entry.value

                rawName to when {
                    // A rare solo-style icon? No need to allow a user to pass in a style parameter in that case
                    styles.size == 1 -> {
                        "@Composable fun $methodName(modifier: Modifier = Modifier) = MdIcon(\"$rawName\", modifier, ${styles.first().name})"
                    }

                    styles.isEmpty() -> {
                        // This shouldn't be possible, but just in case...
                        throw GradleException("Unexpected icon entry with no styles: $entry")
                    }

                    styles.size == IconStyle.values().size -> {
                        // This icon supports all styles. No need to assert input parameters.
                        "@Composable fun $methodName(modifier: Modifier = Modifier, style: IconStyle = FILLED) = MdIcon(\"$rawName\", modifier, style)"
                    }

                    else -> {
                        // Not all styles are valid. For now, we throw a runtime exception (via assertValidStyle) to
                        // inform the user about this, but we can always decide to relax this later.
                        "@Composable fun $methodName(modifier: Modifier = Modifier, style: IconStyle = ${styles.first().name}) = MdIcon(\"$rawName\", modifier, style.also { it.assertValidStyle(\"$methodName\", ${styles.joinToString { it.name }}) })"
                    }
                }
            }.toMap()

        val iconsHeader =
            """
            |//@formatter:off
            |@file:Suppress("unused", "SpellCheckingInspection")
            |
            |// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            |// THIS FILE IS AUTOGENERATED.
            |//
            |// Do not edit this file by hand. Instead, update `md-icon-list.txt` in the module root and run the Gradle
            |// task "generateIcons"
            |// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            |
            |package com.varabyte.kobweb.silk.components.icons.mdi
            |
            |import androidx.compose.runtime.*
            |import com.varabyte.kobweb.compose.ui.Modifier
            |import com.varabyte.kobweb.silk.components.icons.mdi.IconStyle.*
            |
            """.trimMargin()

        val dstDir = genSrcRoot.dir("com/varabyte/kobweb/silk/components/icons/mdi").get().asFile
        // Clean up previously generated files to avoid stale output
        dstDir.deleteRecursively()
        dstDir.mkdirs()

        with(dstDir.resolve("_MdIcon.kt")) {
            writeText(iconsHeader)
            appendText(
                $$"""
                |import com.varabyte.kobweb.compose.ui.toAttrs
                |import org.jetbrains.compose.web.dom.Span
                |import org.jetbrains.compose.web.dom.Text
                |
                |enum class IconStyle {
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
                |fun MdIcon(
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
        }

        // Name files after the raw icon name, not the method name. Method names can differ only by case, which
        // collides on case-insensitive file systems and is rejected by the Kotlin/JS compiler.
        iconMethodEntries.forEach { (rawName, iconCode) ->
            with(dstDir.resolve("$rawName.kt")) {
                writeText(iconsHeader)
                appendText("\n")
                appendText(iconCode)
            }
        }

        logger.info("Generated ${iconMethodEntries.size} icon files")
    }
}
val generateIconsTask = tasks.register<GenerateIconsTask>("generateIcons") {
    group = "Icon"
    description = "Generate Kotlin bindings for Material Design Icons."
    srcFile.set(layout.projectDirectory.file("md-icon-list.txt"))
    genSrcRoot.set(layout.projectDirectory.dir("build/generated/icons/src/jsMain/kotlin"))
}

kotlin {
    js {
        browser()
    }

    sourceSets {
        jsMain {
            kotlin.srcDir(generateIconsTask)
            dependencies {
                implementation(libs.compose.runtime)
                implementation(libs.compose.html.core)

                api(projects.frontend.kobwebCompose)
            }
        }
    }
}

kobwebPublication {
    artifactName.set("Kobweb Silk Icons (Material Design Icons)")
    artifactId.set("silk-icons-mdi")
    description.set("A collection of composables that directly wrap Material Design Icons.")
}
