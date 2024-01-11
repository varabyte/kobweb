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
        binaries.executable()
    }
}

kobwebPublication {
    artifactId.set("kobweb-worker-interface")
    description.set("Common interface for worker implementations")
    filter.set(FILTER_OUT_MULTIPLATFORM_PUBLICATIONS)
}
