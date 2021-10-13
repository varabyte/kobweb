// Add compose gradle plugin
plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    id("com.varabyte.kobweb.internal.publish")
}

group = "com.varabyte.kobweb"
version = libs.versions.kobweb.get()

// Enable JS(IR) target and add dependencies
kotlin {
    jvm()
    js(IR) {
        browser()
        binaries.executable()
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
            }
        }
        val jsMain by getting {
            dependencies {
                implementation(compose.web.core)

                implementation(project(":frontend:web-compose-ext"))
            }
        }
    }
}

kobwebPublication {
    artifactId.set("kobweb")
    description.set("An opinionated framework making it easy to build web apps, leveraging Kotlin and Compose")
}