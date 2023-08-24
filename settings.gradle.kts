pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

rootProject.name = "kobweb"

include(":common:kobweb-common")
include(":common:client-server-models")
include(":frontend:kobweb-core")
include(":frontend:kobweb-compose")
include(":frontend:kobweb-silk")
include(":frontend:silk-foundation")
include(":frontend:silk-widgets")
include(":frontend:silk-widgets-kobweb")
include(":frontend:silk-icons-fa")
include(":frontend:silk-icons-mdi")
include(":frontend:relocated:kobweb-silk-widgets")
include(":frontend:relocated:kobweb-silk-icons-fa")
include(":frontend:relocated:kobweb-silk-icons-mdi")
include(":frontend:kobwebx-markdown")
include(":frontend:compose-html-ext")
include(":backend:kobweb-api")
include(":backend:server")
include(":backend:server-plugin")
include(":gradle-plugins:core")
include(":gradle-plugins:library")
include(":gradle-plugins:application")
include(":gradle-plugins:extensions:markdown")
include(":gradle-plugins:ksp-processor")
