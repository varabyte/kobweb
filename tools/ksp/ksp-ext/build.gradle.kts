plugins {
    alias(libs.plugins.kotlin.jvm)
    id("com.varabyte.kobweb.internal.publish")
}

group = "com.varabyte.kobweb"
version = libs.versions.kobweb.libs.get()

dependencies {
    implementation(libs.ksp.processing)
    implementation(libs.kotlinx.serialization.json)
    implementation(projects.tools.processorCommon)
}

kobwebPublication {
    artifactName.set("Kobweb KSP Extensions")
    artifactId.set("kobweb-ksp-ext")
    description.set("Useful KSP utility methods that are shared across projects.")
}
