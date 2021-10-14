// Add compose gradle plugin
plugins {
    kotlin("jvm")
    id("com.varabyte.kobweb.internal.publish")
}

group = "com.varabyte.kobweb"
version = libs.versions.kobweb.get()

// Enable JS(IR) target and add dependencies
java {
    withJavadocJar()
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

kobwebPublication {
    artifactId.set("kobweb-api")
    description.set("Classes related to extending API routes handled by a Kobweb server")
}