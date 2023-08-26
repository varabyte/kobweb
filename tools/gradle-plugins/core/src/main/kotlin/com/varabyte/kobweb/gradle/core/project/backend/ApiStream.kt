package com.varabyte.kobweb.gradle.core.project.backend

import com.varabyte.kobweb.gradle.core.project.common.KOBWEB_API_FQN_PREFIX
import kotlinx.serialization.Serializable

const val API_STREAM_SIMPLE_NAME = "ApiStream"
const val API_STREAM_FQN = "${KOBWEB_API_FQN_PREFIX}stream.$API_STREAM_SIMPLE_NAME"

@Serializable
class ApiStreamEntry(
    val fqn: String,
    val route: String,
)
