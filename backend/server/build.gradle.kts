import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    application
    alias(libs.plugins.shadow)
}

group = "com.varabyte.kobweb.server"
version = libs.versions.kobweb.get()

dependencies {
    implementation(kotlin("stdlib"))
    implementation(libs.bundles.ktor)
    implementation(libs.kaml)

    implementation(project(":backend:kobweb-api"))
    implementation(project(":backend:server-api"))
    implementation(project(":common:kobweb-project"))

    testImplementation(libs.truthish)
    testImplementation(libs.ktor.server.tests)
    testImplementation(kotlin("test"))
}

tasks.jar {
    archiveFileName.set("kobweb-server.jar")
}

val applicationClass = "com.varabyte.kobweb.server.ApplicationKt"
project.setProperty("mainClassName", applicationClass)
application {
    mainClass.set(applicationClass)
}

tasks.withType<ShadowJar> {
    minimize()
}
