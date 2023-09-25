import com.varabyte.kobweb.gradle.application.util.configAsKobwebApplication

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.jetbrains.compose)
    id("com.varabyte.kobweb.application")
    id("com.varabyte.kobwebx.markdown")
}

group = "playground"
version = "1.0-SNAPSHOT"

kobweb {
    markdown {
        imports.add(".components.widgets.*")
    }
}

kotlin {
    configAsKobwebApplication(includeServer = true)

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
                implementation("com.varabyte.kobwebx:kobwebx-markdown")
                implementation(project(":sitelib"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation("com.varabyte.kobweb:kobweb-api")
                implementation(project(":sitelib"))
            }
        }
    }
}
