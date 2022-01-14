package com.varabyte.kobweb.cli.common.template

import com.varabyte.kobweb.cli.create.freemarker.FreemarkerState
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Disable directory dot operations, e.g. "test/../../../../system"
 * to prevent template commands from escaping out of their root directories.
 */
private fun String.requireNoDirectoryDots() {
    require(this.split("/").none { it == "." || it == ".." })
}

/**
 * The base class for all instructions. Note that an instruction can be skipped if its condition evaluates to false.
 *
 * @param condition A value that should ultimately evaluate to "true" or "false". If "false", the instruction will be
 *   skipped. This value will be processed by freemarker and can be dynamic!
 */
@Serializable
sealed class Instruction(
    val condition: String? = null,
) {
    /**
     * A collection of related instructions, useful for example when a single condition applies to two or more
     * instructions.
     */
    @Serializable
    @SerialName("Group")
    class Group(
        val instructions: List<Instruction>
    ) : Instruction()

    /**
     * Inform the user about something.
     *
     * @param message The message to show to the user. This value will be processed by freemarker and can be dynamic!
     */
    @Serializable
    @SerialName("Inform")
    class Inform(
        val message: String,
    ) : Instruction()

    /**
     * Prompt the user to specify a value for a variable.
     *
     * @param name The name of this variable, which can be referenced in freemarker expressions later.
     * @param prompt The prompt to show the user.
     * @param default The default value to use if nothing is typed. This value will be processed by freemarker and can
     *   be dynamic!
     * @param validation One of a set of built in Kobweb validators. See the "Validators" region inside
     *   [FreemarkerState.model] for the list.
     * @param transform Logic to convert a user's answer before assigning it to a variable, e.g. "Yes" -> "true".
     *   An automatic variable called "value" will be provided for the scope of this function. See the "Converters"
     *   region inside [FreemarkerState.model] for the list.
     */
    @Serializable
    @SerialName("QueryVar")
    class QueryVar(
        val name: String,
        val prompt: String,
        val default: String? = null,
        val validation: String? = null,
        val transform: String? = null,
    ) : Instruction()

    /**
     * Directly define a variable, useful if the user already defined another variable elsewhere and this is just a
     * transformation on top of it.
     *
     * @param name The name of this variable, which can be referenced in freemarker expressions later.
     * @param value The value of the variable. This value will be processed by freemarker and can be dynamic!
     */
    @Serializable
    @SerialName("DefineVar")
    class DefineVar(
        val name: String,
        val value: String,
    ) : Instruction()

    /**
     * Search the project for all files that end in ".ftl", process them, and discard them.
     */
    @Serializable
    @SerialName("ProcessFreemarker")
    class ProcessFreemarker : Instruction()

    /**
     * Move files and/or folders to a destination directory.
     *
     * This instruction does not change the name of any of the files. Use the [Rename] instruction first if you need to
     * accomplish that.
     *
     * @param from The files to copy. This can use standard wildcard syntax, e.g. "*.txt" and "a/b/**/README.md"
     * @param to The directory location to copy to. This value will be processed by freemarker and can be dynamic!
     * @param description An optional description to show to users, if set, instead of the default message, which
     *   may be too detailed.
     */
    @Serializable
    @SerialName("Move")
    class Move(
        val from: String,
        val to: String,
        val description: String? = null,
    ) : Instruction() {
        init {
            from.requireNoDirectoryDots()
            to.requireNoDirectoryDots()
        }
    }

    /**
     * Rename a file in place.
     *
     * @param name The new filename. This value will be processed by freemarker and can be dynamic!
     * @param description An optional description to show to users, if set, instead of the default message, which
     *   may be too detailed.
     */
    @Serializable
    @SerialName("Rename")
    class Rename(
        val file: String,
        val name: String,
        val description: String? = null,
    ) : Instruction() {
        init {
            file.requireNoDirectoryDots()
            require(name.contains("/")) { "Rename value must be a filename only without directories. "}
        }
    }

    /**
     * Specify files for deletion. Directories will be deleted recursively.
     *
     * @param files The list of files to delete
     * @param description An optional description to show to users, if set, instead of the default message.
     */
    @Serializable
    @SerialName("Delete")
    class Delete(
        val files: String,
        val description: String? = null,
    ) : Instruction() {
        init {
            files.requireNoDirectoryDots()
        }
    }
}