plugins {
    `kotlin-dsl`
    id("com.varabyte.kobweb.internal.publish")
}

group = "com.varabyte.kobwebx.gradle"
version = libs.versions.kobweb.libs.get()

dependencies {
    // Get access to Kotlin multiplatform source sets
    implementation(kotlin("gradle-plugin"))

    implementation(libs.bundles.commonmark)
    implementation(libs.jsoup)

    implementation(project(":common:kobweb-common"))

    // Compile only - the plugin itself should exist at runtime, provided by either the
    // Library or Application plugin.
    compileOnly(project(":tools:gradle-plugins:core"))
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
