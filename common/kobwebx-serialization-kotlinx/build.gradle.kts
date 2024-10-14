import com.varabyte.kobweb.gradle.publish.setForMultiplatform

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    id("com.varabyte.kobweb.internal.publish")
}

group = "com.varabyte.kobwebx"
version = libs.versions.kobweb.libs.get()

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
    artifactId.setForMultiplatform("kobwebx-serialization-kotlinx")
    description.set("Generally useful Kotlinx Serialization extensions for various Kobweb APIs.")
}
