package com.varabyte.kobweb.gradle.application.extensions

import java.io.File

fun File.isDescendantOf(maybeAncestorTest: (File) -> Boolean): Boolean {
    var curr: File? = this
    while (curr != null) {
        if (maybeAncestorTest(curr)) {
            return true
        }
        curr = curr.parentFile
    }
    return false
}

fun File.isDescendantOf(maybeAncestor: File): Boolean = isDescendantOf { ancestor -> ancestor == maybeAncestor }