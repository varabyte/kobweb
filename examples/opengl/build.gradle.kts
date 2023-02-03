import com.varabyte.kobweb.gradle.application.util.configAsKobwebApplication
import kotlinx.html.script

@Suppress("DSL_SCOPE_VIOLATION")
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

group = "opengl"
version = "1.0-SNAPSHOT"

kobweb.app.index.head.add {
    script {
        src = "https://cdnjs.cloudflare.com/ajax/libs/gl-matrix/3.4.2/gl-matrix-min.js"
    }
}

kotlin {
    configAsKobwebApplication()

    sourceSets {
        val jsMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(libs.kobweb.core)
                implementation(libs.kobweb.silk.core)
                implementation(libs.kobweb.silk.icons.fa)
            }
        }
    }
}