import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

@Suppress("DSL_SCOPE_VIOLATION") // https://youtrack.jetbrains.com/issue/KTIJ-19369
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
    implementation(project(":common:kobweb-common"))

    testImplementation(libs.truthish)
    testImplementation(libs.ktor.server.tests)
    testImplementation(kotlin("test"))
}

val applicationClass = "com.varabyte.kobweb.server.ApplicationKt"
project.setProperty("mainClassName", applicationClass)
application {
    mainClass.set(applicationClass)
}

tasks.withType<ShadowJar> {
    minimize {
        // Leave all kotlin-reflect bits in, as many libraries expect it to be there. When users register
        // server API routes, their code should be able to use it.
        exclude(dependency("org.jetbrains.kotlin:kotlin-reflect:.*"))
        // Code may end up getting referenced via reflection
        exclude(project(":backend:kobweb-api"))
        // Logger classes are accessed at runtime
        exclude(dependency("ch.qos.logback:.*:.*"))
    }

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
        // Leave varabyte code in place. Anything the user is using in their code should be
        // provided by the server
        exclude("com.varabyte.**")
    }
    relocate("org", "relocated.org") {
        // Need to exclude this or else we get an exception at runtime
        exclude("org.xml.sax.**")
    }
    relocate("io", "relocated.io") {
        exclude("io.netty.**") // Relocating io.netty causes exceptions to happen on server startup
    }
    relocate("kotlinx", "relocated.kotlinx")
}