plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

group = "com.varabyte.kobweb.server"
version = libs.versions.kobweb.get()

dependencies {
    implementation(kotlin("stdlib"))
    implementation(libs.kaml)
    implementation(project(":common:kobweb-project"))
}

// Avoid ambiguity / add clarity in generated artifacts
tasks.jar {
    archiveFileName.set("kobweb-server-api.jar")
}