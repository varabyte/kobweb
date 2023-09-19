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

                api(project(":frontend:kobweb-core"))
                api(project(":frontend:silk-foundation"))
                api(project(":frontend:silk-widgets"))
                api(project(":frontend:silk-widgets-kobweb"))
            }
        }
    }
}

kobwebPublication {
    artifactId.set("kobweb-silk")
    description.set("An artifact that includes all relevant Silk dependencies and glues them together.")
    filter.set(FILTER_OUT_MULTIPLATFORM_PUBLICATIONS)
}
