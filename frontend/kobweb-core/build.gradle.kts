plugins {
    alias(libs.plugins.kotlin.multiplatform)
    id("kobweb-compose")
    id("com.varabyte.kobweb.internal.publish")
}

group = "com.varabyte.kobweb"
version = libs.versions.kobweb.libs.get()

kotlin {
    js {
        browser()
    }

    sourceSets {
        jsMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.html.core)
            implementation(libs.kotlinx.serialization.json)
            api(projects.frontend.composeHtmlExt)
            implementation(projects.common.clientServerInternal)
            api(projects.frontend.kobwebWorkerInterface)
        }

        jsTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.truthish)
        }
    }
}

kobwebPublication {
    artifactName.set("Kobweb Core")
    artifactId.set("kobweb-core")
    description.set("Core classes and annotations in the Kobweb framework for defining pages and handling routing on the frontend.")
}
