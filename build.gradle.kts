plugins {
    val kotlinVersion = "1.5.10"
    val composeVersion = "0.5.0-build262"
    kotlin("multiplatform") version kotlinVersion apply false
    kotlin("jvm") version kotlinVersion apply false
    kotlin("plugin.serialization") version kotlinVersion apply false
    id("org.jetbrains.compose") version composeVersion apply false
}
