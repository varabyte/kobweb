package com.varabyte.kobweb.gradle.core.tasks

import com.varabyte.kobweb.gradle.core.metadata.IconsMetadata
import com.varabyte.kobweb.ksp.KOBWEB_METADATA_ICONS
import com.varabyte.kobweb.project.common.nonStrictDefault
import kotlinx.serialization.json.Json
import org.gradle.api.provider.MapProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.work.DisableCachingByDefault

@DisableCachingByDefault(because = "Trivial output, not worth caching.")
abstract class KobwebGenerateIconsMetadataTask :
    KobwebTask("Generate an icons.json metadata file into this project's jar metadata, which tells apps which icon sets this artifact uses.") {

    /** This module's icon sets mapped to their locked versions, e.g. `"fa" to "6.5.2"`. */
    @get:Input
    abstract val iconSetVersions: MapProperty<String, String>

    /** This module's icon sets mapped to their packages, e.g. `"fa" to "com.example.components.icons.fa"`. */
    @get:Input
    abstract val iconSetPackageNames: MapProperty<String, String>

    @OutputDirectory
    fun getGenResDir() = projectLayout.buildDirectory.dir("generated/kobweb/icons/metadata")

    @TaskAction
    fun execute() {
        val iconsMetadataFile = getGenResDir().get().file(KOBWEB_METADATA_ICONS).asFile
        // Modules without icon sets don't get the file, so remove it if an earlier build wrote one
        iconsMetadataFile.delete()

        val packageNames = iconSetPackageNames.get()
        val iconSets = iconSetVersions.get().mapValues { (id, version) ->
            IconsMetadata.IconSet(version, packageNames.getValue(id))
        }
        if (iconSets.isEmpty()) return

        iconsMetadataFile.parentFile.mkdirs()
        iconsMetadataFile.writeText(Json.nonStrictDefault.encodeToString(IconsMetadata(iconSets)))
    }
}
