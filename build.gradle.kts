// Plugins declared here instead of settings.gradle.kts because otherwise I get an error saying the kotlin plugin was
// applied multiple times.
plugins {
    val kotlinVersion = "1.5.30"
    val composeVersion = "1.0.0-alpha4-build331"
    val kspVersion = "1.5.30-1.0.0"
    kotlin("multiplatform") version kotlinVersion apply false
    kotlin("jvm") version kotlinVersion apply false
    kotlin("plugin.serialization") version kotlinVersion apply false
    id("org.jetbrains.compose") version composeVersion apply false
    id("com.google.devtools.ksp") version kspVersion apply false
    id("com.varabyte.kobweb") version "0.1.0-SNAPSHOT" apply false
}

subprojects {
    repositories {
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}