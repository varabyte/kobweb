@file:Suppress("LeakingThis") // Following official Gradle guidance

package com.varabyte.kobweb.gradle.application.tasks

import com.varabyte.kobweb.gradle.application.KOBWEB_SERVER_JAR
import com.varabyte.kobweb.gradle.core.tasks.KobwebTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.security.MessageDigest
import java.util.*

/**
 * Extract the server jar from this plugin's resources into the .kobweb folder.
 *
 * Doing this has two benefits:
 *
 * 1. It gives us a permanent, consistent place to put the server jar, so that the start and export tasks can use it
 *    when running.
 *
 * 2. This allows the .kobweb folder to be self-contained, which can be very useful for people who want to deploy their
 *    Kobweb site on some external hosting service.
 */
abstract class KobwebUnpackServerJarTask : KobwebTask("Extract a server.jar resource from the Gradle plugin and move it into the .kobweb folder") {
    private val serverJarResource = KobwebUnpackServerJarTask::class.java.getResourceAsStream("/server.jar")!!.readAllBytes()

    @Input
    fun getServerJarResourceHash(): String {
        return String(Base64.getEncoder().encode(
            MessageDigest.getInstance("SHA-256").digest(serverJarResource)!!
        ))
    }

    @OutputFile
    fun getServerJar() = kobwebApplication.kobwebFolder.resolve(KOBWEB_SERVER_JAR).toFile()

    @TaskAction
    fun execute() {
        getServerJar().writeBytes(serverJarResource)
    }
}