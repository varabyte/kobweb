// Add compose gradle plugin
plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    id("com.varabyte.kobweb.internal.publish")
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

                implementation(project(":frontend:web-compose-ext"))
                implementation(project(":frontend:kobweb"))
            }
        }
    }
}

kobwebPublication {
    artifactId.set("kobweb-silk")
    description.set("A set of rich UI components built on top of Kobweb and inspired by the best parts of Compose and Chakra UI")
}
