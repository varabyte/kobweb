package com.varabyte.kobweb.plugins.publish

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

private fun artifactSuffix(name: String): String {
    return when(name) {
        // For multiplatform targets, append them, so e.g. "kobweb" becomes "kobweb-js"
        "js", "jvm" -> "-${name}"
        else -> ""
    }
}

internal fun PublishingExtension.addVarabyteArtifact(
    project: Project,
    artifactId: String,
    description: String,
    site: String,
) {
    if (project.shouldSign() && project.shouldPublishToGCloud()) {
        repositories {
            maven { gcloudAuth(project) }
        }
    }

    publications.withType(MavenPublication::class.java) {
        this.artifactId = artifactId + artifactSuffix(name)
        pom {
            this.description.set(description)
            url.set(site)
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

internal fun Project.configurePublishing(extension: KobwebPublicationExtension) {
    publishing {
        addVarabyteArtifact(
            project,
            extension.artifactId.get(),
            extension.description.get(),
            extension.site.get(),
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