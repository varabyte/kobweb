import com.varabyte.kobweb.gradle.application.util.configAsKobwebApplication
import kotlinx.html.meta

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.jetbrains.compose)
    id("com.varabyte.kobweb.application")
    id("com.varabyte.kobwebx.markdown")
}

group = "playground"
version = "1.0-SNAPSHOT"

kobweb {
    app.index {
        head.add {
            val x = description.get()
            meta {
                this.name = x
            }
        }
    }
    markdown {
        imports.add(".components.widgets.*")
        process.set { markdownEntries ->
            generateMarkdown("markdown/listing.md", buildString {
                appendLine("# Listing Index")
                markdownEntries.forEach { entry ->
                    appendLine("* [${entry.filePath}](${entry.route})")
                }
            })
        }
    }
    kspProcessorDependency.set("com.varabyte.kobweb:site-processors")
}

kotlin {
    configAsKobwebApplication(includeServer = true)

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
        }
        jsMain {
            dependencies {
                implementation(compose.html.core)
                implementation("com.varabyte.kobweb:kobweb-core")
                implementation("com.varabyte.kobweb:kobweb-silk")
                implementation("com.varabyte.kobwebx:silk-icons-fa")
                implementation("com.varabyte.kobwebx:kobwebx-markdown")
                implementation(project(":sitelib"))
                implementation(project(":worker"))
            }
        }
        jvmMain.dependencies {
            implementation("com.varabyte.kobweb:kobweb-api")
            implementation(project(":sitelib"))
        }
    }
}

fun Project.hasJsDependencyNamed(name: String): Boolean {
    check(project.state.executed)
    return configurations.asSequence()
        .flatMap { config -> config.dependencies }
        .any { dependency -> dependency.name == name }
}

project.afterEvaluate {
    project.afterEvaluate {
        println("Has silk: ${hasJsDependencyNamed("kobweb-silk")}")
//    println(kobweb.markdown.handlers.useSilk.get())
//        println(tasks.named<ConvertMarkdownTask>("kobwebxMarkdownConvert").get().dependsOnMarkdownArtifact.get())
//        println(tasks.named<KobwebGenerateSiteIndexTask>("kobwebGenSiteIndex").get().hasFaDependency.get())
//        println(tasks.named<KobwebGenerateSiteIndexTask>("kobwebGenSiteIndex").get().hasMdiDependency.get())
//        println(tasks.named<KobwebGenerateSiteEntryTask>("kobwebGenSiteEntry").get().silkSupport.get())
    }
}
