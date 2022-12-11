package com.varabyte.kobweb.common.path

import java.io.File

/** Convert a path String to one with Unix separators, useful for standardizing across OSes if necessary */
fun String.toUnixSeparators() = this.replace(File.separatorChar, '/')
fun File.toUnixSeparators() = this.toString().toUnixSeparators()