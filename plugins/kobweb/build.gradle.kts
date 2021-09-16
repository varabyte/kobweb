plugins {
    id("com.google.devtools.ksp")
    kotlin("jvm")
    `java-gradle-plugin`
    `maven-publish`
}

group = "com.varabyte.kobweb"
version = "0.1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    // For processing the user's project
    implementation(libs.ksp)
    // For kobweb.conf.yaml
    implementation(libs.kaml)
}

gradlePlugin {
    plugins {
        create("kobwebPlugin") {
            id = "com.varabyte.kobweb"
            implementationClass = "com.varabyte.kobweb.plugins.kobweb.KobwebPlugin"
        }
    }
}

inline val PluginDependenciesSpec.`kobweb-plugin`: PluginDependencySpec
    get() = id("com.varabyte.kobweb")