plugins {
    `kotlin-dsl`
    id("kobweb-gradle-portal")
    id("com.varabyte.kobweb.internal.publish")
}

group = "com.varabyte.kobwebx.gradle"
version = libs.versions.kobweb.get()

dependencies {
    // Get access to Kotlin multiplatform source sets
    compileOnly(kotlin("gradle-plugin"))

    implementation(libs.bundles.commonmark)
    implementation(libs.jsoup)

    implementation(projects.common.kobwebCommon)

    // Compile only - the plugin itself should exist at runtime, provided by either the
    // Library or Application plugin.
    compileOnly(projects.tools.gradlePlugins.core)
}

gradlePlugin {
    plugins {
        create("kobwebxMarkdown") {
            id = "com.varabyte.kobwebx.markdown"
            displayName = "Kobwebx Markdown Plugin"
            description = "Adds markdown support to a Kobweb project."
            implementationClass = "com.varabyte.kobwebx.gradle.markdown.KobwebxMarkdownPlugin"

            kobwebPublication {
                artifactName.set(this@create.displayName)
                description.set(this@create.description)
            }
        }
    }
}
