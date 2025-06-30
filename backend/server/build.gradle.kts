import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlinx.serialization)
    application
    alias(libs.plugins.shadow)
}

group = "com.varabyte.kobweb.server"
version = libs.versions.kobweb.get()

dependencies {
    implementation(libs.bundles.ktor)
    implementation(libs.kaml)
    runtimeOnly(libs.bundles.logback)

    implementation(projects.backend.kobwebApi)
    implementation(projects.backend.serverPlugin)
    implementation(projects.common.kobwebCommon)
    implementation(projects.common.clientServerInternal)

    testImplementation(libs.truthish)
    testImplementation(libs.ktor.server.tests)
    testImplementation(kotlin("test"))
}

application {
    mainClass = "com.varabyte.kobweb.server.ApplicationKt"
}

tasks.withType<ShadowJar>().configureEach {
    manifest {
        attributes["Kobweb-Version"] = version
        // Custom ktor version attribute to work around ktor's own way of getting the version that breaks when we
        // build our fat jar. See also HTTP.kt where we read this attribute.
        attributes["Ktor-Version"] = libs.versions.ktor.get()
    }

    // NOTE: We used to minimize the jar but we had to keep making exceptions for things that got stripped that we needed at
    // runtime. Last check, the minimized jar was 15M vs. 18M not minimized, but with the added danger that things could
    // break at any time in the future. It just wasn't worth it.
}
