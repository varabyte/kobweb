import com.varabyte.kobweb.gradle.publish.configureDokka
import org.gradle.kotlin.dsl.support.uppercaseFirstChar
import org.jetbrains.dokka.gradle.tasks.DokkaGenerateTask

plugins {
    alias(libs.plugins.dokka)
    id("com.varabyte.kobweb.internal.publish") apply false // to access the `configureDokka` function
}

group = "com.varabyte.kobweb"
version = libs.versions.kobweb.get()

repositories {
    mavenCentral()
}

configureDokka()

dokka {
    moduleName = "kobweb"
}

val includedProjects = setOf(
    projects.common.frameworkAnnotations,
    projects.common.kobwebCommon,
    projects.common.kobwebSerialization,
    projects.common.kobwebxFrontmatter,
    projects.common.kobwebxSerializationKotlinx,
    projects.frontend.kobwebCore,
    projects.frontend.kobwebCompose,
    projects.frontend.kobwebSilk,
    projects.frontend.kobwebWorker,
    projects.frontend.kobwebWorkerInterface,
    projects.frontend.silkFoundation,
    projects.frontend.silkWidgets,
    projects.frontend.silkWidgetsKobweb,
    projects.frontend.silkIconsFa,
    projects.frontend.silkIconsMdi,
    projects.frontend.silkIconsMs,
    projects.frontend.silkIconsLucide,
    projects.frontend.kobwebxMarkdown,
    projects.frontend.composeHtmlExt,
    projects.frontend.browserExt,
    projects.backend.io,
    projects.backend.kobwebApi,
    projects.backend.serverPlugin,
    projects.tools.gradlePlugins.core,
    projects.tools.gradlePlugins.library,
    projects.tools.gradlePlugins.application,
    projects.tools.gradlePlugins.worker,
    projects.tools.gradlePlugins.extensions.markdown,
)

val excludedProjects = setOf(
    projects.backend.server,
    projects.common.clientServerInternal,
    projects.frontend.test.composeTestUtils,
    projects.tools.ksp.siteProcessors,
    projects.tools.ksp.workerProcessor,
    projects.tools.ksp.kspExt,
    projects.tools.aggregateDocs,
    projects.tools.processorCommon,
)

dependencies {
    includedProjects.forEach { project -> dokka(project) }
}

// region Dokka setup validation

// Here, we report if someone adds a new module and does not explicitly register it in either the included or excluded
// project lists. We do this in a way that is Gradle "isolated projects" compatible.

val subprojectPathsProvider = provider {
    val allPaths = rootProject.subprojects.map { it.path }.toSet()

    // Remove empty "container" projects, like ":frontend"
    allPaths.filter { path ->
        allPaths.none { other -> other != path && other.startsWith("$path:") }
    }.toSet()
}

@UntrackedTask(because = "Validation task with no output files")
abstract class ValidateDokkaConfiguredProjectsTask : DefaultTask() {
    @get:Input
    abstract val allProjectPaths: SetProperty<String>

    @get:Input
    abstract val includedProjectPaths: SetProperty<String>

    @get:Input
    abstract val excludedProjectPaths: SetProperty<String>

    @TaskAction
    fun validate() {
        fun String.kebabCaseToCamelCase(): String {
            return this
                .split("-")
                .mapIndexed { i, part ->
                    if (i == 0) part else part.uppercaseFirstChar()
                }.joinToString("")
        }

        fun String.pathToProjectAccessor(): String {
            val pathParts = this.split(":")
            return "projects${pathParts.joinToString(".") { it.kebabCaseToCamelCase() }}"
        }

        val doubleEntered = includedProjectPaths.get().intersect(excludedProjectPaths.get())
        if (doubleEntered.isNotEmpty()) {
            throw GradleException(
                """
                |The following project(s) were entered in both the `includedProjects` and `excludedProjects` lists:
                |${doubleEntered.joinToString("\n") { path -> " - ${path.pathToProjectAccessor()}" }}
                |
                |Please ensure each project only appears in one list or the other.
                """.trimMargin()
            )
        }

        val unconfigured = allProjectPaths.get() - includedProjectPaths.get() - excludedProjectPaths.get()
        if (unconfigured.isNotEmpty()) {
            throw GradleException(
                """
                |The following project(s) are missing an explicit Dokka entry in `tools/aggregate-docs`:
                |${unconfigured.joinToString("\n") { path -> " - ${path.pathToProjectAccessor()}" }}
                |
                |Please make an explicit choice:
                | - Add to `includedProjects` if public documentation should be generated.
                | - Add to `excludedProjects` if this module is internal/private.
                """.trimMargin()
            )
        }
    }
}

val validateDokkaConfiguredProjectsTask = tasks.register<ValidateDokkaConfiguredProjectsTask>("validateDokkaConfiguredProjects") {
    description = "Make sure that all modules are explicitly opted in or out from dokka docs generation"
    group = "verification"
    allProjectPaths.set(subprojectPathsProvider)
    includedProjectPaths.set(includedProjects.map { it.path })
    excludedProjectPaths.set(excludedProjects.map { it.path })
}

tasks.withType<DokkaGenerateTask>().configureEach {
    dependsOn(validateDokkaConfiguredProjectsTask)
}

// endregion
