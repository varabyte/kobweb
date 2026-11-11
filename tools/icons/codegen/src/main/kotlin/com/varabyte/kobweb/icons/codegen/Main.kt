package com.varabyte.kobweb.icons.codegen

import com.varabyte.kobweb.icons.codegen.generate.writeMetadata
import java.io.File
import kotlin.system.exitProcess

private const val USAGE = """Usage:
  fetch <icon-set> --version <version> --icons <file>
  generate <icon-set> --icons <file> --out <dir> --package <name>
  metadata <icon-set> --version <version> --out <dir>"""

/**
 * Command-line entry point, used by Kobweb's own icon modules, which can't apply the Kobweb Gradle plugins since they
 * live in the same build.
 */
fun main(args: Array<String>) {
    try {
        run(args.toList())
    } catch (e: IllegalArgumentException) {
        System.err.println("${e.message}\n\n$USAGE")
        exitProcess(1)
    } catch (e: IllegalStateException) {
        System.err.println(e.message)
        exitProcess(1)
    }
}

private fun run(args: List<String>) {
    require(args.size >= 2) { "Missing command or icon set." }
    val (command, iconSetId) = args
    val iconSet = IconSetCodegen.all.find { it.id == iconSetId }
        ?: throw IllegalArgumentException("Unknown icon set \"$iconSetId\". Expected one of: ${IconSetCodegen.all.map { it.id }}")

    val flags = args.drop(2).chunked(2).associate { flagAndValue ->
        require(flagAndValue.size == 2 && flagAndValue[0].startsWith("--")) { "Malformed arguments: $flagAndValue" }
        flagAndValue[0].removePrefix("--") to flagAndValue[1]
    }
    fun flag(name: String) = flags[name] ?: throw IllegalArgumentException("Missing --$name")

    when (command) {
        "fetch" -> iconSet.fetch(flag("version"), File(flag("icons")))
        "generate" -> iconSet.generateBindings(File(flag("icons")), flag("package"), File(flag("out")))
        "metadata" -> iconSet.writeMetadata(flag("version"), File(flag("out")))
        else -> throw IllegalArgumentException("Unknown command \"$command\"")
    }
}
