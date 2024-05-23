plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.compiler)
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
