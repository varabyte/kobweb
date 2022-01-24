plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    application
}

group = "com.varabyte.kobweb.cli"
version = libs.versions.kobweb.cli.get()

dependencies {
    implementation(kotlin("stdlib"))
    implementation(libs.kotlinx.cli)
    implementation(libs.kotlinx.coroutines)
    implementation(libs.kotter)
    implementation(libs.jgit)
    implementation(libs.freemarker)
    implementation(libs.kaml)
    implementation(project(":common:kobweb-common"))
}

application {
    applicationDefaultJvmArgs = listOf("-Dkobweb.version=${version}")
    mainClass.set("MainKt")
}

// Avoid ambiguity / add clarity in generated artifacts
tasks.jar {
    archiveFileName.set("kobweb-cli.jar")
}