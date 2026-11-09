package com.varabyte.kobweb.gradle.core.tasks

import com.varabyte.kobweb.gradle.core.icons.IconsLock
import com.varabyte.kobweb.icons.codegen.IconCodegenOptions
import com.varabyte.kobweb.icons.codegen.IconSet
import com.varabyte.kobweb.project.common.PackageName
import com.varabyte.kobweb.project.common.PackageShortcut
import org.gradle.api.GradleException
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction

@CacheableTask
abstract class KobwebGenerateIconsTask :
    KobwebTask("Generate Kotlin bindings for the icon sets in the `icons { ... }` block, from data fetched by kobwebFetchIcons.") {
    /** Each configured icon set's id mapped to its requested version, e.g. `"fa" to "6.+"`. */
    @get:Input
    abstract val versionSpecs: MapProperty<String, String>

    /** The folder that kobwebFetchIcons writes fetched data and lock files to. */
    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val storeDir: DirectoryProperty

    /** The Gradle group of the current project, which is used to resolve [rootPackage] shortcuts. */
    @get:Input
    abstract val projectGroup: Property<String>

    @get:Input
    abstract val rootPackage: Property<String>

    @get:Input
    abstract val public: Property<Boolean>

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @TaskAction
    fun execute() {
        val rootPackageName = PackageName(projectGroup.get()).resolve(PackageShortcut(rootPackage.get()))
        rootPackageName.findProblems().takeIf { it.isNotEmpty() }?.let { problems ->
            throw GradleException(
                "Invalid `rootPackage` \"${rootPackage.get()}\" in the `icons { ... }` block: ${problems.joinToString(" ")}"
            )
        }
        // Clear out bindings for icon sets that are no longer configured
        val outDir = outputDir.get().asFile
        outDir.deleteRecursively()
        outDir.mkdirs()

        val storeRoot = storeDir.get().asFile
        versionSpecs.get().forEach { (id, spec) ->
            val iconSet = IconSet.all.single { it.id == id }
            val iconsFile = storeRoot.resolve(iconSet.iconsFileName)
            val lock = IconsLock.readFrom(IconsLock.fileFor(storeRoot, id))
            if (lock == null || !iconsFile.exists()) {
                throw GradleException(
                    "No data has been fetched for the \"$id\" icon set yet. Run the kobwebFetchIcons task, then check " +
                        "in the files it writes to ${storeRoot.relativeTo(projectLayout.projectDirectory.asFile)}."
                )
            }
            if (lock.spec != spec) {
                throw GradleException(
                    "The \"$id\" icon set is configured to use version \"$spec\", but its data was fetched for " +
                        "\"${lock.spec}\"${if (lock.version != lock.spec) " (resolved to ${lock.version})" else ""}. " +
                        "Run the kobwebFetchIcons task to update it."
                )
            }

            iconSet.generateBindings(
                iconsFile,
                IconCodegenOptions(packageName = "${rootPackageName.value}.$id", isPublic = public.get()),
                outDir,
            )
        }
    }
}
