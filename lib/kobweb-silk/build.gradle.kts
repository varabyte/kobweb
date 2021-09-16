import com.varabyte.kobweb.plugins.publish.addVarabyteArtifact
import com.varabyte.kobweb.plugins.publish.shouldSign

// Add compose gradle plugin
plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    `maven-publish`
    signing
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
                implementation(project(":lib:kobweb"))
            }
        }
    }
}

publishing {
    addVarabyteArtifact(
        project,
        "kobweb-silk",
        "A set of rich UI components built on top of Kobweb and inspired by the best parts of Compose and Chakra UI",
    )
}

if (shouldSign()) {
    signing {
        // Signing requires following steps at https://docs.gradle.org/current/userguide/signing_plugin.html#sec:signatory_credentials
        // and adding singatory properties somewhere reachable, e.g. ~/.gradle/gradle.properties
        sign(publishing.publications)
    }
}