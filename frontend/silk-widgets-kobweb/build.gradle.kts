import com.varabyte.kobweb.gradle.publish.FILTER_OUT_MULTIPLATFORM_PUBLICATIONS
import com.varabyte.kobweb.gradle.publish.set

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

            implementation(projects.frontend.kobwebCore)
            api(projects.frontend.silkWidgets)
        }
    }
}

kobwebPublication {
    artifactName.set("Kobweb Silk Widgets")
    artifactId.set("silk-widgets-kobweb")
    description.set("Silk UI components tightly integrated with Kobweb functionality -- they cannot be used without Kobweb.")
    filter.set(FILTER_OUT_MULTIPLATFORM_PUBLICATIONS)
}
