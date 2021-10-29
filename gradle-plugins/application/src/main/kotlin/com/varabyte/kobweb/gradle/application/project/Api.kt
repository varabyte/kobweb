package com.varabyte.kobweb.gradle.application.project

const val API_SIMPLE_NAME = "Api"
const val INIT_SIMPLE_NAME = "Init"
const val API_FQCN = "$KOBWEB_API_FQCN_PREFIX$API_SIMPLE_NAME"
const val INIT_FQCN = "$KOBWEB_API_FQCN_PREFIX$INIT_SIMPLE_NAME"

class InitEntry(
    val fqcn: String,
)

class ApiEntry(
    val fqcn: String,
    val route: String,
)