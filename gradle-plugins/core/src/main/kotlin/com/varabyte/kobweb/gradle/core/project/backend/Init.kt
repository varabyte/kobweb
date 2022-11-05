package com.varabyte.kobweb.gradle.core.project.backend

import com.varabyte.kobweb.gradle.core.project.common.KOBWEB_API_FQN_PREFIX
import kotlinx.serialization.Serializable

const val INIT_SIMPLE_NAME = "InitApi"
const val INIT_FQN = "$KOBWEB_API_FQN_PREFIX$INIT_SIMPLE_NAME"

@Serializable
class InitApiEntry(
    val fqn: String,
)