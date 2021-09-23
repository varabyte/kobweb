pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")

        // Used for "examples" subprojects only, for finding the "Kobweb" plugins
        mavenLocal() // Useful for locally testing kobweb plugin work before publishing
        maven("https://us-central1-maven.pkg.dev/varabyte-repos/public")
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
include(":examples:helloworld")