pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}
enableFeaturePreview("VERSION_CATALOGS")

rootProject.name = "kobweb"

include(":cli:kobweb")
include(":common:kobweb-project")
include(":frontend:kobweb-core")
include(":frontend:kobweb-silk")
include(":frontend:kobweb-silk-icons-fa")
include(":frontend:web-compose-ext")
include(":backend:kobweb-api")
include(":backend:server-api")
include(":backend:server")
include(":gradle-plugins:application")
include(":gradle-plugins:extensions:markdown")