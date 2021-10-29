package com.varabyte.kobweb.gradle.application.project

const val APP_SIMPLE_NAME = "App"
const val APP_FQCN = "$KOBWEB_CORE_FQCN_PREFIX$APP_SIMPLE_NAME"

class AppEntry(val fqcn: String)