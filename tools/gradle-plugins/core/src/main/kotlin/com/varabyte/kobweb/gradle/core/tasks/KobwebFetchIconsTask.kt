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
    /** Each configured icon set's id mapped to its version. */
    @get:Input
    abstract val versions: MapProperty<String, String>

    /** The folder that fetched data and lock files are written to. */
    @get:Internal
    abstract val storeDir: DirectoryProperty

    @TaskAction
    fun execute() {
        if (versions.get().isEmpty()) {
            logger.warn("w: No icon sets are configured in `kobweb { icons { ... } }`, so there's nothing to fetch.")
            return
        }

        val store = storeDir.get().asFile
        store.mkdirs()
        versions.get().forEach { (id, version) ->
            val iconSet = IconSet.all.single { it.id == id }
            iconSet.fetch(version, store.resolve(iconSet.iconsFileName))
            IconsLock(version).writeTo(IconsLock.fileFor(store, id))
        }
    }
}
