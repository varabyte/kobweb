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
        jsMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.html.core)
            api(libs.kotlinx.coroutines)
        }

        jsTest.dependencies {
            implementation(kotlin("test-js"))
            implementation(libs.truthish)
        }
    }
}

kobwebPublication {
    artifactId.set("compose-html-ext")
    description.set("Generally useful Compose extensions that could potentially move upstream someday; until then, needed now for Kobweb")
    filter.set(FILTER_OUT_MULTIPLATFORM_PUBLICATIONS)
}
