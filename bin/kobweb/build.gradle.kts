plugins {
    kotlin("jvm")
    application
}

group = "com.github.bitspittle"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(libs.kotlinx.cli)
}

application {
    mainClass.set("MainKt")
}