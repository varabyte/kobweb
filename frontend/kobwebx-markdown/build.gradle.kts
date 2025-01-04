plugins {
    alias(libs.plugins.kotlin.multiplatform)
    id("kobweb-compose")
    id("com.varabyte.kobweb.internal.publish")
}

group = "com.varabyte.kobwebx"
version = libs.versions.kobweb.libs.get()

kotlin {
    js {
        browser()
    }

    sourceSets {
        jsMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.html.core)

            implementation(projects.frontend.kobwebCore)
        }
    }
}

kobwebPublication {
    artifactName.set("Kobweb Markdown")
    artifactId.set("kobwebx-markdown")
    description.set("Classes useful for projects using the Kobweb markdown plugin.")
}
