pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}

rootProject.name = "playground"

includeBuild("../")

include(":site")
include(":sitelib")
include(":workers:countdown")
include(":workers:sum")
