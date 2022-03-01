import org.jreleaser.model.Active

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    application
    alias(libs.plugins.jreleaser)
}

group = "com.varabyte.kobweb.cli"
version = libs.versions.kobweb.cli.get()

dependencies {
    implementation(kotlin("stdlib"))
    implementation(libs.kotlinx.cli)
    implementation(libs.kotlinx.coroutines)
    implementation(libs.kotter)
    implementation(libs.jgit)
    implementation(libs.freemarker)
    implementation(libs.kaml)
    implementation(project(":common:kobweb-common"))
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
            website.set("https://kobweb.varabyte.com/")
            docsUrl.set("https://kobweb.varabyte.com/docs")
            description.set("Set up and manage your Compose for Web app")
            longDescription.set(
                """
                Kobweb CLI provides commands to handle the tedious parts of building a Compose for Web app, including
                project setup and configuration.
            """.trimIndent()
            )
            authors.set(listOf("David Herman"))
            license.set("Apache-2.0")
            licenseUrl.set("http://www.apache.org/licenses/LICENSE-2.0")
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
                owner.set("varabyte")
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

            // Re-enable this when https://github.com/jreleaser/jreleaser/issues/737 is fixed
//            val (key, token) = listOf(findProperty("sdkman.key") as? String, findProperty("sdkman.token") as? String)
//            if (key != null && token != null) {
//                sdkman {
//                    downloadUrl.set(artifactDownloadPath)
//                    consumerKey.set(key)
//                    consumerToken.set(token)
//                    active.set(Active.RELEASE)
//                }
//            }
//            else {
//                println("SDKMAN! packager disabled on this machine since key and/or token are not defined")
//            }
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