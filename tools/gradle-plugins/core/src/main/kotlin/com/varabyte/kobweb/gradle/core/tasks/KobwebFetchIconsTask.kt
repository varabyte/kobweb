package com.varabyte.kobweb.gradle.core.tasks

import com.varabyte.kobweb.gradle.core.icons.IconsLock
import com.varabyte.kobweb.icons.codegen.IconSet
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.UntrackedTask

@UntrackedTask(because = "It downloads from the network, which Gradle can't check for changes.")
abstract class KobwebFetchIconsTask :
    KobwebTask("Download data for the icon sets in `kobweb { icons { ... } }` into this module's icons store, which should be checked in.") {
    /** Each configured icon set's id mapped to its requested version, e.g. `"fa" to "6.+"`. */
    @get:Input
    abstract val versionSpecs: MapProperty<String, String>

    /** The folder that fetched data and lock files are written to. */
    @get:Internal
    abstract val storeDir: DirectoryProperty

    @TaskAction
    fun execute() {
        if (versionSpecs.get().isEmpty()) {
            logger.warn("w: No icon sets are configured in `kobweb { icons { ... } }`, so there's nothing to fetch.")
            return
        }

        val store = storeDir.get().asFile
        store.mkdirs()
        versionSpecs.get().forEach { (id, spec) ->
            val iconSet = IconSet.all.single { it.id == id }
            val version = iconSet.resolveVersion(spec)
            if (version != spec) logger.lifecycle("Resolved \"$id\" icon set version \"$spec\" to $version")
            iconSet.fetch(version, store.resolve(iconSet.iconsFileName))
            IconsLock(spec, version).writeTo(IconsLock.fileFor(store, id))
        }
    }
}
