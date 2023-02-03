import com.varabyte.kobweb.gradle.library.util.configAsKobwebLibrary

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.kotlin.serialization)
    id(libs.plugins.kobweb.library.get().pluginId)
}

group = "multimodule.chat"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

kotlin {
    configAsKobwebLibrary(includeServer = true)

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(libs.kotlinx.serialization.json)
                implementation(project(":core"))
                implementation(project(":auth"))
            }
        }
        val jsMain by getting {
            dependencies {
                implementation(libs.kobweb.core)
                implementation(libs.kobweb.silk.core)
                implementation(libs.kobweb.silk.icons.fa)
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(libs.kobweb.api)
            }
        }
    }
}