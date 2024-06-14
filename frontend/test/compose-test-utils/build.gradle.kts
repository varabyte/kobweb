plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.jetbrains.compose)
}

group = "com.varabyte.kobweb"
version = libs.versions.kobweb.libs.get()

kotlin {
    js {
        browser()
    }

    sourceSets {
        jsMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.html.core)
            api(libs.kotlinx.coroutines)
            api(projects.frontend.browserExt) // If you want compose-html-ext, you also want browser-ext
        }

        jsTest.dependencies {
            implementation(kotlin("test-js"))
            implementation(libs.truthish)
        }
    }
}
