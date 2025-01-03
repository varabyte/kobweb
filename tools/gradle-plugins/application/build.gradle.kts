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

    implementation(libs.kotlinx.serialization.json)

    // Common Gradle plugin used by Library, Application, and Worker plugins
    api(projects.tools.gradlePlugins.core)

    // For generating code / html
    implementation(libs.kotlinpoet)

    // Export
    implementation(libs.playwright)
    implementation(libs.jsoup)

    implementation(projects.common.kobwebCommon)
}

gradlePlugin {
    plugins {
        create("kobwebApplication") {
            id = "com.varabyte.kobweb.application"
            displayName = "Kobweb Application Plugin"
            description = "Generates boilerplate for a Kobweb application."
            implementationClass = "com.varabyte.kobweb.gradle.application.KobwebApplicationPlugin"

            kobwebPublication {
                artifactName.set(this@create.displayName)
                description.set(this@create.description)
            }
        }
    }
}

val serverJar by configurations.registering {
    isCanBeConsumed = false
    isTransitive = false
}
dependencies {
    @Suppress("UnstableApiUsage")
    serverJar(project(projects.backend.server.dependencyProject.path, configuration = "shadow"))
}

/**
 * Embed a copy of the latest Kobweb server, naming it server.jar and putting it into the project's resources/ dir, so
 * we can run it from the plugin at runtime.
 */
val copyServerJar by tasks.registering(Sync::class) {
    from(serverJar)
    into(layout.buildDirectory.dir("generated/kobweb/server"))
    rename("server-${libs.versions.kobweb.libs.get()}-all.jar", "server.jar")
}

kotlin.sourceSets.main {
    resources.srcDir(copyServerJar)
}
