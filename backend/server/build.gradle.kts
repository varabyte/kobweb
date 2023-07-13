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
    implementation(kotlin("stdlib"))
    implementation(libs.bundles.ktor)
    implementation(libs.kaml)

    implementation(project(":backend:kobweb-api"))
    implementation(project(":backend:server-plugin"))
    implementation(project(":common:kobweb-common"))

    // Needed for the model object created and sent by the client
    implementation(project(":frontend:kobweb-streams"))

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
// NOTE: We used to minimize the jar but we had to keep making exceptions for things that got stripped that we needed at
// runtime. Last check, the minimized jar was 15M vs. 18M not minimized, but with the added danger that things could
// break at any time in the future. It just wasn't worth it.
// However, we leave a record of what we were excluding from minimization before. Additionaly, we'd probably have to
// exclude some ktor stuff, because a service loader started crashing with v2.3.0
//    minimize {
//        // Leave all kotlin-reflect bits in, as many libraries expect it to be there. When users register
//        // server API routes, their code should be able to use it.
//        exclude(dependency("org.jetbrains.kotlin:kotlin-reflect:.*"))
//        // Code may end up getting referenced via reflection
//        exclude(project(":backend:kobweb-api"))
//        // Logger classes are accessed at runtime
//        exclude(dependency("ch.qos.logback:.*:.*"))
//    }

    // Move as much as we can out of the way, to avoid the change that we have our own dependency that conflicts with
    // any code registered by the user in the uber jar they provide for handling server API routes.
    // See also: build/libs, unzip server-0.12.4-SNAPSHOT-all after building `:shadowJar`
    // Note: This workaround is (probably) necessary because Java classloading has disappointing limitations when it
    // comes to isolating execution environments. In other words, we want to tell the JVM "this user's uber jar? It
    // should get its own environment, except for a few interfaces it should get from us."
    // Ideally, I'd have a common classloader, the user's classloader, and our server's classloader, where kobweb-api
    // would live in the common classloader, and otherwise the user's classloader and our server's classloader would be
    // totally separate. After a day fighting code, I couldn't figure out how to get this to work.
    relocate("com", "relocated.com") {
        // Leave varabyte code in place. If the user is referencing any varabyte classes in their server APIs, the
        // versions should be synced up with anything also referenced in the server (since the server is versioned the
        // same as varabyte artifacts).
        exclude("com.varabyte.**")
    }
    relocate("org", "relocated.org") {
        exclude("org.xml.sax.**") // Need to exclude this or else we get an exception at runtime
        exclude("org.slf4j.**") // Don't relocate ktor; might be referenced by Kobweb server plugins
    }
    relocate("io", "relocated.io") {
        exclude("io.netty.**") // Relocating io.netty causes exceptions to happen on server startup
        exclude("io.ktor.**") // Don't relocate ktor; might be referenced by Kobweb server plugins
    }
    relocate("kotlinx", "relocated.kotlinx") {
        // Coroutines are provided by ktor, so let's leave them in place
        exclude("kotlinx.coroutines.**")
    }
}
