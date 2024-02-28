plugins {
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.jetbrains.compose) apply false
    id("com.varabyte.kobweb.application") apply false
    id("com.varabyte.kobweb.library") apply false
    id("com.varabyte.kobwebx.markdown") apply false
}

subprojects {
    repositories {
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        google()
    }
}
