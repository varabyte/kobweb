package com.varabyte.kobweb.gradle.application.project

/** Interface for a simple class that reports when something went wrong parsing the target project */
interface Reporter {
    fun report(message: String)
}