package com.varabyte.kobweb.cli.common.template

import com.charleskorn.kaml.Yaml
import com.varabyte.kobweb.project.KobwebFolder
import com.varabyte.kobweb.project.io.KobwebReadableTextFile
import kotlinx.serialization.Serializable

@Serializable
class Metadata(
    val description: String,
)

@Serializable
class KobwebTemplate(
    val metadata: Metadata,
    val instructions: List<Instruction> = emptyList(),
)

class KobwebTemplateFile(kobwebFolder: KobwebFolder) : KobwebReadableTextFile<KobwebTemplate>(
    kobwebFolder,
    "template.yaml",
    deserialize = { text -> Yaml.default.decodeFromString(KobwebTemplate.serializer(), text) }
)