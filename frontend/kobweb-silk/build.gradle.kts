import com.varabyte.kobweb.gradle.publish.FILTER_OUT_MULTIPLATFORM_PUBLICATIONS
import com.varabyte.kobweb.gradle.publish.set

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.jetbrains.compose)
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
            implementation(compose.runtime)
            implementation(compose.html.core)

            api(projects.frontend.kobwebCore)
            api(projects.frontend.silkFoundation)
            api(projects.frontend.silkWidgets)
            api(projects.frontend.silkWidgetsKobweb)
        }
    }
}

kobwebPublication {
    artifactId.set("kobweb-silk")
    description.set("An artifact that includes all relevant Silk dependencies and glues them together.")
    filter.set(FILTER_OUT_MULTIPLATFORM_PUBLICATIONS)
}
