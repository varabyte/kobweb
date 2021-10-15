plugins {
    kotlin("multiplatform") version "1.5.31"
    id("org.jetbrains.compose") version "1.0.0-alpha4-build385"
    id("com.varabyte.kobweb.application")
    id("com.varabyte.kobwebx.markdown")
}

group = "helloworld"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

kotlin {
    jvm() {
        tasks.named("jvmJar", Jar::class.java).configure {
            archiveFileName.set("helloworld.jar")
        }
    }
    js(IR) {
        browser()
        binaries.executable()
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
            }
        }
        val jsMain by getting {
            dependencies {
                implementation("com.varabyte.kobweb:web-compose-ext")
                implementation("com.varabyte.kobweb:kobweb")
                implementation("com.varabyte.kobweb:kobweb-silk")
                implementation("com.varabyte.kobweb:kobweb-silk-icons-fa")
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation("com.varabyte.kobweb:kobweb-api")
            }
        }
    }
}