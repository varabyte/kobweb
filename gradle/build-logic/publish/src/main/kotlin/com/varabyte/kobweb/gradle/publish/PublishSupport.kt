package com.varabyte.kobweb.gradle.publish

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.authentication.http.BasicAuthentication

val Project.gcloudSecret: String? get() = findProperty("gcloud.artifact.registry.secret") as? String

/**
 * @param artifactName A display name for the artifact. Should almost always be set, but can be null if somewhere else
 *   ends up being responsible for setting the maven publication's name (e.g. like the `gradlePlugin` block). If a name
 *   ultimately never gets registered, publishing artifacts locally will work but maven central will reject it.
 *
 * @param artifactId Can be null if we want to let the system use the default value for this project.
 *   This is particularly useful for publishing Gradle plugins, which does some of its own magic that we don't want
 *   to fight with.
 */
internal fun PublishingExtension.addVarabyteArtifact(
    project: Project,
    artifactName: String?,
    artifactId: String?,
    relocationDetails: KobwebPublicationConfig.RelocationDetails,
    description: String?,
    site: String?,
) {
    if (!relocationDetails.artifactId.isPresent) {
        project.mavenPublishing {
            if (artifactId != null) {
                coordinates(artifactId = artifactId)
            }

            publishToMavenCentral(automaticRelease = true)
            signAllPublications()

            pom {
                val githubPath = "https://github.com/varabyte/kobweb"
                url.set(githubPath)
                if (artifactName != null) this.name.set(artifactName)
                description?.let { this.description.set(it) }
                site?.let { this.url.set(it) }
                scm {
                    url.set(githubPath)
                    val connectionPath = "scm:git:${githubPath}.git"
                    connection.set(connectionPath)
                    developerConnection.set(connectionPath)
                }
                developers {
                    developer {
                        id.set("bitspittle")
                        name.set("David Herman")
                        email.set("bitspittle@gmail.com")
                        url.set("https://github.com/bitspittle")
                    }
                    developer {
                        id.set("dennistsar")
                        name.set("Dennis Tsar")
                        url.set("https://github.com/dennistsar")
                    }
                }
                licenses {
                    license {
                        name.set("Apache-2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
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
}

private fun Project.publishing(configure: Action<PublishingExtension>): Unit =
    extensions.configure("publishing", configure)

internal fun Project.configurePublishing(config: KobwebPublicationConfig) {
    val project = this

    publishing {
        project.gcloudSecret?.let { gcloudSecret ->
            repositories {
                maven {
                    name = "GCloudMaven"
                    url = project.uri("https://us-central1-maven.pkg.dev/varabyte-repos/public")
                    credentials {
                        username = "_json_key_base64"
                        password = gcloudSecret
                    }
                    authentication {
                        create("basic", BasicAuthentication::class.java)
                    }
                }
            }
        }

        addVarabyteArtifact(
            project,
            config.artifactName.orNull,
            config.artifactId.orNull,
            config.relocationDetails,
            config.description.orNull,
            config.site.orNull,
        )
    }
}
