import com.varabyte.kobweb.gradle.library.util.configAsKobwebLibrary

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.compiler)
    id("com.varabyte.kobweb.library")
}

group = "playground"
version = "1.0-SNAPSHOT"

kobweb {
    kspProcessorDependency.set("com.varabyte.kobweb:site-processors")
}

kotlin {
    configAsKobwebLibrary(includeServer = true)

    sourceSets {
        jsMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.html.core)
            implementation("com.varabyte.kobweb:kobweb-core")
            implementation("com.varabyte.kobweb:kobweb-silk")
            implementation("com.varabyte.kobwebx:silk-icons-fa")
        }
        jvmMain.dependencies {
            implementation("com.varabyte.kobweb:kobweb-api")
        }
    }
}
