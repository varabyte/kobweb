plugins {
    id("kobweb-compilation-settings")
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlinx.serialization)
    id("com.varabyte.kobweb.internal.publish")
}

group = "com.varabyte.kobweb"
version = libs.versions.kobweb.get()

dependencies {
    implementation(libs.kotlinx.serialization.json)

    testImplementation(kotlin("test"))
    testImplementation(libs.truthish)
}

kobwebPublication {
    artifactName.set("Kobweb Icons Codegen")
    artifactId.set("kobweb-icons-codegen")
    description.set("Downloads icon set data and generates Kotlin bindings for it. Used by the Kobweb Gradle plugin.")
}
