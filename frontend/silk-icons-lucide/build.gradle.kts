plugins {
    id("kobweb-compilation-settings")
    alias(libs.plugins.kotlin.multiplatform)
    id("kobweb-compose")
    id("com.varabyte.kobweb.internal.publish")
    id("kobweb-silk-icons")
}

group = "com.varabyte.kobwebx"
version = libs.versions.kobweb.get()

silkIcons {
    iconSet.set("lucide")
    version.set("1.69.0")
    iconsFile.set("lucide-icons.json")
}

kotlin {
    sourceSets {
        jsMain {
            dependencies {
                implementation(projects.frontend.silkWidgets)
            }
        }
    }
}

kobwebPublication {
    artifactName.set("Kobweb Silk Icons (Lucide)")
    artifactId.set("silk-icons-lucide")
    description.set("A collection of composables that directly wrap Lucide icons.")
}
