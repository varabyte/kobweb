import com.varabyte.kobweb.gradle.application.extensions.BodyTarget
import com.varabyte.kobweb.gradle.application.extensions.body
import com.varabyte.kobweb.gradle.application.util.configAsKobwebApplication
import com.varabyte.kobwebx.gradle.markdown.handlers.SilkCalloutBlockquoteHandler
import kotlinx.html.script
import kotlinx.html.unsafe

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.compiler)
    id("com.varabyte.kobweb.application")
    id("com.varabyte.kobwebx.markdown")
    alias(libs.plugins.kotlinx.serialization)
}

group = "playground"
version = "1.0-SNAPSHOT"

kobweb {
    app {
        index {
            interceptUrls {
                enableSelfHosting()
            }


            // Test the basic body block functionality (AFTER_SCRIPT position - default)
            body {
                script {
                    src = "https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"
                    attributes["integrity"] = "sha384-geWF76RCwLtnZ8qwWowPQNguL3RmwHVBC9FhGdlKrxdiJJigb/j/68SIy3Te4Bkz"
                    attributes["crossorigin"] = "anonymous"
                }
            }
            body {
                script {
                    unsafe {
                        raw(
                            """
                            console.log('AFTER_SCRIPT position: Analytics script loaded');
                            console.log('This script runs after the main Kobweb app script');
                            console.log('Page title:', document.title);
                        """.trimIndent()
                        )
                    }
                }
            }

            // Test START position - elements before root div and main script
            body(target = BodyTarget.START) {
                script {
                    unsafe {
                        raw("console.log('START position: Early script loaded before main app');")
                    }
                }
            }

            // Test END position - elements at the very end of body
            body(target = BodyTarget.END) {
                script {
                    unsafe {
                        raw("console.log('END position: Final script loaded at end of body');")
                    }
                }
            }
        }
    }
    markdown {
        defaultLayout.set(".components.layouts.MarkdownLayout")
        imports.add(".components.widgets.*")
        process.set { markdownEntries ->
            generateMarkdown("markdown/listing.md", buildString {
                // Disable the layout for this page; it looks weird when centered
                appendLine("""
                    ---
                    layout:
                    ---
                """.trimIndent()
                )
                appendLine("# Listing Index")
                markdownEntries.sortedBy { it.route }.forEach { entry ->
                    appendLine("* [${entry.filePath}](${entry.route})")
                }
            })
        }
        handlers.blockquote.set(SilkCalloutBlockquoteHandler(labels = mapOf("QUOTE" to "")))
    }
    kspProcessorDependency.set("com.varabyte.kobweb:site-processors")
}

val generateTestMarkdownTask = tasks.register("generateTestMarkdown") {
    // $name here to create a unique output directory just for this task
    val genOutputDir = layout.buildDirectory.dir("generated/$name/src/jsMain/resources/markdown")

    outputs.dir(genOutputDir)

    doLast {
        genOutputDir.get().file("markdown/GenerateTest.md").asFile.apply {
            parentFile.mkdirs()
            writeText("""
                # HELLO WORLD
            """.trimIndent()
            )

            println("Generated $absolutePath")
        }
    }
}

kobweb.markdown.addSource(generateTestMarkdownTask)
kobweb.markdown.addSource(project.layout.projectDirectory.dir("src/jsMain/resources/markdown-src"), ".")

kotlin {
    configAsKobwebApplication(includeServer = true)

    sourceSets {
        jsMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.html.core)
            implementation(libs.kotlinx.serialization.json)
            implementation("com.varabyte.kobweb:kobweb-core")
            implementation("com.varabyte.kobweb:kobweb-silk")
            implementation("com.varabyte.kobwebx:silk-icons-fa")
            implementation("com.varabyte.kobwebx:silk-icons-mdi")
            implementation("com.varabyte.kobwebx:kobwebx-markdown")
            implementation("com.varabyte.kobwebx:kobwebx-serialization-kotlinx")
            implementation(project(":sitelib"))
            implementation(project(":worker"))
        }
        jvmMain.dependencies {
            implementation("com.varabyte.kobweb:kobweb-api")
            implementation(project(":sitelib"))
        }
    }
}
