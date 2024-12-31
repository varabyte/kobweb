import org.gradle.kotlin.dsl.findByType
import org.gradle.plugin.devel.GradlePluginDevelopmentExtension

plugins {
    id("com.gradle.plugin-publish")
}

val gradlePluginExtension = extensions.findByType<GradlePluginDevelopmentExtension>()
gradlePluginExtension?.apply {
    website.convention("https://kobweb.varabyte.com/")
    vcsUrl.convention("https://github.com/varabyte/kobweb.git")

    afterEvaluate {
        gradlePluginExtension.plugins.forEach { pluginDeclaration ->
            @Suppress("UnstableApiUsage")
            pluginDeclaration.tags.addAll(
                "kotlin",
                "kobweb",
                "web",
            )
        }
    }
} ?: run {
    logger.error("Kobweb gradle portal plugin applied but no `gradlePlugin` block was found.")
}

// Snapshots are not allowed in the Gradle plugin portal. Let's play it safe and disable the task in this case.
// See also: https://plugins.gradle.org/docs/publish-plugin
tasks.named("publishPlugins") {
    // Use providers as a way to avoid having a hard reference to this task's project as this causes a problem with
    // the configuration cache.
    // See also: https://docs.gradle.org/current/userguide/configuration_cache.html#config_cache:requirements:use_project_during_execution
    val versionProvider = providers.provider { version.toString() }
    val isSnapshotProvider = providers.provider { versionProvider.get().endsWith("-SNAPSHOT") }
    val fullTaskNameProvider = providers.provider { "${project.path}:$name" }

    onlyIf {
        val isSnapshot = isSnapshotProvider.get()
        if (isSnapshot) {
            project.logger.warn("Disabling task '${fullTaskNameProvider.get()}' because version is a snapshot (${versionProvider.get()}).")
        }

        !isSnapshot
    }
}
