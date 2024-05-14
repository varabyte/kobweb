package com.varabyte.kobweb.gradle.core.tasks.migration

import com.varabyte.kobweb.gradle.core.tasks.KobwebModuleTask
import org.gradle.api.tasks.TaskAction

abstract class KobwebMigrateToCssStyleTask :
    KobwebModuleTask("Make an attempt to automatically migrate this Kobweb codebase to using the new CssStyle API.") {

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

    private val replacements = buildMap<Regex, String> {
        // If a user is using "*" imports, we need to split that into two "*" imports just in case, even if they
        // don't use the new `selector` package in their code. Better an unused import than a compile error!
        put(
            "import com.varabyte.kobweb.silk.components.style.*",
            listOf(
                "import com.varabyte.kobweb.silk.style.*",
                "import com.varabyte.kobweb.silk.style.selector.*"
            ).joinToString("\n")
        )

        put(
            "import com.varabyte.kobweb.silk.components.style.ComponentStyle",
            "import com.varabyte.kobweb.silk.style.CssStyle"
        )
        migratedSelectors.forEach { selector ->
            put(
                "import com.varabyte.kobweb.silk.components.style.$selector",
                "import com.varabyte.kobweb.silk.style.selector.$selector"
            )
        }
        put("import com.varabyte.kobweb.silk.components.style", "import com.varabyte.kobweb.silk.style")
        // Restore ComponentVariant import to not break existing references like `variant: ComponentVariant? = null`
        put(
            "import com.varabyte.kobweb.silk.style.ComponentVariant",
            "import com.varabyte.kobweb.silk.components.style.ComponentVariant"
        )

        put(
            "import com.varabyte.kobweb.silk.components.layout.breakpoint",
            "import com.varabyte.kobweb.silk.style.breakpoint"
        )

        put(
            "import com.varabyte.kobweb.silk.components.animation",
            "import com.varabyte.kobweb.silk.style.animation"
        )

        put("by ComponentStyle", "= CssStyle")
        put("by (.+).addVariant".toRegex(), "= $1.addVariant")
        put("by Keyframes", "= Keyframes")

        put("(modify|replace)Component([a-zA-Z]+)".toRegex(), "$1$2")

        put("extraModifiers =", "extraModifier =")
    }


    private fun MutableMap<Regex, String>.put(rawText: String, to: String) {
        put(Regex.escape(rawText).toRegex(), to)
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
                replacements.forEach { (find, replace) ->
                    content = find.replace(content, replace)
                }
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
                println("NOTE: Some users may get compile errors around style variants, despite our best efforts. If this happens to you, please see https://github.com/varabyte/kobweb/blob/main/docs/css-style.md#migration for more information.")
            }
        }
    }
}
