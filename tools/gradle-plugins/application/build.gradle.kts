plugins {
    alias(libs.plugins.kotlin.jvm)
    id("kobweb-gradle-portal")
    id("com.varabyte.kobweb.internal.publish")
    alias(libs.plugins.kotlinx.serialization)
}

group = "com.varabyte.kobweb.gradle"
version = libs.versions.kobweb.get()

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

val serverJar = configurations.register("serverJar") {
    isCanBeConsumed = false
    isTransitive = false
}
dependencies {
    serverJar(project(projects.backend.server.path, configuration = "shadow"))
}

/**
 * Embed a copy of the latest Kobweb server, naming it server.jar and putting it into the project's resources/ dir, so
 * we can run it from the plugin at runtime.
 */
val copyServerJar = tasks.register<Sync>("copyServerJar") {
    description = "Copy the built Kobweb server into a final location where it can be run from."
    from(serverJar)
    into(layout.buildDirectory.dir("generated/kobweb/server"))
    rename("server-${libs.versions.kobweb.get()}-all.jar", "server.jar")
}

kotlin.sourceSets.main {
    resources.srcDir(copyServerJar)
}
