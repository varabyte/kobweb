@Suppress("DSL_SCOPE_VIOLATION") // https://youtrack.jetbrains.com/issue/KTIJ-19369
plugins {
    `kotlin-dsl`
    alias(libs.plugins.kotlin.jvm)
    id("com.varabyte.kobweb.internal.publish")
    `java-library`
    `java-gradle-plugin`
    alias(libs.plugins.kotlinx.serialization)
}

group = "com.varabyte.kobweb.gradle"
version = libs.versions.kobweb.libs.get()

dependencies {
    implementation(kotlin("stdlib"))
    // Get access to Kotlin multiplatform source sets
    implementation(kotlin("gradle-plugin"))

    // Common Gradle plugin used by Library and Application plugins
    api(project(":gradle-plugins:core"))

    // For generating code
    implementation(libs.kotlinpoet)

    // For creating a metadata file
    implementation(libs.kotlinx.serialization.json)

    implementation(project(":common:kobweb-common"))
}

val DESCRIPTION = "A Gradle plugin that generates useful code for a user's Kobweb library"
gradlePlugin {
    plugins {
        create("kobwebLibrary") {
            id = "com.varabyte.kobweb.library"
            displayName = "Kobweb Library Plugin"
            description = DESCRIPTION
            implementationClass = "com.varabyte.kobweb.gradle.library.KobwebLibraryPlugin"
        }
    }
}

kobwebPublication {
    // Leave artifactId blank. It will be set to the name of this module, and then the gradlePlugin step does some
    // additional tweaking that we don't want to interfere with.
    description.set(DESCRIPTION)
}
