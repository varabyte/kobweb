import com.varabyte.kobweb.gradle.publish.set

plugins {
    alias(libs.plugins.kotlin.jvm)
    id("com.varabyte.kobweb.internal.publish")
}

group = "com.varabyte.kobweb"
version = libs.versions.kobweb.libs.get()

dependencies {
    implementation(libs.ksp.processing)
    implementation(libs.kotlinx.serialization.json)
    implementation(projects.common.kobwebCommon)
    implementation(projects.tools.processorCommon)
    implementation(projects.tools.ksp.kspExt)
}

kobwebPublication {
    artifactName.set("Kobweb KSP Site Processors")
    artifactId.set("kobweb-ksp-site-processors")
    description.set("KSP processors for Kobweb library and app modules, generating both frontend and backend metadata.")
}
