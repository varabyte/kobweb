@file:Suppress("LeakingThis") // Following official Gradle guidance

package com.varabyte.kobweb.gradle.application.tasks

import com.varabyte.kobweb.gradle.application.KOBWEB_SERVER_JAR
import com.varabyte.kobweb.gradle.application.KOBWEB_SERVER_START_BAT_FILE
import com.varabyte.kobweb.gradle.application.KOBWEB_SERVER_START_SHELL_SCRIPT
import com.varabyte.kobweb.gradle.application.util.getServerJar
import com.varabyte.kobweb.gradle.core.tasks.KobwebTask
import com.varabyte.kobweb.server.api.ServerEnvironment
import com.varabyte.kobweb.server.api.SiteLayout
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

/**
 * A simple task for creating scripts which can be use to run the Kobweb server in production mode.
 */
abstract class KobwebCreateServerScriptsTask : KobwebTask("Create scripts which can be used to start the Kobweb server in production mode") {
    @OutputFile
    fun getServerStartShellScript() = kobwebApplication.kobwebFolder.resolve(KOBWEB_SERVER_START_SHELL_SCRIPT).toFile()

    @OutputFile
    fun getServerStartBatFile() = kobwebApplication.kobwebFolder.resolve(KOBWEB_SERVER_START_BAT_FILE).toFile()

    @TaskAction
    fun execute() {
        val javaArgs = listOf(
            ServerEnvironment.PROD.toSystemPropertyParam(),
            SiteLayout.KOBWEB.toSystemPropertyParam(),
            "-Dio.ktor.development=false",
            "-jar",
            kobwebApplication.kobwebFolder.getServerJar().absolutePath.let {
                val kobwebFolderIndex = it.indexOf(".kobweb")
                it.substring(kobwebFolderIndex)
            }
        ).joinToString(" ")

        getServerStartBatFile().writeText("""
            @echo off
            
            :: Find the kobweb project folder, using this file's location to start form
            cd /d "%~dp0"
            cd ..\..
            
            if defined JAVA_HOME (
                set "java_cmd=%JAVA_HOME%\bin\java"
            ) else (
                set "java_cmd=java"
            )
            
            :: Run the java command with the common parameters
            %java_cmd% $javaArgs
        """.trimIndent())

        getServerStartShellScript().apply {
            writeText("""
                #!/bin/bash

                # Find the kobweb project folder, using this file's location to start form
                cd "$(dirname "$0")"
                cd ../..
            
                args="$javaArgs"
                
                if [ -z "${'$'}JAVA_HOME" ]; then
                    # JAVA_HOME is not set, use default java command
                    java ${'$'}args
                else
                    # JAVA_HOME is set, use custom java command
                    "${'$'}JAVA_HOME/bin/java" ${'$'}args
                fi
            """.trimIndent())
            setExecutable(true)
        }
    }
}