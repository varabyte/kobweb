package com.varabyte.kobweb.gradle.core.tasks.migration

import com.varabyte.kobweb.gradle.core.kmp.jsTarget
import com.varabyte.kobweb.gradle.core.tasks.KobwebTask
import com.varabyte.kobweb.gradle.core.util.RootAndFile
import com.varabyte.kobweb.gradle.core.util.getSourceFilesWithRoots
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

abstract class KobwebMigrateToCssStyleTask :
    KobwebTask("Make an attempt to automatically migrate this Kobweb codebase to using the new CssStyle API.") {
    @Internal
    fun getSourceFilesJsWithRoots(): Sequence<RootAndFile> = project.getSourceFilesWithRoots(project.jsTarget)

    // These selectors used to be at the top level (`silk.components.style`) but have since moved into a new
    // subpackage that didn't exist before (`silk.style.selector`). We need special handling for this new location.
    private val migratedSelectors = mutableListOf(
        "active",
        "after",
        "anyLink",
        "autofill",
        "before",
        "checked",
        "default",
        "disabled",
        "empty",
        "enabled",
        "firstChild",
        "firstLetter",
        "firstLine",
        "firstOfType",
        "focus",
        "focusVisible",
        "focusWithin",
        "hover",
        "inRange",
        "indeterminate",
        "invalid",
        "lastChild",
        "lastOfType",
        "link",
        "onlyChild",
        "onlyOfType",
        "optional",
        "outOfRange",
        "placeholder",
        "placeholderShown",
        "readOnly",
        "readWrite",
        "required",
        "root",
        "selection",
        "target",
        "userInvalid",
        "userValid",
        "valid",
        "visited",
    )

    private class Replacement(
        val from: Regex,
        val to: String,
        val skipIf: Replacement.(String) -> Boolean = { false }
    ) {
        constructor(rawText: String, to: String, skipIf: Replacement.(String) -> Boolean = { false }) : this(
            Regex.escape(rawText).toRegex(), to, skipIf
        )

        fun replace(content: String): String {
            if (skipIf(content)) return content
            return from.replace(content, to)
        }
    }

    private val replacements = buildList {
        add(
            Replacement(
                "import com.varabyte.kobweb.silk.components.style.ComponentStyle",
                "import com.varabyte.kobweb.silk.style.CssStyle",
                skipIf = { text -> text.contains("import com.varabyte.kobweb.silk.style.*") }
                // skipIf because we might be running this task a second time, at which point the ComponentStyle might
                // be in here because it was added by us (below).
            )
        )
        migratedSelectors.forEach { selector ->
            add(
                Replacement(
                    "import com.varabyte.kobweb.silk.components.style.$selector",
                    "import com.varabyte.kobweb.silk.style.selectors.$selector"
                )
            )
        }
        // If a user is using "*" imports, we need to split that into two "*" imports just in case, even if they
        // don't use the new `selector` package in their code. Better an unused import than a compile error!
        // We also keep the old ComponentStyle and ComponentVariant imports around just in case they're used (but do NOT
        // import "silk.components.style.*" because that would pull in conflicting `toModifier` etc. extension methods.
        add(
            Replacement(
                "import com.varabyte.kobweb.silk.components.style.*",
                listOf(
                    "import com.varabyte.kobweb.silk.components.style.ComponentStyle",
                    "import com.varabyte.kobweb.silk.components.style.ComponentVariant",
                    "import com.varabyte.kobweb.silk.style.*",
                    "import com.varabyte.kobweb.silk.style.selectors.*",
                ).joinToString("\n")
            )
        )
        // Aggressive catch-all find/replace
        add(Replacement("import com.varabyte.kobweb.silk.components.style", "import com.varabyte.kobweb.silk.style"))
        // If ComponentStyle/ComponentVariant got moved over from the previous aggressive find/replace, put them back
        // because they don't exist in the new package.
        add(
            Replacement(
                "import com.varabyte.kobweb.silk.style.ComponentVariant",
                "import com.varabyte.kobweb.silk.components.style.ComponentVariant"
            )
        )
        add(
            Replacement(
                "import com.varabyte.kobweb.silk.style.ComponentStyle",
                "import com.varabyte.kobweb.silk.components.style.ComponentStyle"
            )
        )

        add(
            Replacement(
                "import com.varabyte.kobweb.silk.components.layout.breakpoint",
                "import com.varabyte.kobweb.silk.style.breakpoint"
            )
        )

        add(
            Replacement(
                "import com.varabyte.kobweb.silk.components.animation",
                "import com.varabyte.kobweb.silk.style.animation"
            )
        )

        add(Replacement("by ComponentStyle", "= CssStyle"))
        add(Replacement("= ComponentStyle", "= CssStyle"))
        add(Replacement("by (.+).addVariant".toRegex(), "= $1.addVariant"))
        add(Replacement("= (.+).addVariant".toRegex(), "= $1.addVariant"))
        add(Replacement("by Keyframes", "= Keyframes"))
        add(Replacement("= Keyframes", "= Keyframes"))

        add(Replacement("(modify|replace)Component([a-zA-Z]+)".toRegex(), "$1$2"))

        add(Replacement("extraModifiers =", "extraModifier ="))
    }


    @TaskAction
    fun execute() {
        val buildDir = projectLayout.buildDirectory.asFile.get()
        var numUpdatedFiles = 0
        var addVariantCount = 0
        getSourceFilesJsWithRoots()
            // Don't edit source files under the build dir; they're generated and will get overwritten
            .filter { !it.file.absolutePath.startsWith(buildDir.absolutePath) }
            .forEach { rootAndFile ->
                val file = rootAndFile.file
                val originalContent = file.readText()
                var content = originalContent
                replacements.forEach { content = it.replace(content) }
                if (originalContent != content) {
                    println("Updated ${rootAndFile.relativeFile}")
                    file.writeText(content)
                    numUpdatedFiles++

                    if (content.contains("addVariant")) {
                        addVariantCount++
                    }
                }
            }

        if (numUpdatedFiles == 0) {
            println("No files were updated. Your codebase is up to date!")
        } else {
            println()
            println("$numUpdatedFiles file(s) were updated.")
            if (addVariantCount > 0) {
                println("NOTE: Some users may get compile errors and/or deprecation warnings around component styles and variants, despite our best efforts. If this happens to you, please see https://github.com/varabyte/kobweb/blob/main/docs/css-style.md#migration for more information.")
            }
        }
    }
}
