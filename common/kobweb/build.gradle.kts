plugins {
    kotlin("jvm")
}

group = "com.varabyte.kobweb.common"
version = libs.versions.kobweb.get()

dependencies {
    implementation(kotlin("stdlib"))
}

// Avoid ambiguity / add clarity in generated artifacts
tasks.jar {
    archiveFileName.set("kobweb-common.jar")
}