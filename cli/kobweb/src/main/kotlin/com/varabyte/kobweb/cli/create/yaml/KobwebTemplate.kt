package com.varabyte.kobweb.cli.create.yaml

import kotlinx.serialization.Serializable

@Serializable
class Metadata(
    val description: String? = null,
)

@Serializable
class KobwebTemplate(
    val metadata: Metadata,
    val instructions: List<Instruction>,
)