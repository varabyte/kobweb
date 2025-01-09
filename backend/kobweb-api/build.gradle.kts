plugins {
    alias(libs.plugins.kotlin.jvm)
    id("com.varabyte.kobweb.internal.publish")
}

group = "com.varabyte.kobweb"
version = libs.versions.kobweb.get()

dependencies {
    implementation(projects.common.clientServerInternal)
}

kobwebPublication {
    artifactName.set("Kobweb API")
    artifactId.set("kobweb-api")
    description.set("Core classes and annotations in the Kobweb framework for defining API routes and API streams on the backend.")
}
