package com.varabyte.kobweb.gradle.application.tasks

import com.varabyte.kobweb.gradle.core.metadata.IconsMetadata
import com.varabyte.kobweb.gradle.core.util.searchZipFor
import com.varabyte.kobweb.ksp.KOBWEB_METADATA_ICONS
import com.varabyte.kobweb.project.common.nonStrictDefault
import kotlinx.serialization.json.Json
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import java.io.File

// NOTE: This task in meant as an internal API so it does not inherit from KobwebTask
/**
 * Collect the icon sets of this app and every dependency that generates icon bindings, writing them to [iconSetsFile]
 * as a map of module names to [IconsMetadata].
 *
 * This fails if multiple modules generate bindings into the same package. Bindings are internal by default, so that
 * compiles, but Kotlin/JS links declarations by their fully qualified name, so one module's code would end up calling
 * another's bindings, and the site would crash when it loads (or the link would fail with a cryptic error). Tasks that
 * link the site's JavaScript should take [iconSetsFile] as an input, so that this check runs first.
 */
@CacheableTask
abstract class KobwebCacheIconSetsTask : DefaultTask() {
    init {
        description =
            "Collect the icon sets of this app and its dependencies into a file that downstream tasks can read, failing if multiple modules generate icon bindings into the same package."
    }

    /** The name of this app's Gradle project, e.g. "site", used to refer to it alongside its dependencies. */
    @get:Input
    abstract val projectName: Property<String>

    /** Icon sets this app generates bindings for, mapped to their locked versions, e.g. `"fa" to "6.5.2"`. */
    @get:Input
    abstract val iconSetVersions: MapProperty<String, String>

    /**
     * Icon sets this app generates bindings for, mapped to the packages they're generated into, e.g.
     * `"fa" to "com.example.components.icons.fa"`.
     */
    @get:Input
    abstract val iconSetPackageNames: MapProperty<String, String>

    // Dependencies are named after their files, so file names matter, but not where the files are
    @get:InputFiles
    @get:PathSensitive(PathSensitivity.NAME_ONLY)
    abstract val compileClasspath: ConfigurableFileCollection

    @get:OutputFile
    abstract val iconSetsFile: RegularFileProperty

    @TaskAction
    fun execute() {
        val appPackageNames = iconSetPackageNames.get()
        val iconsMetadataByModule = buildMap {
            put(projectName.get(), IconsMetadata(iconSetVersions.get().mapValues { (id, version) ->
                IconsMetadata.IconSet(version, appPackageNames.getValue(id))
            }))
            compileClasspath.forEach { file ->
                file.searchZipFor(KOBWEB_METADATA_ICONS) { bytes ->
                    put(file.dependencyName, Json.nonStrictDefault.decodeFromString<IconsMetadata>(bytes.decodeToString()))
                }
            }
        }

        val modulesByPackageName = mutableMapOf<String, MutableList<String>>()
        iconsMetadataByModule.forEach { (module, iconsMetadata) ->
            iconsMetadata.iconSets.values.forEach { iconSet ->
                modulesByPackageName.getOrPut(iconSet.packageName) { mutableListOf() }.add(module)
            }
        }
        modulesByPackageName.forEach { (packageName, modules) ->
            if (modules.size > 1) {
                throw GradleException(
                    "e: Multiple modules (${modules.joinToString { "\"$it\"" }}) generate icon bindings into the same " +
                        "package, \"$packageName\", which would crash the site when it loads. To fix this, give these " +
                        "modules different Gradle groups, generate the icons once in a library that they share (with " +
                        "`public.set(true)` in its `icons` block), or set `rootPackage` in one of their `icons` blocks."
                )
            }
        }

        iconSetsFile.get().asFile.writeText(Json.encodeToString(iconsMetadataByModule))
    }
}

/** The name of the dependency this artifact belongs to, e.g. "sitelib-js-1.0.klib" -> "sitelib". */
internal val File.dependencyName get() = nameWithoutExtension.substringBeforeLast("-js-")
