package com.varabyte.kobweb.ksp.util

import com.google.devtools.ksp.symbol.KSFile

val KSFile.nameWithoutExtension: String
    get() = fileName.substringBeforeLast(".")
