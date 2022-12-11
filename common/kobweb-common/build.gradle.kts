plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("com.varabyte.kobweb.internal.publish")
}

group = "com.varabyte.kobweb"
version = libs.versions.kobweb.libs.get()

dependencies {
    implementation(kotlin("stdlib"))
    implementation(libs.kaml)
    implementation(libs.kotlinx.coroutines)
}

kobwebPublication {
    artifactId.set("kobweb-common")
    description.set("A collection of utility classes for interacting with a Kobweb project needed by both frontend and backend codebases.")
}