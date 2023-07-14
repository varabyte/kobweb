plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlinx.serialization)
    id("com.varabyte.kobweb.internal.publish")
}

group = "com.varabyte.kobweb"
version = libs.versions.kobweb.libs.get()

kotlin {
    jvm()
    js(IR) {
        browser()
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.kotlinx.serialization.core)
            }
        }

        val jsMain by getting {
            dependencies {
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.kotlinx.coroutines)
            }
        }
    }
}

kobwebPublication {
    artifactId.set {
        "kobweb-streams" + if (it == "kotlinMultiplatform") "" else "-$it"
    }
    description.set("Support for using Kobweb API streams from a Kobweb site.")
}
