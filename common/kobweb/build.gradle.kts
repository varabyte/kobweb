plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

group = "com.varabyte.kobweb.common"
version = libs.versions.kobweb.get()

dependencies {
    implementation(kotlin("stdlib"))
    implementation(libs.kaml)
}

// Avoid ambiguity / add clarity in generated artifacts
tasks.jar {
    archiveFileName.set("kobweb-common.jar")
}