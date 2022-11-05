import com.varabyte.kobweb.gradle.application.util.kobwebServerJar

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.jetbrains.compose)
    id(libs.plugins.kobweb.application.get().pluginId)
}

group = "multimodule.site"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

kotlin {
    js(IR) {
        browser()
        binaries.executable()
    }

    jvm {
        kobwebServerJar("multimodule.jar")
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(project(":core"))
                implementation(project(":auth"))
                implementation(project(":chat"))
            }
        }
        val jsMain by getting {
            dependencies {
                implementation(libs.kobweb.core)
                implementation(libs.kobweb.silk.core)
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(libs.kobweb.api)
            }
        }
    }
}