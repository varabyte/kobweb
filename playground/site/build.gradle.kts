import com.varabyte.kobweb.gradle.application.util.configAsKobwebApplication

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.jetbrains.compose)
    id("com.varabyte.kobweb.application")
    id("com.varabyte.kobwebx.markdown")
}

group = "playground"
version = "1.0-SNAPSHOT"

kobweb {
    markdown {
        imports.add(".components.widgets.*")
    }
    kspProcessorDependency.set("com.varabyte.kobweb:project-processors")
}

val getWorkerScript by configurations.registering {}

dependencies {
    getWorkerScript(project(path = ":worker", configuration = "exposeWorkerScript"))
}

val customTask by tasks.registering(Sync::class) {
    from(getWorkerScript)
    into(layout.buildDirectory.dir("generated/worker/public"))

    outputs.dir(layout.buildDirectory.dir("generated/worker"))
}


kotlin {
    configAsKobwebApplication(includeServer = true)

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
        }
        jsMain {
            resources.srcDir(customTask)

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
