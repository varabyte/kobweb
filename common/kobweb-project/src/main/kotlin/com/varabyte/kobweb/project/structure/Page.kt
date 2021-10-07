package com.varabyte.kobweb.project.structure

const val PAGE_SIMPLE_NAME = "Page"
const val PAGE_FQCN = "$KOBWEB_FQCN_PREFIX$PAGE_SIMPLE_NAME"

class PageEntry(
    val fqcn: String,
    val route: String,
)