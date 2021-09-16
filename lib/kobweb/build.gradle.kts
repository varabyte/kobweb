// Add compose gradle plugin
plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    id("com.varabyte.kobweb.publish")
}

group = "com.varabyte.kobweb"
version = "0.3.0-SNAPSHOT"

// Enable JS(IR) target and add dependencies
kotlin {
    js(IR) {
        browser()
        binaries.executable()
    }
    sourceSets {
        val jsMain by getting {
            dependencies {
                implementation(compose.web.core)
                implementation(compose.runtime)

                implementation(project(":lib:web-compose-ext"))
            }
        }
    }
}

kobwebPublication {
    artifactId.set("kobweb")
    description.set("An opinionated framework making it easy to build web apps, leveraging Kotlin and Compose")
}