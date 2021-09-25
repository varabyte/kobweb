plugins {
    kotlin("jvm")
}

group = "com.varabyte.kobweb.server"
version = libs.versions.kobweb.get()

dependencies {
    implementation(kotlin("stdlib"))
}

tasks.jar {
    archiveFileName.set("kobweb-server.jar")
}