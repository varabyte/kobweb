import com.varabyte.kobweb.gradle.library.util.configAsKobwebLibrary

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.jetbrains.compose)
    id("com.varabyte.kobweb.library")
}

group = "playground"
version = "1.0-SNAPSHOT"

kobweb {
    includeKspDependency.set(false)
}

val kspDependency = "com.varabyte.kobweb:ksp-processor"
configurations.matching { it.name == "kspJs" || it.name == "kspJvm" }.configureEach {
    dependencies {
        add(this@configureEach.name, kspDependency)
    }
}

kotlin {
    configAsKobwebLibrary(includeServer = true)

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
            }
        }
        val jsMain by getting {
            dependencies {
                implementation(compose.html.core)
                implementation("com.varabyte.kobweb:kobweb-core")
                implementation("com.varabyte.kobweb:kobweb-silk")
                implementation("com.varabyte.kobwebx:silk-icons-fa")
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation("com.varabyte.kobweb:kobweb-api")
            }
        }
    }
}
