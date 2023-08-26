package com.varabyte.kobweb.gradle.application

enum class Browser(internal val playwrightName: String) {
    Chromium("chromium"),
    Firefox("firefox"),
    WebKit("webkit"),
    // Edge doesn't seem to install correctly via the Playwright CLI, despite these instructions:
    // https://playwright.dev/docs/browsers#installing-google-chrome--microsoft-edge
    // Will skip for now but can investigate later if there's any interest.
    // Edge("msedge"),
}