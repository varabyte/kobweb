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
}

kobwebPublication {
    artifactName.set("Kobweb Worker Interface")
    artifactId.set("kobweb-worker-interface")
    description.set("Common interface for worker implementations.")
}
