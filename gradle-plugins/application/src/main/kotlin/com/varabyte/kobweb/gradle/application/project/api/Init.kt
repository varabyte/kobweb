package com.varabyte.kobweb.gradle.application.project.api

import com.varabyte.kobweb.gradle.application.project.common.KOBWEB_API_FQN_PREFIX

const val INIT_SIMPLE_NAME = "InitApi"
const val INIT_FQN = "$KOBWEB_API_FQN_PREFIX$INIT_SIMPLE_NAME"

class InitApiEntry(
    val fqn: String,
)