plugins {
    alias(libs.plugins.kotlin.multiplatform)
    id("kobweb-compose")
    id("com.varabyte.kobweb.internal.publish")
}

group = "com.varabyte.kobwebx"
version = libs.versions.kobweb.get()

enum class IconCategory {
    SOLID,
    REGULAR,
    BRAND,
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
        // {SOLID=[ad, address-book, address-card, ...], REGULAR=[address-book, address-card, angry, ...], ... }
        val iconRawNames = srcFile.get().asFile
            .readLines().asSequence()
            .filter { line -> !line.startsWith("#") }
            .associate { line ->
                // Convert icon name to function name, e.g.
                // align-left -> FaAlignLeft
                line.split("=", limit = 2).let { parts ->
                    val category = when (parts[0]) {
                        "fas" -> IconCategory.SOLID
                        "far" -> IconCategory.REGULAR
                        "fab" -> IconCategory.BRAND
                        else -> throw GradleException("Unexpected category string: ${parts[0]}")
                    }
                    val names = parts[1]

                    category to names
                        .split(",")
                        // Each icon gets written to its own file, so to prevent file name collisions from ever
                        // happening, normalize each name to its version without dashes (since those get removed later)
                        // and keep only the longest version (e.g. "print-shop" would win out over "printshop".
                        .groupBy { it.replace("-", "") }
                        .map { (_, values) -> values.maxBy { it.length } }
                }
            }

        // For each icon name, figure out what categories they are in. This will affect the function signature we generate.
        // {ad=[SOLID], address-book=[SOLID, REGULAR], address-card=[SOLID, REGULAR], ...
        val iconCategories = mutableMapOf<String, MutableSet<IconCategory>>()
        iconRawNames.forEach { entry ->
            val category = entry.key
            entry.value.forEach { rawName ->
                iconCategories.computeIfAbsent(rawName, { mutableSetOf() }).add(category)
            }
        }

        // Sanity check results
        iconCategories
            .filterNot { entry ->
                val categories = entry.value
                categories.size == 1 ||
                    (categories.size == 2 && categories.contains(IconCategory.SOLID) && categories.contains(IconCategory.REGULAR))
            }
            .let { invalidGroupings ->
                if (invalidGroupings.isNotEmpty()) {
                    throw GradleException("Found unexpected groupings. An icon should only be in its own category OR it can have solid and regular versions: $invalidGroupings")
                }
            }

        // Generate four types of functions: solid only, regular only, solid or regular, and brand
        val iconMethodEntries = iconCategories
            .map { entry ->
                val rawName = entry.key
                // Convert e.g. "align-items" to "FaAlignItems"
                @Suppress("DEPRECATION") // capitalize is way more readable than a direct replacement
                val methodName = "Fa${rawName.split("-").joinToString("") { it.capitalize() }}"
                val categories = entry.value

                methodName to when {
                    categories.size == 2 -> {
                        "@Composable fun $methodName(modifier: Modifier = Modifier, style: IconStyle = IconStyle.OUTLINE, size: IconSize? = null) = FaIcon(\"$rawName\", modifier, style.category, size)"
                    }

                    categories.contains(IconCategory.SOLID) -> {
                        "@Composable fun $methodName(modifier: Modifier = Modifier, size: IconSize? = null) = FaIcon(\"$rawName\", modifier, IconCategory.SOLID, size)"
                    }

                    categories.contains(IconCategory.REGULAR) -> {
                        "@Composable fun $methodName(modifier: Modifier = Modifier, size: IconSize? = null) = FaIcon(\"$rawName\", modifier, IconCategory.REGULAR, size)"
                    }

                    categories.contains(IconCategory.BRAND) -> {
                        "@Composable fun $methodName(modifier: Modifier = Modifier, size: IconSize? = null) = FaIcon(\"$rawName\", modifier, IconCategory.BRAND, size)"
                    }

                    else -> throw GradleException("Unhandled icon entry: $entry")
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
            |// Do not edit this file by hand. Instead, update `fa-icon-list.txt` in the module root and run the Gradle
            |// task "generateIcons"
            |// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            |
            |package com.varabyte.kobweb.silk.components.icons.fa
            |
            |import androidx.compose.runtime.*
            |import com.varabyte.kobweb.compose.ui.Modifier
            |
            """.trimMargin()

        val dstDir = genSrcRoot.dir("com/varabyte/kobweb/silk/components/icons/fa").get().asFile
        dstDir.mkdirs()

        with(dstDir.resolve("_FaIcon.kt")) {
            writeText(iconsHeader)
            appendText(
                $$"""
                |import com.varabyte.kobweb.compose.ui.toAttrs
                |import org.jetbrains.compose.web.dom.Span
                |
                |enum class IconCategory(internal val className: String) {
                |    REGULAR("far"),
                |    SOLID("fas"),
                |    BRAND("fab");
                |}
                |
                |enum class IconStyle(internal val category: IconCategory) {
                |    FILLED(IconCategory.SOLID),
                |    OUTLINE(IconCategory.REGULAR);
                |}
                |
                |// See: https://fontawesome.com/docs/web/style/size
                |enum class IconSize(internal val className: String) {
                |    // Relative sizes
                |    XXS("fa-2xs"),
                |    XS("fa-xs"),
                |    SM("fa-sm"),
                |    LG("fa-lg"),
                |    XL("fa-xl"),
                |    XXL("fa-2xl"),
                |
                |    // Literal sizes
                |    X1("fa-1x"),
                |    X2("fa-2x"),
                |    X3("fa-3x"),
                |    X4("fa-4x"),
                |    X5("fa-5x"),
                |    X6("fa-6x"),
                |    X7("fa-7x"),
                |    X8("fa-8x"),
                |    X9("fa-9x"),
                |    X10("fa-10x");
                |}
                |
                |@Composable
                |fun FaIcon(
                |    name: String,
                |    modifier: Modifier,
                |    style: IconCategory = IconCategory.REGULAR,
                |    size: IconSize? = null,
                |) {
                |    Span(
                |        attrs = modifier.toAttrs {
                |            classes(style.className, "fa-$name")
                |            if (size != null) {
                |                classes(size.className)
                |            }
                |        }
                |    )
                |}
                |
                """.trimMargin()
            )
        }

        iconMethodEntries.forEach { (methodName, iconCode) ->
            with(dstDir.resolve("$methodName.kt")) {
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
    description = "Generate Kotlin bindings for Font Awesome icons."
    srcFile.set(layout.projectDirectory.file("fa-icon-list.txt"))
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
    artifactName.set("Kobweb Silk Icons (Font Awesome)")
    artifactId.set("silk-icons-fa")
    description.set("A collection of composables that directly wrap Font Awesome icons.")
}
