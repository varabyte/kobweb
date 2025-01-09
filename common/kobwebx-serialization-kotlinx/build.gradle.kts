plugins {
    alias(libs.plugins.kotlin.multiplatform)
    id("com.varabyte.kobweb.internal.publish")
}

group = "com.varabyte.kobwebx"
version = libs.versions.kobweb.get()

kotlin {
    jvm()
    js {
        browser()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.common.kobwebSerialization)
            implementation(libs.kotlinx.serialization.json)
        }

        jsMain.dependencies {
            implementation(libs.kotlinx.coroutines)
            implementation(projects.frontend.kobwebCore)
        }

        jsTest.dependencies {
            implementation(kotlin("test-js"))
            implementation(libs.truthish)
        }
    }
}

kobwebPublication {
    artifactName.set("Kobweb Serialization Utilities for Kotlinx")
    artifactId.set("kobwebx-serialization-kotlinx")
    description.set("Generally useful Kotlinx Serialization extensions for various Kobweb APIs.")
}
