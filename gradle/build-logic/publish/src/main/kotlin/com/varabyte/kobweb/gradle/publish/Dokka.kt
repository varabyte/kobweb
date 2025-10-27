package com.varabyte.kobweb.gradle.publish

import org.gradle.api.Project
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.named
import org.jetbrains.dokka.gradle.DokkaExtension
import org.jetbrains.dokka.gradle.engine.plugins.DokkaHtmlPluginParameters

@Suppress("UnstableApiUsage")
fun Project.configureDokka() {
    configure<DokkaExtension> {
        dokkaSourceSets.configureEach {
            sourceLink {
                val path = project.projectDir.relativeTo(layout.settingsDirectory.asFile).invariantSeparatorsPath
                localDirectory = project.projectDir.resolve("src")
                remoteUrl("https://github.com/varabyte/kobweb/tree/main/$path/src")
                remoteLineSuffix = "#L"
            }
        }
        pluginsConfiguration.named<DokkaHtmlPluginParameters>("html") {
            homepageLink.set("https://kobweb.varabyte.com")
            customStyleSheets.from(layout.settingsDirectory.file("tools/aggregate-docs/styles/transition.css"))
        }
    }
}
