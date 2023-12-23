pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

rootProject.name = "playground"

includeBuild("../")

include(":site")
include(":sitelib")
include(":worker")
