@file:Suppress("LeakingThis") // Following official Gradle guidance

package com.varabyte.kobweb.gradle.library.tasks

import com.varabyte.kobweb.gradle.core.KOBWEB_METADATA_FRONTEND
import com.varabyte.kobweb.gradle.core.extensions.KobwebBlock
import com.varabyte.kobweb.gradle.core.processors.FrontendDataProcessor
import com.varabyte.kobweb.gradle.core.project.common.PackageUtils.resolvePackageShortcut
import com.varabyte.kobweb.gradle.core.project.frontend.FrontendData
import com.varabyte.kobweb.gradle.core.tasks.KobwebGenerateMetadataTask
import com.varabyte.kobweb.gradle.core.util.LoggingReporter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import javax.inject.Inject

abstract class KobwebGenerateMetadataFrontendTask @Inject constructor(kobwebBlock: KobwebBlock)
    : KobwebGenerateMetadataTask<FrontendData>(kobwebBlock, "Generate Kobweb metadata about this project's frontend structure that can be consumed later by a Kobweb app.") {

    override fun getSourceFiles() = getSourceFilesJs()
    override fun getGeneratedMetadataFile() = kobwebBlock.getGenJsResRoot(project).resolve(KOBWEB_METADATA_FRONTEND)

    override fun createProcessor() = FrontendDataProcessor(
        LoggingReporter(project.logger),
        resolvePackageShortcut(project.group.toString(), kobwebBlock.pagesPackage.get())
    )

    override fun encodeToString(value: FrontendData) = Json.encodeToString(value)
}
