plugins {
    id("org.jetbrains.kotlin.plugin.compose")
}

composeCompiler {
    // Trace markers are "pure overhead" for the JS target & needlessly increase the bundle size, but
    // must be explicitly disabled until https://youtrack.jetbrains.com/issue/KT-69900 is resolved
    includeTraceMarkers = false
    // As per its KDoc, source information is meant to be removed in production builds, but is explicitly
    // disabled since Webpack does not remove it.
    includeSourceInformation = false
}
