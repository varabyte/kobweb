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

// Read about JReleaser at https://jreleaser.org/guide/latest/index.html
jreleaser {
    dryrun.set(false) // Specified explicitly for convenience - set dryrun to true when experimenting with values!
    gitRootSearch.set(true)
    project {
        website.set("https://kobweb.varabyte.com/")
        docsUrl.set("https://kobweb.varabyte.com/docs")
        description.set("Kobweb CLI provides commands to handle the tedious parts of building a Compose for Web app")
        longDescription.set("""
            Kobweb CLI provides commands to handle the tedious parts of building a Compose for Web app, including
            project setup and configuration.
        """.trimIndent())
        authors.set(listOf("David Herman"))
        license.set("Apache-2.0")
        copyright.set("Copyright © 2022 Varabyte. All rights reserved.")
    }
    release {
        github {
            owner.set("varabyte")
            tagName.set("cli-v{{projectVersion}}")

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

            // These values are specified in ~/.gradle/gradle.properties; otherwise sorry, no jreleasing for you :P
            mapOf<String, (String) -> Unit>(
                "varabyte.github.username" to { username.set(it) },
                "varabyte.github.token" to { token.set(it) },
            ).forEach { (key, setter) ->
                (findProperty(key) as? String)?.let { setter(it) } ?: run {
                    println("\"$key\" is missing so disabling github release")
                    enabled.set(false)
                }
            }
        }
    }
    packagers {
        brew {
            active.set(Active.RELEASE)
            // Redundant downloadUrl temporarily required. See https://github.com/jreleaser/jreleaser/issues/699
            downloadUrl.set("https://{{repoHost}}/{{repoOwner}}/{{repoName}}/releases/download/{{tagName}}/{{artifactFile}}")
            templateDirectory.set(File("jreleaser/templates/brew"))
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