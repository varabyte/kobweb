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
    implementation(project(":gradle-plugins:core"))
}

val DESCRIPTION = "KSP processor for Kobweb" // TODO

kobwebPublication {
    artifactId.set("kobweb-ksp-processor") // TODO
    description.set(DESCRIPTION)
}
