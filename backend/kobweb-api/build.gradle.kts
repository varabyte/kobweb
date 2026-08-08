plugins {
    id("kobweb-compilation-settings")
    alias(libs.plugins.kotlin.jvm)
    id("com.varabyte.kobweb.internal.publish")
}

group = "com.varabyte.kobweb"
version = libs.versions.kobweb.get()

dependencies {
    api(projects.common.frameworkAnnotations) // api or else opt-in message won't be shown
    api(projects.backend.io)
    implementation(projects.common.clientServerInternal)
    testImplementation(kotlin("test"))
    testImplementation(libs.truthish)
}

kobwebPublication {
    artifactName.set("Kobweb API")
    artifactId.set("kobweb-api")
    description.set("Core classes and annotations in the Kobweb framework for defining API routes and API streams on the backend.")
}
