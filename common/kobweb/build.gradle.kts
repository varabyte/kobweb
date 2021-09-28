plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("com.varabyte.kobweb.internal.publish")
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

kobwebPublication {
    description.set("A collection of common Kobweb-related utility classes used by multiple modules")
}