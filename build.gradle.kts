plugins {
    val kotlinVersion = "1.5.30"
    val composeVersion = "1.0.0-alpha4-build331"
    kotlin("multiplatform") version kotlinVersion apply false
    kotlin("jvm") version kotlinVersion apply false
    kotlin("plugin.serialization") version kotlinVersion apply false
    id("org.jetbrains.compose") version composeVersion apply false
}

subprojects {
    repositories {
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}