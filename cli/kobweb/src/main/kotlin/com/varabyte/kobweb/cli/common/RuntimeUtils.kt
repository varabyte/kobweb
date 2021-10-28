package com.varabyte.kobweb.cli.common

fun Runtime.gradlew(vararg args: String): Process {
    val finalArgs = mutableListOf(
        if (!Os.isWindows()) "./gradlew" else "./gradlew.bat"
    )

    finalArgs.addAll(args)
    return exec(finalArgs.toTypedArray())
}