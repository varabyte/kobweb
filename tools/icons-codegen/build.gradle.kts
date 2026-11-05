plugins {
    id("kobweb-compilation-settings")
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlinx.serialization)
}

group = "com.varabyte.kobweb"
version = libs.versions.kobweb.get()

dependencies {
    implementation(libs.kotlinx.serialization.json)

    testImplementation(kotlin("test"))
    testImplementation(libs.truthish)
}
