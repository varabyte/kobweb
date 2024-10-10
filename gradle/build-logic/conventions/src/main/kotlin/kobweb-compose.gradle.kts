plugins {
    id("org.jetbrains.kotlin.plugin.compose")
}

composeCompiler {
    // Trace markers are "pure overhead" for the JS target & needlessly increase the bundle size, but
    // must be explicitly disabled until https://youtrack.jetbrains.com/issue/KT-69900 is resolved
    includeTraceMarkers = false
}
