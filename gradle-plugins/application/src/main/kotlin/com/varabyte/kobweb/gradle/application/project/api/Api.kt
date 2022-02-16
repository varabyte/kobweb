package com.varabyte.kobweb.gradle.application.project.api

import com.varabyte.kobweb.gradle.application.project.common.KOBWEB_API_FQN_PREFIX

const val API_SIMPLE_NAME = "Api"
const val API_FQN = "$KOBWEB_API_FQN_PREFIX$API_SIMPLE_NAME"

class ApiEntry(
    val fqn: String,
    val route: String,
)