package com.varabyte.kobweb.gradle.core.util

import org.gradle.api.Project
import org.gradle.kotlin.dsl.findByType
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeCompilerGradlePluginExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType

internal const val KOBWEB_CONFIGURE_COMPOSE_COMPILER = "kobweb.configureComposeCompiler"

fun Project.configureComposeCompiler() {
    pluginManager.withPlugin("org.jetbrains.kotlin.plugin.compose") {
        // As described in https://github.com/gradle/gradle/issues/23792#issuecomment-1431053503,
        // if the compose plugin is loaded in a separate classloader, we can't access and configure it.
        val composeCompilerExt = try {
            extensions.findByType<ComposeCompilerGradlePluginExtension>()
        } catch (e: Throwable) {
            when (e) {
                is TypeNotPresentException, is ClassNotFoundException, is NoClassDefFoundError -> {
                    project.logger.warn(
                        "w: The Kobweb plugin failed to configure the compose compiler plugin. " +
                            "This prevents it from applying settings which may improve the size / performance of your site. " +
                            "To fix this, ensure that the plugin `org.jetbrains.kotlin.plugin.compose` is loaded at the same " +
                            "level as the Kobweb plugin. For example, if you load the Kobweb plugin in the project root " +
                            "with `alias(libs.plugins.kobweb.application) apply false`, you should also load the compose " +
                            "plugin there (e.g. with `alias(libs.plugins.compose.compiler) apply false`). " +
                            "To disable Kobweb's configuration of the compose compiler plugin, " +
                            "add `$KOBWEB_CONFIGURE_COMPOSE_COMPILER=false` to your project's `gradle.properties` file."
                    )
                    null
                }

                else -> throw e
            }
        } ?: return@withPlugin
        // Trace markers are "pure overhead" for the JS target & needlessly increase the bundle size, but
        // must be explicitly disabled until https://youtrack.jetbrains.com/issue/KT-69900 is resolved.
        composeCompilerExt.includeTraceMarkers.set(false)
        // Unlike standard multiplatform applications, Kobweb projects only use compose on the JS frontend,
        // so we disable the compiler plugin for other targets.
        composeCompilerExt.targetKotlinPlatforms.set(setOf(KotlinPlatformType.js))
    }
}
