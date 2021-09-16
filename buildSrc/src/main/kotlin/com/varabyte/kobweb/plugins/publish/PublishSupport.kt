package com.varabyte.kobweb.plugins.publish

import org.gradle.api.Project
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.authentication.http.BasicAuthentication

fun Project.shouldSign() = (findProperty("kobweb.sign") as? String).toBoolean()
fun Project.shouldPublishToGCloud(): Boolean {
    return (findProperty("kobweb.gcloud.publish") as? String).toBoolean()
            && findProperty("gcloud.artifact.registry.secret") != null
}

fun MavenArtifactRepository.gcloudAuth(project: Project) {
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

fun PublishingExtension.addVarabyteArtifact(
    project: Project,
    artifactId: String,
    description: String,
    site: String = "https://github.com/varabyte/kobweb",
) {
    if (project.shouldPublishToGCloud()) {
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