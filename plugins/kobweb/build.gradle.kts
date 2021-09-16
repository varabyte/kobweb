plugins {
    id("com.google.devtools.ksp")
    kotlin("jvm")
    id("com.varabyte.kobweb.publish")
    `java-gradle-plugin`
}

group = "com.varabyte.kobweb.gradle"
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

val DESCRIPTION = "A Gradle plugin enabling a user to generate additional code on top of their Kobweb web app source"
gradlePlugin {
    plugins {
        create("kobwebPlugin") {
            id = "com.varabyte.kobweb"
            displayName = "Kobweb Plugin"
            description = DESCRIPTION
            implementationClass = "com.varabyte.kobweb.plugins.kobweb.KobwebPlugin"
        }
    }
}

kobwebPublication {
    artifactId.set("kobweb")
    description.set(DESCRIPTION)
}

inline val PluginDependenciesSpec.`kobweb-plugin`: PluginDependencySpec
    get() = id("com.varabyte.kobweb")