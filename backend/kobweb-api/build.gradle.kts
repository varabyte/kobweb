plugins {
    kotlin("jvm")
    id("com.varabyte.kobweb.internal.publish")
}

group = "com.varabyte.kobweb"
version = libs.versions.kobweb.libs.get()

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