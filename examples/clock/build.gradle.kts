plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.jetbrains.compose)
    id(libs.plugins.kobweb.application.get().pluginId)
}

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

group = "clock"
version = "1.0-SNAPSHOT"

kotlin {
    // TODO(#52): We should be able to remove this jvm block without breaking kobweb run
    jvm {
        tasks.named("jvmJar", Jar::class.java).configure {
            archiveFileName.set("clock.jar")
        }
    }
    js(IR) {
        moduleName = "clock"
        browser {
            commonWebpackConfig {
                outputFileName = "clock.js"
            }
        }
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
                implementation(libs.kobweb.core)
                implementation(libs.kobweb.silk.core)
                implementation(libs.kobweb.silk.icons.fa)
             }
        }

        // TODO(#52): We should be able to remove this jvm block without breaking kobweb run
        val jvmMain by getting {
            dependencies {
                implementation(libs.kobweb.api)
             }
        }
    }
}