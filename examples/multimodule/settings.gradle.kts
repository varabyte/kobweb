pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

rootProject.name = "multimodule"

includeBuild("../../")

include(":core")
include(":auth")
include(":chat")
include(":site")
