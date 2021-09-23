package com.varabyte.kobweb.gradle.publish

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.create

abstract class KobwebPublicationExtension {
    abstract val artifactId: Property<String>
    abstract val description: Property<String>
    abstract val site: Property<String>
}

/**
 * An internal plugin that helps configure and publish Varabyte artifacts to our maven repository.
 *
 * By default, this will only publish to mavenLocal, unless you have "kobweb.gcloud.publish" set to true and
 * "gcloud.artifact.registry.secret" set with the correct authentication key.
 *
 * Additionally, this will attempt to sign your files only if you have "kobweb.sign" set to true. If this is false,
 * then files will not be published to the cloud.
 */
class KobwebPublishPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.pluginManager.apply {
            apply("org.gradle.maven-publish")
            apply("org.gradle.signing")
        }

        val extension = project.extensions.create<KobwebPublicationExtension>("kobwebPublication")
        project.afterEvaluate {
            // Configure after evaluating to allow the user to set extension values first
            configurePublishing(extension)
        }
    }
}