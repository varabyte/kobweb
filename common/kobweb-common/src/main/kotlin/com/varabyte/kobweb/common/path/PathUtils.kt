package com.varabyte.kobweb.common.path

import java.io.File

/**
 * A convenience version for [File.invariantSeparatorsPath] for strings that represent paths.
 */
val String.invariantSeparatorsPath get() = File(this).invariantSeparatorsPath
