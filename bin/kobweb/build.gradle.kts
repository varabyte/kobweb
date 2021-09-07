plugins {
    kotlin("jvm")
    application
}

group = "com.varabyte"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven { url = uri("https://us-central1-maven.pkg.dev/varabyte-repos/public") }
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(libs.kotlinx.cli)
    implementation(libs.konsole)
}

application {
    mainClass.set("MainKt")
}