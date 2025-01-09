plugins {
    alias(libs.plugins.kotlin.jvm)
    id("com.varabyte.kobweb.internal.publish")
}

group = "com.varabyte.kobweb"
version = libs.versions.kobweb.get()

dependencies {
    implementation(libs.ksp.processing)
    implementation(projects.tools.processorCommon)
    implementation(projects.tools.ksp.kspExt)
}

kobwebPublication {
    artifactName.set("Kobweb KSP Worker Processor")
    artifactId.set("kobweb-ksp-worker-processor")
    description.set("KSP processor that generates boilerplate code for Kobweb worker modules.")
}
