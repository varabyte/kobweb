pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}
enableFeaturePreview("VERSION_CATALOGS")

rootProject.name = "kobweb"

include(":cli:kobweb")
include(":common:kobweb")
include(":frontend:kobweb")
include(":frontend:kobweb-silk")
include(":frontend:kobweb-silk-icons-fa")
include(":frontend:web-compose-ext")
include(":frontend:gradle:application")
include(":backend:api")
include(":backend:server")
