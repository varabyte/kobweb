package com.varabyte.kobweb.gradle.application.project

const val API_SIMPLE_NAME = "Api"
const val API_INIT_SIMPLE_NAME = "ApiInit"
const val API_FQCN = "$KOBWEB_API_FQCN_PREFIX$API_SIMPLE_NAME"
const val API_INIT_FQCN = "$KOBWEB_API_FQCN_PREFIX$API_INIT_SIMPLE_NAME"

class ApiInitEntry(
    val fqcn: String,
)

class ApiEntry(
    val fqcn: String,
    val route: String,
)
