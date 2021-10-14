package com.varabyte.kobweb.project.structure

const val API_SIMPLE_NAME = "Api"
const val API_FQCN = "$KOBWEB_API_FQCN_PREFIX$API_SIMPLE_NAME"

class ApiEntry(
    val fqcn: String,
    val route: String,
)