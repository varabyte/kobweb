plugins {
    alias(libs.plugins.dokka)
}

group = "com.varabyte.kobweb"
version = libs.versions.kobweb.get()

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
    projects.frontend.silkIconsLucide,
    projects.frontend.silkIconsMdi,
    projects.frontend.kobwebxMarkdown,
    projects.frontend.composeHtmlExt,
    projects.frontend.browserExt,
    projects.backend.kobwebApi,
    projects.backend.serverPlugin,
    projects.tools.gradlePlugins.core,
    projects.tools.gradlePlugins.library,
    projects.tools.gradlePlugins.application,
    projects.tools.gradlePlugins.worker,
    projects.tools.gradlePlugins.extensions.markdown,
)

val excludedProjects = setOf(
    projects.common.clientServerInternal,
    projects.tools.ksp.siteProcessors,
    projects.tools.ksp.workerProcessor,
    projects.tools.ksp.kspExt,
    projects.tools.aggregateDocs,
    projects.tools.processorCommon,
)

dependencies {
    includedProjects.forEach { project -> dokka(project) }
}

// Warn if we ever add a new module and forget to add a dokka entry for it (which is too easy to do).
// The Gradle team may decide to prevent this code from working in the future; if that happens, then I guess we'll just
// have to remove it.
gradle.projectsEvaluated {
    val combinedProjects = (includedProjects + excludedProjects).toSet()
    val referencedPaths = combinedProjects.map { it.path }.toSet()
    val dokkaId = libs.plugins.dokka.get().pluginId

    rootProject.subprojects {
        if (referencedPaths.contains(project.path) && !project.plugins.hasPlugin(dokkaId)) {
            logger.warn("w: Project ${project.path} doesn't apply the dokka plugin and doesn't have to be included in `aggregate-docs`")
        } else if (!referencedPaths.contains(project.path) && project.plugins.hasPlugin(dokkaId)) {
            logger.warn("w: Project ${project.path} has no dokka entry. Please make an explicit choice to include or exclude it in `aggregate-docs`")
        }
    }
}

