import com.varabyte.kobweb.gradle.publish.setForMultiplatform

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    id("kobweb-compose")
    id("com.varabyte.kobweb.internal.publish")
}

group = "com.varabyte.kobweb"
version = libs.versions.kobweb.libs.get()

kotlin {
    js {
        browser()
    }

    sourceSets {
        jsMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.html.core)

            api(projects.frontend.kobwebCompose)
            api(projects.frontend.silkFoundation)
            implementation(projects.frontend.composeHtmlExt)
        }
    }
}

kobwebPublication {
    artifactName.set("Silk Widgets")
    artifactId.setForMultiplatform("silk-widgets")
    description.set("A subset of Silk UI components that don't depend on Kobweb at all, extracted into their own library in case projects want to use it without Kobweb.")
}
