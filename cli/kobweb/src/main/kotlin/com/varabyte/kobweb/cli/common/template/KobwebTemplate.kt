package com.varabyte.kobweb.cli.common.template

import com.charleskorn.kaml.Yaml
import com.varabyte.kobweb.common.yaml.nonStrictDefault
import kotlinx.serialization.Serializable
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.readBytes

/**
 * @param shouldHighlight This template is considered important and should be called out
 *   separately when all templates for a repository are listed.
 */
@Serializable
class Metadata(
    val description: String,
    val shouldHighlight: Boolean = false,
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
    }
}