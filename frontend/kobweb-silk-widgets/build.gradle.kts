plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    id("com.varabyte.kobweb.internal.publish")
}

group = "com.varabyte.kobweb"
version = libs.versions.kobweb.libs.get()

kotlin {
    js(IR) {
        browser()
        binaries.executable()
    }
    sourceSets {
        val jsMain by getting {
            dependencies {
                implementation(compose.web.core)
                implementation(compose.runtime)

                api(project(":frontend:kobweb-compose"))
            }
        }
    }
}

kobwebPublication {
    artifactId.set("kobweb-silk-widgets")
    description.set("The subset of Silk that doesn't depend on Kobweb at all, extracted into its own library in case projects want to use it without Kobweb")
}