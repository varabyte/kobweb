import com.varabyte.kobweb.gradle.publish.set

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlinx.serialization)
    id("com.varabyte.kobweb.internal.publish")
}

group = "com.varabyte.kobweb"
version = libs.versions.kobweb.libs.get()

kotlin {
    jvm()
    js {
        browser()
        binaries.executable()
    }
}

kobwebPublication {
    artifactId.set("kobweb-serialization")
    description.set("A collection of utility classes related to serialization.")
}
