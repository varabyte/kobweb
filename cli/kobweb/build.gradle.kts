import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jreleaser.model.Active

@Suppress("DSL_SCOPE_VIOLATION") // https://youtrack.jetbrains.com/issue/KTIJ-19369
plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    application
    alias(libs.plugins.jreleaser)
    alias(libs.plugins.shadow)
}

group = "com.varabyte.kobweb.cli"
version = libs.versions.kobweb.cli.get()

repositories {
    // For Gradle Tooling API
    maven { url = uri("https://repo.gradle.org/gradle/libs-releases") }
    // maven("https://us-central1-maven.pkg.dev/varabyte-repos/public") // <-- For testing kotter snapshots
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(libs.clikt)
    implementation(libs.kotlinx.coroutines)
    implementation(libs.kotter)
    implementation(libs.freemarker)
    implementation(libs.kaml)
    implementation(libs.okhttp)
    implementation(project(":common:kobweb-common"))

    // For Gradle Tooling API (used for starting up / communicating with a gradle daemon)
    implementation("org.gradle:gradle-tooling-api:${gradle.gradleVersion}")
    runtimeOnly("org.slf4j:slf4j-nop:2.0.6") // Needed by gradle tooling
}

application {
    applicationDefaultJvmArgs = listOf("-Dkobweb.version=${version}")
    mainClass.set("MainKt")
}

tasks.withType<ShadowJar> {
    minimize {
        // Leave Jansi deps in place, or else Windows won't work
        exclude(dependency("org.fusesource.jansi:.*:.*"))
        exclude(dependency("org.jline:jline-terminal-jansi:.*"))
        // Leave SLF4J in place, or else a warning is spit out
        exclude(dependency("org.slf4j.*:.*:.*"))
    }
}

distributions {
    named("shadow") {
        // We choose to make the output names of "assembleShadowDist" the same as "assembleDist" here, since ideally
        // they should be interchangeable (the shadow version just has dead code removed). However, this means if you
        // run "assembleDist" and then "assembleShadowDist" (or the other way around), the latter command will overwrite
        // the output of the prior one.
        distributionBaseName.set("kobweb")
    }
}

// Avoid ambiguity / add clarity in generated artifacts
tasks.jar {
    archiveFileName.set("kobweb-cli.jar")
}

// These values are specified in ~/.gradle/gradle.properties; otherwise sorry, no jreleasing for you :P
val (githubUsername, githubToken) = listOf("varabyte.github.username", "varabyte.github.token")
    .map { key -> findProperty(key) as? String }

if (githubUsername != null && githubToken != null) {
    // Read about JReleaser at https://jreleaser.org/guide/latest/index.html
    jreleaser {
        dryrun.set(false) // Specified explicitly for convenience - set dryrun to true when experimenting with values!
        gitRootSearch.set(true)
        project {
            links {
                homepage.set("https://kobweb.varabyte.com/")
                documentation.set("https://kobweb.varabyte.com/docs")
                license.set("http://www.apache.org/licenses/LICENSE-2.0")
            }
            description.set("Set up and manage your Compose for Web app")
            longDescription.set(
                """
                Kobweb CLI provides commands to handle the tedious parts of building a Compose for Web app, including
                project setup and configuration.
            """.trimIndent()
            )
            authors.set(listOf("David Herman"))
            license.set("Apache-2.0")
            copyright.set("Copyright © 2022 Varabyte. All rights reserved.")

            // Set the Java version explicitly, even though in theory this value should be coming from our root
            // build.gradle file, but it does not seem to when I run "jreleaserPublish" from the command line.
            // See also: https://github.com/jreleaser/jreleaser/issues/785
            java {
                version.set(JavaVersion.VERSION_11.toString())
            }
        }
        release {
            github {
                repoOwner.set("varabyte")
                tagName.set("cli-v{{projectVersion}}")
                username.set(githubUsername)
                token.set(githubToken)

                // Tags and releases are handled manually via the GitHub UI for now. TODO(#104)
                skipTag.set(true)
                skipRelease.set(true)

                overwrite.set(true)
                uploadAssets.set(Active.RELEASE)
                commitAuthor {
                    name.set("David Herman")
                    email.set("bitspittle@gmail.com")
                }
                changelog {
                    enabled.set(false)
                }
                milestone {
                    // milestone management handled manually for now
                    close.set(false)
                }
                prerelease {
                    enabled.set(false)
                }
            }
        }
        packagers {
            brew {
                active.set(Active.RELEASE)
                templateDirectory.set(File("jreleaser/templates/brew"))
            }
            scoop {
                active.set(Active.RELEASE)
            }

            val (key, token) = listOf(findProperty("sdkman.key") as? String, findProperty("sdkman.token") as? String)
            if (key != null && token != null) {
                sdkman {
                    consumerKey.set(key)
                    consumerToken.set(token)
                    active.set(Active.RELEASE)
                }
            }
            else {
                println("SDKMAN! packager disabled on this machine since key and/or token are not defined")
            }
        }

        distributions {
            create("kobweb") {
                listOf("zip", "tar").forEach { artifactExtension ->
                    artifact {
                        setPath("build/distributions/{{distributionName}}-{{projectVersion}}.$artifactExtension")
                    }
                }
            }
        }
    }
} else {
    println(
        """
            NOTE: JReleaser disabled for this machine due to missing github username and/or token properties.
            This is expected (unless you intentionally configured these values).
        """.trimIndent()
    )
}