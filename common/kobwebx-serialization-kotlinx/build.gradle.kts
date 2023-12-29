import com.varabyte.kobweb.gradle.publish.FILTER_OUT_MULTIPLATFORM_PUBLICATIONS
import com.varabyte.kobweb.gradle.publish.set

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    id("com.varabyte.kobweb.internal.publish")
}

group = "com.varabyte.kobwebx"
version = libs.versions.kobweb.libs.get()

kotlin {
    js {
        browser()
        binaries.executable()
    }

    sourceSets {
        jsMain.dependencies {
            compileOnly(project(":frontend:kobweb-worker"))
            implementation(libs.kotlinx.serialization.json)
        }
    }
}

kobwebPublication {
    artifactId.set("kobwebx-serialization-kotlinx")
    description.set("Support code for using Kobweb workers in projects using Kotlinx Serialization")
    filter.set(FILTER_OUT_MULTIPLATFORM_PUBLICATIONS)
}
