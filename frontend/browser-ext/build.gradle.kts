plugins {
    alias(libs.plugins.kotlin.multiplatform)
    id("com.varabyte.kobweb.internal.publish")
}

group = "com.varabyte.kobweb"
version = libs.versions.kobweb.get()

kotlin {
    js {
        browser()
    }

    sourceSets {
        jsMain.dependencies {
            api(libs.kotlinx.coroutines)
        }

        jsTest.dependencies {
            implementation(kotlin("test-js"))
            implementation(libs.truthish)
        }
    }
}

kobwebPublication {
    artifactName.set("Kobweb Browser Extensions")
    artifactId.set("browser-ext")
    description.set("Generally useful Kotlin/JS extensions for the browser (not Node) APIs that could potentially move upstream someday.")
}
