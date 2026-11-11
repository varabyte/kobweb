plugins {
    id("kobweb-compilation-settings")
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlinx.serialization)
}

group = "com.varabyte.kobweb"
version = libs.versions.kobweb.framework.get()

dependencies {
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kobweb.icons.parsers)
    implementation(projects.tools.icons.metadata)

    testImplementation(kotlin("test"))
    testImplementation(libs.truthish)
}
