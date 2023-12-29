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
    implementation(projects.tools.processorCommon)
}

kobwebPublication {
    artifactId.set("kobweb-ksp-ext")
    description.set("Useful KSP utility methods that are shared across projects.")
}
