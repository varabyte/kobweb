package com.varabyte.kobweb.cli.create

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class Metadata(
    val description: String? = null,
)

@Serializable
sealed class Instruction(val condition: String? = null) {
    @Serializable
    @SerialName("QueryVar")
    class QueryVar(
        val name: String,
        val prompt: String,
        val default: String? = null,
        val validation: String? = null,
    ) : Instruction()

    @Serializable
    @SerialName("DefineVar")
    class DefineVar(
        val name: String,
        val value: String,
    ) : Instruction()

    @Serializable
    @SerialName("ProcessFreemarker")
    class ProcessFreemarker(
        val files: String = "**"
    ) : Instruction()

    @Serializable
    @SerialName("Move")
    class Move(
        val from: String,
        val to: String,
    ) : Instruction()

    @Serializable
    @SerialName("Keep")
    class Keep(
        val files: String = "**",
        val exclude: String? = null,
    ) : Instruction()
}

@Serializable
class KobwebTemplate(
    val metadata: Metadata,
    val instructions: List<Instruction>,
)