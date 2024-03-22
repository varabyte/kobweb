import com.varabyte.kobweb.gradle.publish.setForMultiplatform

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
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.serialization.core)
        }
        commonTest.dependencies {
            implementation(libs.truthish)
            implementation(kotlin("test"))
        }
    }
}

kobwebPublication {
    artifactId.setForMultiplatform("kobweb-client-server-internal")
    description.set("Miscellaneous multiplatform common classes and utilities shared between Kobweb core and server codebases")
}
