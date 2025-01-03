import com.varabyte.kobweb.gradle.publish.set

plugins {
    alias(libs.plugins.kotlin.jvm)
    id("com.varabyte.kobweb.internal.publish")
}

group = "com.varabyte.kobweb"
version = libs.versions.kobweb.libs.get()

val compileOnlyApi: Configuration by configurations.creating
configurations["compileClasspath"].extendsFrom(compileOnlyApi)
configurations["apiElements"].extendsFrom(compileOnlyApi)

dependencies {
    compileOnlyApi(libs.bundles.ktor)
}

kobwebPublication {
    artifactName.set("Kobweb Server Plugin")
    artifactId.set("kobweb-server-plugin")
    description.set("A plugin API that users can implement to run custom code on a Kobweb server.")
}