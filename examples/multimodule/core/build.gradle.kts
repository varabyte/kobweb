import com.varabyte.kobweb.gradle.library.util.configAsKobwebLibrary

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.jetbrains.compose)
    id(libs.plugins.kobweb.library.get().pluginId)
    id(libs.plugins.kobwebx.markdown.get().pluginId)
}

group = "multimodule.core"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

kotlin {
    // Even though this module doesn't actually define any server routes itself, 'includeServer = true' allows us to
    // depened on ":core" as a commonMain dependency from other modules, instead of a JS-only dependency.
    configAsKobwebLibrary(includeServer = true)

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
    }
}