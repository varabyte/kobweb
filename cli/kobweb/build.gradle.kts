import org.jreleaser.model.Active

@Suppress("DSL_SCOPE_VIOLATION") // https://youtrack.jetbrains.com/issue/KTIJ-19369
plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    application
    alias(libs.plugins.jreleaser)
}

group = "com.varabyte.kobweb.cli"
version = libs.versions.kobweb.cli.get()

repositories {
    // For Gradle Tooling API
    maven { url = uri("https://repo.gradle.org/gradle/libs-releases") }
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(libs.kotlinx.cli)
    implementation(libs.kotlinx.coroutines)
    implementation(libs.kotter)
    implementation(libs.freemarker)
    implementation(libs.kaml)
    implementation(project(":common:kobweb-common"))

    // For Gradle Tooling API
    implementation("org.gradle:gradle-tooling-api:${gradle.gradleVersion}")
    runtimeOnly("org.slf4j:slf4j-nop:2.0.6")
}

application {
    applicationDefaultJvmArgs = listOf("-Dkobweb.version=${version}")
    mainClass.set("MainKt")
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