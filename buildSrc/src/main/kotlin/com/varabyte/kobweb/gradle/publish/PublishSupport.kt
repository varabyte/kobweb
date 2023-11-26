package com.varabyte.kobweb.gradle.publish

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.authentication.http.BasicAuthentication
import org.gradle.kotlin.dsl.provideDelegate
import org.gradle.kotlin.dsl.withType
import org.gradle.plugins.signing.SigningExtension
import java.io.File

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

internal fun PublishingExtension.prepareRepositories(project: Project) {
    if (project.shouldSign() && project.shouldPublishToGCloud()) {
        repositories {
            maven { gcloudAuth(project) }
        }
    }
}

/**
 * @param artifactId Can be null if we want to let the system use the default value for this project.
 *   This is particularly useful for publishing Gradle plugins, which does some of its own magic that we don't want
 *   to fight with.
 */
internal fun PublishingExtension.addVarabyteArtifact(
    project: Project,
    artifactId: ((String) -> String)?,
    relocationDetails: KobwebPublicationConfig.RelocationDetails,
    description: String?,
    site: String?,
) {
    if (!relocationDetails.artifactId.isPresent) {
        val javaComponent = project.components.findByName("java")
        if (javaComponent != null) {
            project.java?.let {
                it.withJavadocJar()
                it.withSourcesJar()
            }
        }

        // kotlin("jvm") projects don't automatically declare a maven publication
        if (publications.none { it is MavenPublication }) {
            check(javaComponent != null) // This seems to always be true so far
            publications.register("maven", MavenPublication::class.java) {
                groupId = project.group.toString()
                if (artifactId != null) {
                    this.artifactId = artifactId.invoke(this.name)
                }
                version = project.version.toString()

                from(javaComponent)
            }
        }
    } else {
        publications.register("relocation", MavenPublication::class.java) {
            pom {
                distributionManagement {
                    relocation {
                        relocationDetails.groupId.orNull?.let { this.groupId.set(it) }
                        this.artifactId.set(relocationDetails.artifactId)
                        this.message.set(relocationDetails.message)
                    }
                }
            }
        }
    }

    publications.withType<MavenPublication>().configureEach {
        if (artifactId != null) {
            this.artifactId = artifactId.invoke(this.name)
        }
        pom {
            description?.let { this.description.set(it) }
            site?.let { this.url.set(it) }
            licenses {
                license {
                    name.set("The Apache License, Version 2.0")
                    url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                }
            }
        }
    }
}

private val Project.java: JavaPluginExtension?
    get() = extensions.getByName("java") as? JavaPluginExtension

private val Project.publishing: PublishingExtension
    get() = extensions.getByName("publishing") as PublishingExtension

private fun Project.publishing(configure: Action<PublishingExtension>): Unit =
    extensions.configure("publishing", configure)

private fun Project.signing(configure: Action<SigningExtension>): Unit =
    extensions.configure("signing", configure)

internal fun Project.configurePublishing(config: KobwebPublicationConfig) {
    val project = this

    publishing {
        prepareRepositories(project)
        addVarabyteArtifact(
            project,
            config.artifactId.orNull,
            config.relocationDetails,
            config.description.orNull,
            config.site.orNull,
        )
    }

    if (shouldSign()) {
        signing {
            val secretKeyRingExists = (findProperty("signing.secretKeyRingFile") as? String)
                ?.let { File(it).exists() }
                ?: false

            // If "shouldSign" returns true, then singing password should be set
            val signingPassword = findProperty("signing.password") as String

            // If here, we're on a CI. Check for the signing key which must be set in an environment variable.
            // See also: https://docs.gradle.org/current/userguide/signing_plugin.html#sec:in-memory-keys
            if (!secretKeyRingExists) {
                val signingKey: String? by project
                useInMemoryPgpKeys(signingKey, signingPassword)
            }

            // Signing requires following steps at https://docs.gradle.org/current/userguide/signing_plugin.html#sec:signatory_credentials
            // and adding singatory properties somewhere reachable, e.g. ~/.gradle/gradle.properties
            sign(publishing.publications.matching { config.filter.get().invoke(it) })
        }
    }
}
