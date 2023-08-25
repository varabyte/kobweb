@file:Suppress("LeakingThis") // Following official Gradle guidance

package com.varabyte.kobweb.gradle.application.tasks

import com.varabyte.kobweb.gradle.application.KOBWEB_APP_METADATA_FRONTEND
import com.varabyte.kobweb.gradle.application.processors.AppDataProcessor
import com.varabyte.kobweb.gradle.core.extensions.KobwebBlock
import com.varabyte.kobweb.gradle.core.project.common.PackageUtils.resolvePackageShortcut
import com.varabyte.kobweb.gradle.core.project.frontend.AppData
import com.varabyte.kobweb.gradle.core.tasks.KobwebGenerateMetadataTask
import com.varabyte.kobweb.gradle.core.util.LoggingReporter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.gradle.api.tasks.CacheableTask
import javax.inject.Inject

@CacheableTask
abstract class KobwebGenerateMetadataFrontendTask @Inject constructor(kobwebBlock: KobwebBlock) :
    KobwebGenerateMetadataTask<AppData>(
        kobwebBlock,
        "Generate Kobweb metadata about this project's frontend structure that can be consumed later by a Kobweb app."
    ) {

    override fun getSourceFiles() = getSourceFilesJs()
    override fun getGeneratedMetadataFile() =
        project.layout.buildDirectory.file(KOBWEB_APP_METADATA_FRONTEND).get().asFile

    override fun createProcessor() = AppDataProcessor(
        LoggingReporter(project.logger),
        resolvePackageShortcut(project.group.toString(), kobwebBlock.pagesPackage.get())
    )

    override fun encodeToString(value: AppData) = Json.encodeToString(value)
}
