// Add compose gradle plugin
plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

group = "com.varabyte"
version = "1.0-SNAPSHOT"

// Enable JS(IR) target and add dependencies
kotlin {
    js(IR) {
        browser()
        binaries.executable()
    }
    sourceSets {
        val jsMain by getting {
            dependencies {
                implementation(compose.runtime)

                implementation(project(":lib:core"))
                implementation(project(":lib:ui"))
                implementation(project(":lib:ui-icons-fa"))
            }
        }
    }
}