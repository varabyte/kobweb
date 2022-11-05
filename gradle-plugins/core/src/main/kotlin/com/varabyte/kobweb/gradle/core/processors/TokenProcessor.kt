package com.varabyte.kobweb.gradle.core.processors

import org.jetbrains.kotlin.psi.KtFile
import java.io.File

/** Interface for handling the tokens of a parsed [KtFile]. */
interface TokenProcessor<T> {
    /**
     * Handle the tokens of the parsed [KtFile].
     *
     * @param file The source file that was parsed, in case its metadata is useful.
     */
    fun handle(file: File, ktFile: KtFile)

    /**
     * Called to indicate that all source files have been handled.
     *
     * At this time, any code collected during the processing is now expected to produce some final result.
     */
    fun finish(): T
}
