import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlinx.serialization)
    application
    alias(libs.plugins.shadow)
}

group = "com.varabyte.kobweb.server"
version = libs.versions.kobweb.libs.get()

dependencies {
    implementation(libs.bundles.ktor)
    implementation(libs.kaml)

    implementation(projects.backend.kobwebApi)
    implementation(projects.backend.serverPlugin)
    implementation(projects.common.kobwebCommon)
    implementation(projects.common.clientServerModels)

    testImplementation(libs.truthish)
    testImplementation(libs.ktor.server.tests)
    testImplementation(kotlin("test"))
}

val applicationClass = "com.varabyte.kobweb.server.ApplicationKt"
project.setProperty("mainClassName", applicationClass)
application {
    mainClass.set(applicationClass)
}

tasks.withType<ShadowJar>().configureEach {
    manifest {
        // Custom ktor version attribute to work around ktor's own way of getting the version that breaks when we
        // build our fat jar. See also HTTP.kt where we read this attribute.
        attributes["Ktor-Version"] = libs.versions.ktor.get()
    }

    // NOTE: We used to minimize the jar but we had to keep making exceptions for things that got stripped that we needed at
    // runtime. Last check, the minimized jar was 15M vs. 18M not minimized, but with the added danger that things could
    // break at any time in the future. It just wasn't worth it.

    // Move as much as we can out of the way, to avoid the change that we have our own dependency that conflicts with
    // any code registered by the user in the uber jar they provide for handling server API routes.
    //
    // To see all dependencies used by the server, build `:shadowJar`, then go into `build/libs`, find
    // `server-(version)-SNAPSHOT-all.jar`, unzip it, and look at the contents to see what was included.
    //
    // TODO: When running a Kobweb server, we really want three classloaders -- one for the user's code, one for the
    //  server implementation, and a common classloader which provides interfaces used by both. This way, the user's
    //  code and the server code won't stomp on each other. After a day fighting code, I couldn't figure out how to get
    //  this to work, but it may be worth a shot trying again, as it will allow us to delete all this relocating logic.

    // NOTE: All patterns have a trailing . below, which is to avoid renaming string values that happen to have the
    // same prefix. See also https://github.com/johnrengelman/shadow/issues/232
    relocate("com.", "relocated.com.") {
        // Leave varabyte code in place. If the user is referencing any varabyte classes in their server APIs, the
        // versions should be synced up with anything also referenced in the server (since the server is versioned the
        // same as varabyte artifacts).
        exclude("com.varabyte.**")
    }
    relocate("org.", "relocated.org.") {
        exclude("org.xml.sax.**") // Need to exclude this or else we get an exception at runtime
        exclude("org.slf4j.**") // Don't relocate ktor; might be referenced by Kobweb server plugins
    }
    relocate("io.", "relocated.io.") {
        exclude("io.netty.**") // Relocating io.netty causes exceptions to happen on server startup
        exclude("io.ktor.**") // Don't relocate ktor; might be referenced by Kobweb server plugins
    }
    relocate("kotlinx.", "relocated.kotlinx.") {
        // Coroutines are provided by ktor, so let's leave them in place
        exclude("kotlinx.coroutines.**")
    }
}
