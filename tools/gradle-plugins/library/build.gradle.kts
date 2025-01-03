plugins {
    `kotlin-dsl`
    id("kobweb-gradle-portal")
    id("com.varabyte.kobweb.internal.publish")
    alias(libs.plugins.kotlinx.serialization)
}

group = "com.varabyte.kobweb.gradle"
version = libs.versions.kobweb.libs.get()

dependencies {
    // Get access to Kotlin multiplatform source sets
    compileOnly(kotlin("gradle-plugin"))

    // Common Gradle plugin used by Library, Application, and Worker plugins
    api(projects.tools.gradlePlugins.core)

    // For generating code / html
    implementation(libs.kotlinpoet)
    api(libs.kotlinx.html) // Exposed as api dependency because it's exposed by the kobweb.library.index API anyway.

    // For creating a metadata file
    implementation(libs.kotlinx.serialization.json)

    implementation(projects.common.kobwebCommon)
}

gradlePlugin {
    plugins {
        create("kobwebLibrary") {
            id = "com.varabyte.kobweb.library"
            displayName = "Kobweb Library Plugin"
            description = "Generates metadata for a Kobweb library that can then be consumed by a Kobweb application."
            implementationClass = "com.varabyte.kobweb.gradle.library.KobwebLibraryPlugin"

            kobwebPublication {
                artifactName.set(this@create.displayName)
                description.set(this@create.description)
            }
        }
    }
}
