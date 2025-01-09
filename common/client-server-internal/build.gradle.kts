plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlinx.serialization)
    id("com.varabyte.kobweb.internal.publish")
}

group = "com.varabyte.kobweb"
version = libs.versions.kobweb.get()

kotlin {
    jvm()
    js {
        browser()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.serialization.core)
        }
        commonTest.dependencies {
            implementation(libs.truthish)
            implementation(kotlin("test"))
        }
    }
}

kobwebPublication {
    artifactName.set("Kobweb Client Server Internal")
    artifactId.set("kobweb-client-server-internal")
    description.set("Miscellaneous multiplatform classes and utilities shared between Kobweb core and server codebases. This is published for compilation purposes but users are not expected to depend on this artifact directly themselves.")
}
