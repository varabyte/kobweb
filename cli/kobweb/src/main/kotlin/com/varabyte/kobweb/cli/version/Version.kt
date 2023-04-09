package com.varabyte.kobweb.cli.version

fun handleVersion() {
    // Use raw system property here and not SemVer.Parsed, because SemVar doesn't support pre-release suffixes, which
    // can be useful to show here if this is a dev (SNAPSHOT) build.
    println("kobweb ${System.getProperty("kobweb.version")}")
}