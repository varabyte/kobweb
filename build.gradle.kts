import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

// Plugins declared here instead of settings.gradle.kts because otherwise I get an error saying the kotlin plugin was
// applied multiple times.
plugins {
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlinx.serialization) apply false
    alias(libs.plugins.jetbrains.compose) apply false
}

subprojects {
    repositories {
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://us-central1-maven.pkg.dev/varabyte-repos/public")
    }

    val versionStr = JavaVersion.VERSION_11.toString()
    tasks.withType<JavaCompile> {
        sourceCompatibility = versionStr
        targetCompatibility = versionStr
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = versionStr
        }
    }
}