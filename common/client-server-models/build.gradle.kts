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
        val commonMain by getting {
            dependencies {
                implementation(libs.kotlinx.serialization.core)
            }
        }
    }
}

kobwebPublication {
    artifactId.setForMultiplatform("kobweb-client-server-models")
    description.set("Model classes that get shared between a Kobweb server (JVM) and client (JS) via serialization")
}
