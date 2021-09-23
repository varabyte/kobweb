pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}
enableFeaturePreview("VERSION_CATALOGS")

rootProject.name = "kobweb"

include(":bin:kobweb")
include(":lib:kobweb")
include(":lib:kobweb-silk")
include(":lib:kobweb-silk-icons-fa")
include(":lib:web-compose-ext")
include(":plugins:application")