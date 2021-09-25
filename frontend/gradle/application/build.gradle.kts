plugins {
    `kotlin-dsl`
    kotlin("jvm")
    id("com.varabyte.kobweb.internal.publish")
    kotlin("plugin.serialization")
    `java-gradle-plugin`
}

group = "com.varabyte.kobweb.gradle"
version = libs.versions.kobweb.get()

dependencies {
    implementation(kotlin("stdlib"))
    // Get access to Kotlin multiplatform source sets
    implementation(kotlin("gradle-plugin"))
    // For parsing code. Instead, use KSP someday? See also: Bug #4
    implementation(kotlin("compiler-embeddable"))
    // For kobweb.conf.yaml
    implementation(libs.kaml)
}

val DESCRIPTION = "A Gradle plugin that completes a user's Kobweb app"
gradlePlugin {
    plugins {
        create("kobwebApplication") {
            id = "com.varabyte.kobweb.application"
            displayName = "Kobweb Application Plugin"
            description = DESCRIPTION
            implementationClass = "com.varabyte.kobweb.gradle.application.KobwebApplicationPlugin"
        }
    }
}

kobwebPublication {
    // Leave artifactId blank. It will be set to the name of this module, and then the gradlePlugin step does some
    // additional tweaking that we don't want to interfere with.
    description.set(DESCRIPTION)
}