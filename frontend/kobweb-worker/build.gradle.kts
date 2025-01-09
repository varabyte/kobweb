plugins {
    alias(libs.plugins.kotlin.multiplatform)
    id("com.varabyte.kobweb.internal.publish")
}

group = "com.varabyte.kobweb"
version = libs.versions.kobweb.get()

kotlin {
    js {
        browser()
    }

    sourceSets {
        jsMain.dependencies {
            api(projects.kobweb.common.kobwebSerialization)
            api(projects.kobweb.frontend.kobwebWorkerInterface)
        }
    }
}

kobwebPublication {
    artifactName.set("Kobweb Worker")
    artifactId.set("kobweb-worker")
    description.set("Utility classes for creating a type-safe web worker in Kobweb.")
}
