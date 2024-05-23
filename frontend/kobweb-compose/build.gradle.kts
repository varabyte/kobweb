import com.varabyte.kobweb.gradle.publish.FILTER_OUT_MULTIPLATFORM_PUBLICATIONS
import com.varabyte.kobweb.gradle.publish.set

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.compiler)
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
            implementation(libs.compose.runtime)
            implementation(libs.compose.html.core)

            api(projects.frontend.composeHtmlExt)
        }

        jsTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.truthish)
        }
    }
}

kobwebPublication {
    artifactId.set("kobweb-compose")
    description.set("Additions to Web Compose that attempt to mimic Jetpack Compose as much as possible")
    filter.set(FILTER_OUT_MULTIPLATFORM_PUBLICATIONS)
}
