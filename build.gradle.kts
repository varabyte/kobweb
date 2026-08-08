// Plugins declared here instead of settings.gradle.kts because otherwise I get an error saying the kotlin plugin was
// applied multiple times.
plugins {
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.sam.receiver) apply false
    alias(libs.plugins.kotlin.assignment) apply false
    alias(libs.plugins.kotlinx.serialization) apply false
    alias(libs.plugins.vanniktech.publish) apply false
}
