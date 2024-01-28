import java.util.*

plugins {
    `kotlin-dsl`
    id("com.varabyte.kobweb.internal.publish")
    alias(libs.plugins.kotlinx.serialization)
}

group = "com.varabyte.kobweb.gradle"
version = libs.versions.kobweb.libs.get()

dependencies {
    // Get access to Kotlin multiplatform source sets
    implementation(kotlin("gradle-plugin"))

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.ksp.plugin)

    implementation(projects.common.kobwebCommon)
    api(projects.tools.processorCommon)
}

val DESCRIPTION = "A Gradle plugin that provides common support for the Library and Application plugins."
gradlePlugin {
    plugins {
        create("kobwebLibrary") {
            id = "com.varabyte.kobweb.core"
            displayName = "Kobweb Core Plugin"
            description = DESCRIPTION
            implementationClass = "com.varabyte.kobweb.gradle.core.KobwebCorePlugin"
        }
    }
}

kobwebPublication {
    // Leave artifactId blank. It will be set to the name of this module, and then the gradlePlugin step does some
    // additional tweaking that we don't want to interfere with.
    description.set(DESCRIPTION)
}

// Make the version available to the plugin code, so that it can be used to determine the version of the ksp processor
// dependency to add to the project
val generateVersionProperties by tasks.registering {
    val projectVersion = version.toString() // store outside of task action for configuration cache compatibility
    val generatedVersionDir = layout.buildDirectory.dir("generated-version")
    outputs.dir(generatedVersionDir)
    doLast {
        val properties = Properties()
        properties["version"] = projectVersion
        val propertiesFile = generatedVersionDir.get().file("version.properties").asFile
        propertiesFile.writer().use { properties.store(it, null) }
    }
}

sourceSets.main {
    resources.srcDir(generateVersionProperties)
}
