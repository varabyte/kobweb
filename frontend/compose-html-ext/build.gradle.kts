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
            api(libs.kotlinx.coroutines)
            api(projects.frontend.browserExt) // If you want compose-html-ext, you also want browser-ext
        }

        jsTest.dependencies {
            implementation(kotlin("test-js"))
            implementation(libs.truthish)
        }
    }
}

kobwebPublication {
    artifactName.set("Kobweb Compose HTML Extensions")
    artifactId.set("compose-html-ext")
    description.set("Generally useful Compose extensions that could potentially move upstream someday; until then, needed now for Kobweb.")
}
