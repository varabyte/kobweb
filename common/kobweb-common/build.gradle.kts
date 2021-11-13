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
    artifactId.set("kobweb-common")
    description.set("A collection of utility classes for interacting with a Kobweb project needed by both frontend and backend codebases.")
}