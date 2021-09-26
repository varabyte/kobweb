plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    application
}

group = "com.varabyte.kobweb.server"
version = libs.versions.kobweb.get()

dependencies {
    implementation(kotlin("stdlib"))
    implementation(libs.bundles.ktor)
    implementation(project(":backend:api"))

    testImplementation(libs.truthish)
    testImplementation(libs.ktor.server.tests)
    testImplementation(kotlin("test"))
}

tasks.jar {
    archiveFileName.set("kobweb-server.jar")
}

application {
    mainClass.set("com.varabyte.kobweb.server.ApplicationKt")
}