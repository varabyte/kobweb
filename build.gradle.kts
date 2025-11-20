import org.gradle.api.plugins.JavaPlugin.API_ELEMENTS_CONFIGURATION_NAME
import org.jetbrains.kotlin.assignment.plugin.gradle.AssignmentExtension
import org.jetbrains.kotlin.assignment.plugin.gradle.AssignmentSubplugin
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.samWithReceiver.gradle.SamWithReceiverExtension
import org.jetbrains.kotlin.samWithReceiver.gradle.SamWithReceiverGradleSubplugin

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

subprojects {
    repositories {
        mavenCentral()
        google()
    }

    // Require Java 11 for a few APIs. A very important one is ProcessHandle, used for detecting if a
    // server is running in a cross-platform way.
    val jvmTarget = JvmTarget.JVM_11
    tasks.withType<JavaCompile>().configureEach {
        sourceCompatibility = jvmTarget.target
        targetCompatibility = jvmTarget.target
    }

    tasks.withType<KotlinCompile>().configureEach {
        compilerOptions.jvmTarget.set(jvmTarget)
    }

    // Set jdk-release for all compilation targets. See also: https://jakewharton.com/kotlins-jdk-release-compatibility-flag/
    // (Short version: resolves ambiguity Kotlin extension methods and new methods added into more recent JDKs)

    pluginManager.withPlugin("org.jetbrains.kotlin.multiplatform") {
        configure<KotlinMultiplatformExtension> {
            targets.withType<KotlinJvmTarget>().configureEach {
                compilerOptions.freeCompilerArgs.add("-Xjdk-release=${jvmTarget.target}")
            }
        }
    }

    pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
        configure<KotlinJvmExtension> {
            compilerOptions.freeCompilerArgs.add("-Xjdk-release=${jvmTarget.target}")
        }
    }

    // Configure the Kobweb Gradle plugins explicitly instead of using `kotlin-dsl` to avoid version conflicts
    // See also: https://mbonnin.net/2025-07-10_the_case_against_kotlin_dsl/
    pluginManager.withPlugin("java-gradle-plugin") {
        pluginManager.apply(SamWithReceiverGradleSubplugin::class)
        configure<SamWithReceiverExtension> {
            annotation(HasImplicitReceiver::class.qualifiedName!!)
        }
        pluginManager.apply(AssignmentSubplugin::class)
        configure<AssignmentExtension> {
            annotation(SupportsKotlinAssignmentOverloading::class.qualifiedName!!)
        }
        configure<KotlinJvmExtension> {
            compilerOptions {
                // Pin to the oldest supported language level to ensure compatibility with older Gradle versions.
                // https://docs.gradle.org/current/userguide/compatibility.html#kotlin
                val target = @Suppress("DEPRECATION") KotlinVersion.KOTLIN_1_9
                languageVersion = target
                apiVersion = target
                // Suppress warning about deprecated language version
                freeCompilerArgs.add("-Xsuppress-version-warnings")
            }
        }
        // Set minimum supported Gradle version
        configurations.named(API_ELEMENTS_CONFIGURATION_NAME) {
            attributes.attribute(GradlePluginApiVersion.GRADLE_PLUGIN_API_VERSION_ATTRIBUTE, objects.named("8.3"))
        }
    }
}
