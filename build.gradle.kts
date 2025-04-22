import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

// Plugins declared here instead of settings.gradle.kts because otherwise I get an error saying the kotlin plugin was
// applied multiple times.
plugins {
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    `kotlin-dsl` apply false
    alias(libs.plugins.kotlinx.serialization) apply false
}

subprojects {
    repositories {
        mavenCentral()
        google()
        maven("https://s01.oss.sonatype.org/content/repositories/snapshots/") {
            content {
                includeGroup("com.varabyte.truthish")
            }
        }
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

    plugins.withId("org.jetbrains.kotlin.multiplatform") {
        extensions.findByType(KotlinMultiplatformExtension::class.java)?.let { kotlin ->
            kotlin.targets.withType<KotlinJvmTarget> {
                compilerOptions.freeCompilerArgs.add("-Xjdk-release=${jvmTarget.target}")
            }
        }
    }

    plugins.withId("org.jetbrains.kotlin.jvm") {
        project.tasks.withType(KotlinCompile::class.java).configureEach {
            compilerOptions.freeCompilerArgs.add("-Xjdk-release=${jvmTarget.target}")
        }
    }
}
