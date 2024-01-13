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
    }
}

kobwebPublication {
    artifactId.setForMultiplatform("kobwebx-serialization-kotlinx")
    description.set("Support code for using Kobweb workers in projects using Kotlinx Serialization")
}
