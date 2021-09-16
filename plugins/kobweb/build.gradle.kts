import com.varabyte.kobweb.plugins.publish.addVarabyteArtifact
import com.varabyte.kobweb.plugins.publish.shouldSign

plugins {
    id("com.google.devtools.ksp")
    kotlin("jvm")
    `java-gradle-plugin`
    `maven-publish`
    signing
}

group = "com.varabyte.kobweb.gradle"
version = "0.1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    // For processing the user's project
    implementation(libs.ksp)
    // For kobweb.conf.yaml
    implementation(libs.kaml)
}

val DESCRIPTION = "A Gradle plugin enabling a user to generate additional code on top of their Kobweb web app source"
gradlePlugin {
    plugins {
        create("kobwebPlugin") {
            id = "com.varabyte.kobweb"
            displayName = "Kobweb Plugin"
            description = DESCRIPTION
            implementationClass = "com.varabyte.kobweb.plugins.kobweb.KobwebPlugin"
        }
    }
}

publishing {
    addVarabyteArtifact(
        project,
        "kobweb",
        DESCRIPTION,
    )
}

if (shouldSign()) {
    signing {
        // Signing requires following steps at https://docs.gradle.org/current/userguide/signing_plugin.html#sec:signatory_credentials
        // and adding singatory properties somewhere reachable, e.g. ~/.gradle/gradle.properties
        sign(publishing.publications)
    }
}


inline val PluginDependenciesSpec.`kobweb-plugin`: PluginDependencySpec
    get() = id("com.varabyte.kobweb")