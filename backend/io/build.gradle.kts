plugins {
    id("kobweb-compilation-settings")
    alias(libs.plugins.kotlin.jvm)
    id("com.varabyte.kobweb.internal.publish")
}

group = "com.varabyte.kobweb"
version = libs.versions.kobweb.get()

dependencies {
    implementation(libs.kotlinx.coroutines.core)
    testImplementation(kotlin("test"))
    testImplementation(libs.truthish)
    testImplementation(libs.kotlinx.coroutines.test)
}

kobwebPublication {
    artifactName.set("Kobweb I/O")
    artifactId.set("kobweb-io")
    description.set("Custom I/O support used by the Kobweb project")
}
