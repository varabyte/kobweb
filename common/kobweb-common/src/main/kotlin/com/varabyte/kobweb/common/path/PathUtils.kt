package com.varabyte.kobweb.common.path

import java.io.File

/**
 * A convenience version for [File.invariantSeparatorsPath] for strings that represent paths.
 */
// NOTE: We don't delegate to File(this).invariantSeparatorsPath because that has a side effect of stripping out
// trailing slashes, which we don't want.
val String.invariantSeparatorsPath get() = if (File.separatorChar != '/') this.replace(File.separatorChar, '/') else this
