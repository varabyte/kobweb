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
                implementation(compose.runtime)
                implementation(compose.web.core)
            }
        }
    }
}

publishing {
    addVarabyteArtifact(
        project,
        "web-compose-ext",
        "Generally useful Compose extensions that could potentially move upstream someday; until then, needed now for Kobweb",
    )
}

if (shouldSign()) {
    signing {
        // Signing requires following steps at https://docs.gradle.org/current/userguide/signing_plugin.html#sec:signatory_credentials
        // and adding singatory properties somewhere reachable, e.g. ~/.gradle/gradle.properties
        sign(publishing.publications)
    }
}