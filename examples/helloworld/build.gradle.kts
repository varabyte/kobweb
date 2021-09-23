buildscript {
    repositories {
        mavenLocal()
        maven("https://us-central1-maven.pkg.dev/varabyte-repos/public")
    }
}

// Add compose gradle plugin
plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    id("com.varabyte.kobweb.application")
}

group = "helloworld"
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

                implementation(project(":lib:web-compose-ext"))
                implementation(project(":lib:kobweb"))
                implementation(project(":lib:kobweb-silk"))
                implementation(project(":lib:kobweb-silk-icons-fa"))
            }
        }
    }
}