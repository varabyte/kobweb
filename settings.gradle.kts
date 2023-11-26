pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

plugins {
    id("com.gradle.enterprise") version ("3.15.1")
}

gradleEnterprise {
    if (System.getenv("CI") != null) {
        buildScan {
            termsOfServiceUrl = "https://gradle.com/terms-of-service"
            termsOfServiceAgree = "yes"
        }
    }
}

rootProject.name = "kobweb"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

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
include(":tools:gradle-plugins:core")
include(":tools:gradle-plugins:library")
include(":tools:gradle-plugins:application")
include(":tools:gradle-plugins:extensions:markdown")
include(":tools:ksp:project-processors")
include(":tools:processor-common")
