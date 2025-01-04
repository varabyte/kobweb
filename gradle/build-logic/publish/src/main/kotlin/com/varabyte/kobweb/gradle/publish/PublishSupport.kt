package com.varabyte.kobweb.gradle.publish

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.component.SoftwareComponent
import org.gradle.api.component.SoftwareComponentContainer
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.tasks.AbstractPublishToMaven
import org.gradle.authentication.http.BasicAuthentication
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.provideDelegate
import org.gradle.kotlin.dsl.withType
import org.gradle.plugins.signing.Sign
import org.gradle.plugins.signing.SigningExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import java.io.File

internal fun Project.shouldSign() = (findProperty("kobweb.sign") as? String).toBoolean()
internal fun Project.shouldPublishToGCloud(): Boolean {
    return (findProperty("kobweb.gcloud.publish") as? String).toBoolean()
        && findProperty("gcloud.artifact.registry.secret") != null
}

internal fun Project.shouldPublishToMavenCentral(): Boolean {
    return (findProperty("kobweb.maven.publish") as? String).toBoolean()
        && findProperty("ossrhToken") != null && findProperty("ossrhTokenPassword") != null
}

internal fun MavenArtifactRepository.gcloudAuth(project: Project) {
    with(project) {
        url = uri("https://us-central1-maven.pkg.dev/varabyte-repos/public")
        credentials {
            username = "_json_key_base64"
            password = findProperty("gcloud.artifact.registry.secret") as String
        }
        authentication {
            create("basic", BasicAuthentication::class.java)
        }
    }
}

fun MavenArtifactRepository.sonatypeAuth(project: Project) {
    name = "SonatypeMaven"
    with(project) {
        url = if (!version.toString().endsWith("SNAPSHOT"))
            uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
        else uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")

        credentials {
            username = findProperty("ossrhToken") as String
            password = findProperty("ossrhTokenPassword") as String
        }
        authentication {
            create<BasicAuthentication>("basic")
        }
    }
}

internal fun PublishingExtension.prepareRepositories(project: Project) {
    if (project.shouldPublishToGCloud()) {
        repositories {
            maven {
                name = "GCloudMaven"
                gcloudAuth(project)
            }
        }
    }
    if (project.shouldPublishToMavenCentral()) {
        repositories {
            maven {
                name = "SonatypeMaven"
                sonatypeAuth(project)
            }
        }
    }
}

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
        val javaComponent = project.components.java
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
        project.tasks.dokkaHtmlJar
            // For now, only generate a javadoc for the JVM target. It's the only artifact type enforced by maven
            // central, and if I allow all targets, it adds ~20MB of extra files across all modules. We can easily
            // remove this line later if it becomes obvious there's value in supporting javadocs for all targets.
            .takeIf { this.name == "jvm" }
            // The Kotlin multiplatform plugin doesn't automatically add javadoc jars, so we use dokka to generate them
            // ourselves. We skip this for java projects, as this is already accomplished above via the withJavadocJar()
            // call.
            .takeIf { project.isKotlinMultiplatform }
            ?.let { javadocJar -> artifact(javadocJar) }

        if (artifactId != null) {
            val platformName = this.name
            this.artifactId = buildString {
                append(artifactId)
                when (platformName) {
                    "js", "jvm" -> append("-$platformName")
                }
            }
        }
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
}

private val Project.java: JavaPluginExtension?
    get() = extensions.findByName("java") as? JavaPluginExtension

private val Project.isKotlinMultiplatform: Boolean
    get() = extensions.findByType<KotlinMultiplatformExtension>() != null

private val SoftwareComponentContainer.java: SoftwareComponent?
    get() = findByName("java")

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
            config.artifactName.orNull,
            config.artifactId.orNull,
            config.relocationDetails,
            config.description.orNull,
            config.site.orNull,
        )
    }

    if (shouldSign()) {
        // Workaround for https://youtrack.jetbrains.com/issue/KT-61858
        val signingTasks = tasks.withType<Sign>()
        tasks.withType<AbstractPublishToMaven>().configureEach {
            mustRunAfter(signingTasks)
        }

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
            sign(publishing.publications)
        }
    }
}
