package com.varabyte.kobweb.cli.common.template

import com.charleskorn.kaml.Yaml
import com.varabyte.kobweb.common.path.toUnixSeparators
import com.varabyte.kobweb.common.yaml.nonStrictDefault
import kotlinx.serialization.Serializable
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.readBytes
import kotlin.io.path.relativeTo

/**
 * @property name If specified, the name of this template that can be used to reference it, e.g. "site/example". If not
 *   specified, this name will come from the template's path to the root. See also: [KobwebTemplateFile.getName].
 * @property shouldHighlight This template is considered important and should be called out
 *   separately when all templates for a repository are listed.
 * @property minimumVersion The minimum version of the Kobweb CLI that this template is compatible with.
 */
@Serializable
class Metadata(
    val description: String,
    val name: String? = null,
    val shouldHighlight: Boolean = false,
    val minimumVersion: String = "0.0.0"
)

@Serializable
class KobwebTemplate(
    val metadata: Metadata,
    val instructions: List<Instruction> = emptyList(),
)

private val Path.templateFile get() = this.resolve(".kobweb-template.yaml")

class KobwebTemplateFile private constructor(val folder: Path = Path.of("")) {
    val path = folder.templateFile
    val template = Yaml.nonStrictDefault.decodeFromString(
        KobwebTemplate.serializer(),
        folder.templateFile.readBytes().toString(Charsets.UTF_8)
    )

    companion object {
        fun inPath(path: Path): KobwebTemplateFile? {
            return if (path.templateFile.exists()) KobwebTemplateFile(path) else null
        }

        fun isFoundIn(path: Path): Boolean = inPath(path) != null
    }
}

/**
 * Return the name for this template file.
 *
 * This is done either by finding it explicitly configured or else using some root directory as a way to extract the
 * name automatically from the template's path.
 */
fun KobwebTemplateFile.getName(rootPath: Path): String {
    return template.metadata.name ?: folder.relativeTo(rootPath).toString()
        // Even on Windows, show Unix-style slashes, as `kobweb create` expects that format
        .toUnixSeparators()
}
