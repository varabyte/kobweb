package com.varabyte.kobweb.gradle.application

enum class Browser(internal val playwrightName: String) {
    Chromium("chromium"),
    Edge("msedge"),
    Firefox("firefox"),
    WebKit("webkit"),
}