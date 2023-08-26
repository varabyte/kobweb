import com.varabyte.kobweb.gradle.publish.set

plugins {
    alias(libs.plugins.kotlin.jvm)
    id("com.varabyte.kobweb.internal.publish")
}

group = "com.varabyte.kobweb"
version = libs.versions.kobweb.libs.get()

dependencies {
    implementation(libs.ksp.processing)
    implementation(libs.kotlinx.serialization.json)
    implementation(project(":tools:gradle-plugins:core"))
}

// TODO: strings + filter?
kobwebPublication {
    artifactId.set("kobweb-project-processors")
    description.set("KSP processors for Kobweb library and site modules")
}
