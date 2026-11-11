plugins {
    id("kobweb-compilation-settings")
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlinx.serialization)
    id("com.varabyte.kobweb.internal.publish")
}

group = "com.varabyte.kobweb"
version = libs.versions.kobweb.framework.get()

dependencies {
    implementation(libs.kotlinx.serialization.core)
}

kobwebPublication {
    artifactName.set("Kobweb Icons Metadata")
    artifactId.set("kobweb-icons-metadata")
    description.set("The format of the icons.json metadata file that Kobweb's icon artifacts include.")
}
