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

    // Common Gradle plugin used by Library, Application, and Worker plugins
    api(projects.tools.gradlePlugins.core)

    implementation(libs.kotlinx.serialization.json)
}

val DESCRIPTION = "A Gradle plugin that helps wrap vanilla web workers with type-safe APIs"
gradlePlugin {
    plugins {
        create("kobwebWorker") {
            id = "com.varabyte.kobweb.worker"
            displayName = "Kobweb Worker Plugin"
            description = DESCRIPTION
            implementationClass = "com.varabyte.kobweb.gradle.worker.KobwebWorkerPlugin"
        }
    }
}

kobwebPublication {
    // Leave artifactId blank. It will be set to the name of this module, and then the gradlePlugin step does some
    // additional tweaking that we don't want to interfere with.
    description.set(DESCRIPTION)
}

//tasks.register<Copy>("copyServerJar") {
//    dependsOn(":backend:server:shadowJar")
//
//    val serverJarName = "server-${libs.versions.kobweb.libs.get()}-all.jar"
//    val serverJarFile = projects.backend.server.dependencyProject.layout.buildDirectory.file("libs/$serverJarName")
//
//    from(serverJarFile)
//    into(file("$projectDir/build/resources/main"))
//    rename(serverJarName, "server.jar")
//}
//tasks.named("processResources") {
//    dependsOn("copyServerJar")
//}
