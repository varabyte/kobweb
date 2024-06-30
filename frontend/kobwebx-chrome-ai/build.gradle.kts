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
    }

    sourceSets {
        jsMain.dependencies {
            api(libs.kotlin.web)
        }
    }
}

kobwebPublication {
    artifactId.set("kobwebx-chrome-ai")
    description.set("Classes useful for projects in need of chrome ai support")
    filter.set(FILTER_OUT_MULTIPLATFORM_PUBLICATIONS)
}