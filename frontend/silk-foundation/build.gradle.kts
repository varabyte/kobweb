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

            implementation(projects.frontend.composeHtmlExt)
            api(projects.frontend.kobwebCompose)
        }
        jsTest.dependencies {
            implementation(kotlin("test"))
            implementation(projects.frontend.test.composeTestUtils)
            implementation(libs.truthish)
        }
    }
}

kobwebPublication {
    artifactName.set("Silk Foundation")
    artifactId.set("silk-foundation")
    description.set("The foundational layer of Silk that provides general purpose styling via CSS style blocks and functionality like CSS breakpoints, keyframes, animations, light/dark color modes, and theming support.")
}
