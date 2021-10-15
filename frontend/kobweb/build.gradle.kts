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

                implementation(project(":frontend:web-compose-ext"))
            }
        }
    }
}

kobwebPublication {
    artifactId.set("kobweb")
    description.set("An opinionated framework making it easy to build web apps, leveraging Kotlin and Compose")
}