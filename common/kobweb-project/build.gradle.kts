plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("com.varabyte.kobweb.internal.publish")
}

group = "com.varabyte.kobweb"
version = libs.versions.kobweb.get()

dependencies {
    implementation(kotlin("stdlib"))
    implementation(libs.kaml)
}

kobwebPublication {
    artifactId.set("kobweb-project")
    description.set("A collection of utility classes for identifying and working with files in a Kobweb project.")
}