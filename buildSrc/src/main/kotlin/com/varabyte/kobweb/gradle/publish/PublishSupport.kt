package com.varabyte.kobweb.gradle.publish

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.authentication.http.BasicAuthentication
import org.gradle.plugins.signing.SigningExtension

internal fun Project.shouldSign() = (findProperty("kobweb.sign") as? String).toBoolean()
internal fun Project.shouldPublishToGCloud(): Boolean {
    return (findProperty("kobweb.gcloud.publish") as? String).toBoolean()
            && findProperty("gcloud.artifact.registry.secret") != null
}

internal fun MavenArtifactRepository.gcloudAuth(project: Project) {
    url = project.uri("https://us-central1-maven.pkg.dev/varabyte-repos/public")
    credentials {
        username = "_json_key_base64"
        password = project.findProperty("gcloud.artifact.registry.secret") as String
    }
    authentication {
        create("basic", BasicAuthentication::class.java)
    }
}

/**
 * @param artifactId Can be null if we want to let the system use the default value for this project.
 *   This is particularly useful for publishing Gradle plugins, which does some of its own magic that we don't want
 *   to fight with.
 */
internal fun PublishingExtension.addVarabyteArtifact(
    project: Project,
    artifactId: String?,
    description: String?,
    site: String?,
) {
    if (project.shouldSign() && project.shouldPublishToGCloud()) {
        repositories {
            maven { gcloudAuth(project) }
        }
    }

    // kotlin("jvm") projects don't automatically declare a maven publication
    if (publications.none { it is MavenPublication }) {
        val javaComponent = project.components.findByName("java")
        if (javaComponent != null) {
            publications.create("maven", MavenPublication::class.java) {
                groupId = project.group.toString()
                this.artifactId = artifactId
                version = project.version.toString()

                from(javaComponent)
            }
        }
    }

    publications.withType(MavenPublication::class.java) {
        if (artifactId != null) {
            this.artifactId = artifactId
        }
        pom {
            description?.let { this.description.set(it) }
            url.set(site ?: "https://github.com/varabyte/kobweb")
            licenses {
                license {
                    name.set("The Apache License, Version 2.0")
                    url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                }
            }
        }
    }
}

private val Project.publishing: PublishingExtension get() =
    (this as ExtensionAware).extensions.getByName("publishing") as PublishingExtension

private fun Project.publishing(configure: Action<PublishingExtension>): Unit =
    (this as ExtensionAware).extensions.configure("publishing", configure)

private fun Project.signing(configure: Action<SigningExtension>): Unit =
    (this as ExtensionAware).extensions.configure("signing", configure)

internal fun Project.configurePublishing(config: KobwebPublicationConfig) {
    publishing {
        addVarabyteArtifact(
            project,
            config.artifactId.orNull,
            config.description.orNull,
            config.site.orNull,
        )
    }

    if (shouldSign()) {
        signing {
            // Signing requires following steps at https://docs.gradle.org/current/userguide/signing_plugin.html#sec:signatory_credentials
            // and adding singatory properties somewhere reachable, e.g. ~/.gradle/gradle.properties
            sign(publishing.publications)
        }
    }
}