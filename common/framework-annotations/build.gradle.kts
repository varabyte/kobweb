plugins {
    alias(libs.plugins.kotlin.multiplatform)
    id("com.varabyte.kobweb.internal.publish")
}

group = "com.varabyte.kobweb"
version = libs.versions.kobweb.get()

kotlin {
    jvm()
    js {
        browser()
    }
}

kobwebPublication {
    artifactName.set("Framework Annotations")
    artifactId.set("framework-annotations")
    description.set("A collection of annotations used to tag methods in the Kobweb framework, as a way to communicate intent to developers.")
}
