pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_PROJECT)

    repositories {
        mavenCentral()
        google()
    }
}

rootProject.name = "playground"

includeBuild("../")

include(":site")
include(":sitelib")
include(":workers:countdown")
include(":workers:sum")
