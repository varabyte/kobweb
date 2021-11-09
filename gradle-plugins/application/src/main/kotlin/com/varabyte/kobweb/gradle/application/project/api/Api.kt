package com.varabyte.kobweb.gradle.application.project.api

import com.varabyte.kobweb.gradle.application.project.KOBWEB_API_FQN_PREFIX

const val API_SIMPLE_NAME = "Api"
const val INIT_SIMPLE_NAME = "Init"
const val API_FQN = "$KOBWEB_API_FQN_PREFIX$API_SIMPLE_NAME"
const val INIT_FQN = "$KOBWEB_API_FQN_PREFIX$INIT_SIMPLE_NAME"

class InitEntry(
    val fqn: String,
)

class ApiEntry(
    val fqn: String,
    val route: String,
)