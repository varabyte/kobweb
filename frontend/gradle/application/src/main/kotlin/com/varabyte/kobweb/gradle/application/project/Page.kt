package com.varabyte.kobweb.gradle.application.project

const val PAGE_SIMPLE_NAME = "Page"
const val PAGE_FQCN = "$KOBWEB_CORE_FQCN_PREFIX$PAGE_SIMPLE_NAME"

class PageEntry(
    val fqcn: String,
    val route: String,
)