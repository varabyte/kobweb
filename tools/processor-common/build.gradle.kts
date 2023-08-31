import com.varabyte.kobweb.gradle.publish.set

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlinx.serialization)
    id("com.varabyte.kobweb.internal.publish")
}

group = "com.varabyte.kobweb"
version = libs.versions.kobweb.libs.get()

dependencies {
    implementation(libs.kotlinx.serialization.json)
}

kobwebPublication {
    artifactId.set("kobweb-processor-common")
    description.set("Common code shared between KSP and Gradle code responsible for processing a Kobweb project")
}
