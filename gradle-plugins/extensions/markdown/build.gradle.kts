import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
    kotlin("jvm")
    id("com.varabyte.kobweb.internal.publish")
    `java-library`
    `java-gradle-plugin`
}

group = "com.varabyte.kobwebx.gradle"
version = libs.versions.kobweb.libs.get()

// kotlin-dsl uses 1.4 by default, but this spawns warnings at build time.
// See also: https://handstandsam.com/2022/04/13/using-the-kotlin-dsl-gradle-plugin-forces-kotlin-1-4-compatibility/
afterEvaluate {
    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions {
            apiVersion = "1.5"
            languageVersion = "1.5"
        }
    }
}

dependencies {
    implementation(kotlin("stdlib"))
    // Get access to Kotlin multiplatform source sets
    implementation(kotlin("gradle-plugin"))

    implementation(libs.bundles.commonmark)

    implementation(project(":common:kobweb-common"))

    // Compile only - the plugin itself should exist at runtime
    compileOnly(project(":gradle-plugins:application"))
}

val DESCRIPTION = "A Gradle plugin that adds markdown support to a Kobweb project"
gradlePlugin {
    plugins {
        create("kobwebxMarkdown") {
            id = "com.varabyte.kobwebx.markdown"
            displayName = "Kobwebx Markdown Plugin"
            description = DESCRIPTION
            implementationClass = "com.varabyte.kobwebx.gradle.markdown.KobwebxMarkdownPlugin"
        }
    }
}

kobwebPublication {
    // Leave artifactId blank. It will be set to the name of this module, and then the gradlePlugin step does some
    // additional tweaking that we don't want to interfere with.
    description.set(DESCRIPTION)
}