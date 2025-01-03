import java.util.*

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
    // Get access to the Compose compiler gradle plugin extension
    compileOnly(libs.compose.compiler.plugin)

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.ksp.plugin)
    api(libs.kotlinx.html) // Exposed in app & library index.html configuration

    implementation(projects.common.kobwebCommon)
    api(projects.tools.processorCommon)
}

gradlePlugin {
    plugins {
        create("kobwebCore") {
            id = "com.varabyte.kobweb.core"
            displayName = "Kobweb Core Plugin"
            description = "Provides common support for Kobweb library and application plugins."
            implementationClass = "com.varabyte.kobweb.gradle.core.KobwebCorePlugin"

            kobwebPublication {
                artifactName.set(this@create.displayName)
                description.set(this@create.description)
            }
        }
    }
}

// Make the version available to the plugin code, so that it can be used to determine the version of the ksp processor
// dependency to add to the project
val generateVersionProperties by tasks.registering {
    val projectVersion = version.toString() // store outside of task action for configuration cache compatibility
    val generatedVersionDir = layout.buildDirectory.dir("generated-version")
    inputs.property("projectVersion", projectVersion)
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
