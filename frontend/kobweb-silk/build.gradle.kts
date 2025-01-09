plugins {
    alias(libs.plugins.kotlin.multiplatform)
    id("kobweb-compose")
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
            implementation(libs.compose.runtime)
            implementation(libs.compose.html.core)

            api(projects.frontend.kobwebCore)
            api(projects.frontend.silkFoundation)
            api(projects.frontend.silkWidgets)
            api(projects.frontend.silkWidgetsKobweb)
        }
    }
}

kobwebPublication {
    artifactName.set("Kobweb Silk")
    artifactId.set("kobweb-silk")
    description.set("An artifact that includes all relevant Silk dependencies and glues them together.")
}
