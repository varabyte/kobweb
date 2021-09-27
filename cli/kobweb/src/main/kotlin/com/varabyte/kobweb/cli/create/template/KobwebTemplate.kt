package com.varabyte.kobweb.cli.create.template

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