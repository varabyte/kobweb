package com.varabyte.kobweb.gradle.core.project.backend

import com.varabyte.kobweb.gradle.core.project.common.KOBWEB_API_FQN_PREFIX
import kotlinx.serialization.Serializable

const val API_SIMPLE_NAME = "Api"
const val API_FQN = "$KOBWEB_API_FQN_PREFIX$API_SIMPLE_NAME"

@Serializable
class ApiEntry(
    val fqn: String,
    val route: String,
)