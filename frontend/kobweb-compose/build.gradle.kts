plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    id("com.varabyte.kobweb.internal.publish")
}

group = "com.varabyte.kobweb"
version = libs.versions.kobweb.get()

kotlin {
    js(IR) {
        browser()
        binaries.executable()
    }
    sourceSets {
        val jsMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.web.core)

                api(project(":frontend:web-compose-ext"))
            }
        }
    }
}

kobwebPublication {
    artifactId.set("kobweb-compose")
    description.set("Additions to Web Compose that attempt to mimic Jetpack Compose as much as possible")
}