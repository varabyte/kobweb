package com.varabyte.kobweb.cli.common.template

import com.charleskorn.kaml.Yaml
import com.varabyte.kobweb.common.yaml.nonStrictDefault
import com.varabyte.kobweb.project.KobwebFolder
import com.varabyte.kobweb.project.io.KobwebReadableTextFile
import kotlinx.serialization.Serializable

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

class KobwebTemplateFile(kobwebFolder: KobwebFolder) : KobwebReadableTextFile<KobwebTemplate>(
    kobwebFolder,
    "template.yaml",
    deserialize = { text -> Yaml.nonStrictDefault.decodeFromString(KobwebTemplate.serializer(), text) }
)