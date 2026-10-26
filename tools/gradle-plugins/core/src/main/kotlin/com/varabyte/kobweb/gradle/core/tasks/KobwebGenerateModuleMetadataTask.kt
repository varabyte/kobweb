package com.varabyte.kobweb.gradle.core.tasks

import com.varabyte.kobweb.gradle.core.metadata.ModuleMetadata
import com.varabyte.kobweb.gradle.core.util.KobwebVersionUtil
import com.varabyte.kobweb.ksp.KOBWEB_METADATA_MODULE
import kotlinx.serialization.json.Json
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.work.DisableCachingByDefault

@DisableCachingByDefault(because = "Trivial output, not worth caching.")
abstract class KobwebGenerateModuleMetadataTask :
    KobwebTask("Generate a module.json metadata file into this project's jar metadata, which identifies this artifact as one built by Kobweb.") {
    @Input
    fun getKobwebVersion() = KobwebVersionUtil.version

    /**
     * The Gradle group of the current project, which is used to resolve package shortcuts in this module's code.
     */
    @get:Input
    abstract val projectGroup: Property<String>

    /**
     * The path of public resources inside this project's resources folder, which apps need to know to find them.
     */
    @get:Input
    abstract val publicPath: Property<String>

    @OutputDirectory
    fun getGenResDir() = projectLayout.buildDirectory.dir("generated/kobweb/module")

    @TaskAction
    fun execute() {
        val moduleMetadataFile = getGenResDir().get().file(KOBWEB_METADATA_MODULE)
        moduleMetadataFile.asFile.apply {
            parentFile.mkdirs()
            writeText(Json.encodeToString(
                ModuleMetadata(getKobwebVersion(), projectGroup.get(), publicPath.get())
            ))
        }
    }
}
