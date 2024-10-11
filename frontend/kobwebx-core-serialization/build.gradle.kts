import com.varabyte.kobweb.gradle.publish.FILTER_OUT_MULTIPLATFORM_PUBLICATIONS
import com.varabyte.kobweb.gradle.publish.set

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    id("com.varabyte.kobweb.internal.publish")
}

group = "com.varabyte.kobweb"
version = libs.versions.kobweb.libs.get()

kotlin {
    js {
        browser()
    }

    sourceSets {
        jsMain.dependencies {
            api(libs.kotlinx.coroutines)
            api(projects.frontend.kobwebCore)
            implementation(libs.kotlinx.serialization.json)
        }

        jsTest.dependencies {
            implementation(kotlin("test-js"))
            implementation(libs.truthish)
        }
    }
}

kobwebPublication {
    artifactId.set("kobwebx-core-serialization")
    description.set("Generally useful Kotlinx Serialization extensions for various Kobweb APIs.")
    filter.set(FILTER_OUT_MULTIPLATFORM_PUBLICATIONS)
}
