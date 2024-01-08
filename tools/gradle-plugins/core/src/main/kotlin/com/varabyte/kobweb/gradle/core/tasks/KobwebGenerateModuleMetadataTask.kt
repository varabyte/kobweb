package com.varabyte.kobweb.gradle.core.tasks

import com.varabyte.kobweb.gradle.core.metadata.ModuleMetadata
import com.varabyte.kobweb.gradle.core.util.KobwebVersionUtil
import com.varabyte.kobweb.ksp.KOBWEB_METADATA_MODULE
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

abstract class KobwebGenerateModuleMetadataTask :
    KobwebTask("Generate a module.json metadata file into this project's jar metadata, which identifies this artifact as one built by Kobweb.") {
    @Input
    fun getKobwebVersion() = KobwebVersionUtil.version

    @OutputDirectory
    fun getGenResDir() = project.layout.buildDirectory.dir("generated/kobweb/module")

    @TaskAction
    fun execute() {
        val moduleMetadataFile = getGenResDir().get().file(KOBWEB_METADATA_MODULE)
        moduleMetadataFile.asFile.apply {
            parentFile.mkdirs()
            writeText(Json.encodeToString(ModuleMetadata(getKobwebVersion())))
        }
    }
}
