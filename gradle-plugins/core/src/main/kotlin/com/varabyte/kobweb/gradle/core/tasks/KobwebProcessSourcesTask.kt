package com.varabyte.kobweb.gradle.core.tasks

import com.varabyte.kobweb.gradle.core.extensions.KobwebBlock
import com.varabyte.kobweb.gradle.core.processors.TokenProcessor
import com.varabyte.kobweb.gradle.core.project.common.PsiUtils
import com.varabyte.kobweb.gradle.core.project.common.parseKotlinFile
import java.io.File

/**
 * A base class for tasks which use [TokenProcessor] instances to process the user's code after it was parsed into tokens.
 */
abstract class KobwebProcessSourcesTask(
    kobwebBlock: KobwebBlock,
    desc: String,
) : KobwebModuleTask(kobwebBlock, desc) {
    protected fun <T> process(sources: List<File>, processor: TokenProcessor<T>): T {
        val kotlinProject = PsiUtils.createKotlinProject()
        sources.forEach { file ->
            val ktFile = kotlinProject.parseKotlinFile(file)
            processor.handle(file, ktFile)
        }
        return processor.finish()
    }
}