import com.varabyte.kobweb.gradle.publish.FILTER_OUT_MULTIPLATFORM_PUBLICATIONS
import com.varabyte.kobweb.gradle.publish.set

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.jetbrains.compose)
    id("com.varabyte.kobweb.internal.publish")
}

group = "com.varabyte.kobweb"
version = libs.versions.kobweb.libs.get()

kotlin {
    js {
        browser()
        binaries.executable()
    }

    sourceSets {
        val jsMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.html.core)
                implementation(libs.kotlinx.serialization.json)
                api(projects.frontend.composeHtmlExt)
                implementation(projects.common.clientServerModels)
            }
        }
    }
}

kobwebPublication {
    artifactId.set("kobweb-core")
    description.set("An opinionated framework making it easy to build web apps, leveraging Kotlin and Compose")
    filter.set(FILTER_OUT_MULTIPLATFORM_PUBLICATIONS)
}
