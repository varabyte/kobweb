plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlinx.serialization)
    id("com.varabyte.kobweb.internal.publish")
}

group = "com.varabyte.kobweb"
version = libs.versions.kobweb.libs.get()

dependencies {
    // Expose kaml as an API dependency. If you use kobweb-common, many classes are associated with yaml files. Using
    // kobweb-common and your own different version of kaml can result in runtime exceptions, so just expose this one.
    api(libs.kaml)
    implementation(libs.kotlinx.coroutines)

    testImplementation(kotlin("test"))
    testImplementation(libs.truthish)
    testImplementation(libs.kotlinx.serialization.json)
}

kobwebPublication {
    artifactName.set("Kobweb Common")
    artifactId.set("kobweb-common")
    description.set("A collection of utility classes for interacting with a Kobweb project needed by both frontend and backend codebases.")
}
